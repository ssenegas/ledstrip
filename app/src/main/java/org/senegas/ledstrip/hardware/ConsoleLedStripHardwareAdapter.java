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
        System.out.println(ansi(state) );
    }

    String ansi(LedStripState state) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < state.getLength(); i++) {
            RgbColor c = state.getColorAt(i);
            sb.append(String.format(
                    "\u001B[48;2;%d;%d;%dm  \u001B[0m",
                    c.getRed(), c.getGreen(), c.getBlue()
            ));
        }

        return sb.toString();
    }

    public int getApplyCount() {
        return applyCount;
    }
}
