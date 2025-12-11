package org.senegas.ledstrip.hardware;

import org.senegas.ledstrip.domain.led.LedStripState;

/* Simple adapter that does nothing — useful for unit tests / headless runs */
public class NoopAdapter implements LedStripHardwareAdapter {
    @Override
    public void apply(LedStripState state) { /* no-op */ }
}
