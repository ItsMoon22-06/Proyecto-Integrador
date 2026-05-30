package vistas;

import controlador.DirectorControlador;
import modelado.Tipopractica;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;


/**
 * Panel "Gestión Tipo de Práctica" del Portal Director.
 * Permite: Registrar/Modificar Tipo Práctica | Ver lista de tipos.
 * Diseño consistente con PanelGestionarUsuarios.
 */
public class PanelGestionTipoPractica extends JPanel {

    private static final Color BG = new Color(242, 242, 242);
    private static final Color DARK = new Color(45, 45, 45);

    private final DirectorControlador ctrl;
    private final PortalDirector portal;

    // Tabs
    private JButton btnRegistrar;
    private JButton btnListar;

    // Cards
    private CardLayout contentCard;
    private JPanel contentPanel;

    // Panel Registrar
    private JTextField txtNombre;
    private JComboBox<String> comboNumSemestres;
    private JTextField txtHorasRequeridas;

    private JButton btnReg;
    private JButton btnCancelar;
    private boolean modoEdicion = false;
    private String idEdicion = "";

    private JPanel listaPanel;
    private JLabel lblConteo;
    private JScrollPane scrollListar;

    public PanelGestionTipoPractica(DirectorControlador ctrl, PortalDirector portal) {
        this.ctrl = ctrl;
        this.portal = portal;
        setBackground(BG);
        setLayout(new BorderLayout());
        construirUI();
    }

    private void construirUI() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(BG);
        outer.setBorder(new EmptyBorder(25, 40, 30, 40));

        // ── Tabs ──
        JPanel tabs = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        tabs.setBackground(BG);

        btnRegistrar = createTabButton("Registrar Tipo Práctica", true);
        btnListar = createTabButton("Listar Tipos", false);

        btnRegistrar.addActionListener(e -> {
            seleccionarTab(true);
            contentCard.show(contentPanel, "Registrar");
        });
        btnListar.addActionListener(e -> {
            seleccionarTab(false);
            contentCard.show(contentPanel, "Listar");
            cargarLista();
        });

        tabs.add(btnRegistrar);
        tabs.add(btnListar);

        // ── Contenido ──
        contentCard = new CardLayout();
        contentPanel = new JPanel(contentCard);
        contentPanel.setBackground(BG);
        contentPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        contentPanel.add(crearPanelRegistrar(), "Registrar");
        contentPanel.add(crearPanelListar(), "Listar");

        outer.add(tabs, BorderLayout.NORTH);
        outer.add(contentPanel, BorderLayout.CENTER);
        add(outer, BorderLayout.CENTER);

