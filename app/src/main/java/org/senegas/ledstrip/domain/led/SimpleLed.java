package org.senegas.ledstrip.domain.led;

import org.senegas.ledstrip.domain.color.RgbColor;

import java.util.Objects;

public final class SimpleLed  implements Led {
    private final int index;
    private boolean on;
    private RgbColor color;

    SimpleLed(int index) {
        this.index = index;
        this.on = false;
        this.color = RgbColor.OFF;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public void turnOn() {
        this.on = true;
        if (this.color == null) {
            this.color = RgbColor.OFF;
        }
    }

    @Override
    public void turnOff() {
        this.on = false;
    }

    @Override
    public boolean isOn() {
        return on;
    }

    @Override
    public void setColor(RgbColor color) {
        this.color = Objects.requireNonNull(color);
    }

    @Override
    public RgbColor getColor() {
        return on ? color : RgbColor.OFF;
    }
}
