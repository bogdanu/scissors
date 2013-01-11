/*
 * Copyright (C) 2013 Bogdan Pistol
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gg.pistol.scissors.game;

import gg.pistol.scissors.player.GameObserver;
import gg.pistol.scissors.player.OpponentAwarePlayer;
import gg.pistol.scissors.player.Player;

import javax.annotation.concurrent.ThreadSafe;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Referee implementation that provides fairness to all the players. When the game starts the referee will start
 * counting by sending count down events to the players and to the game observers. When reaching zero the referee
 * will ask all the players to throw their gestures and when a player throws his/her gesture the referee will also
 * notify the game observers. After the throwing phase of the game the referee will announce the game results to all
 * the players and observers.
 *
 * <p>A simple implementation for the referee would be to maintain a list of players and observers and to send
 * the events iteratively to all the players and observers. The disadvantage of the simple implementation is that
 * it does not provide fairness to players: some players receive the events sooner and other receive them later.
 * A player that receives the events later would not know the game state as fast as other players and could be
 * disadvantaged.
 *
 * <p>The referee implementation provides fairness to players and observers by not favoring any one player or observer
 * while delivering the events. This is achieved by delivering the events to N players and M observers using N+M threads
 * and by synchronizing the execution of all the threads (with a CountDownLatch).
 *
 * @author Bogdan Pistol
 */
@ThreadSafe
public class RefereeImpl implements Referee {

    private static final Logger LOG = Logger.getLogger(RefereeImpl.class.getName());

    private final GameImpl game;
    private final List<GameObserver> observers;
    private final ExecutorService executor;

    /**
     * @param countdownStart from where to start the countdown
     * @param timeBetweenCounts how much time to wait between counts in millis
     * @param throwTimeLimit how much time to wait for every player to throw the gesture in millis
     * @param players all the players participating in the game
     * @param observers all the game observers that will watch the game
     */
    public RefereeImpl(int countdownStart, long timeBetweenCounts, long throwTimeLimit, List<Player> players, List<GameObserver> observers) {
        if (players == null || observers == null) {
            throw new NullPointerException("The players or/and the observers are null.");
        }
        game = new GameImpl(countdownStart, timeBetweenCounts, throwTimeLimit, players);
        this.observers = new ArrayList<GameObserver>(observers);
        executor = Executors.newCachedThreadPool();

        // for all the players implementing OpponentAwarePlayer make them aware of their opponents
        for (Player p : game.getPlayers()) {
            if (p instanceof OpponentAwarePlayer) {
                ((OpponentAwarePlayer) p).observeGame(game, getOpponentPlayers(p));
            }
        }
    }

    private List<Player> getOpponentPlayers(Player player) {
        List<Player> ret = new ArrayList<Player>();
        for (Player p : game.getPlayers()) {
            if (!player.equals(p)) {
                ret.add(p);
            }
        }
        return ret;
    }

    public void startGame() throws GameAlreadyStartedException {
        if (LOG.isLoggable(Level.INFO)) {
            LOG.info("Starting new game " + game);
        }
        if (game.getState() != GameState.NOT_STARTED) {
            throw new GameAlreadyStartedException();
        }
        executor.execute(new RefereeTask());
    }

