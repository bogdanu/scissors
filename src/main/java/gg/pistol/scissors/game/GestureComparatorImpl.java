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

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

// package private
@ThreadSafe
class GestureComparatorImpl implements GestureComparator {

    @Override
    public int compare(@Nullable Gesture gesture1, @Nullable Gesture gesture2) {
        if (gesture1 == gesture2) {
            return 0;
        }
        if ((gesture1 == Gesture.ROCK && gesture2 == Gesture.PAPER) ||
                (gesture1 == Gesture.PAPER && gesture2 == Gesture.SCISSORS) ||
                (gesture1 == Gesture.SCISSORS && gesture2 == Gesture.ROCK) ||
                gesture1 == null) {
            return -1;
        }
        return 1;
    }

}
