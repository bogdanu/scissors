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
import gg.pistol.scissors.game.GameResult;
import gg.pistol.scissors.game.GameState;
import gg.pistol.scissors.game.Gesture;
import gg.pistol.scissors.game.RefereeImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SmartComputerPlayerTest {

    private SmartComputerPlayer player;
    private Player opponentPlayer;
    private Game game;

    @Before
    public void setUp() throws Exception {
        player = new SmartComputerPlayer("player");
        opponentPlayer = mock(Player.class);
        game = mock(Game.class);
    }

    @Test
    public void testGetPlayerGesture() throws Exception {
        player.observeGame(game, Arrays.asList(opponentPlayer));
        when(game.getPlayerGesture(opponentPlayer)).thenReturn(Gesture.PAPER);

        Gesture playerGesture = player.getPlayerGesture();
        assertEquals(Gesture.SCISSORS, playerGesture);
    }

}
