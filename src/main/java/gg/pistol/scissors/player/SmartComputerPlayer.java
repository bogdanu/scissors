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
package gg.pistol.scissors.player;

import gg.pistol.scissors.game.Game;
import gg.pistol.scissors.game.Gesture;
import gg.pistol.scissors.game.GestureComparator;

import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A computer player that waits until the last moment to throw the gesture while trying to peek at
 * the opponent's gesture.
 *
 * @author Bogdan Pistol
 */
@ThreadSafe
public class SmartComputerPlayer extends AbstractPlayer implements OpponentAwarePlayer {

    private static final Logger LOG = Logger.getLogger(RandomComputerPlayer.class.getName());
    private static final long SAFE_LIMIT_DELTA = 150L; // the number of millis to subtract from the thrown limit to be safe
    private static final long POLL_INTERVAL = 100L; // the number of millis to wait until peeking again at the opponent

    private final Random random;
    private final Lock lock;

    @GuardedBy("lock") @Nullable private Game game;
    @GuardedBy("lock") @Nullable private List<Player> opponentPlayers;

    public SmartComputerPlayer(String name) {
        super(name);
        random = new Random();
        lock = new ReentrantLock();
    }

    @Override
    public void observeGame(Game game, List<Player> opponentPlayers) {
        if (game == null || opponentPlayers == null) {
            throw new NullPointerException("The game or/and the opponentPlayers is null.");
        }
        lock.lock();
        try {
            this.game = game;
            this.opponentPlayers = opponentPlayers;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void countdown(int count) {
        if (LOG.isLoggable(Level.INFO)) {
            LOG.info("Countdown " + count);
        }
    }

    @Override
    public Gesture getPlayerGesture() {
        long throwStart = System.currentTimeMillis();
        lock.lock();
        try {
            if (game == null || opponentPlayers == null) {
                throw new IllegalStateException("OpponentAwarePlayer.observeGame() was not called.");
            }

            Gesture gesture = getPlayerGesture0(throwStart);
            if (LOG.isLoggable(Level.INFO)) {
                LOG.info(getName() + " throws " + gesture);
            }

            return gesture;
        } finally {
            lock.unlock();
        }
    }

    @Nullable
    private Gesture getPlayerGesture0(long throwStart) {
        if (opponentPlayers.isEmpty()) {
            return getRandomGesture();
        }
        long safeLimit = game.getThrowTimeLimit() - SAFE_LIMIT_DELTA;

        for (;;) {
            Gesture opponentGesture = getOpponentGesture();
            if (opponentGesture != null) {
                return getHigherGesture(opponentGesture);
            }

            // Math.abs() is used because in some cases (e.g. NTP, bad hardware) the difference could be negative.
            long delay = Math.abs(System.currentTimeMillis() - throwStart);
            if (delay > safeLimit) {
                break;
            }

            try {
                Thread.sleep(POLL_INTERVAL);
            } catch (InterruptedException e) {
                break;
            }
            if (Thread.interrupted()) { // in case the thread is interrupted do not wait anymore
                break;
            }
        }

        // As a last resort throw a random gesture.
        return getRandomGesture();
    }

    @Nullable
    private Gesture getOpponentGesture() {
        for (Player p : opponentPlayers) { // return the first found opponent gesture
            Gesture gesture = game.getPlayerGesture(p);
            if (gesture != null) {
                return gesture;
            }
        }
        return null;
    }

    private Gesture getRandomGesture() {
        return Gesture.values()[random.nextInt(Gesture.values().length)];
    }

    private Gesture getHigherGesture(Gesture gesture) {
        GestureComparator gestureComparator = Gesture.getGestureComparator();
        for (Gesture g : Gesture.values()) {
            if (gestureComparator.compare(gesture, g) < 0) {
                return g;
            }
        }
        throw new IllegalStateException("Should never throw.");
    }

    @Override
    public void gameFinished(Game game) {
        if (game == null) {
            throw new NullPointerException("The game is null.");
        }
        if (LOG.isLoggable(Level.INFO)) {
            LOG.info(getName() + "' game result: " + game.getPlayerResult(this));
        }
    }
}
