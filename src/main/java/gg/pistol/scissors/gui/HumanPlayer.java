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
package gg.pistol.scissors.gui;

import gg.pistol.scissors.game.Game;
import gg.pistol.scissors.game.Gesture;
import gg.pistol.scissors.player.AbstractPlayer;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import javax.swing.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Player used by the GameDialog to forward human gestures to the referee.
 *
 * @author Bogdan Pistol
 */
// package private
@ThreadSafe
class HumanPlayer extends AbstractPlayer {

    /**
     * Queue used to wait for user's gesture input.
     */
    private final BlockingQueue<Gesture> humanGesture;

    /**
     * The thread (that is coming from the referee) and asks for the gesture (by calling {@link #getPlayerGesture}).
     */
    @Nullable private AtomicReference<Thread> refereeThread;

    // package private
    HumanPlayer(String name) {
        super(name);
        humanGesture = new LinkedBlockingQueue<Gesture>(1); // only one gesture to wait for
        refereeThread = new AtomicReference<Thread>();
    }

    /**
     * User's gesture input.
     *
     * @param gesture
     */
    // package private
    void setHumanGesture(Gesture gesture) {
        humanGesture.offer(gesture);
    }

    /**
     * Cancel waiting for user's gesture input and interrupt the blocked referee thread.
     */
    // package private
    void cancelHumanGesture() {
        Thread thread = refereeThread.get();
        if (thread != null) {
            thread.interrupt();
        }
    }

    @Override
    public Gesture getPlayerGesture() {
        refereeThread.set(Thread.currentThread());

        try {
            return humanGesture.take();
        } catch (InterruptedException e) {
            // canceled
        }
        return null;
    }

    @Override
    public void countdown(int count) {
    }

    @Override
    public void gameFinished(Game game) {
    }
}
