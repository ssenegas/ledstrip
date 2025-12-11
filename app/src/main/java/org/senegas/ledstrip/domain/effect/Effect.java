package org.senegas.ledstrip.domain.effect;

import org.senegas.ledstrip.domain.led.LedStrip;

public interface Effect {
    /**
     * Apply the effect to the logical strip. Implementations should not push to hardware;
     * return true if they modified state (caller can then call adapter).
     *
     * @param strip the logical strip
     * @param timestampMillis monotonic time in ms to allow time-based effects
     * @return true if state changed
     */
    boolean apply(LedStrip strip, long timestampMillis);
}
