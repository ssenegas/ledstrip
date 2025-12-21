package org.senegas.ledstrip.domain.effect;

import org.senegas.ledstrip.domain.color.RgbColor;
import org.senegas.ledstrip.domain.led.LedStrip;

public final class PingPongDotEffect extends AbstractEffect {

    private static final long STEP_DURATION_MS = 100;

    private int position = 0;
    private int direction = 1;
    private long lastStepTime = 0;

    public PingPongDotEffect() {
        super("Ping-Pong Dot");
    }

    @Override
    public boolean apply(LedStrip strip, long timeMillis) {
        if (strip.getLength() == 0) {
            return false;
        }

        if (timeMillis - lastStepTime < STEP_DURATION_MS) {
            return false;
        }

        lastStepTime = timeMillis;

        strip.clear();
        strip.setPixel(position, RgbColor.RED);

        position += direction;

        if (position == strip.getLength() - 1) {
            direction = -1;
        } else if (position == 0) {
            direction = 1;
        }

        return true;
    }

    @Override
    public void reset() {
        position = 0;
        direction = 1;
        lastStepTime = 0;
    }
}

