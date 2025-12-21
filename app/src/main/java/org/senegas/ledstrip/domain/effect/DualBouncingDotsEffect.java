package org.senegas.ledstrip.domain.effect;

import org.senegas.ledstrip.domain.color.RgbColor;
import org.senegas.ledstrip.domain.led.LedStrip;

public final class DualBouncingDotsEffect extends AbstractEffect {

    private static final long STEP_DURATION_MS = 100;

    private int leftPos;
    private int rightPos;
    private int direction;
    private long lastStepTime;

    public DualBouncingDotsEffect() {
        super("Dual Bouncing Dots");
        reset();
    }

    @Override
    public boolean apply(LedStrip strip, long timeMillis) {
        int size = strip.getLength();
        if (size == 0) {
            return false;
        }

        if (timeMillis - lastStepTime < STEP_DURATION_MS) {
            return false;
        }

        if (rightPos == Integer.MAX_VALUE) {
            rightPos = strip.getLength() - 1;
        }

        lastStepTime = timeMillis;

        strip.clear();
        strip.setPixel(leftPos, RgbColor.GREEN);
        strip.setPixel(rightPos, RgbColor.GREEN);

        leftPos += direction;
        rightPos -= direction;

        boolean crossedOrMet = leftPos >= rightPos;
        boolean atEdges = leftPos <= 0 || rightPos >= size - 1;

        if (crossedOrMet || atEdges) {
            direction = -direction;
        }

        return true;
    }

    @Override
    public void reset() {
        leftPos = 0;
        rightPos = Integer.MAX_VALUE; // will be corrected on first apply
        direction = 1;
        lastStepTime = 0;
    }
}
