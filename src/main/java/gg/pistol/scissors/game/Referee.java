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

import java.util.List;

/**
 * The official who manages the game and ensure that the rules are adhered to.
 *
 * <p>The implementations of this interface should be thread-safe.
 *
 * @author Bogdan Pistol
 */
public interface Referee {

    /**
     * Start a new game, the referee will start counting.
     *
     * @throws GameAlreadyStartedException
     */
    void startGame() throws GameAlreadyStartedException;

    /**
     * @return the current game
     */
    Game getGame();

    /**
     * Forcibly stop the current game.
     */
    void stopGame();

}
