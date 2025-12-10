package org.senegas.ledstrip.domain.led;

import org.senegas.ledstrip.domain.color.RgbColor;

public interface Colorable {
    void setColor(RgbColor color);
    RgbColor getColor();
}
