package org.senegas.ledstrip.domain.color;

import java.util.Objects;

 public final class RgbColor {
    public static final RgbColor OFF = new RgbColor(0, 0, 0);
    public static final RgbColor WHITE = new RgbColor(255, 255, 255);
     public static final RgbColor GRAY = new RgbColor(128, 128, 128);
    public static final RgbColor RED = new RgbColor(255, 0, 0);
    public static final RgbColor GREEN = new RgbColor(0, 255, 0);
    public static final RgbColor BLUE = new RgbColor(0, 0, 255);
    public static final RgbColor YELLOW = new RgbColor(255, 255, 0);
    public static final RgbColor CYAN = new RgbColor(0, 255, 255);
    public static final RgbColor MAGENTA = new RgbColor(255, 0, 255);

    private final int red;
    private final int green;
    private final int blue;

    public RgbColor(int red, int green, int blue) {
        validateComponent(red);
        validateComponent(green);
        validateComponent(blue);
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    private static void validateComponent(int c) {
        if (c < 0 || c > 255) {
            throw new IllegalArgumentException("Color component must be 0..255");
        }
    }

    public int getRed() {
        return red;
    }

    public int getGreen() {
        return green;
    }

    public int getBlue() {
        return blue;
    }

    public boolean isOff() {
        return red == 0 && green == 0 && blue == 0;
    }

    public RgbColor withBrightness(double factor) {
        if (factor < 0.0 || factor > 1.0) throw new IllegalArgumentException("factor 0..1");
        return new RgbColor(
                (int)Math.round(red * factor),
                (int)Math.round(green * factor),
                (int)Math.round(blue * factor)
        );
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RgbColor)) return false;
        RgbColor other = (RgbColor) o;
        return red == other.red && green == other.green && blue == other.blue;
    }

    @Override
    public int hashCode() {
        return Objects.hash(red, green, blue);
    }

    @Override
    public String toString() {
        return String.format("RgbColor(%d,%d,%d)", red, green, blue);
    }
}
