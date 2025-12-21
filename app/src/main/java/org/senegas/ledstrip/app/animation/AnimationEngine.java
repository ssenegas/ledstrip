package org.senegas.ledstrip.app.animation;

import org.senegas.ledstrip.domain.effect.AbstractEffect;
import org.senegas.ledstrip.domain.effect.Effect;
import org.senegas.ledstrip.domain.led.LedStripController;

import java.util.Objects;
import java.util.concurrent.*;

public final class AnimationEngine implements AutoCloseable {

    private final ScheduledExecutorService scheduler;
    private final LedStripController controller;
    private volatile Effect currentEffect;
    private final long framePeriodMillis;

    private ScheduledFuture<?> task;

    public AnimationEngine(
            LedStripController controller,
            long framePeriodMillis
    ) {
        this.controller = Objects.requireNonNull(controller);
        this.framePeriodMillis = framePeriodMillis;

        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "ledstrip-animation-thread");
            t.setDaemon(true);
            return t;
        });
    }

    public synchronized void start() {
        if (currentEffect == null) {
            throw new IllegalStateException("No effect selected");
        }

        currentEffect.reset();

        long startTime = System.currentTimeMillis();

        task = scheduler.scheduleAtFixedRate(() -> {
            long now = System.currentTimeMillis() - startTime;
            controller.applyEffect(currentEffect, now);
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

    public void setCurrentEffect(AbstractEffect abstractEffect) {
        if (task != null && !task.isCancelled()) {
            throw new IllegalStateException("Cannot change effect while running");
        }

        this.currentEffect = Objects.requireNonNull(abstractEffect);
    }
}