        contentCard.show(contentPanel, "Registrar");
    }

    // ════════════════════════════════════════════════════════════════════
    // PANEL REGISTRAR
    // ════════════════════════════════════════════════════════════════════

    private JPanel crearPanelRegistrar() {
        JPanel wrap = new JPanel(new GridBagLayout());
        wrap.setBackground(BG);

        JPanel card = createRoundedPanel(20, Color.WHITE);
        card.setLayout(new GridBagLayout());
        card.setBorder(new EmptyBorder(25, 30, 25, 30));
        card.setPreferredSize(new Dimension(680, 380));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.weightx = 1.0;

        // Campos
        txtNombre = roundField();
        txtHorasRequeridas = roundField();
        ((AbstractDocument) txtHorasRequeridas.getDocument()).setDocumentFilter(new NumericFilter());

        // ComboBox de semestres (1 a 8)
        String[] semestres = new String[9];
        semestres[0] = "— Seleccione semestre —";
        for (int i = 1; i <= 8; i++) {
            semestres[i] = String.valueOf(i);
        }
        comboNumSemestres = roundCombo(semestres);

        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(30, 8, 6, 8); // push down Nombre
        card.add(labeledField("Nombre de la práctica", txtNombre), gbc);
        gbc.gridwidth = 1;

        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.insets = new Insets(6, 8, 6, 8); // reset insets
        card.add(labeledField("Núm. Semestres", comboNumSemestres), gbc);
        gbc.gridx = 1;
        card.add(labeledField("Horas Requeridas", txtHorasRequeridas), gbc);

        // Botones
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);

        btnCancelar = createRedButton("Cancelar");
        btnCancelar.addActionListener(e -> {
            limpiarFormulario();
            if (modoEdicion) {
                modoEdicion = false;
                idEdicion = "";
                btnReg.setText("Registrar tipo");
                seleccionarTab(false);
                contentCard.show(contentPanel, "Listar");
                cargarLista();
            }
        });

        btnReg = createDarkButton("Registrar tipo");
        btnReg.addActionListener(e -> registrar());

        btnPanel.add(btnCancelar);
        btnPanel.add(btnReg);

        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(25, 8, 6, 8); // push down buttons
        card.add(btnPanel, gbc);

        wrap.add(card);
        return wrap;
    }

    private void registrar() {
        String nombre = txtNombre.getText().trim();
        String semSeleccion = (String) comboNumSemestres.getSelectedItem();
        String horasStr = txtHorasRequeridas.getText().trim();

        boolean faltaNombre = nombre.isEmpty();
        boolean faltaSemestre = semSeleccion == null || semSeleccion.startsWith("—");
        boolean faltaHoras = horasStr.isEmpty();

        List<String> errores = new java.util.ArrayList<>();

        if (faltaNombre && faltaSemestre && faltaHoras) {
            errores.add("• Deben llenarse todos los campos.");
        } else {
            if (faltaNombre) {
                errores.add("• Ingrese el nombre de la práctica.");
            }
            if (faltaSemestre) {
                errores.add("• Seleccione un número de semestre.");
            }
            if (faltaHoras) {
                errores.add("• Ingrese las horas requeridas.");
            }
        }

        if (!errores.isEmpty()) {
            StringBuilder msj = new StringBuilder("Por favor, corrija los siguientes errores:\n\n");
            for (String err : errores) {
                msj.append(err).append("\n");
            }
            JOptionPane.showMessageDialog(this, msj.toString(), "Errores de Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int semestres = Integer.parseInt(semSeleccion);
            int horas = Integer.parseInt(horasStr);

            boolean ok;
            if (modoEdicion) {
                ok = ctrl.actualizarTipoPractica(idEdicion, nombre, semestres, horas);
                if (ok) {
                    modoEdicion = false;
                    idEdicion = "";
                    btnReg.setText("Registrar tipo");
                    limpiarFormulario();
                    portal.actualizarCombosCrearPractica();
                    seleccionarTab(false);
                    contentCard.show(contentPanel, "Listar");
                    cargarLista();
                }
            } else {
                String nuevoId = "PRAC-" + Long.toString(System.currentTimeMillis(), 36).toUpperCase();
                ok = ctrl.registrarTipoPractica(nuevoId, nombre, semestres, horas);
                if (ok) {
                    limpiarFormulario();
                    portal.actualizarCombosCrearPractica();
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Semestres y horas deben ser numéricos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarFormulario() {
        txtNombre.setText("");
        comboNumSemestres.setSelectedIndex(0);
        txtHorasRequeridas.setText("");
    }

    // ════════════════════════════════════════════════════════════════════
    // PANEL LISTAR
    // ════════════════════════════════════════════════════════════════════

    private JPanel crearPanelListar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG);

        // Barra superior con conteo
        JPanel topBar = new JPanel(new BorderLayout(10, 0));
        topBar.setBackground(BG);
        topBar.setBorder(new EmptyBorder(0, 0, 15, 0));

        lblConteo = new JLabel("Nº Tipos: 0");
        lblConteo.setFont(new Font("SansSerif", Font.BOLD, 14));

        JPanel rightWrapper = new JPanel(new GridBagLayout());
        rightWrapper.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.SOUTH;
        rightWrapper.add(lblConteo, gbc);

        topBar.add(rightWrapper, BorderLayout.EAST);

        // Lista de tipos
        listaPanel = new JPanel();
        listaPanel.setLayout(new BoxLayout(listaPanel, BoxLayout.Y_AXIS));
        listaPanel.setBackground(BG);

        scrollListar = new JScrollPane(listaPanel);
        scrollListar.setBorder(null);
        scrollListar.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollListar.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(topBar, BorderLayout.NORTH);
        panel.add(scrollListar, BorderLayout.CENTER);

        // Cargar datos al inicio
        SwingUtilities.invokeLater(this::cargarLista);

        return panel;
    }

    public void cargarLista() {
        listaPanel.removeAll();
        List<Tipopractica> lista = ctrl.listarTiposPractica();
        int conteo = lista.size();

        if (lista.isEmpty()) {
            JLabel placeholder = new JLabel("No hay tipos de práctica registrados.");
            placeholder.setFont(new Font("SansSerif", Font.ITALIC, 14));
            placeholder.setForeground(Color.GRAY);
            placeholder.setBorder(new EmptyBorder(10, 0, 0, 0));
            listaPanel.add(placeholder);
        } else {
            for (Tipopractica t : lista) {
                crearFilaTipo(t);
            }
        }

        lblConteo.setText("Nº Tipos: " + conteo);
        listaPanel.add(Box.createVerticalGlue());
        listaPanel.revalidate();
        listaPanel.repaint();
    }

    private void crearFilaTipo(Tipopractica t) {
        JPanel card = createRoundedPanel(15, Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(12, 20, 12, 20));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Info
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);

        JLabel lblTipo = new JLabel("TIPO DE PRÁCTICA");
        lblTipo.setFont(new Font("SansSerif", Font.BOLD, 10));
        lblTipo.setForeground(Color.GRAY);

        JLabel lblNombre = new JLabel(t.getNombre());
        lblNombre.setFont(new Font("SansSerif", Font.BOLD, 14));

        JLabel lblDetalle = new JLabel("ID: " + t.getIdTipopractica() + "  |  Semestre: " + t.getNumSemestre() + "  |  Horas: " + t.getHorasRequeridas());
        lblDetalle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblDetalle.setForeground(Color.GRAY);

        info.add(lblTipo);
        info.add(lblNombre);
        info.add(lblDetalle);

        // Botón de 3 puntos
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        acciones.setOpaque(false);

        boolean enUsoActivo = ctrl.tipoPracticaEnUsoActivo(t.getIdTipopractica());
        boolean enUsoCualquier = ctrl.tipoPracticaEnUsoCualquierEstado(t.getIdTipopractica());

        JButton btnMenu = createDotsButton();
        btnMenu.addActionListener(e -> {
            JPopupMenu popup = createStyledPopup();

            JMenuItem itemMod = new JMenuItem("Modificar", crearIconoLapiz());
            styleMenuItem(itemMod, new Color(30, 30, 30), false);
            if (enUsoActivo) {
                itemMod.setEnabled(false);
                itemMod.setToolTipText("No se puede modificar (en uso activo)");
            } else {
                itemMod.addActionListener(ev -> abrirModificacion(t));
            }

            JMenuItem itemDel = new JMenuItem("Eliminar", crearIconoBasura());
            styleMenuItem(itemDel, new Color(200, 30, 30), true);
            if (enUsoCualquier) {
                itemDel.setEnabled(false);
                itemDel.setToolTipText("No se puede eliminar (ya ha sido usado)");
            } else {
                itemDel.addActionListener(ev -> eliminarTipo(t.getIdTipopractica()));
            }

            popup.add(itemMod);
            popup.addSeparator();
            popup.add(itemDel);
            popup.show(btnMenu, btnMenu.getWidth() - popup.getPreferredSize().width, btnMenu.getHeight());
        });

        acciones.add(btnMenu);

        card.add(info, BorderLayout.CENTER);
        card.add(acciones, BorderLayout.EAST);

        listaPanel.add(card);
        listaPanel.add(Box.createRigidArea(new Dimension(0, 8)));
    }

    private void abrirModificacion(Tipopractica t) {
        txtNombre.setText(t.getNombre());

        // Seleccionar el semestre en el combo
        String sem = String.valueOf(t.getNumSemestre());
        for (int i = 0; i < comboNumSemestres.getItemCount(); i++) {
            if (sem.equals(comboNumSemestres.getItemAt(i))) {
                comboNumSemestres.setSelectedIndex(i);
                break;
            }
        }

        txtHorasRequeridas.setText(String.valueOf(t.getHorasRequeridas()));

        modoEdicion = true;
        idEdicion = t.getIdTipopractica();
        btnReg.setText("Guardar cambios");

        seleccionarTab(true);
        contentCard.show(contentPanel, "Registrar");
    }

    private void eliminarTipo(String id) {
        if (ctrl.eliminarTipoPractica(id, this)) {
            cargarLista();
            portal.actualizarCombosCrearPractica();
        }
    }

    // ── Helpers de tabs ───────────────────────────────────────────────────────

    private void seleccionarTab(boolean registrar) {
        btnRegistrar.setBackground(registrar ? DARK : new Color(210, 210, 210));
        btnRegistrar.setForeground(registrar ? Color.WHITE : Color.DARK_GRAY);
        btnListar.setBackground(!registrar ? DARK : new Color(210, 210, 210));
        btnListar.setForeground(!registrar ? Color.WHITE : Color.DARK_GRAY);
    }

    // ── Helpers de UI ─────────────────────────────────────────────────────────

    private JButton createTabButton(String text, boolean active) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.PLAIN, 13));
        b.setBackground(active ? DARK : new Color(210, 210, 210));
        b.setForeground(active ? Color.WHITE : Color.DARK_GRAY);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setPreferredSize(new Dimension(190, 36));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JButton createDarkButton(String text) {
        JButton b = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(DARK);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                super.paintComponent(g);
                g2.dispose();
            }
        };
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 13));
        b.setPreferredSize(new Dimension(170, 38));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JButton createRedButton(String text) {
        JButton b = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(180, 40, 40));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                super.paintComponent(g);
                g2.dispose();
            }
        };
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 13));
        b.setPreferredSize(new Dimension(140, 38));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JPanel labeledField(String label, JComponent field) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        field.setAlignmentX(Component.CENTER_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        p.add(lbl);
        p.add(Box.createRigidArea(new Dimension(0, 3)));
        p.add(field);
        return p;
    }

    private JTextField roundField() {
        JTextField f = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(235, 235, 235));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                super.paintComponent(g);
                g2.dispose();
            }
        };
        f.setOpaque(false);
        f.setBorder(new EmptyBorder(0, 10, 0, 10));
        f.setPreferredSize(new Dimension(0, 36));
        f.setFont(new Font("SansSerif", Font.PLAIN, 13));
        return f;
    }

    private JComboBox<String> roundCombo(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setFont(new Font("SansSerif", Font.PLAIN, 13));
        cb.setBackground(new Color(235, 235, 235));
        cb.setOpaque(true);
        cb.setPreferredSize(new Dimension(0, 36));
        cb.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        cb.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 210, 210), 1, true),
                BorderFactory.createEmptyBorder(2, 4, 2, 4)));
        return cb;
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

    private JButton createDotsButton() {
        JButton btn = new JButton("\u22EE") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(210, 225, 245));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(new Color(100, 150, 220));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("SansSerif", Font.BOLD, 16));
        btn.setForeground(new Color(50, 90, 180));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(30, 30));
        btn.setOpaque(false);
        return btn;
    }

    private JPopupMenu createStyledPopup() {
        JPopupMenu popup = new JPopupMenu();
        popup.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 150, 220), 1, true),
                BorderFactory.createEmptyBorder(4, 0, 4, 0)));
        popup.setBackground(Color.WHITE);
        return popup;
    }

    private void styleMenuItem(JMenuItem item, Color fg, boolean bold) {
        item.setFont(new Font("SansSerif", bold ? Font.BOLD : Font.PLAIN, 13));
        item.setForeground(fg);
        item.setBackground(Color.WHITE);
        item.setOpaque(true);
    }

    private Icon crearIconoLapiz() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(30, 30, 30));
                g2.drawLine(x + 2, y + 10, x + 10, y + 2);
                g2.drawLine(x + 10, y + 2, x + 12, y + 4);
                g2.drawLine(x + 12, y + 4, x + 4, y + 12);
                g2.drawLine(x + 4, y + 12, x + 2, y + 10);
            }

            @Override
            public int getIconWidth() { return 14; }

            @Override
            public int getIconHeight() { return 14; }
        };
    }

    private Icon crearIconoBasura() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(200, 30, 30));
                g2.drawRect(x + 2, y + 4, 8, 8);
                g2.drawLine(x + 1, y + 4, x + 11, y + 4);
                g2.drawLine(x + 4, y + 2, x + 8, y + 2);
            }

            @Override
            public int getIconWidth() { return 12; }

            @Override
            public int getIconHeight() { return 14; }
        };
    }

    // Filtro para solo números
    private static class NumericFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (string == null || string.isEmpty() || string.matches("\\d+")) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (text == null || text.isEmpty() || text.matches("\\d+")) {
                super.replace(fb, offset, length, text, attrs);
            }
        }
    }
}
