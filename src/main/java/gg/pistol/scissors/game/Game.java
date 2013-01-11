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
import java.util.List;
import java.util.Set;

/**
 * Rock-paper-scissors game round.
 *
 * <p>The game flows like this: the referee starts to count down until reaching zero, then the players will throw their
 * gestures to the referee and afterwards the referee will announce the game results.
 *
 * <p>Note that the game is not restricted to only 2 players, there can be any number of players. For information about
 * who wins a game with more than 2 players see {@link GameResult}.
 *
 * <p>The implementations of this class are thread safe.
 *
 * @author Bogdan Pistol
 */
public interface Game {

    /**
     * Return the gesture thrown by a player.
     *
     * @param player
     * @return the gesture of the player or null in case the game doesn't know the gesture of the player.
     */
    @Nullable
    Gesture getPlayerGesture(Player player);

    /**
     * The players could have the strategy to wait and try to see what the opponent throws. In order to limit
     * this strategy and to make the game more balanced there is a gesture throw time limit in place.
     * Exceeding the time limit will lose the game (with the exception when both players exceed the limit
     * in which case it is a tie).
     *
     * @return the throw gesture time limit in milliseconds
     */
    long getThrowTimeLimit();

    /**
     * Return the list of players participating in the game. The returned list is unmodifiable.
     *
     * @return all the players
     */
    List<Player> getPlayers();

    /**
     * Return the result of the game for a player.
     *
     * @param player
     * @return the player game result or null in case the game is not finished
     */
    @Nullable
    GameResult getPlayerResult(Player player);

    /**
     * @return the state of the game
     */
    GameState getState();

}
