import javax.swing.*;
import java.awt.*;

public class Przycisk extends JButton {
    private static final int SREDNICA_PREFEROWANA = 20;
    private final Color hoverBackgroundColor = Color.LIGHT_GRAY;
    private final Color pressedBackgroundColor = Color.GRAY;
    private final Color borderColor = Color.DARK_GRAY;

    public Przycisk(String text) {
        super(text);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
        setPreferredSize(new Dimension(SREDNICA_PREFEROWANA, SREDNICA_PREFEROWANA));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int actualDiameter = Math.min(getWidth(), getHeight());
        int x = (getWidth() - actualDiameter) / 2;
        int y = (getHeight() - actualDiameter) / 2;

        if (getModel().isArmed()) {
            g2.setColor(pressedBackgroundColor);
        } else if (getModel().isRollover()) {
            g2.setColor(hoverBackgroundColor);
        } else {
            g2.setColor(getBackground());
        }
        g2.fillOval(x, y, actualDiameter, actualDiameter);
        g2.setColor(borderColor);
        g2.drawOval(x, y, actualDiameter - 1, actualDiameter - 1);
        String currentText = getText();
        if (currentText != null && !currentText.isEmpty()) {
            g2.setColor(getForeground());
            FontMetrics metrics = g2.getFontMetrics(getFont());
            int stringX = x + (actualDiameter - metrics.stringWidth(currentText)) / 2;
            int stringY = y + ((actualDiameter - metrics.getHeight()) / 2) + metrics.getAscent();
            g2.drawString(currentText, stringX, stringY);
        }
        g2.dispose();
    }
}