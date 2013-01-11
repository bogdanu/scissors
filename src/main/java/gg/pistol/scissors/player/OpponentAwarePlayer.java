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

import java.util.List;

/**
 * Player that is aware of the game and of his/her opponents.
 *
 * <p>Having knowledge of the opponents, the player can have better strategies when throwing his/her gesture.
 * The player could throw his/her gesture immediately or instead he/she could try to peek at the opponent
 * to see what gesture is being thrown. This strategy is risky and can lose the game if the
 * player waits too long (trying to see the move of the opponent and exceeding the throw gesture time limit)
 * and the Referee notices.
 *
 * <p>The implementations of this interface are thread-safe.
 *
 * @author Bogdan Pistol
 */
public interface OpponentAwarePlayer extends Player {

    /**
     * This method will be called before the game starts and the provided information can be used to peek at
     * the opponents.
     *
     * @param game
     * @param opponentPlayers
     */
    void observeGame(Game game, List<Player> opponentPlayers);

}
