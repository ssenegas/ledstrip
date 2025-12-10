package org.senegas.ledstrip.domain.led;

import org.senegas.ledstrip.domain.color.RgbColor;

import java.util.Arrays;

public final class LedStripState {
    private final RgbColor[] colors;

    public LedStripState(RgbColor[] colors) {
        this.colors = Arrays.copyOf(colors, colors.length);
    }

    public int length() { return colors.length; }
    public RgbColor colorAt(int index) {
        if (index < 0 || index >= colors.length) throw new IndexOutOfBoundsException();
        return colors[index];
    }

    public RgbColor[] toArray() {
        return Arrays.copyOf(colors, colors.length);
    }

    @Override
    public String toString() {
        return "LedStripState[length=" + colors.length + "]";
    }
}
