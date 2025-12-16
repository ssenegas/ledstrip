package org.senegas.ledstrip.view;

import org.senegas.ledstrip.domain.color.RgbColor;
import org.senegas.ledstrip.domain.led.LedStripController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class LedStripControlPanel extends JPanel {
    private final LedStripController controller;

    public LedStripControlPanel(LedStripController controller) {
        if (controller == null) {
            throw new IllegalArgumentException("Controller cannot be null");
        }

        this.controller = controller;

        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
    }

    private void initComponents() {
        add(createButton("All On", e -> controller.turnOnAll()));
        add(createButton("All Off", e -> controller.turnOffAll()));

        add(createSeparator());

        add(createColorButton("Red", RgbColor.RED));
        add(createColorButton("Green", RgbColor.GREEN));
        add(createColorButton("Blue", RgbColor.BLUE));
    }

    private JButton createButton(String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.addActionListener(listener);
        return button;
    }

    private JButton createColorButton(String text, RgbColor color) {
        JButton button = new JButton(text);

        // Set button color to match LED color
        java.awt.Color awtColor = new java.awt.Color(
                color.getRed(), color.getGreen(), color.getBlue()
        );
        button.setBackground(awtColor);

        // Choose contrasting text color
        double luminance = (0.299 * color.getRed() +
                0.587 * color.getGreen() +
                0.114 * color.getBlue()) / 255;
        button.setForeground(luminance > 0.5 ? java.awt.Color.BLACK : java.awt.Color.WHITE);
        button.setOpaque(true);

        button.addActionListener(e -> controller.fill(color));
        return button;
    }

    private Component createSeparator() {
        JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
        separator.setPreferredSize(new Dimension(2, 30));
        return separator;
    }
}
