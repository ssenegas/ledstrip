package org.senegas.ledstrip.domain.effect;

import org.senegas.ledstrip.domain.color.RgbColor;
import org.senegas.ledstrip.domain.led.LedStrip;

/**
 * Breathing effect that mimics the standby breathing of well-known i-Devices.
 *
 * The effect gradually fades in and out with variable speed - slower at the
 * extremes (fully off/on) and faster in the middle, creating a natural
 * breathing pattern.
 *
 * Based on the WS2812FX mode_breath implementation.
 */
public final class BreathEffect extends AbstractEffect {

    private static final int LUM_MIN = 15;
    private static final int LUM_MAX = 255;
    private static final int LUM_RANGE = 512;  // Full cycle: 15 -> 255 -> 15

    private final RgbColor baseColor;
    private final RgbColor backgroundColor;

    private int luminanceStep = LUM_MIN;
    private long lastStepTime = 0;

    /**
     * Creates a breath effect with the specified color.
     * @param baseColor The color to breathe (fade in/out)
     */
    public BreathEffect(RgbColor baseColor) {
        this(baseColor, RgbColor.OFF);
    }

    /**
     * Creates a breath effect between two colors.
     * @param backgroundColor The color when fully "exhaled" (dim)
     * @param baseColor The color when fully "inhaled" (bright)
     */
    public BreathEffect(RgbColor backgroundColor, RgbColor baseColor) {
        super("Breath");
        if (baseColor == null) {
            throw new IllegalArgumentException("Base color cannot be null");
        }
        if (backgroundColor == null) {
            throw new IllegalArgumentException("Background color cannot be null");
        }
        this.baseColor = baseColor;
        this.backgroundColor = backgroundColor;
    }

    @Override
    public boolean apply(LedStrip strip, long timeMillis) {
        if (strip.getLength() == 0) {
            return false;
        }

        // Calculate current delay based on luminance position
        long delay = calculateDelay(luminanceStep);

        if (timeMillis - lastStepTime < delay) {
            return false;
        }

        lastStepTime = timeMillis;

        // Calculate current luminance (0-255 scale)
        int currentLum = luminanceStep;
        if (currentLum > LUM_MAX) {
            // Fade out phase: mirror the luminance
            currentLum = LUM_RANGE - 1 - currentLum;
        }

        // Blend between background and base color based on luminance
        RgbColor blendedColor = backgroundColor.blend(baseColor, currentLum / 255.0);

        // Apply color to entire strip
        strip.fill(blendedColor);

        // Advance to next step
        luminanceStep += 2;
        if (luminanceStep >= (LUM_RANGE - LUM_MIN)) {
            luminanceStep = LUM_MIN;
            // Optional: mark cycle complete if needed
        }

        return true;
    }

    /**
     * Calculates delay in milliseconds based on current luminance.
     * Creates the characteristic breathing pattern:
     * - Long pause at minimum brightness (970ms)
     * - Slow at extremes
     * - Fast in the middle
     *
     * @param lum Current luminance step (15-511)
     * @return Delay in milliseconds
     */
    private long calculateDelay(int lum) {
        int effectiveLum = lum;
        if (effectiveLum > LUM_MAX) {
            effectiveLum = LUM_RANGE - 1 - effectiveLum;
        }

        // Variable speed based on position in breath cycle
        if (effectiveLum == LUM_MIN) {
            return 970;  // Long pause at minimum (970ms = ~1 second)
        } else if (effectiveLum <= 25) {
            return 19;
        } else if (effectiveLum <= 50) {
            return 18;
        } else if (effectiveLum <= 75) {
            return 14;
        } else if (effectiveLum <= 100) {
            return 10;
        } else if (effectiveLum <= 125) {
            return 7;
        } else if (effectiveLum <= 150) {
            return 5;
        } else {
            return 4;  // Fastest in the middle
        }
    }

    @Override
    public void reset() {
        luminanceStep = LUM_MIN;
        lastStepTime = 0;
    }

    /**
     * Gets the current luminance value (for debugging/testing).
     * @return Current luminance (0-255)
     */
    public int getCurrentLuminance() {
        int lum = luminanceStep;
        if (lum > LUM_MAX) {
            lum = LUM_RANGE - 1 - lum;
        }
        return lum;
    }
}