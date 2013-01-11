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
package gg.pistol.scissors;

import static org.junit.Assert.*;

/**
 * Test utility to verify standard {@link Object} methods.
 *
 * @author Bogdan Pistol
 */
public class ObjectVerifier {

    public static void verifyHashCode(Object obj, Object objCopy) {
        assertTrue(obj.equals(objCopy));
        assertTrue(obj.hashCode() == objCopy.hashCode());
    }

    public static void verifyEquals(Object first, Object firstCopy, Object second) {
        assertTrue(first.equals(first));
        assertTrue(first.equals(firstCopy));
        assertTrue(firstCopy.equals(first));

        assertFalse(first.equals(second));
        assertFalse(first.equals(null));
        assertFalse(first.equals(new Object()));
    }

}
