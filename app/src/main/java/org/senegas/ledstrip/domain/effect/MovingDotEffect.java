package org.senegas.ledstrip.domain.effect;

import org.senegas.ledstrip.domain.color.RgbColor;
import org.senegas.ledstrip.domain.led.LedStrip;

import java.util.Objects;

public class MovingDotEffect implements Effect {
    private final RgbColor color;
    private final long periodMillis; // time to complete one cycle

    public MovingDotEffect(RgbColor color, long periodMillis) {
        this.color = Objects.requireNonNull(color);
        if (periodMillis <= 0) throw new IllegalArgumentException("period > 0");
        this.periodMillis = periodMillis;
    }

    @Override
    public boolean apply(LedStrip strip, long timestampMillis) {
        int n = strip.length();
        if (n == 0) return false;

        long t = timestampMillis % periodMillis;
        double fraction = (double)t / periodMillis;
        int pos = (int)Math.floor(fraction * n);

        // only update if different from previous colors (simplified)
        boolean changed = false;
        for (int i = 0; i < n; i++) {
            if (i == pos) {
                if (!strip.getLed(i).getColor().equals(color) || !strip.getLed(i).isOn()) {
                    strip.getLed(i).setColor(color);
                    strip.getLed(i).turnOn();
                    changed = true;
                }
            } else {
                if (strip.getLed(i).isOn() || !strip.getLed(i).getColor().isOff()) {
                    strip.getLed(i).setColor(RgbColor.OFF);
                    strip.getLed(i).turnOff();
                    changed = true;
                }
            }
        }
        return changed;
    }
}
