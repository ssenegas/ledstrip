package org.senegas.ledstrip.domain.effect;

import org.senegas.ledstrip.domain.color.RgbColor;

import java.util.List;

public class DefaultEffectRegistry  implements EffectRegistry {

    private final List<AbstractEffect> effects;

    public DefaultEffectRegistry() {
        effects = List.of(
                new MovingDotEffect(RgbColor.BLUE, 2000),
                new PingPongDotEffect(),
                new DualBouncingDotsEffect(),
                new RainbowEffect(),
                new BreathEffect(RgbColor.BLUE),
                new ColorWipeEffect(RgbColor.RED, RgbColor.OFF, 30),
                new RunningLightsEffect(RgbColor.RED, RgbColor.BLUE, 30)
        );
    }

    @Override
    public List<AbstractEffect> availableEffects() {
        return effects;
    }
}
