package org.senegas.ledstrip.domain.led;

import org.senegas.ledstrip.domain.color.RgbColor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class LedStrip {
    private final List<Led> leds;

    // Private constructor; use builder / factory
    LedStrip(int length) {
        if (length <= 0) throw new IllegalArgumentException("length must be >= 1");
        this.leds = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            this.leds.add(new SimpleLed(i));
        }
    }

    public static LedStrip ofLength(int length) {
        return new LedStrip(length);
    }

    public int length() {
        return leds.size();
    }

    public Led getLed(int index) {
        checkIndex(index);
        return leds.get(index);
    }

    public List<Led> leds() {
        return Collections.unmodifiableList(leds);
    }

    public void fill(RgbColor color) {
        Objects.requireNonNull(color);
        for (Led led : leds) {
            led.setColor(color);
            led.turnOn();
        }
    }

    public void clear() {
        for (Led led : leds) {
            led.turnOff();
            led.setColor(RgbColor.OFF);
        }
    }

    public void setPixel(int index, RgbColor color) {
        checkIndex(index);
        Led led = leds.get(index);
        led.setColor(Objects.requireNonNull(color));
        led.turnOn();
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= leds.size()) {
            throw new IndexOutOfBoundsException("index " + index + " for length " + leds.size());
        }
    }

    /**
     * Snapshot of current state intended to be passed to adapters.
     * Immutable view object.
     */
    public LedStripState snapshot() {
        RgbColor[] colors = new RgbColor[leds.size()];
        for (int i = 0; i < leds.size(); i++) {
            colors[i] = leds.get(i).getColor();
        }
        return new LedStripState(colors);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        LedStripState state = snapshot();
        sb.append("LED Strip (").append(state.length()).append(" LEDs)\n");
        sb.append("--------------------------------------------\n");

        for (int i = 0; i < state.length(); i++) {
            RgbColor c = state.colorAt(i);
            sb.append(String.format(
                    "[%02d] R:%3d G:%3d B:%3d",
                    i, c.red(), c.green(), c.blue()
            ));
            sb.append("\n");
        }
        return sb.toString();
    }
}
