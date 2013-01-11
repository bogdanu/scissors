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

/**
 * The possible results of the game for a player. The game can have more than 2 players and the definition
 * of winning the game, losing the game or a having a tie are below.
 *
 * <p>The winners of the game are all the players that have no losses, have wins and optionally can have ties against
 * other players.
 *
 * <p>The losers of the game are all the players that have any losses (with the exception when all the players
 * have losses then it is a tie for all the players) against other players.
 *
 * <p>In all the other cases (same gesture for all the players or all the players have losses) it is a game tie
 * for all the players.
 *
 * <p>Note: a game tie is possible only for all the players, it is not possible for all the players to win and
 * it is not possible for all the players to lose.
 *
 * @author Bogdan Pistol
 */
public enum GameResult {
    LOSS, WIN, TIE
}
