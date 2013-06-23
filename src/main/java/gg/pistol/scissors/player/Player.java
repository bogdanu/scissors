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

import gg.pistol.scissors.game.Gesture;
import gg.pistol.scissors.game.RefereeListener;

import javax.annotation.Nullable;

/**
 * Entity that is able to play a game.
 *
 * <p>The implementations of this interface are thread-safe.
 *
 * @author Bogdan Pistol
 */
public interface Player extends RefereeListener {

    /**
     * @return the name of the player
     */
    String getName();

    /**
     * The Referee asks for the player gesture. This method should be implemented by a player to return
     * the gesture quickly (e.g. it should not take more time than the throw limit) or the player will lose.
     *
     * @return the gesture the player throws or null in case the player does not throw (e.g. the throw limit is exceeded)
     */
    @Nullable
    Gesture getPlayerGesture();

}
