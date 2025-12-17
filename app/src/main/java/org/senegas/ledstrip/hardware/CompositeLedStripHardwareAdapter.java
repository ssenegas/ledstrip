package org.senegas.ledstrip.hardware;

import org.senegas.ledstrip.domain.led.LedStripState;

public class CompositeLedStripHardwareAdapter  implements LedStripHardwareAdapter {
    private final java.util.List<LedStripHardwareAdapter> adapters;

    public CompositeLedStripHardwareAdapter(LedStripHardwareAdapter... adapters) {
        this.adapters = new java.util.ArrayList<>();
        for (LedStripHardwareAdapter adapter : adapters) {
            if (adapter != null) {
                this.adapters.add(adapter);
            }
        }
    }

    public void addAdapter(LedStripHardwareAdapter adapter) {
        if (adapter != null && !adapters.contains(adapter)) {
            adapters.add(adapter);
        }
    }

    public void removeAdapter(LedStripHardwareAdapter adapter) {
        adapters.remove(adapter);
    }

    @Override
    public void apply(LedStripState state) {
        for (LedStripHardwareAdapter adapter : adapters) {
            adapter.apply(state);
        }
    }
}
