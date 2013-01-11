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

import gg.pistol.scissors.player.Player;

import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// package private
@ThreadSafe
class GameImpl implements Game {

    private final int countdownStart;
    private final long timeBetweenCounts; // millis
    private final long throwTimeLimit; // millis
    private final List<Player> players;
    private final Set<Player> playerSet; // the same players as in the "players" field, but with efficient retrieval

    @GuardedBy("lock") private final Map<Player, Gesture> playerGestures;
    @GuardedBy("lock") private final Map<Player, GameResult> playerResults;
    @GuardedBy("lock") private GameState state;
    private final Lock lock;

    // package private
    GameImpl(int countdownStart, long timeBetweenCounts, long throwTimeLimit, List<Player> players) {
        if (players == null) {
            throw new NullPointerException("The players list is null.");
        }
        this.countdownStart = countdownStart;
        this.timeBetweenCounts = timeBetweenCounts;

        this.throwTimeLimit = throwTimeLimit;
        playerSet = new LinkedHashSet<Player>(players);
        this.players = new ArrayList<Player>(playerSet); // the list will contain unique players
        playerGestures = new HashMap<Player, Gesture>();
        playerResults = new HashMap<Player, GameResult>();
        state = GameState.NOT_STARTED;
        lock = new ReentrantLock();
    }

    /**
     * Associate a player with his/her thrown gesture. Once a player-gesture is associated it cannot be changed.
     * If this method is called when the game is not in the throwing state the gesture will be ignored.
     *
     * @param player
     * @param gesture
     */
    // package private
    void setPlayerGesture(Player player, Gesture gesture) {
        if (player == null || gesture == null) {
            throw new NullPointerException("The player or/and gesture are null.");
        }
        if (!playerSet.contains(player)) {
            throw new IllegalArgumentException("The player is not from this game.");
        }
        lock.lock();
        try {
            if (state != GameState.THROWING) {
                return; // the game is not in the throwing phase and the gesture is ignored
            }
            if (playerGestures.get(player) != null) {
                throw new IllegalStateException("The player has already thrown the gesture.");
            }
            playerGestures.put(player, gesture);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Set the result for a player. This should be called only when the game is finished.
     *
     * @param player
     * @param result
     */
    // package private
    void setPlayerResult(Player player, GameResult result) {
        if (player == null || result == null) {
            throw new NullPointerException("The player or/and result are null.");
        }
        if (!playerSet.contains(player)) {
            throw new IllegalArgumentException("The player is not from this game.");
        }
        lock.lock();
        try {
            if (state != GameState.FINISHED) {
                throw new IllegalStateException("The game is not finished.");
            }
            if (playerResults.get(player) != null) {
                throw new IllegalStateException("The player result is already set.");
            }
            playerResults.put(player, result);
        } finally {
            lock.unlock();
        }
    }

    @Nullable
    @Override
    public Gesture getPlayerGesture(Player player) {
        if (player == null) {
            throw new NullPointerException("The player is null.");
        }
        lock.lock();
        try {
            return playerGestures.get(player);
        } finally {
            lock.unlock();
        }
    }

    /**
     * @return true in case all the players have thrown their gestures, false otherwise
     */
    // package private
    boolean isGameFinishReady() {
        lock.lock();
        try {
            return playerGestures.values().size() == players.size();
        } finally {
            lock.unlock();
        }
    }

    // package private
    int getCountdownStart() {
        return countdownStart;
    }

    // package private
    long getTimeBetweenCounts() {
        return timeBetweenCounts;
    }

    @Override
    public long getThrowTimeLimit() {
        return throwTimeLimit;
    }

    @Override
    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    @Nullable
    @Override
    public GameResult getPlayerResult(Player player) {
        lock.lock();
        try {
            return playerResults.get(player);
        } finally {
            lock.unlock();
        }
    }

    // package private
    void setState(GameState newState) {
        if (newState == null) {
            throw new NullPointerException("The newState is null.");
        }
        lock.lock();
        try {
            if (state.ordinal() > newState.ordinal()) {
                throw new IllegalArgumentException("The newState is illegal because it is smaller than the current state " + state);
            }
            state = newState;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public GameState getState() {
        lock.lock();
        try {
            return state;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String toString() {
        lock.lock();
        try {
            return "GameImpl{" +
                    "countdownStart=" + countdownStart +
                    ", throwTimeLimit=" + throwTimeLimit +
                    ", players=" + players +
                    ", playerGestures=" + playerGestures +
                    ", playerResults=" + playerResults +
                    ", state=" + state +
                    '}';
        } finally {
            lock.unlock();
        }
    }

}
