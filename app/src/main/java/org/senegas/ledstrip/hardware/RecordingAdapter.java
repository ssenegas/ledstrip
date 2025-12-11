package org.senegas.ledstrip.hardware;

import org.senegas.ledstrip.domain.led.LedStripState;

import java.util.Optional;

/* Adapter that records last applied state (for tests / simulation) */
public class RecordingAdapter implements LedStripHardwareAdapter {
    private LedStripState last;

    @Override
    public void apply(LedStripState state) {
        this.last = state;
        // Could log or render to UI in a simulation
    }

    public Optional<LedStripState> last() {
        return Optional.ofNullable(last);
    }
}
