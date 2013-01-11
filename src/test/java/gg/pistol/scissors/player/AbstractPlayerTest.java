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

import gg.pistol.scissors.ObjectVerifier;
import gg.pistol.scissors.game.Game;
import gg.pistol.scissors.game.Gesture;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Nullable;

public class AbstractPlayerTest {

    private String player1Name = "Player 1";
    private String player2Name = "Player 2";
    private AbstractPlayer player1;
    private AbstractPlayer player1Copy;
    private AbstractPlayer player2;

    private AbstractPlayer createAbstractPlayer(String name) {
        return new AbstractPlayer(name) {
            @Nullable
            @Override
            public Gesture getPlayerGesture() {
                return null;
            }

            @Override
            public void countdown(int count) {
            }

            @Override
            public void gameFinished(Game game) {
            }
        };
    }

    @Before
    public void setUp() throws Exception {
        player1 = createAbstractPlayer(player1Name);
        player1Copy = createAbstractPlayer(player1Name);
        player2 = createAbstractPlayer(player2Name);
    }

    @Test
    public void testEquals() throws Exception {
        ObjectVerifier.verifyEquals(player1, player1Copy, player2);
    }

    @Test
    public void testHashCode() throws Exception {
        ObjectVerifier.verifyHashCode(player1, player1Copy);

    }

    @Test
    public void testGetName() throws Exception {
        Assert.assertEquals(player1Name, player1.getName());
    }
}
