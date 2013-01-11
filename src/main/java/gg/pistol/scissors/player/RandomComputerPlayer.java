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

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A computer player that returns random gestures.
 *
 * @author Bogdan Pistol
 */
@ThreadSafe
public class RandomComputerPlayer extends AbstractPlayer {

    private static final Logger LOG = Logger.getLogger(RandomComputerPlayer.class.getName());

    private final Random random;

    public RandomComputerPlayer(String name) {
        super(name);
        random = new Random();
    }

    @Override
    public void countdown(int count) {
        if (LOG.isLoggable(Level.INFO)) {
            LOG.info("Countdown " + count);
        }
    }

    @Override
    public Gesture getPlayerGesture() {
        Gesture gesture = Gesture.values()[random.nextInt(Gesture.values().length)];
        if (LOG.isLoggable(Level.INFO)) {
            LOG.info(getName() + " throws " + gesture);
        }
        return gesture;
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
