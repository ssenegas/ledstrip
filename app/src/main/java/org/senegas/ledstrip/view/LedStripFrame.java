package org.senegas.ledstrip.view;

import org.senegas.ledstrip.domain.color.RgbColor;
import org.senegas.ledstrip.domain.led.LedStrip;
import org.senegas.ledstrip.domain.led.LedStripController;
import org.senegas.ledstrip.hardware.CompositeLedStripHardwareAdapter;
import org.senegas.ledstrip.hardware.ConsoleLedStripHardwareAdapter;
import org.senegas.ledstrip.hardware.LedStripHardwareAdapter;
import org.senegas.ledstrip.hardware.SwingLedStripHardwareAdapter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Logger;

public class LedStripFrame extends JFrame {
    private static final Logger LOGGER = Logger.getLogger(LedStripFrame.class.getName());

    public LedStripFrame(LedStrip strip) {
        super("ledstrip");

        setLayout(new BorderLayout());

        SwingLedStripHardwareAdapter visualizer =
                new SwingLedStripHardwareAdapter(strip.getLength());

        LedStripHardwareAdapter console = new ConsoleLedStripHardwareAdapter();

        CompositeLedStripHardwareAdapter composite =
                new CompositeLedStripHardwareAdapter(visualizer, console);

        LedStripController controller = new LedStripController(strip, composite);

        LedStripControlPanel controls = new LedStripControlPanel(controller);

        controller.fill(RgbColor.RED);

        this.add(visualizer, BorderLayout.CENTER);
        this.add(controls, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                controls.shutdown();
            }
        });
    }
}
