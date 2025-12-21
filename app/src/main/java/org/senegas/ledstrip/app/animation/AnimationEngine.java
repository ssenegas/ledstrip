package org.senegas.ledstrip.app.animation;

import org.senegas.ledstrip.domain.effect.Effect;
import org.senegas.ledstrip.domain.led.LedStrip;
import org.senegas.ledstrip.domain.led.LedStripController;
import org.senegas.ledstrip.hardware.LedStripHardwareAdapter;

import java.util.Objects;
import java.util.concurrent.*;

public final class AnimationEngine implements AutoCloseable {

    private final ScheduledExecutorService scheduler;
    private final LedStripController controller;
    private final Effect effect;
    private final long framePeriodMillis;

    private ScheduledFuture<?> task;

    public AnimationEngine(
            LedStripController controller,
            Effect effect,
            long framePeriodMillis
    ) {
        this.controller = Objects.requireNonNull(controller);
        this.effect = Objects.requireNonNull(effect);
        this.framePeriodMillis = framePeriodMillis;

        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "ledstrip-animation-thread");
            t.setDaemon(true);
            return t;
        });
    }

    public synchronized void start() {
        if (task != null && !task.isCancelled()) {
            return;
        }

        long startTime = System.currentTimeMillis();

        task = scheduler.scheduleAtFixedRate(() -> {
            long now = System.currentTimeMillis() - startTime;
            controller.applyEffect(effect, now);
        }, 0, framePeriodMillis, TimeUnit.MILLISECONDS);
    }

    public synchronized void stop() {
        if (task != null) {
            task.cancel(false);
            task = null;
        }
    }

    @Override
    public void close() {
        stop();
        scheduler.shutdownNow();
    }
}
