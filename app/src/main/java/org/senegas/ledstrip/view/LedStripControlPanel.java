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

        add(createSeparator());

        add(createLabel("LED Control:"));

        JLabel lblIndex = new JLabel("Index:");
        add(lblIndex);

        int ledCount = controller.getSnapshot().getLength();
        SpinnerNumberModel indexModel = new SpinnerNumberModel(0, 0, ledCount - 1, 1);
        JSpinner spinnerIndex = new JSpinner(indexModel);
        ((JSpinner.DefaultEditor) spinnerIndex.getEditor()).getTextField().setColumns(3);
        add(spinnerIndex);

        add(createButton("Toggle", e -> {
            int index = (Integer) spinnerIndex.getValue();
            controller.togglePixel(index);
        }));

        add(createButton("Set Color", e -> {
            int index = (Integer) spinnerIndex.getValue();
            showColorChooserForLed(index);
        }));
    }

    private void showColorChooserForLed(int index) {
        RgbColor currentColor = controller.getSnapshot().getColorAt(index);

        java.awt.Color initialColor = new java.awt.Color(
                currentColor.getRed(),
                currentColor.getGreen(),
                currentColor.getBlue()
        );

        java.awt.Color awtColor = JColorChooser.showDialog(
                this,
                "Choose Color for LED " + index,
                initialColor
        );

        if (awtColor != null) {
            RgbColor newColor = new RgbColor(
                    awtColor.getRed(),
                    awtColor.getGreen(),
                    awtColor.getBlue()
            );
            controller.setPixel(index, newColor);
        }
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        return label;
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
