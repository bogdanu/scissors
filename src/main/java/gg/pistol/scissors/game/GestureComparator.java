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

/**
 * Comparator that determines the winning gesture.
 *
 * <p><b>NOTE:</b> This is not an implementation of {@link java.util.Comparator} and is not a defining a total order
 * relationship between the gestures (because the game gestures are not ordered, they are in a cycle).
 *
 * <p>The implementations of this class are thread safe.
 *
 * @author Bogdan Pistol
 */
public interface GestureComparator {

    /**
     * Compare the gestures to determine the winning gesture.
     *
     * <p>The comparison rules are: rock beats scissors, scissors beat paper, paper beats rock,
     * non-null beats null, same gestures is tie (including null-null).
     *
     * @param gesture1
     * @param gesture2
     * @return a negative integer if the first gesture is losing against the second,
     *         a positive integer if the first gesture is winning against the second and
     *         zero if there is a tie.
     */
    int compare(@Nullable Gesture gesture1, @Nullable Gesture gesture2);

}
