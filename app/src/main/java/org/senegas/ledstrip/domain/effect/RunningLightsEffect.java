package org.senegas.ledstrip.domain.effect;

import org.senegas.ledstrip.domain.color.RgbColor;
import org.senegas.ledstrip.domain.led.LedStrip;

/**
 * Running lights effect with smooth sine wave transition.
 *
 * Creates a wave pattern that moves along the strip, with LEDs transitioning
 * smoothly between two colors using a sine wave. The effect resembles a
 * flowing liquid or a wave traveling through the strip.
 *
 * Based on the WS2812FX mode_running_lights implementation.
 * ## How It Works:
 *
 * 1. **Sine Wave Calculation:**
 *    - Each LED's position (i) plus phase offset creates a unique point on the sine wave
 *    - `sine8()` converts position to brightness value (0-255)
 *    - Colors are blended based on sine wave value
 *
 * 2. **Wave Size Effect:**
 * ```
 *    waveSize = 1: [▁▃▅▇█▇▅▃▁]        One complete wave
 *    waveSize = 2: [▁▅█▅▁▅█▅▁]        Two complete waves
 *    waveSize = 4: [▁█▁█▁█▁█▁]        Four complete waves
 * ```
 *
 * 3. **Animation:**
 *    - `phaseStep` increments each frame (0-255)
 *    - This shifts the entire wave pattern along the strip
 *    - Creates smooth, continuous motion
 *
 * ## Visual Examples:
 * ```
 * Strip: [0 1 2 3 4 5 6 7 8 9]  (10 LEDs, waveSize=1)
 *
 * Phase 0:   [░░▒▒▓▓██▓▓▒▒]  Wave peak at center
 * Phase 64:  [▓▓██▓▓▒▒░░▒▒]  Wave shifted right
 * Phase 128: [▒▒░░▒▒▓▓██▓▓]  Wave peak moved
 * Phase 192: [██▓▓▒▒░░▒▒▓▓]  Continues cycling...
 *
 * Where: ░ = mostly color2, ▒ = blend, ▓ = more color1, █ = fully color1
 */
public final class RunningLightsEffect extends AbstractEffect {

    private final RgbColor color1;
    private final RgbColor color2;
    private final long stepDurationMs;
    private final boolean reverse;
    private final int waveSize;

    private int phaseStep = 0;
    private long lastStepTime = 0;
    private int cycleCount = 0;

    /**
     * Creates a running lights effect with default wave size.
     * @param color1 First color (wave peak)
     * @param color2 Second color (wave trough)
     * @param stepDurationMs Duration of each animation step in milliseconds
     */
    public RunningLightsEffect(RgbColor color1, RgbColor color2, long stepDurationMs) {
        this(color1, color2, stepDurationMs, false, 1);
    }

    /**
     * Creates a running lights effect with full control.
     * @param color1 First color (wave peak)
     * @param color2 Second color (wave trough)
     * @param stepDurationMs Duration of each animation step in milliseconds
     * @param reverse If true, wave travels in reverse direction
     * @param waveSize Size multiplier for the wave (1, 2, 4, 8...)
     */
    public RunningLightsEffect(RgbColor color1, RgbColor color2, long stepDurationMs,
                               boolean reverse, int waveSize) {
        super("Running Lights");
        if (color1 == null || color2 == null) {
            throw new IllegalArgumentException("Colors cannot be null");
        }
        if (stepDurationMs <= 0) {
            throw new IllegalArgumentException("Step duration must be positive");
        }
        if (waveSize < 1) {
            throw new IllegalArgumentException("Wave size must be at least 1");
        }

        this.color1 = color1;
        this.color2 = color2;
        this.stepDurationMs = stepDurationMs;
        this.reverse = reverse;
        this.waveSize = waveSize;
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

        // Calculate sine increment based on strip length and wave size
        // This determines how many complete sine waves fit on the strip
        int sineIncrement = (256 / stripLength) * waveSize;
        sineIncrement = Math.max(sineIncrement, 1); // Ensure at least 1

        // Apply sine wave pattern across all LEDs
        for (int i = 0; i < stripLength; i++) {
            // Calculate sine wave value for this LED position
            int sineInput = ((i + phaseStep) * sineIncrement) & 0xFF; // Keep in 0-255 range
            int luminance = sine8(sineInput);

            // Blend between two colors based on sine wave value
            RgbColor color = color1.blend(color2, luminance / 255.0);

            // Set pixel (with optional reverse direction)
            int position = reverse ? i : (stripLength - 1 - i);
            strip.setPixel(position, color);
        }

        // Advance phase
        phaseStep = (phaseStep + 1) % 256;
        if (phaseStep == 0) {
            cycleCount++;
        }

        return true;
    }

    /**
     * Fast 8-bit sine approximation.
     * Maps 0-255 input to 0-255 output following a sine wave.
     *
     * This is a performance-optimized sine function that uses a quarter-wave
     * lookup table and symmetry to calculate sine values.
     *
     * Input 0 represents angle 0°, input 255 represents angle ~360°
     * Output 0 represents sine value -1, output 128 represents 0, output 255 represents +1
     *
     * @param x Input value (0-255 represents 0-2π)
     * @return Sine value (0-255, where 128 is the midpoint/zero)
     */
    private int sine8(int x) {
        x = x & 0xFF; // Ensure 0-255 range

        // Quarter wave lookup table (0° to 90°)
        // Values represent sine from 0.0 to 1.0, scaled to 0-127
        final int[] SINE_TABLE = {
                0,   2,   5,   8,  11,  14,  17,  20,  23,  26,  29,  32,  35,  38,  41,  44,
                47,  49,  52,  55,  58,  61,  64,  66,  69,  72,  75,  77,  80,  83,  85,  88,
                91,  93,  96,  98, 101, 103, 106, 108, 111, 113, 115, 118, 120, 122, 125, 127
        };

        // Use symmetry to calculate full sine wave from quarter wave
        if (x < 64) {
            // First quarter (0° to 90°): rising from 0 to peak
            // Map x (0-63) to table index (0-47)
            int index = (x * 48) / 64;
            return 128 + SINE_TABLE[index];
        } else if (x < 128) {
            // Second quarter (90° to 180°): falling from peak to 0
            int index = ((127 - x) * 48) / 64;
            return 128 + SINE_TABLE[index];
        } else if (x < 192) {
            // Third quarter (180° to 270°): falling from 0 to trough
            int index = ((x - 128) * 48) / 64;
            return 128 - SINE_TABLE[index];
        } else {
            // Fourth quarter (270° to 360°): rising from trough to 0
            int index = ((255 - x) * 48) / 64;
            return 128 - SINE_TABLE[index];
        }
    }

    @Override
    public void reset() {
        phaseStep = 0;
        lastStepTime = 0;
        cycleCount = 0;
    }

    /**
     * Gets the number of complete wave cycles.
     * @return The cycle count
     */
    public int getCycleCount() {
        return cycleCount;
    }

    /**
     * Gets the current phase step (0-255).
     * @return Current phase
     */
    public int getCurrentPhase() {
        return phaseStep;
    }

    /**
     * Gets the wave size multiplier.
     * @return Wave size
     */
    public int getWaveSize() {
        return waveSize;
    }
}
