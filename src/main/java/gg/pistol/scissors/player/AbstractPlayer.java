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

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public abstract class AbstractPlayer implements Player {

    private final String name;

    protected AbstractPlayer(String name) {
        if (name == null) {
            throw new NullPointerException("The name is null.");
        }
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractPlayer)) return false;

        AbstractPlayer that = (AbstractPlayer) o;

        if (!name.equals(that.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "AbstractPlayer{" +
                "name='" + name + '\'' +
                '}';
    }

    @Override
    public String getName() {
        return name;
    }
}
