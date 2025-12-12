package org.senegas.ledstrip.domain.led;

public interface Led extends Switchable, Colorable {
    int getIndex();
    default void toggle() {
        if (isOn()) {
            turnOff();
        } else {
            turnOn();
        }
    }
}
