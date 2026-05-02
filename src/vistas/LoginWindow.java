
package vistas;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Vista de Login — diseño original de SIGEP.zip conservado íntegro.
 * Se agregaron únicamente:
 *   • getCorreo()          → para que LoginControlador lea el campo usuario
 *   • getContrasena()      → para que LoginControlador lea la contraseña
 *   • getRolSeleccionado() → para que LoginControlador sepa el rol
 *   • getBtnIngresar()     → para que LoginControlador adjunte el ActionListener
 */
public class LoginWindow extends JFrame {

    private JTextField    campoUsuario;
    private JPasswordField campoContrasena;
    private JComboBox<?>  comboRol;
    private JButton       btnIngresar;

    private static final String PH_USUARIO = "Ingresa tu usuario";

    public LoginWindow() {
        setTitle("SIGEP - Inicio de Sesión");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);

        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, Color.BLACK, getWidth(), 0, new Color(50, 50, 50));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        backgroundPanel.setLayout(new GridBagLayout());

        JPanel formPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.WHITE);
                g2d.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
            }
        };
        formPanel.setOpaque(false);
        formPanel.setLayout(new GridBagLayout());
        formPanel.setPreferredSize(new Dimension(380, 600));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        gbc.gridy = 0;
        gbc.insets = new Insets(30, 20, 10, 20);
        formPanel.add(UIUtils.crearLogo(75, 38), gbc);

        gbc.gridy++; gbc.insets = new Insets(0, 20, 5, 20);
        JLabel titleLabel = new JLabel("SIGEP", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        formPanel.add(titleLabel, gbc);

        gbc.gridy++; gbc.insets = new Insets(0, 20, 20, 20);
        JLabel subtitleLabel = new JLabel("Software de Gestión de Prácticas Pedagógicas", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        formPanel.add(subtitleLabel, gbc);

        gbc.gridy++; gbc.insets = new Insets(5, 30, 2, 30);
        JLabel userLabel = new JLabel("Usuario");
        userLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        formPanel.add(userLabel, gbc);

        gbc.gridy++; gbc.insets = new Insets(0, 30, 15, 30);
        campoUsuario = UIUtils.crearCampoTextoRedondeado(PH_USUARIO);
        formPanel.add(campoUsuario, gbc);

        gbc.gridy++; gbc.insets = new Insets(0, 30, 2, 30);
        JLabel passLabel = new JLabel("Contraseña");
        passLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        formPanel.add(passLabel, gbc);

        gbc.gridy++; gbc.insets = new Insets(0, 30, 15, 30);
        JPanel passWrapper = UIUtils.crearCampoPasswordRedondeado();
        for (Component c : passWrapper.getComponents()) {
            if (c instanceof JPasswordField) {
                campoContrasena = (JPasswordField) c;
                break;
            }
        }
        formPanel.add(passWrapper, gbc);

        gbc.gridy++; gbc.insets = new Insets(0, 30, 2, 30);
        JLabel roleLabel = new JLabel("Rol");
        roleLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        formPanel.add(roleLabel, gbc);

        gbc.gridy++; gbc.insets = new Insets(0, 30, 25, 30);
        JPanel comboWrapper = UIUtils.crearComboBoxRedondeado();
        for (Component c : comboWrapper.getComponents()) {
            if (c instanceof JComboBox<?>) {
                comboRol = (JComboBox<?>) c;
                break;
            }
        }
        formPanel.add(comboWrapper, gbc);

        // Botón expuesto para que el controlador adjunte el listener
        gbc.gridy++; gbc.insets = new Insets(5, 30, 25, 30);
        btnIngresar = UIUtils.crearBotonLogin();
        formPanel.add(btnIngresar, gbc);

        gbc.gridy++; gbc.insets = new Insets(0, 30, 10, 30);
        JSeparator separator = new JSeparator();
        separator.setForeground(Color.LIGHT_GRAY);
        formPanel.add(separator, gbc);

        gbc.gridy++; gbc.insets = new Insets(0, 30, 30, 30);
        JLabel manualLabel = new JLabel("Manual de Usuario", SwingConstants.CENTER);
        manualLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        manualLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        formPanel.add(manualLabel, gbc);

        backgroundPanel.add(formPanel);
        add(backgroundPanel);
    }

    // ── Getters para el controlador ──────────────────────────────────────────

    public String getCorreo() {
        String txt = campoUsuario.getText().trim();
        return txt.equals(PH_USUARIO) ? "" : txt;
    }

    public String getContrasena() {
        if (campoContrasena == null) return "";
        return new String(campoContrasena.getPassword()).trim();
    }

    public String getRolSeleccionado() {
        if (comboRol == null || comboRol.getSelectedItem() == null) return "";
        return comboRol.getSelectedItem().toString();
    }

    public JButton getBtnIngresar() {
        return btnIngresar;
    }
}