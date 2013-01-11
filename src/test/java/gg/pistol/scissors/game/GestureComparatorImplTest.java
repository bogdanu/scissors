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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GestureComparatorImplTest {

    private GestureComparatorImpl comparator;

    @Before
    public void setUp() throws Exception {
        comparator = new GestureComparatorImpl();
    }

    @Test
    public void testCompare() throws Exception {
        assertEquals(-1, comparator.compare(Gesture.ROCK, Gesture.PAPER));
        assertEquals(1, comparator.compare(Gesture.PAPER, Gesture.ROCK));
        assertEquals(-1, comparator.compare(Gesture.PAPER, Gesture.SCISSORS));
        assertEquals(1, comparator.compare(Gesture.SCISSORS, Gesture.PAPER));
        assertEquals(-1, comparator.compare(Gesture.SCISSORS, Gesture.ROCK));
        assertEquals(1, comparator.compare(Gesture.ROCK, Gesture.SCISSORS));

        assertEquals(0, comparator.compare(Gesture.ROCK, Gesture.ROCK));
        assertEquals(0, comparator.compare(Gesture.PAPER, Gesture.PAPER));
        assertEquals(0, comparator.compare(Gesture.SCISSORS, Gesture.SCISSORS));
        assertEquals(0, comparator.compare(null, null));

        assertEquals(1, comparator.compare(Gesture.ROCK, null));
        assertEquals(1, comparator.compare(Gesture.PAPER, null));
        assertEquals(1, comparator.compare(Gesture.SCISSORS, null));
        assertEquals(-1, comparator.compare(null, Gesture.ROCK));
        assertEquals(-1, comparator.compare(null, Gesture.PAPER));
        assertEquals(-1, comparator.compare(null, Gesture.SCISSORS));
    }
}