    /**
     * Wait until the game is finished or the <code>awaitTime</code> is exceeded.
     *
     * @param awaitTime the time to wait in millis
     */
    // package private
    void waitGameFinish(long awaitTime) {
        if (LOG.isLoggable(Level.INFO)) {
            LOG.info("Waiting for the game to finish.");
        }
        try {
            executor.awaitTermination(awaitTime, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            // the game was stopped
        }
    }

    @Override
    public Game getGame() {
        return game;
    }

    @Override
    public void stopGame() {
        if (LOG.isLoggable(Level.INFO)) {
            LOG.info("Stopping current game " + game);
        }
        executor.shutdownNow();
        game.setState(GameState.STOPPED);
    }

    /**
     * Notify the players and the observers with a countdown event.
     */
    private void sendCountdown(int count) throws InterruptedException {
        List<Runnable> events = new ArrayList<Runnable>();
        for (Player player : game.getPlayers()) {
            events.add(new CountdownEvent(player, count));
        }
        for (GameObserver observer : observers) {
            events.add(new CountdownEvent(observer, count));
        }
        sendConcurrentEvents(events);
    }

    /**
     * Notify the players to throw their gestures.
     */
    private void retrievePlayerGestures(Thread refereeThread) throws InterruptedException {
        List<Runnable> events = new ArrayList<Runnable>();
        for (Player player : game.getPlayers()) {
            events.add(new PlayerGestureRetriever(player, refereeThread));
        }
        sendConcurrentEvents(events);
    }

    /**
     * Handle the gesture thrown by a player.
     */
    private void handlePlayerGesture(Player player, Gesture gesture, Thread refereeThread) throws InterruptedException {
        game.setPlayerGesture(player, gesture);

        // notify the observers of the thrown gesture
        List<Runnable> events = new ArrayList<Runnable>();
        for (GameObserver observer : observers) {
            events.add(new GestureThrownEvent(observer, player, gesture));
        }
        sendConcurrentEvents(events);

        if (game.isGameFinishReady()) {
            // In case all the players threw their gestures then the game can finish.
            // Wake up the RefereeTask from Thread.sleep (by interrupting the referee thread) to finish the game.
            refereeThread.interrupt();
        }
    }

    private void finishGame() throws InterruptedException {
        // compute the game results
        GestureComparator comparator = Gesture.getGestureComparator();
        int loserPlayers = 0;
        for (Player player : game.getPlayers()) {
            Gesture playerGesture = game.getPlayerGesture(player);

            boolean oneWin = false;
            boolean oneLoss = false;
            for (Player opponent : game.getPlayers()) {
                if (player.equals(opponent)) {
                    continue;
                }
                Gesture opponentGesture = game.getPlayerGesture(opponent);
                if (comparator.compare(playerGesture, opponentGesture) > 0) {
                    oneWin = true;
                } else if (comparator.compare(playerGesture, opponentGesture) < 0) {
                    oneLoss = true;
                }
            }
            if (oneLoss) {
                game.setPlayerResult(player, GameResult.LOSS);
                loserPlayers++;
            } else if (oneWin) {
                game.setPlayerResult(player, GameResult.WIN);
            } else {
                game.setPlayerResult(player, GameResult.TIE);
            }
        }
        if (loserPlayers == game.getPlayers().size()) { // all players lost the game so it is a tie for everybody
            for (Player player : game.getPlayers()) {
                game.setPlayerResult(player, GameResult.TIE);
            }
        }

        // notify the players and the observers with the game results
        List<Runnable> events = new ArrayList<Runnable>();
        for (Player player : game.getPlayers()) {
            events.add(new GameFinishedEvent(player, game));
        }
        for (GameObserver observer : observers) {
            events.add(new GameFinishedEvent(observer, game));
        }
        sendConcurrentEvents(events);
    }

    /**
     * Send a list of events concurrently and fairly by not favoring any event recipient.
     */
    private void sendConcurrentEvents(List<Runnable> events) throws InterruptedException {
        // The +1 is because the current thread also needs to synchronize its execution (awaiting for all the threads
        // to be ready before continuing and returning from this method).
        CountDownLatch ready = new CountDownLatch(events.size() + 1);

        for (Runnable event : events) {
            // all the submitted tasks to the executor will block awaiting on the CountDownLatch
            executor.execute(new ConcurrentEvent(event, ready));
        }
        ready.countDown();
        ready.await();
    }

    /**
     * The management of the game done by the referee.
     */
    private class RefereeTask implements Runnable {
        @Override
        public void run() {
            try {
                game.setState(GameState.COUNTING);
                for (int i = game.getCountdownStart(); i >= 0; i--) {
                    sendCountdown(i);
                    if (i > 0) {
                        Thread.sleep(game.getTimeBetweenCounts());
                    }
                }

                game.setState(GameState.THROWING);
                retrievePlayerGestures(Thread.currentThread());
                try {
                    Thread.sleep(game.getThrowTimeLimit());
                } catch (InterruptedException e) {
                    if (!game.isGameFinishReady()) { // interrupted by stopping the game, quiting
                        throw e;
                    }
                    // interrupted because all the players threw their gestures, can finish the game
                }

                game.setState(GameState.FINISHED);
                finishGame();
                executor.shutdown();
            } catch (InterruptedException e) {
                // quit task
            }
        }
    }

    private static class CountdownEvent implements Runnable {
        private final RefereeListener listener;
        private final int count;

        CountdownEvent(RefereeListener listener, int count) {
            this.listener = listener;
            this.count = count;
        }

        @Override
        public void run() {
            listener.countdown(count);
        }
    }

    private static class GameFinishedEvent implements Runnable {
        private final RefereeListener listener;
        private final Game game;

        GameFinishedEvent(RefereeListener listener, Game game) {
            this.listener = listener;
            this.game = game;
        }

        @Override
        public void run() {
            listener.gameFinished(game);
        }
    }

    private class PlayerGestureRetriever implements Runnable {
        private final Player player;
        private final Thread refereeThread;

        PlayerGestureRetriever(Player player, Thread refereeThread) {
            this.player = player;
            this.refereeThread = refereeThread;
        }

        @Override
        public void run() {
            try {
                Gesture gesture = player.getPlayerGesture();
                if (gesture != null) {
                    handlePlayerGesture(player, gesture, refereeThread);
                }
            } catch (InterruptedException e) {
                // quit task
            }
        }
    }

    private static class GestureThrownEvent implements Runnable {
        private final GameObserver listener;
        private final Player player;
        private final Gesture gesture;

        GestureThrownEvent(GameObserver listener, Player player, Gesture gesture) {
            this.listener = listener;
            this.player = player;
            this.gesture = gesture;
        }

        @Override
        public void run() {
            listener.gestureThrown(player, gesture);
        }
    }

    /**
     * Wrapper for events that provides concurrent and fair execution.
     */
    private static class ConcurrentEvent implements Runnable {
        private final Runnable event;
        private final CountDownLatch ready;

        ConcurrentEvent(Runnable event, CountDownLatch ready) {
            this.event = event;
            this.ready = ready;
        }

        @Override
        public void run() {
            ready.countDown();
            try {
                ready.await(); // await until all other concurrent threads are ready
            } catch (InterruptedException e) { // can happen if the game is stopped
                return;
            }
            event.run();
        }
    }

}
