
package vistas;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class PanelHome extends JPanel {

    private final String ICON_PRACTICA = "\uD83D\uDCD6";

    public PanelHome() {
        setLayout(new GridBagLayout());
        setOpaque(false);

        JPanel centralLogoContainer = new JPanel();
        centralLogoContainer.setLayout(new BoxLayout(centralLogoContainer, BoxLayout.Y_AXIS));
        centralLogoContainer.setOpaque(false);

        // Círculo perfecto con degradado para el logo central
        JPanel logoCirclePanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(80, 80, 80), 0, getHeight(), new Color(30, 30, 30));
                g2.setPaint(gp);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        logoCirclePanel.setOpaque(false);
        logoCirclePanel.setPreferredSize(new Dimension(130, 130));
        logoCirclePanel.setMaximumSize(new Dimension(130, 130));
        logoCirclePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel largeLogoLabel = new JLabel(ICON_PRACTICA);
        largeLogoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 65));
        largeLogoLabel.setForeground(Color.WHITE);
        logoCirclePanel.add(largeLogoLabel);

        JLabel largeSigepLabel = new JLabel("SIGEP");
        largeSigepLabel.setFont(new Font("SansSerif", Font.BOLD, 65));
        largeSigepLabel.setForeground(new Color(50, 50, 50));
        largeSigepLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        largeSigepLabel.setBorder(new EmptyBorder(10, 0, 0, 0));

        centralLogoContainer.add(logoCirclePanel);
        centralLogoContainer.add(largeSigepLabel);

        add(centralLogoContainer);
    }
}