package org.senegas.ledstrip.domain.effect;

import java.util.Objects;

public abstract class AbstractEffect implements Effect {

    private final String name;

    protected AbstractEffect(String name) {
        this.name = Objects.requireNonNull(name);
    }

    public final String getName() {
        return name;
    }

    @Override
    public final String toString() {
        return name;
    }
}
