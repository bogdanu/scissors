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

import gg.pistol.scissors.player.GameObserver;
import gg.pistol.scissors.player.OpponentAwarePlayer;
import gg.pistol.scissors.player.Player;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class RefereeImplTest {

    private RefereeImpl referee;
    private int countdownStart;
    private long timeBetweenCounts;
    private long throwTimeLimit;
    private long finishMaxTime;
    private OpponentAwarePlayer player1;
    private Player player2;
    private GameObserver observer;

    @Before
    public void setUp() throws Exception {
        countdownStart = 2;
        timeBetweenCounts = 0L;
        throwTimeLimit = 500L;
        finishMaxTime = 2000L;
        player1 = mock(OpponentAwarePlayer.class);
        player2 = mock(Player.class);
        observer = mock(GameObserver.class);
        referee = new RefereeImpl(countdownStart, timeBetweenCounts, throwTimeLimit,
                Arrays.<Player>asList(player1, player2), Arrays.<GameObserver>asList(observer));
    }

    @After
    public void tearDown() throws Exception {
        referee.stopGame();
    }

    @Test(expected = GameAlreadyStartedException.class)
    public void testStartGameTwice() throws Exception {
        referee.startGame();
        referee.startGame();
    }

    @Test
    public void testStartGame() throws Exception {
        when(player1.getPlayerGesture()).thenReturn(Gesture.ROCK);
        when(player2.getPlayerGesture()).thenReturn(Gesture.PAPER);

        referee.startGame();
        referee.waitGameFinish(finishMaxTime);

        // player1 is OpponentAwarePlayer
        verify(player1).observeGame(referee.getGame(), Arrays.asList(player2));

        for (int i = countdownStart; i >= 0; i--) {
            verify(observer).countdown(i);
            verify(player1).countdown(i);
            verify(player2).countdown(i);
        }

        verify(player1).getPlayerGesture();
        verify(player2).getPlayerGesture();
        verify(observer).gestureThrown(player1, Gesture.ROCK);
        verify(observer).gestureThrown(player2, Gesture.PAPER);

        verify(observer).gameFinished(referee.getGame());
        verify(player1).gameFinished(referee.getGame());
        verify(player2).gameFinished(referee.getGame());

        assertEquals(GameState.FINISHED, referee.getGame().getState());
        assertEquals(GameResult.LOSS, referee.getGame().getPlayerResult(player1));
        assertEquals(GameResult.WIN, referee.getGame().getPlayerResult(player2));
    }

    @Test
    public void testStopGame() throws Exception {
        referee.startGame();
        referee.stopGame();
        assertEquals(GameState.STOPPED, referee.getGame().getState());
    }
}
