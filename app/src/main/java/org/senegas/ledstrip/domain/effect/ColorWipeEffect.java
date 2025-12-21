package org.senegas.ledstrip.domain.effect;

import org.senegas.ledstrip.domain.color.RgbColor;
import org.senegas.ledstrip.domain.led.LedStrip;

/**
 * Color wipe effect that sequentially fills LEDs with a color, then wipes them
 * back to another color.
 *
 * The effect has two phases:
 * 1. Wipe in: LEDs are turned on (color1) one by one
 * 2. Wipe out: LEDs are turned off (color2) one by one
 *
 * Based on the WS2812FX color_wipe implementation.
 */
public final class ColorWipeEffect extends AbstractEffect {

    private final RgbColor color1;
    private final RgbColor color2;
    private final boolean reverse;
    private final boolean reverseWipeOut;
    private final long stepDurationMs;

    private int step = 0;
    private long lastStepTime = 0;
    private int cycleCount = 0;

    /**
     * Creates a color wipe effect.
     * @param color1 The color to wipe in
     * @param color2 The color to wipe out (typically OFF/BLACK)
     * @param stepDurationMs Duration of each step in milliseconds
     */
    public ColorWipeEffect(RgbColor color1, RgbColor color2, long stepDurationMs) {
        this(color1, color2, stepDurationMs, false, false);
    }

    /**
     * Creates a color wipe effect with directional control.
     * @param color1 The color to wipe in
     * @param color2 The color to wipe out
     * @param stepDurationMs Duration of each step in milliseconds
     * @param reverse If true, wipe starts from the end of the strip
     * @param reverseWipeOut If true, wipe out goes in opposite direction of wipe in
     */
    public ColorWipeEffect(RgbColor color1, RgbColor color2, long stepDurationMs,
                           boolean reverse, boolean reverseWipeOut) {
        super("Color Wipe");
        if (color1 == null || color2 == null) {
            throw new IllegalArgumentException("Colors cannot be null");
        }
        if (stepDurationMs <= 0) {
            throw new IllegalArgumentException("Step duration must be positive");
        }

        this.color1 = color1;
        this.color2 = color2;
        this.stepDurationMs = stepDurationMs;
        this.reverse = reverse;
        this.reverseWipeOut = reverseWipeOut;
    }

    @Override
    public boolean apply(LedStrip strip, long timeMillis) {
        int stripLength = strip.getLength();
        if (stripLength == 0) {
            return false;
        }

        if (timeMillis - lastStepTime < stepDurationMs) {
            return false;
        }

        lastStepTime = timeMillis;

        // Determine which phase we're in
        if (step < stripLength) {
            // Phase 1: Wipe in with color1
            applyWipeIn(strip, stripLength);
        } else {
            // Phase 2: Wipe out with color2
            applyWipeOut(strip, stripLength);
        }

        // Advance step
        step++;
        if (step >= stripLength * 2) {
            step = 0;
            cycleCount++;
        }

        return true;
    }

    /**
     * Applies the wipe-in phase (filling with color1).
     */
    private void applyWipeIn(LedStrip strip, int stripLength) {
        int ledOffset = step;
        int position;

        if (reverse) {
            // Wipe from end to start
            position = stripLength - 1 - ledOffset;
        } else {
            // Wipe from start to end
            position = ledOffset;
        }

        strip.setPixel(position, color1);
    }

    /**
     * Applies the wipe-out phase (clearing with color2).
     */
    private void applyWipeOut(LedStrip strip, int stripLength) {
        int ledOffset = step - stripLength;
        int position;

        // Determine direction based on reverse and reverseWipeOut flags
        boolean shouldReverseDirection = (reverse && !reverseWipeOut) ||
                (!reverse && reverseWipeOut);

        if (shouldReverseDirection) {
            // Wipe out from end to start
            position = stripLength - 1 - ledOffset;
        } else {
            // Wipe out from start to end
            position = ledOffset;
        }

        strip.setPixel(position, color2);
    }

    @Override
    public void reset() {
        step = 0;
        lastStepTime = 0;
        cycleCount = 0;
    }

    /**
     * Gets the number of complete cycles (wipe in + wipe out) performed.
     * @return The cycle count
     */
    public int getCycleCount() {
        return cycleCount;
    }

    /**
     * Gets the current step in the effect.
     * @return Current step (0 to 2*stripLength - 1)
     */
    public int getCurrentStep() {
        return step;
    }

    /**
     * Checks if currently in the wipe-in phase.
     * @param stripLength The length of the strip
     * @return True if wiping in, false if wiping out
     */
    public boolean isWipingIn(int stripLength) {
        return step < stripLength;
    }
}
