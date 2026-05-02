
package vistas;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Panel de Perfil reutilizable para los tres roles.
 * Diseño visual conservado íntegro del original.
 * Adaptación: acepta datos reales del usuario autenticado en el constructor.
 */
public class PanelPerfil extends JPanel {

    // Constructor adaptado — recibe los datos reales del usuario
    public PanelPerfil(String rol, String nombre, String apellido,
                       String tipoDoc, String numDoc, String correo, String estado) {
        setBackground(new Color(242, 242, 242));
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(30, 40, 30, 40));

        // ── Título ──
        JLabel lblTitulo = new JLabel("Perfil");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 26));
        add(lblTitulo, BorderLayout.NORTH);

        // ── Tarjeta blanca ──
        JPanel card = createRoundedPanel(20, Color.WHITE);
        card.setLayout(new GridBagLayout());
        card.setBorder(new EmptyBorder(30, 35, 30, 35));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 30, 20);

        // Fila 1: Nombre | Apellido | Tipo de documento
        gbc.gridy = 0;
        gbc.gridx = 0;
        card.add(createInfoBlock("Nombre", nombre), gbc);
        gbc.gridx = 1;
        card.add(createInfoBlock("Apellido", apellido), gbc);
        gbc.gridx = 2;
        gbc.insets = new Insets(0, 0, 30, 0);
        card.add(createInfoBlock("Tipo de documento", tipoDoc), gbc);

        // Fila 2: Número de documento | Correo institucional | Estado
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, 0, 20);
        card.add(createInfoBlock("Número de documento", numDoc), gbc);
        gbc.gridx = 1;
        card.add(createInfoBlock("Correo institucional", correo), gbc);
        gbc.gridx = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        card.add(createInfoBlock("Estado", estado), gbc);

        // Etiqueta del rol en parte inferior
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(20, 0, 0, 0);
        JLabel lblRol = new JLabel("Rol: " + rol);
        lblRol.setFont(new Font("SansSerif", Font.ITALIC, 12));
        lblRol.setForeground(new Color(120, 120, 120));
        card.add(lblRol, gbc);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(20, 0, 0, 0));
        wrapper.add(card, BorderLayout.NORTH);
        add(wrapper, BorderLayout.CENTER);
    }

    // Constructor de compatibilidad con la vista original (solo rol)
    public PanelPerfil(String rol) {
        this(rol, "—", "—", "—", "—", "—", "—");
    }

    private JPanel createInfoBlock(String titulo, String valor) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        JLabel t = new JLabel(titulo);
        t.setFont(new Font("SansSerif", Font.BOLD, 14));
        JLabel v = new JLabel(valor != null && !valor.isEmpty() ? valor : "—");
        v.setFont(new Font("SansSerif", Font.PLAIN, 14));
        v.setBorder(new EmptyBorder(5, 0, 0, 0));
        p.add(t);
        p.add(v);
        return p;
    }

    private JPanel createRoundedPanel(int r, Color bg) {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), r, r));
                g2.dispose();
            }
        };
    }
}
