
package vistas;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class UIUtils {

    public static JComponent crearLogo(int size, int fontSize) {
        JLabel logoLabel = new JLabel("\uD83D\uDCD6", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(70, 70, 70), 0, getHeight(),
                        new Color(20, 20, 20));
                g2.setPaint(gp);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        logoLabel.setPreferredSize(new Dimension(size, size));
        logoLabel.setMinimumSize(new Dimension(size, size));
        logoLabel.setOpaque(false);
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, fontSize));

        JPanel container = new JPanel(new FlowLayout(FlowLayout.CENTER));
        container.setOpaque(false);
        container.add(logoLabel);
        return container;
    }

    public static JTextField crearCampoTextoRedondeado(String placeholder) {
        JTextField textField = new JTextField(placeholder) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(235, 235, 235), 0, getHeight(),
                        new Color(215, 215, 215));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        textField.setOpaque(false);
        textField.setBorder(new EmptyBorder(0, 15, 0, 15));
        textField.setPreferredSize(new Dimension(300, 40));
        textField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        textField.setForeground(Color.DARK_GRAY);

        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setForeground(Color.DARK_GRAY);
                    textField.setText(placeholder);
                }
            }
        });
        return textField;
    }

    public static JPanel crearComboBoxRedondeado() {
        // Roles actualizados según los mockups
        String[] roles = {
                "Seleccione su rol",
                "Estudiante",
                "Tutor académico",
                "Asesor pedagógico",
                "Director"
        };
        JComboBox<String> roleCombo = new JComboBox<>(roles);
        roleCombo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        roleCombo.setForeground(Color.DARK_GRAY);
        roleCombo.setOpaque(false);
        roleCombo.setBackground(new Color(0, 0, 0, 0));
        roleCombo.setBorder(new EmptyBorder(0, 5, 0, 0));

        roleCombo.setUI(new BasicComboBoxUI() {
            @Override
            public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
            }

            @Override
            protected JButton createArrowButton() {
                JButton button = new JButton("V");
                button.setFont(new Font("SansSerif", Font.PLAIN, 11));
                button.setForeground(Color.DARK_GRAY);
                button.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
                button.setContentAreaFilled(false);
                button.setFocusPainted(false);
                return button;
            }
        });

        roleCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected,
                        cellHasFocus);
                label.setBorder(new EmptyBorder(8, 10, 8, 10));
                if (index == -1) {
                    if (value != null && !value.toString().equals("Seleccione su rol")) {
                        label.setForeground(Color.BLACK);
                    } else {
                        label.setForeground(Color.DARK_GRAY);
                    }
                } else {
                    label.setBackground(isSelected ? new Color(110, 110, 110) : new Color(85, 85, 85));
                    label.setForeground(Color.WHITE);
                }
                return label;
            }
        });

        JPanel wrapper = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(235, 235, 235), 0, getHeight(),
                        new Color(215, 215, 215));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
            }
        };
        wrapper.setOpaque(false);
        wrapper.setPreferredSize(new Dimension(300, 40));
        wrapper.add(roleCombo, BorderLayout.CENTER);
        return wrapper;
    }

    /**
     * Crea un panel con JPasswordField redondeado + botón ojo para mostrar/ocultar
     * contraseña.
     * Recuperar el JPasswordField desde la vista:
     * for (Component c : passWrapper.getComponents()) { if (c instanceof
     * JPasswordField) ... }
     */
    public static JPanel crearCampoPasswordRedondeado() {
        JPasswordField passwordField = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(235, 235, 235), 0, getHeight(),
                        new Color(215, 215, 215));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        passwordField.setOpaque(false);
        passwordField.setBorder(new EmptyBorder(0, 15, 0, 5));
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        passwordField.setForeground(Color.DARK_GRAY);
        passwordField.setEchoChar((char) 0); // mostrar placeholder al inicio

        final String PH_PASS = "Ingresa tu contraseña";

        passwordField.setText(PH_PASS);

        passwordField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                String txt = new String(passwordField.getPassword());
                if (txt.equals(PH_PASS)) {
                    passwordField.setText("");
                    passwordField.setEchoChar('\u25CF');
                    passwordField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (passwordField.getPassword().length == 0) {
                    passwordField.setEchoChar((char) 0);
                    passwordField.setText(PH_PASS);
                    passwordField.setForeground(Color.DARK_GRAY);
                }
            }
        });

        // ── Botón mostrar / ocultar ──────────────────────────────────────────
        final boolean[] visible = { false };
        JButton toggleBtn = new JButton("\uD83D\uDC41"); // 👁
        toggleBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        toggleBtn.setForeground(Color.DARK_GRAY);
        toggleBtn.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 10));
        toggleBtn.setContentAreaFilled(false);
        toggleBtn.setFocusPainted(false);
        toggleBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        toggleBtn.setToolTipText("Mostrar / ocultar contraseña");

        toggleBtn.addActionListener(e -> {
            visible[0] = !visible[0];
            passwordField.setEchoChar(visible[0] ? (char) 0 : '\u25CF');
            toggleBtn.setText(visible[0] ? "\uD83D\uDE48" : "\uD83D\uDC41"); // 🙈 / 👁
        });

        // ── Wrapper redondeado ───────────────────────────────────────────────
        JPanel wrapper = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(235, 235, 235), 0, getHeight(),
                        new Color(215, 215, 215));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
            }
        };
        wrapper.setOpaque(false);
        wrapper.setPreferredSize(new Dimension(300, 40));
        wrapper.add(passwordField, BorderLayout.CENTER);
        wrapper.add(toggleBtn, BorderLayout.EAST);
        return wrapper;
    }

    public static JButton crearBotonLogin() {
        JButton button = new JButton("Iniciar sesión") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(90, 90, 90), 0, getHeight(),
                        new Color(15, 15, 15));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(300, 45));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
}