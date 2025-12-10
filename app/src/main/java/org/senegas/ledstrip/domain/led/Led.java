package org.senegas.ledstrip.domain.led;

public interface Led extends Switchable, Colorable {
    int index();
    default void toggle() {
        if (isOn()) {
            turnOff();
        } else {
            turnOn();
        }
    }
}
