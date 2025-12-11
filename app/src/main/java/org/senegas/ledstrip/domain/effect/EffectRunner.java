package org.senegas.ledstrip.domain.effect;

import org.senegas.ledstrip.domain.led.LedStrip;
import org.senegas.ledstrip.hardware.LedStripHardwareAdapter;

/* Synchronous effect runner: call step periodically (e.g., from a loop or test) */
public class EffectRunner {
    private final LedStrip strip;
    private final LedStripHardwareAdapter adapter;
    private final Effect effect;

    public EffectRunner(LedStrip strip, LedStripHardwareAdapter adapter, Effect effect) {
        this.strip = strip;
        this.adapter = adapter;
        this.effect = effect;
    }

    /**
     * Single step — runs effect for the given timestamp and pushes to adapter if changed.
     */
    public void step(long timestampMillis) {
        boolean changed = effect.apply(strip, timestampMillis);
        if (changed) {
            adapter.apply(strip.snapshot());
        }
    }
}
