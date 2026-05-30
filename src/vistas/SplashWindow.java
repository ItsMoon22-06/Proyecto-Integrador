
package vistas;

import controlador.LoginControlador;

import javax.swing.*;
import java.awt.*;

/**
 * Splash Screen — diseño visual conservado íntegro del original.
 * Adaptación: al cerrar instancia LoginWindow y le inyecta el LoginControlador.
 */
public class SplashWindow extends JWindow {

    public SplashWindow() {
        setSize(800, 500);
        setLocationRelativeTo(null);

        JPanel splashPanel = new JPanel(new GridBagLayout());
        splashPanel.setBackground(new Color(242, 242, 242));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;

        splashPanel.add(UIUtils.crearLogo(110, 55), gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(10, 0, 0, 0);
        JLabel title = new JLabel("SPP");
        title.setFont(new Font("SansSerif", Font.BOLD, 60));
        title.setForeground(new Color(30, 30, 30));
        splashPanel.add(title, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(5, 0, 0, 0);
        JLabel subtitle = new JLabel("Software de Prácticas Pedagógicas");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(new Color(100, 100, 100));
        splashPanel.add(subtitle, gbc);

        add(splashPanel);

        // Cierra el splash y abre el Login con su controlador inyectado
        Timer timer = new Timer(3000, e -> {
            dispose();
            LoginWindow login = new LoginWindow();
            new LoginControlador(login); // inyecta el controlador
            login.setVisible(true);
        });
        timer.setRepeats(false);
        timer.start();
    }
}
