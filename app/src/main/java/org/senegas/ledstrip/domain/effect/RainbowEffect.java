package org.senegas.ledstrip.domain.effect;

import org.senegas.ledstrip.domain.led.Led;
import org.senegas.ledstrip.domain.led.LedStrip;
import org.senegas.ledstrip.domain.color.RgbColor;
import org.senegas.ledstrip.domain.led.LedStrip;

public final class RainbowEffect extends AbstractEffect {

    private static final double HUE_RANGE = 360.0;
    private static final double HUE_SPEED_DEG_PER_SEC = 90.0;

    public RainbowEffect() {
        super("Rainbow");
    }

    @Override
    public boolean apply(LedStrip strip, long timeMillis) {
        int ledCount = strip.getLength();
        if (ledCount == 0) {
            return false;
        }

        double timeSeconds = timeMillis / 1000.0;
        double baseHue = (timeSeconds * HUE_SPEED_DEG_PER_SEC) % HUE_RANGE;

        for (int i = 0; i < ledCount; i++) {
            double hue = (baseHue + (HUE_RANGE * i / ledCount)) % HUE_RANGE;
            strip.setPixel(i, hsvToRgb(hue, 1.0, 1.0));
        }

        return true;
    }

    private static RgbColor hsvToRgb(double hue, double saturation, double value) {
        double c = value * saturation;
        double hPrime = hue / 60.0;
        double x = c * (1.0 - Math.abs(hPrime % 2 - 1.0));

        double r1, g1, b1;

        if (hPrime < 1) {
            r1 = c; g1 = x; b1 = 0;
        } else if (hPrime < 2) {
            r1 = x; g1 = c; b1 = 0;
        } else if (hPrime < 3) {
            r1 = 0; g1 = c; b1 = x;
        } else if (hPrime < 4) {
            r1 = 0; g1 = x; b1 = c;
        } else if (hPrime < 5) {
            r1 = x; g1 = 0; b1 = c;
        } else {
            r1 = c; g1 = 0; b1 = x;
        }

        double m = value - c;

        return new RgbColor(
                toByte(r1 + m),
                toByte(g1 + m),
                toByte(b1 + m)
        );
    }

    private static int toByte(double v) {
        return (int) Math.round(255 * Math.max(0, Math.min(1, v)));
    }
}
