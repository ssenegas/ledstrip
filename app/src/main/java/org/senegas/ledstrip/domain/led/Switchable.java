package org.senegas.ledstrip.domain.led;

public interface Switchable {
    void turnOn();
    void turnOff();
    boolean isOn();
}
