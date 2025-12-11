package org.senegas.ledstrip.hardware;

import org.senegas.ledstrip.domain.led.LedStripState;

public interface LedStripHardwareAdapter {
    /**
     * Apply the state to hardware.
     */
    void apply(LedStripState state);
}

