package org.senegas.ledstrip.hardware;

import org.senegas.ledstrip.domain.color.RgbColor;
import org.senegas.ledstrip.domain.led.LedStripState;

import javax.swing.*;
import java.awt.*;

public class SwingLedStripHardwareAdapter extends JPanel implements LedStripHardwareAdapter {

    private static final int LED_RADIUS = 30;
    private static final int LED_SPACING = 5;

    private LedStripState currentState = null;

    public SwingLedStripHardwareAdapter(int ledCount) {
        int totalWidth = ledCount * (LED_RADIUS + LED_SPACING) + LED_SPACING;
        int totalHeight = LED_RADIUS + 2 * LED_SPACING;
        setPreferredSize(new Dimension(totalWidth, totalHeight));
        setBackground(new java.awt.Color(40, 40, 40));
    }

    @Override
    public void apply(LedStripState state) {
        if (state == null) {
            throw new IllegalArgumentException("State cannot be null");
        }

        this.currentState = state;

        // Ensure UI update happens on Event Dispatch Thread
        if (SwingUtilities.isEventDispatchThread()) {
            repaint();
        } else {
            SwingUtilities.invokeLater(this::repaint);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Enable anti-aliasing for smooth rendering
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw each LED from current state
        for (int i = 0; i < currentState.getLength(); i++) {
            drawLed(g2d, i, currentState.getColorAt(i));
        }
    }

    private void drawLed(Graphics2D g2d, int index, RgbColor color) {
        int x = LED_SPACING + index * (LED_RADIUS + LED_SPACING);
        int y = LED_SPACING;

        // Convert RgbColor to AWT Color
        java.awt.Color awtColor = new java.awt.Color(
                color.getRed(),
                color.getGreen(),
                color.getBlue()
        );

        // Determine if LED is "on" (has any color)
        boolean isOn = !color.equals(RgbColor.OFF);

        // Draw LED body (circle)
        g2d.setColor(awtColor);
        g2d.fillOval(x, y, LED_RADIUS, LED_RADIUS);

        // Draw border
        g2d.setColor(isOn ? java.awt.Color.WHITE : java.awt.Color.GRAY);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawOval(x, y, LED_RADIUS, LED_RADIUS);

        // Draw glow effect if LED is on
        if (isOn) {
            drawGlow(g2d, x, y, awtColor);
        }

        // Draw LED index number
        g2d.setColor(getContrastColor(color));
        g2d.setFont(new Font("Arial", Font.BOLD, 10));
        FontMetrics fm = g2d.getFontMetrics();
        String indexStr = String.valueOf(index);
        int textX = x + (LED_RADIUS - fm.stringWidth(indexStr)) / 2;
        int textY = y + ((LED_RADIUS - fm.getHeight()) / 2) + fm.getAscent();
        g2d.drawString(indexStr, textX, textY);
    }

    private void drawGlow(Graphics2D g2d, int x, int y, java.awt.Color color) {
        int glowSize = 10;
        for (int i = 0; i < glowSize; i++) {
            float alpha = 0.05f * (glowSize - i) / glowSize;
            java.awt.Color glowColor = new java.awt.Color(
                    color.getRed() / 255f,
                    color.getGreen() / 255f,
                    color.getBlue() / 255f,
                    alpha
            );
            g2d.setColor(glowColor);
            g2d.fillOval(x - i, y - i,
                    LED_RADIUS + 2 * i, LED_RADIUS + 2 * i);
        }
    }

    private java.awt.Color getContrastColor(RgbColor color) {
        // Calculate relative luminance
        double luminance = (0.299 * color.getRed() +
                0.587 * color.getGreen() +
                0.114 * color.getBlue()) / 255;

        return luminance > 0.5 ? java.awt.Color.BLACK : java.awt.Color.WHITE;
    }

    public LedStripState getCurrentState() {
        return currentState;
    }
}
