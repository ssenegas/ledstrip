package org.senegas.ledstrip.domain.led;

import org.senegas.ledstrip.domain.color.RgbColor;
import org.senegas.ledstrip.domain.effect.Effect;
import org.senegas.ledstrip.hardware.LedStripHardwareAdapter;

import java.util.Objects;

public class LedStripController {
    private final LedStrip strip;
    private final LedStripHardwareAdapter adapter;

    public LedStripController(LedStrip strip, LedStripHardwareAdapter adapter) {
        this.strip = Objects.requireNonNull(strip);
        this.adapter = Objects.requireNonNull(adapter);
    }

    public void setPixel(int index, RgbColor color) {
        strip.setPixel(index, color);
        adapter.apply(strip.snapshot());
    }

    public void togglePixel(int index) {
        strip.getLed(index).toggle();
        adapter.apply(strip.snapshot());
    }

    public void fill(RgbColor color) {
        strip.fill(color);
        adapter.apply(strip.snapshot());
    }

    public void clear() {
        strip.clear();
        adapter.apply(strip.snapshot());
    }

    public void turnOffPixel(int index) {
        strip.getLed(index).turnOff();
        adapter.apply(strip.snapshot());
    }

    public void turnOffAll() {
        for (int i = 0; i < strip.getLength(); i++) {
            strip.getLed(i).turnOff();
        }
        adapter.apply(strip.snapshot());
    }

    public void turnOnPixel(int index) {
        strip.getLed(index).turnOn();
        adapter.apply(strip.snapshot());
    }

    public void turnOnAll() {
        for (int i = 0; i < strip.getLength(); i++) {
            strip.getLed(i).turnOn();
        }
        adapter.apply(strip.snapshot());
    }

    public boolean applyEffect(Effect effect, long timestampMillis) {
        boolean changed = effect.apply(strip, timestampMillis);
        if (changed) {
            adapter.apply(strip.snapshot());
        }
        return changed;
    }

    public LedStripState getSnapshot() {
        return strip.snapshot();
    }
}
