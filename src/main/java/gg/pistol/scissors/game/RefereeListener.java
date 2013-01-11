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

import gg.pistol.scissors.game.GameResult;
import gg.pistol.scissors.game.Gesture;

import java.util.EventListener;

/**
 * Entity that listens for events coming from a Referee.
 *
 * <p>The implementations of this interface should be thread-safe.
 *
 * @author Bogdan Pistol
 */
public interface RefereeListener extends EventListener {

    /**
     * The Referee sends count down events by calling repetitively this method until the count reaches zero
     * when the gestures can be thrown.
     *
     * @param count the current count value
     */
    void countdown(int count);

    /**
     * The Referee announces that the game is finished and the game results are available.
     *
     * @param game
     */
    void gameFinished(Game game);

}
