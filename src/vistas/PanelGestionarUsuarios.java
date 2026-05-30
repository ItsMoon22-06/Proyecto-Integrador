package vistas;

import controlador.DirectorControlador;
import modelado.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

/**
 * Panel "Gestionar Usuarios" del Portal Director.
 * Permite: Registrar Usuario | Filtrar por Rol.
 * Roles: Estudiante, Tutor académico, Asesor pedagógico.
 */
public class PanelGestionarUsuarios extends JPanel {

    private static final Color BG = new Color(242, 242, 242);
    private static final Color DARK = new Color(45, 45, 45);

    private final DirectorControlador ctrl;
    private final PortalDirector portal;

    // Tabs
    private JButton btnRegistrar;
    private JButton btnFiltrar;

    // Cards
    private CardLayout contentCard;
    private JPanel contentPanel;

    // Panel Registrar
    private JTextField txtNumDoc;
    private JTextField txtNombre;
    private JTextField txtApellido;
    private JTextField txtCorreo;
    private JPasswordField txtContrasena;
    private JComboBox<String> comboTipoDoc;
    private JComboBox<String> comboEstado;
    private JComboBox<String> comboRol;
    private JComboBox<String> comboPrograma; // solo para Estudiante/Tutor

    private JButton btnReg; // botón de registro / guardar
    private JButton btnCancelar; // botón de cancelar
    private boolean modoEdicion = false; // flag para saber si estamos editando
    private String docEdicion = "";

    // Panel Filtrar
    private JComboBox<String> comboFiltroRol;
    private JPanel listaPanel;
    private JLabel lblConteo;

