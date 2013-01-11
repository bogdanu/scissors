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
import gg.pistol.scissors.player.Player;

/**
 * A referee listener that is observing the game (it is not a player).
 *
 * <p>The implementations of this interface should be thread-safe.
 *
 * @author Bogdan Pistol
 */
public interface GameObserver extends RefereeListener {

    /**
     * Notification that a player has thrown a gesture.
     *
     * @param player
     * @param gesture
     */
    void gestureThrown(Player player, Gesture gesture);

}
