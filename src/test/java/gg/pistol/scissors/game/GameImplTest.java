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

import gg.pistol.scissors.player.Player;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class GameImplTest {

    private GameImpl game;
    private int countdownStart;
    private long timeBetweenCounts;
    private long throwTimeLimit;
    private Player player1;
    private Player player2;

    @Before
    public void setUp() throws Exception {
        countdownStart = 2;
        timeBetweenCounts = 1000L;
        throwTimeLimit = 500L;
        player1 = mock(Player.class);
        player2 = mock(Player.class);
        game = new GameImpl(countdownStart, timeBetweenCounts, throwTimeLimit, Arrays.<Player>asList(player1, player2));
    }

    @Test
    public void testConstructor() throws Exception {
        assertEquals(countdownStart, game.getCountdownStart());
        assertEquals(timeBetweenCounts, game.getTimeBetweenCounts());
        assertEquals(throwTimeLimit, game.getThrowTimeLimit());
        assertEquals(2, game.getPlayers().size());
        assertEquals(player1, game.getPlayers().get(0));
        assertEquals(player2, game.getPlayers().get(1));
        assertEquals(GameState.NOT_STARTED, game.getState());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetPlayerGestureUnknownPlayer() throws Exception {
        Player unknownPlayer = mock(Player.class);
        game.setPlayerGesture(unknownPlayer, Gesture.ROCK);
    }

    @Test
    public void testSetPlayerGestureIgnore() throws Exception {
        game.setPlayerGesture(player1, Gesture.ROCK);
        assertNull(game.getPlayerGesture(player1));
    }

    @Test
    public void testSetPlayerGesture() throws Exception {
        game.setState(GameState.THROWING);
        assertNull(game.getPlayerGesture(player1));
        game.setPlayerGesture(player1, Gesture.ROCK);
        assertEquals(Gesture.ROCK, game.getPlayerGesture(player1));
    }

    @Test(expected = IllegalStateException.class)
    public void testSetPlayerGestureTwice() throws Exception {
        game.setState(GameState.THROWING);
        game.setPlayerGesture(player1, Gesture.ROCK);
        game.setPlayerGesture(player1, Gesture.PAPER);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetPlayerResultUnknownPlayer() throws Exception {
        Player unknownPlayer = mock(Player.class);
        game.setPlayerResult(unknownPlayer, GameResult.TIE);
    }

    @Test(expected = IllegalStateException.class)
    public void testSetPlayerResultTooEarly() throws Exception {
        game.setPlayerResult(player1, GameResult.TIE);
    }

    @Test
    public void testSetPlayerResult() throws Exception {
        assertNull(game.getPlayerResult(player1));
        game.setState(GameState.FINISHED);
        game.setPlayerResult(player1, GameResult.TIE);
        assertEquals(GameResult.TIE, game.getPlayerResult(player1));
    }

    @Test(expected = IllegalStateException.class)
    public void testSetPlayerResultTwice() throws Exception {
        game.setState(GameState.FINISHED);
        game.setPlayerResult(player1, GameResult.TIE);
        game.setPlayerResult(player1, GameResult.WIN);
    }

    @Test
    public void testIsGameFinishReady() throws Exception {
        assertFalse(game.isGameFinishReady());
        game.setState(GameState.THROWING);
        game.setPlayerGesture(player1, Gesture.ROCK);
        game.setPlayerGesture(player2, Gesture.PAPER);
        assertTrue(game.isGameFinishReady());
    }

    @Test
    public void testSetState() throws Exception {
        assertEquals(GameState.NOT_STARTED, game.getState());
        game.setState(GameState.COUNTING);
        assertEquals(GameState.COUNTING, game.getState());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetStateIllegal() throws Exception {
        game.setState(GameState.COUNTING);
        game.setState(GameState.NOT_STARTED);
    }

}
