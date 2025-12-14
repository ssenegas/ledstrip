package org.senegas.ledstrip.hardware;

import org.senegas.ledstrip.domain.color.RgbColor;
import org.senegas.ledstrip.domain.led.LedStripState;

public class ConsoleLedStripHardwareAdapter implements LedStripHardwareAdapter {
    private int applyCount = 0;

    @Override
    public void apply(LedStripState state) {
        applyCount++;
        System.out.println("=== Hardware Update #" + applyCount + " ===");
        System.out.println("LED Strip Length: " + state.getLength());

        for (int i = 0; i < state.getLength(); i++) {
            RgbColor color = state.getColorAt(i);
            String bar = createColorBar(color);
            System.out.println(String.format("LED[%2d]: %s %s", i, bar, color));
        }
        System.out.println();
    }

    private String createColorBar(RgbColor color) {
        // Create ASCII bar representation of brightness
        int brightness = Math.max(color.getRed(), Math.max(color.getGreen(), color.getBlue()));
        int barLength = brightness / 25; // 0-10 characters
        return "█".repeat(barLength) + "░".repeat(10 - barLength);
    }

    public int getApplyCount() {
        return applyCount;
    }
}