    public PanelGestionarUsuarios(DirectorControlador ctrl, PortalDirector portal) {
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

        btnRegistrar = createTabButton("Registrar Usuario", true);
        btnFiltrar = createTabButton("Filtrar Usuario", false);

        btnRegistrar.addActionListener(e -> {
            actualizarProgramas();
            seleccionarTab(true);
            contentCard.show(contentPanel, "Registrar");
        });
        btnFiltrar.addActionListener(e -> {
            seleccionarTab(false);
            contentCard.show(contentPanel, "Filtrar");
            filtrar();
        });

        tabs.add(btnRegistrar);
        tabs.add(btnFiltrar);

        // ── Contenido ──
        contentCard = new CardLayout();
        contentPanel = new JPanel(contentCard);
        contentPanel.setBackground(BG);
        contentPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        contentPanel.add(crearPanelRegistrar(), "Registrar");
        contentPanel.add(crearPanelFiltrar(), "Filtrar");

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
        card.setPreferredSize(new Dimension(680, 480));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.weightx = 1.0;

        JLabel titulo = new JLabel("SPP", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        card.add(titulo, gbc);
        gbc.gridwidth = 1;

        // Campos
        txtNombre = roundField();
        txtApellido = roundField();
        txtNumDoc = roundField();
        comboTipoDoc = roundCombo(new String[] { "— Seleccione tipo documento —", "Cédula", "Pasaporte", "CE" });
        txtCorreo = roundField();
        comboEstado = roundCombo(new String[] { "— Seleccione estado —", "Activo", "Inactivo" });
        JPanel passWrapper = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(235, 235, 235));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.dispose();
            }
        };
        passWrapper.setOpaque(false);
        passWrapper.setPreferredSize(new Dimension(0, 36));
        passWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        txtContrasena = new JPasswordField();
        txtContrasena.setOpaque(false);
        txtContrasena.setBackground(new Color(0, 0, 0, 0));
        txtContrasena.setBorder(new EmptyBorder(0, 10, 0, 5));
        txtContrasena.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txtContrasena.setEchoChar('\u25CF');

        final boolean[] visiblePass = { false };
        JButton toggleBtn = new JButton("\uD83D\uDC41");
        toggleBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        toggleBtn.setForeground(Color.DARK_GRAY);
        toggleBtn.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 10));
        toggleBtn.setContentAreaFilled(false);
        toggleBtn.setFocusPainted(false);
        toggleBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        toggleBtn.setToolTipText("Mostrar / ocultar contraseña");

        toggleBtn.addActionListener(ev -> {
            visiblePass[0] = !visiblePass[0];
            txtContrasena.setEchoChar(visiblePass[0] ? (char) 0 : '\u25CF');
            toggleBtn.setText(visiblePass[0] ? "\uD83D\uDE48" : "\uD83D\uDC41");
        });

        passWrapper.add(txtContrasena, BorderLayout.CENTER);
        passWrapper.add(toggleBtn, BorderLayout.EAST);

        comboPrograma = roundCombo(cargarProgramas());
        comboPrograma.setEnabled(false);
        comboRol = roundCombo(
                new String[] { "— Seleccione rol —", "Estudiante", "Tutor académico", "Asesor pedagógico" });
        comboRol.addActionListener(e -> {
            String r = (String) comboRol.getSelectedItem();
            if ("Estudiante".equals(r) || "Tutor académico".equals(r)) {
                comboPrograma.setEnabled(true);
            } else {
                comboPrograma.setEnabled(false);
                comboPrograma.setSelectedIndex(0);
            }
        });

        gbc.gridy = 1;
        gbc.gridx = 0;
        card.add(labeledField("Nombre", txtNombre), gbc);
        gbc.gridx = 1;
        card.add(labeledField("Apellido", txtApellido), gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        card.add(labeledField("Número de documento", txtNumDoc), gbc);
        gbc.gridx = 1;
        card.add(labeledField("Tipo de documento", comboTipoDoc), gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        card.add(labeledField("Correo institucional", txtCorreo), gbc);
        gbc.gridx = 1;
        card.add(labeledField("Estado", comboEstado), gbc);

        gbc.gridy = 4;
        gbc.gridx = 0;
        card.add(labeledField("Contraseña", passWrapper), gbc);
        gbc.gridx = 1;
        card.add(labeledField("Seleccionar rol", comboRol), gbc);

        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        card.add(labeledField("Programa Académico", comboPrograma), gbc);
        gbc.gridwidth = 1;

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);

        btnCancelar = createRedButton("Cancelar");
        btnCancelar.addActionListener(e -> {
            limpiarFormulario();
            if (modoEdicion) {
                modoEdicion = false;
                docEdicion = "";
                btnReg.setText("Registrar usuario");
                seleccionarTab(false);
                contentCard.show(contentPanel, "Filtrar");
                filtrar();
            }
        });

        btnReg = createDarkButton("Registrar usuario");
        btnReg.addActionListener(e -> registrar());

        btnPanel.add(btnCancelar);
        btnPanel.add(btnReg);

        gbc.gridy = 6;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.EAST;
        card.add(btnPanel, gbc);

        wrap.add(card);
        return wrap;
    }

    private void registrar() {
        String numDoc = txtNumDoc.getText().trim();
        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String correo = txtCorreo.getText().trim();
        String contrasena = new String(txtContrasena.getPassword()).trim();
        String tipoDoc = (String) comboTipoDoc.getSelectedItem();
        String estado = (String) comboEstado.getSelectedItem();
        String rol = (String) comboRol.getSelectedItem();
        String idPrograma = extraerIdPrograma((String) comboPrograma.getSelectedItem());

        List<String> errores = new java.util.ArrayList<>();

        // Validate text fields
        if (numDoc.isEmpty() || nombre.isEmpty() || apellido.isEmpty() || correo.isEmpty() || contrasena.isEmpty()) {
            errores.add("• Todos los campos obligatorios deben estar completos.");
        }

        // Validate numDoc only numbers
        if (!numDoc.isEmpty() && !numDoc.matches("\\d+")) {
            errores.add("• El número de documento debe contener solo números.");
        }

        // Validate password length
        if (!contrasena.isEmpty() && (contrasena.length() < 5 || contrasena.length() > 8)) {
            errores.add("• La contraseña debe tener entre 5 y 8 caracteres.");
        }

        // Validate combo box selections
        if (tipoDoc == null || tipoDoc.startsWith("—") ||
                estado == null || estado.startsWith("—") ||
                rol == null || rol.startsWith("—")) {
            errores.add("• Faltan opciones por seleccionar en los menús desplegables.");
        }

        // Validate email formatting and program
        if (rol != null && !rol.startsWith("—")) {
            if ("Estudiante".equals(rol) || "Tutor académico".equals(rol)) {
                if (!correo.isEmpty() && !correo.endsWith("@udi.edu.co")) {
                    errores.add("• El correo para estudiantes y tutores debe terminar en @udi.edu.co.");
                }
            } else if ("Asesor pedagógico".equals(rol)) {
                if (!correo.isEmpty() && !correo.contains("@")) {
                    errores.add("• El correo del asesor pedagógico debe ser válido (contener '@').");
                }
            }

            if (("Estudiante".equals(rol) || "Tutor académico".equals(rol)) && idPrograma.isEmpty()) {
                errores.add("• Seleccione un programa académico para el usuario.");
            }
        }

        // Global Uniqueness check for numDoc
        if (!numDoc.isEmpty() && !modoEdicion) {
            boolean docExists = ctrl.buscarEstudiante(numDoc) != null ||
                    ctrl.buscarTutor(numDoc) != null ||
                    ctrl.buscarAsesor(numDoc) != null ||
                    ctrl.buscarDirector(numDoc) != null;
            if (docExists) {
                errores.add("• El número de documento ya se encuentra registrado.");
            }
        }

        // Global Uniqueness check for correo
        if (!correo.isEmpty()) {
            boolean emailExists = false;
            for (Estudiante e : ctrl.listarEstudiantes()) {
                if (e.getCorreoInst().equalsIgnoreCase(correo)
                        && (!modoEdicion || !e.getNumDocumento().equals(docEdicion))) {
                    emailExists = true;
                    break;
                }
            }
            if (!emailExists) {
                for (TutorAcademico t : ctrl.listarTutores()) {
                    if (t.getCorreoInst().equalsIgnoreCase(correo)
                            && (!modoEdicion || !t.getNumDocumento().equals(docEdicion))) {
                        emailExists = true;
                        break;
                    }
                }
            }
            if (!emailExists) {
                for (AsesorPedagogico a : ctrl.listarAsesores()) {
                    if (a.getCorreoInst().equalsIgnoreCase(correo)
                            && (!modoEdicion || !a.getNumDocumento().equals(docEdicion))) {
                        emailExists = true;
                        break;
                    }
                }
            }
            if (!emailExists) {
                for (Director d : ctrl.listarDirectores()) {
                    if (d.getCorreoInst().equalsIgnoreCase(correo)
                            && (!modoEdicion || !d.getNumDocumento().equals(docEdicion))) {
                        emailExists = true;
                        break;
                    }
                }
            }
            if (emailExists) {
                errores.add("• El correo institucional ya se encuentra registrado.");
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

        boolean ok = false;
        if (modoEdicion) {
            switch (rol) {
                case "Estudiante" ->
                    ok = ctrl.actualizarEstudiante(numDoc, tipoDoc, nombre, apellido, correo, contrasena, estado,
                            idPrograma);
                case "Tutor académico" ->
                    ok = ctrl.actualizarTutor(numDoc, tipoDoc, nombre, apellido, correo, contrasena, estado,
                            idPrograma);
                case "Asesor pedagógico" ->
                    ok = ctrl.actualizarAsesor(numDoc, tipoDoc, nombre, apellido, correo, contrasena, estado);
            }
            if (ok) {
                modoEdicion = false;
                docEdicion = "";
                btnReg.setText("Registrar usuario");
                limpiarFormulario();
                seleccionarTab(false);
                contentCard.show(contentPanel, "Filtrar");
                filtrar();
            }
        } else {
            switch (rol) {
                case "Estudiante" ->
                    ok = ctrl.registrarEstudiante(numDoc, tipoDoc, nombre, apellido, correo, contrasena, estado,
                            idPrograma);
                case "Tutor académico" ->
                    ok = ctrl.registrarTutor(numDoc, tipoDoc, nombre, apellido, correo, contrasena, estado, idPrograma);
                case "Asesor pedagógico" ->
                    ok = ctrl.registrarAsesor(numDoc, tipoDoc, nombre, apellido, correo, contrasena, estado);
            }
            if (ok)
                limpiarFormulario();
        }
    }

    private void limpiarFormulario() {
        txtNumDoc.setText("");
        txtNombre.setText("");
        txtApellido.setText("");
        txtCorreo.setText("");
        txtContrasena.setText("");
        comboPrograma.setSelectedIndex(0);
        comboTipoDoc.setSelectedIndex(0);
        comboEstado.setSelectedIndex(0);
        comboRol.setSelectedIndex(0);

        txtNumDoc.setEnabled(true);
        comboTipoDoc.setEnabled(true);
        comboRol.setEnabled(true);
        comboPrograma.setEnabled(false);
        comboEstado.setEnabled(true);
    }

    // ════════════════════════════════════════════════════════════════════
    // PANEL FILTRAR
    // ════════════════════════════════════════════════════════════════════

    private JPanel crearPanelFiltrar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG);

        // Barra de búsqueda
        JPanel searchBar = new JPanel(new BorderLayout(10, 0));
        searchBar.setBackground(BG);
        searchBar.setBorder(new EmptyBorder(0, 0, 15, 0));

        comboFiltroRol = roundCombo(new String[] {
                "— Seleccione rol —", "Estudiante", "Tutor académico", "Asesor pedagógico"
        });
        comboFiltroRol.setPreferredSize(new Dimension(250, 36));
        comboFiltroRol.addActionListener(e -> filtrar());

        lblConteo = new JLabel("Nº Usuarios: 0");
        lblConteo.setFont(new Font("SansSerif", Font.BOLD, 14));

        JPanel filterWrapper = new JPanel(new BorderLayout());
        filterWrapper.setOpaque(false);
        filterWrapper.add(comboFiltroRol, BorderLayout.CENTER);

        searchBar.add(filterWrapper, BorderLayout.WEST);

        // Alinear verticalmente el conteo a la derecha
        JPanel rightWrapper = new JPanel(new GridBagLayout());
        rightWrapper.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.SOUTH;
        rightWrapper.add(lblConteo, gbc);

        searchBar.add(rightWrapper, BorderLayout.EAST);

        // Lista de usuarios
        listaPanel = new JPanel();
        listaPanel.setLayout(new BoxLayout(listaPanel, BoxLayout.Y_AXIS));
        listaPanel.setBackground(BG);

        JScrollPane scroll = new JScrollPane(listaPanel);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(searchBar, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private void filtrar() {
        listaPanel.removeAll();
        String rol = (String) comboFiltroRol.getSelectedItem();
        int conteo = 0;

        if (rol == null || rol.startsWith("—")) {
            JLabel placeholder = new JLabel("Seleccione un rol para ver la lista de usuarios.");
            placeholder.setFont(new Font("SansSerif", Font.ITALIC, 14));
            placeholder.setForeground(Color.GRAY);
            placeholder.setBorder(new EmptyBorder(10, 0, 0, 0));
            listaPanel.add(placeholder);

            lblConteo.setText("Nº Usuarios: 0");
            listaPanel.add(Box.createVerticalGlue());
            listaPanel.revalidate();
            listaPanel.repaint();
            return;
        }

        switch (rol) {
            case "Estudiante" -> {
                List<Estudiante> lista = ctrl.listarEstudiantes();
                conteo = lista.size();
                for (Estudiante u : lista)
                    listaPanel.add(crearFilaUsuario(
                            u.getNumDocumento(), u.getNombre() + " " + u.getApellido(),
                            u.getCorreoInst(), u.getEstado(), "Estudiante", u.getNumDocumento()));
            }
            case "Tutor académico" -> {
                List<TutorAcademico> lista = ctrl.listarTutores();
                conteo = lista.size();
                for (TutorAcademico u : lista)
                    listaPanel.add(crearFilaUsuario(
                            u.getNumDocumento(), u.getNombre() + " " + u.getApellido(),
                            u.getCorreoInst(), u.getEstado(), "Tutor académico", u.getNumDocumento()));
            }
            case "Asesor pedagógico" -> {
                List<AsesorPedagogico> lista = ctrl.listarAsesores();
                conteo = lista.size();
                for (AsesorPedagogico u : lista)
                    listaPanel.add(crearFilaUsuario(
                            u.getNumDocumento(), u.getNombre() + " " + u.getApellido(),
                            u.getCorreoInst(), u.getEstado(), "Asesor pedagógico", u.getNumDocumento()));
            }
        }

        lblConteo.setText("Nº Usuarios: " + conteo);
        listaPanel.add(Box.createVerticalGlue());
        listaPanel.revalidate();
        listaPanel.repaint();
    }

    private JPanel crearFilaUsuario(String numDoc, String nombre, String correo,
            String estado, String rol, String docKey) {
        JPanel card = createRoundedPanel(15, Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(12, 20, 12, 20));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Info
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);
        JLabel lblRol = new JLabel(rol.toUpperCase());
        lblRol.setFont(new Font("SansSerif", Font.BOLD, 10));
        lblRol.setForeground(Color.GRAY);
        JLabel lblNombre = new JLabel(nombre);
        lblNombre.setFont(new Font("SansSerif", Font.BOLD, 14));
        JLabel lblCorreo = new JLabel(correo + "  |  " + estado);
        lblCorreo.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblCorreo.setForeground(Color.GRAY);
        info.add(lblRol);
        info.add(lblNombre);
        info.add(lblCorreo);

        // Botón de 3 puntos
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        acciones.setOpaque(false);

        JButton btnMenu = createDotsButton();
        btnMenu.addActionListener(e -> {
            JPopupMenu popup = createStyledPopup();

            JMenuItem itemPerfil = new JMenuItem("Perfil", crearIconoOjo());
            styleMenuItem(itemPerfil, new Color(30, 30, 30), false);
            itemPerfil.addActionListener(ev -> verPerfil(numDoc, nombre, correo, estado, rol));

            JMenuItem itemMod = new JMenuItem("Modificar", crearIconoLapiz());
            styleMenuItem(itemMod, new Color(30, 30, 30), false);
            itemMod.addActionListener(ev -> abrirModificacion(numDoc, nombre, correo, estado, rol));

            JMenuItem itemDel = new JMenuItem("Eliminar", crearIconoBasura());
            styleMenuItem(itemDel, new Color(200, 30, 30), true);
            itemDel.addActionListener(ev -> eliminarUsuario(docKey, rol));

            popup.add(itemPerfil);
            popup.addSeparator();
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
        return card;
    }

    private void verPerfil(String numDoc, String nombre, String correo, String estado, String rol) {
        // Load details from the database
        String tipoDoc = "—";
        String contrasena = "";
        String programaNom = "";

        if ("Estudiante".equals(rol)) {
            Estudiante e = ctrl.buscarEstudiante(numDoc);
            if (e != null) {
                tipoDoc = e.getTipoDocumento();
                contrasena = e.getContrasena();
                if (e.getIdPrograma() != null && !e.getIdPrograma().isEmpty()) {
                    ProgramaDAO progDAO = new ProgramaDAO();
                    Programa p = progDAO.buscarPorId(e.getIdPrograma());
                    if (p != null) {
                        programaNom = p.getNombre();
                    } else {
                        programaNom = e.getIdPrograma();
                    }
                }
            }
        } else if ("Tutor académico".equals(rol)) {
            TutorAcademico t = ctrl.buscarTutor(numDoc);
            if (t != null) {
                tipoDoc = t.getTipoDocumento();
                contrasena = t.getContrasena();
                if (t.getIdPrograma() != null && !t.getIdPrograma().isEmpty()) {
                    ProgramaDAO progDAO = new ProgramaDAO();
                    Programa p = progDAO.buscarPorId(t.getIdPrograma());
                    if (p != null) {
                        programaNom = p.getNombre();
                    } else {
                        programaNom = t.getIdPrograma();
                    }
                }
            }
        } else if ("Asesor pedagógico".equals(rol)) {
            AsesorPedagogico a = ctrl.buscarAsesor(numDoc);
            if (a != null) {
                tipoDoc = a.getTipoDocumento();
                contrasena = a.getContrasena();
            }
        }

        JDialog dlg = new JDialog(portal, "Perfil — " + nombre, true);
        dlg.setSize(480, 320);
        dlg.setLocationRelativeTo(portal);

        int rows = ("Estudiante".equals(rol) || "Tutor académico".equals(rol)) ? 6 : 5;
        JPanel pPanel = new JPanel(new GridLayout(rows, 2, 10, 10));
        pPanel.setBorder(new EmptyBorder(20, 25, 20, 25));
        pPanel.setBackground(Color.WHITE);

        pPanel.add(new JLabel("Nombre:"));
        pPanel.add(new JLabel(nombre));
        pPanel.add(new JLabel("Correo:"));
        pPanel.add(new JLabel(correo));
        pPanel.add(new JLabel("Estado:"));
        pPanel.add(new JLabel(estado));

        String docText = numDoc
                + (tipoDoc != null && !tipoDoc.isEmpty() && !"—".equals(tipoDoc) ? " (" + tipoDoc + ")" : "");
        pPanel.add(new JLabel("Documento:"));
        pPanel.add(new JLabel(docText));

        if ("Estudiante".equals(rol) || "Tutor académico".equals(rol)) {
            String progText = (programaNom != null && !programaNom.isEmpty()) ? programaNom : "Ninguno";
            pPanel.add(new JLabel("Programa:"));
            pPanel.add(new JLabel(progText));
        }

        JPasswordField txtPwd = new JPasswordField(contrasena);
        txtPwd.setEditable(false);
        txtPwd.setOpaque(false);
        txtPwd.setBackground(new Color(0, 0, 0, 0));
        txtPwd.setBorder(new EmptyBorder(0, 8, 0, 8));
        txtPwd.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txtPwd.setEchoChar('\u25CF');

        final boolean[] visiblePass = { false };
        JButton toggleBtn = new JButton("\uD83D\uDC41");
        toggleBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        toggleBtn.setForeground(Color.DARK_GRAY);
        toggleBtn.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 10));
        toggleBtn.setContentAreaFilled(false);
        toggleBtn.setFocusPainted(false);
        toggleBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        toggleBtn.setToolTipText("Mostrar / ocultar contraseña");

        toggleBtn.addActionListener(ev -> {
            visiblePass[0] = !visiblePass[0];
            txtPwd.setEchoChar(visiblePass[0] ? (char) 0 : '\u25CF');
            toggleBtn.setText(visiblePass[0] ? "\uD83D\uDE48" : "\uD83D\uDC41");
        });

        JPanel passWrapper = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(245, 245, 245));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
            }
        };
        passWrapper.setOpaque(false);
        passWrapper.setPreferredSize(new Dimension(150, 30));
        passWrapper.add(txtPwd, BorderLayout.CENTER);
        passWrapper.add(toggleBtn, BorderLayout.EAST);

        pPanel.add(new JLabel("Contraseña:"));
        pPanel.add(passWrapper);

        JButton cerrar = new JButton("Cerrar");
        cerrar.addActionListener(e -> dlg.dispose());
        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bot.setBackground(Color.WHITE);
        bot.add(cerrar);

        dlg.setLayout(new BorderLayout());
        dlg.add(pPanel, BorderLayout.CENTER);
        dlg.add(bot, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void abrirModificacion(String numDoc, String nombreCompleto, String correo, String estado, String rol) {
        actualizarProgramas();
        String nombreVal = "";
        String apellidoVal = "";
        String tipoDocVal = "";
        String contrasenaVal = "";
        String idProgramaVal = "";

        boolean tienePracticaActiva = false;
        PracticaDAO pDao = new PracticaDAO();

        if ("Estudiante".equals(rol)) {
            Estudiante e = ctrl.buscarEstudiante(numDoc);
            if (e != null) {
                nombreVal = e.getNombre();
                apellidoVal = e.getApellido();
                tipoDocVal = e.getTipoDocumento();
                contrasenaVal = e.getContrasena();
                idProgramaVal = e.getIdPrograma();
            }
            java.util.List<Practica> practicas = pDao.listarPorEstudiante(numDoc);
            for (Practica p : practicas) {
                if ("Activo".equals(p.getEstado())) {
                    tienePracticaActiva = true;
                    break;
                }
            }
        } else if ("Tutor académico".equals(rol)) {
            TutorAcademico t = ctrl.buscarTutor(numDoc);
            if (t != null) {
                nombreVal = t.getNombre();
                apellidoVal = t.getApellido();
                tipoDocVal = t.getTipoDocumento();
                contrasenaVal = t.getContrasena();
                idProgramaVal = t.getIdPrograma();
            }
            java.util.List<Practica> practicas = pDao.listarPorTutor(numDoc);
            for (Practica p : practicas) {
                if ("Activo".equals(p.getEstado())) {
                    tienePracticaActiva = true;
                    break;
                }
            }
        } else if ("Asesor pedagógico".equals(rol)) {
            AsesorPedagogico a = ctrl.buscarAsesor(numDoc);
            if (a != null) {
                nombreVal = a.getNombre();
                apellidoVal = a.getApellido();
                tipoDocVal = a.getTipoDocumento();
                contrasenaVal = a.getContrasena();
            }
            java.util.List<Practica> practicas = pDao.listarPorAsesor(numDoc);
            for (Practica p : practicas) {
                if ("Activo".equals(p.getEstado())) {
                    tienePracticaActiva = true;
                    break;
                }
            }
        }

        txtNombre.setText(nombreVal);
        txtApellido.setText(apellidoVal);
        txtNumDoc.setText(numDoc);
        txtCorreo.setText(correo);
        txtContrasena.setText(contrasenaVal);

        comboTipoDoc.setSelectedItem(tipoDocVal);
        comboEstado.setSelectedItem(estado);
        comboRol.setSelectedItem(rol);

        if ("Estudiante".equals(rol) || "Tutor académico".equals(rol)) {
            comboPrograma.setEnabled(true);
            if (idProgramaVal != null && !idProgramaVal.isEmpty()) {
                for (int i = 0; i < comboPrograma.getItemCount(); i++) {
                    String item = comboPrograma.getItemAt(i);
                    if (extraerIdPrograma(item).equals(idProgramaVal)) {
                        comboPrograma.setSelectedIndex(i);
                        break;
                    }
                }
            } else {
                comboPrograma.setSelectedIndex(0);
            }
        } else {
            comboPrograma.setEnabled(false);
            comboPrograma.setSelectedIndex(0);
        }

        txtNumDoc.setEnabled(false);
        comboTipoDoc.setEnabled(false);
        comboRol.setEnabled(false);

        if (tienePracticaActiva) {
            comboEstado.setEnabled(false);
            comboPrograma.setEnabled(false);
        } else {
            comboEstado.setEnabled(true);
        }

        modoEdicion = true;
        docEdicion = numDoc;
        btnReg.setText("Guardar cambios");

        seleccionarTab(true);
        contentCard.show(contentPanel, "Registrar");
    }

    private void eliminarUsuario(String numDoc, String rol) {
        boolean ok = false;
        switch (rol) {
            case "Estudiante" -> ok = ctrl.eliminarEstudiante(numDoc, this);
            case "Tutor académico" -> ok = ctrl.eliminarTutor(numDoc, this);
            case "Asesor pedagógico" -> ok = ctrl.eliminarAsesor(numDoc, this);
        }
        if (ok)
            filtrar();
    }

    // ── Helpers de tabs ───────────────────────────────────────────────────────

    private void seleccionarTab(boolean registrar) {
        btnRegistrar.setBackground(registrar ? DARK : new Color(210, 210, 210));
        btnRegistrar.setForeground(registrar ? Color.WHITE : Color.DARK_GRAY);
        btnFiltrar.setBackground(!registrar ? DARK : new Color(210, 210, 210));
        btnFiltrar.setForeground(!registrar ? Color.WHITE : Color.DARK_GRAY);
    }

    // ── Helpers de UI ─────────────────────────────────────────────────────────

    private JButton createTabButton(String text, boolean active) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.PLAIN, 13));
        b.setBackground(active ? DARK : new Color(210, 210, 210));
        b.setForeground(active ? Color.WHITE : Color.DARK_GRAY);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setPreferredSize(new Dimension(160, 36));
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
        cb.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                String displayText = value != null ? value.toString() : "";
                // Show only the part after '|' for cleaner display
                if (displayText.contains("|")) {
                    displayText = displayText.substring(displayText.indexOf("|") + 1).trim();
                }
                JLabel comp = (JLabel) super.getListCellRendererComponent(list, displayText, index, isSelected,
                        cellHasFocus);
                if (value != null) {
                    comp.setToolTipText(value.toString());
                }
                comp.setBorder(new EmptyBorder(4, 6, 4, 6));
                return comp;
            }
        });
        // Make the dropdown popup wider so long names/cedulas are fully visible
        cb.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent e) {
                Object popup = cb.getUI().getAccessibleChild(cb, 0);
                if (popup instanceof javax.swing.JPopupMenu) {
                    javax.swing.JPopupMenu pm = (javax.swing.JPopupMenu) popup;
                    JScrollPane scrollPane = (JScrollPane) pm.getComponent(0);
                    // Calculate ideal width based on longest item (display text after '|')
                    int maxWidth = cb.getWidth();
                    FontMetrics fm = cb.getFontMetrics(cb.getFont());
                    for (int i = 0; i < cb.getItemCount(); i++) {
                        String item = cb.getItemAt(i);
                        String display = item.contains("|") ? item.substring(item.indexOf("|") + 1).trim() : item;
                        int w = fm.stringWidth(display) + 40;
                        if (w > maxWidth)
                            maxWidth = w;
                    }
                    scrollPane.setPreferredSize(new Dimension(maxWidth, scrollPane.getPreferredSize().height));
                    scrollPane.setMaximumSize(new Dimension(maxWidth, 300));
                }
            }

            @Override
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent e) {
            }

            @Override
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent e) {
            }
        });
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

    // ── Carga de programas académicos desde la BD ─────────────────────────────

    private String[] cargarProgramas() {
        try {
            ProgramaDAO dao = new ProgramaDAO();
            List<Programa> lista = dao.listarTodos();
            String[] arr = new String[lista.size() + 1];
            arr[0] = "— Seleccione programa —";
            for (int i = 0; i < lista.size(); i++)
                arr[i + 1] = lista.get(i).getIdPrograma() + " | " + lista.get(i).getNombre();
            return arr;
        } catch (Exception e) {
            return new String[] { "— Sin programas —" };
        }
    }

    private void actualizarProgramas() {
        Object seleccionado = comboPrograma.getSelectedItem();
        String[] nuevos = cargarProgramas();
        comboPrograma.setModel(new DefaultComboBoxModel<>(nuevos));
        if (seleccionado != null) {
            comboPrograma.setSelectedItem(seleccionado);
        }
    }

    /** Extrae el ID del item con formato "ID | Nombre" */
    private String extraerIdPrograma(String item) {
        if (item == null || item.startsWith("—"))
            return "";
        return item.contains("|") ? item.split("\\|")[0].trim() : item.trim();
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

    private Icon crearIconoOjo() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(30, 30, 30));
                g2.drawOval(x, y + 4, 12, 8);
                g2.fillOval(x + 4, y + 6, 4, 4);
            }

            @Override
            public int getIconWidth() {
                return 14;
            }

            @Override
            public int getIconHeight() {
                return 14;
            }
        };
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
            public int getIconWidth() {
                return 14;
            }

            @Override
            public int getIconHeight() {
                return 14;
            }
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
            public int getIconWidth() {
                return 12;
            }

            @Override
            public int getIconHeight() {
                return 14;
            }
        };
    }
}
