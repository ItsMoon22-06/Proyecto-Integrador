package vistas;

import controlador.DirectorControlador;
import modelado.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.Date;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

/**
 * Panel "Crear Práctica" del Portal Director.
 * - ID generado automáticamente (no editable por el usuario).
 * - Nombre de práctica ingresado por el usuario.
 * - Programa académico seleccionado con ComboBox (sin escribir IDs).
 * - Fechas seleccionadas con mini-calendario emergente.
 */
public class PanelCrearPractica extends JPanel {

    private static final Color BG = new Color(242, 242, 242);
    private static final Color DARK = new Color(45, 45, 45);
    private static final Color ACCENT = new Color(60, 120, 220);

    private final DirectorControlador ctrl;
    private final PortalDirector portal;

    private JTextField txtEntidad;
    private JTextField txtFechaInicio;
    private JTextField txtFechaFinal;
    private JComboBox<String> comboTipoPractica;

    private JComboBox<String> comboTutor;
    private JComboBox<String> comboEstudiante;
    private JComboBox<String> comboAsesor;

    private boolean isAdjustingCombos = false;

    private Practica practicaEnEdicion;
    private JButton btnCancelar;
    private JButton btnGuardar;

    public PanelCrearPractica(DirectorControlador ctrl, PortalDirector portal) {
        this.ctrl = ctrl;
        this.portal = portal;
        setBackground(BG);
        setLayout(new BorderLayout());
        construirUI();
    }

    private void construirUI() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(BG);
        content.setBorder(new EmptyBorder(25, 40, 40, 40));

        JLabel title = new JLabel("Gestión de Práctica");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(title);
        content.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel card = createRoundedPanel(20, Color.WHITE);
        card.setLayout(new GridBagLayout());
        card.setBorder(new EmptyBorder(30, 35, 30, 35));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.weightx = 1.0;

        // Fila 1: Tipo de Práctica
        gbc.gridy = 0;
        gbc.gridx = 0;
        comboTipoPractica = roundCombo(cargarTiposPractica());
        card.add(labeledField("Tipo de Práctica", comboTipoPractica), gbc);
        gbc.gridx = 1;
        card.add(new JLabel(""), gbc); // spacer
        gbc.gridx = 2;
        card.add(new JLabel(""), gbc); // spacer

        // Fila 2: Fecha Inicio | Fecha Final | Entidad
        gbc.gridy = 1;
        gbc.gridx = 0;
        card.add(labeledField("Fecha Inicio", crearCampoFecha(true)), gbc);
        gbc.gridx = 1;
        card.add(labeledField("Fecha Final", crearCampoFecha(false)), gbc);
        gbc.gridx = 2;
        card.add(labeledField("Entidad / Institución", txtEntidad = roundField()), gbc);

        // Fila 3: Tutor | Estudiante | Asesor
        gbc.gridy = 2;
        gbc.gridx = 0;
        comboTutor = roundCombo(cargarTutores(null));
        card.add(labeledField("Tutor Asignado", comboTutor), gbc);
        gbc.gridx = 1;
        comboEstudiante = roundCombo(cargarEstudiantes(null));
        card.add(labeledField("Estudiante Asignado", comboEstudiante), gbc);
        gbc.gridx = 2;
        comboAsesor = roundCombo(cargarAsesores());
        card.add(labeledField("Asesor Asignado", comboAsesor), gbc);

        comboTutor.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED && !isAdjustingCombos) {
                if (practicaEnEdicion == null) {
                    sincronizarCombosPrograma(true, false);
                }
            }
        });
        comboEstudiante.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED && !isAdjustingCombos) {
                if (practicaEnEdicion == null) {
                    sincronizarCombosPrograma(false, false);
                }
            }
        });

        // Fila 4: espacio
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 3;
        card.add(Box.createRigidArea(new Dimension(0, 15)), gbc);
        gbc.gridwidth = 1;

        // Fila 5: (espacio) | Cancelar | Guardar
        gbc.gridy = 4;
        gbc.gridx = 0;
        card.add(new JLabel(""), gbc); // spacer

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.EAST;
        btnCancelar = createRedButton("Cancelar");
        btnCancelar.addActionListener(e -> cancelar());
        card.add(btnCancelar, gbc);

        gbc.gridx = 2;
        gbc.anchor = GridBagConstraints.EAST;
        btnGuardar = createDarkButton("Guardar");
        btnGuardar.addActionListener(e -> guardar());
        card.add(btnGuardar, gbc);

        content.add(card);

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
    }

    // ── Mini-calendario emergente ──────────────────────────────────────────────

    private JPanel crearCampoFecha(boolean esInicio) {
        JTextField campo = roundField();
        campo.setEditable(false);
        campo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        campo.setToolTipText("Haz clic para seleccionar la fecha");

        if (esInicio)
            txtFechaInicio = campo;
        else
            txtFechaFinal = campo;

        JButton btnCal = new JButton("\uD83D\uDCC5"); // icono calendario
        btnCal.setFont(new Font("SansSerif", Font.PLAIN, 14));
        btnCal.setBackground(new Color(235, 235, 235));
        btnCal.setBorder(new EmptyBorder(4, 8, 4, 8));
        btnCal.setFocusPainted(false);
        btnCal.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCal.addActionListener(e -> mostrarCalendario(campo, btnCal));

        campo.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mostrarCalendario(campo, campo);
            }
        });

        JPanel row = new JPanel(new BorderLayout(4, 0));
        row.setOpaque(false);
        row.add(campo, BorderLayout.CENTER);
        row.add(btnCal, BorderLayout.EAST);
        return row;
    }

    private void mostrarCalendario(JTextField campoDestino, Component ancla) {
        JPopupMenu popup = new JPopupMenu();
        popup.setBorder(new LineBorder(new Color(200, 200, 200), 1));
        popup.setBackground(Color.WHITE);

        final LocalDate[] mesActual = { LocalDate.now().withDayOfMonth(1) };

        JPanel calPanel = new JPanel(new BorderLayout(0, 6));
        calPanel.setBackground(Color.WHITE);
        calPanel.setBorder(new EmptyBorder(10, 12, 10, 12));
        calPanel.setPreferredSize(new Dimension(260, 230));

        // Cabecera
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        JButton btnPrev = navBtn("‹");
        JButton btnNext = navBtn("›");
        JLabel lblMes = new JLabel("", SwingConstants.CENTER);
        lblMes.setFont(new Font("SansSerif", Font.BOLD, 13));
        header.add(btnPrev, BorderLayout.WEST);
        header.add(lblMes, BorderLayout.CENTER);
        header.add(btnNext, BorderLayout.EAST);

        // Grid de días
        JPanel grid = new JPanel(new GridLayout(0, 7, 2, 2));
        grid.setBackground(Color.WHITE);

        Runnable refrescar = () -> {
            YearMonth ym = YearMonth.from(mesActual[0]);
            String nombreMes = mesActual[0].getMonth().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("es"));
            nombreMes = nombreMes.substring(0, 1).toUpperCase() + nombreMes.substring(1);
            lblMes.setText(nombreMes + " " + mesActual[0].getYear());

            grid.removeAll();
            for (String d : new String[] { "Lu", "Ma", "Mi", "Ju", "Vi", "Sa", "Do" }) {
                JLabel lbl = new JLabel(d, SwingConstants.CENTER);
                lbl.setFont(new Font("SansSerif", Font.BOLD, 10));
                lbl.setForeground(Color.GRAY);
                grid.add(lbl);
            }

            int primerDia = mesActual[0].getDayOfWeek().getValue(); // 1=Lu…7=Do
            for (int i = 1; i < primerDia; i++)
                grid.add(new JLabel(""));

            LocalDate hoy = LocalDate.now();
            for (int d = 1; d <= ym.lengthOfMonth(); d++) {
                final LocalDate fecha = mesActual[0].withDayOfMonth(d);
                JButton btnDia = new JButton(String.valueOf(d));
                btnDia.setFont(new Font("SansSerif", Font.PLAIN, 11));
                btnDia.setFocusPainted(false);
                btnDia.setBorderPainted(false);
                btnDia.setMargin(new Insets(0, 0, 0, 0));
                btnDia.setPreferredSize(new Dimension(32, 28));
                btnDia.setCursor(new Cursor(Cursor.HAND_CURSOR));

                if (fecha.equals(hoy)) {
                    btnDia.setBackground(ACCENT);
                    btnDia.setForeground(Color.WHITE);
                } else {
                    btnDia.setBackground(Color.WHITE);
                    btnDia.setForeground(DARK);
                    btnDia.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseEntered(MouseEvent e) {
                            btnDia.setBackground(new Color(230, 238, 255));
                        }

                        @Override
                        public void mouseExited(MouseEvent e) {
                            btnDia.setBackground(Color.WHITE);
                        }
                    });
                }

                btnDia.addActionListener(ev -> {
                    campoDestino.setText(fecha.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                    popup.setVisible(false);
                });
                grid.add(btnDia);
            }
            grid.revalidate();
            grid.repaint();
        };

        btnPrev.addActionListener(e -> {
            mesActual[0] = mesActual[0].minusMonths(1);
            refrescar.run();
        });
        btnNext.addActionListener(e -> {
            mesActual[0] = mesActual[0].plusMonths(1);
            refrescar.run();
        });

        calPanel.add(header, BorderLayout.NORTH);
        calPanel.add(grid, BorderLayout.CENTER);
        refrescar.run();

        popup.add(calPanel);
        popup.show(ancla, 0, ancla.getHeight());
    }

    private JButton navBtn(String txt) {
        JButton b = new JButton(txt);
        b.setFont(new Font("SansSerif", Font.BOLD, 16));
        b.setBackground(Color.WHITE);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    // ── Guardar ───────────────────────────────────────────────────────────────

    private void guardar() {
        try {
            String entidad = txtEntidad.getText().trim();
            String estado = (practicaEnEdicion == null) ? "Activo" : practicaEnEdicion.getEstado();
            String tipo = extraerId((String) comboTipoPractica.getSelectedItem());
            String tutor = extraerId((String) comboTutor.getSelectedItem());
            String estudiante = extraerId((String) comboEstudiante.getSelectedItem());
            String asesor = extraerId((String) comboAsesor.getSelectedItem());
            String fechaIniTxt = txtFechaInicio.getText().trim();
            String fechaFinTxt = txtFechaFinal.getText().trim();

            if (entidad.isEmpty() || tipo.isEmpty() || tutor.isEmpty() || estudiante.isEmpty() || asesor.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Deben llenarse todos los campos.",
                        "Campos incompletos", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (fechaIniTxt.isEmpty() || fechaFinTxt.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Debes seleccionar las fechas usando el calendario.",
                        "Fechas requeridas", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Date fi = Date.valueOf(fechaIniTxt);
            Date ff = Date.valueOf(fechaFinTxt);

            if (ff.getTime() <= fi.getTime()) {
                JOptionPane.showMessageDialog(this,
                        "La fecha final debe ser estrictamente mayor a la fecha de inicio.",
                        "Error en fechas", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Validar que fecha inicio no sea futura
            Date hoy = new Date(System.currentTimeMillis());
            if (fi.toLocalDate().isAfter(hoy.toLocalDate())) {
                JOptionPane.showMessageDialog(this,
                        "La fecha de inicio no puede ser posterior al día de hoy.",
                        "Error en fechas", JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean ok;
            String idCreada = null;
            if (practicaEnEdicion == null) {
                // Modo crear
                idCreada = "PRC-" + Long.toString(System.currentTimeMillis(), 36).toUpperCase();
                ok = ctrl.registrarPractica(idCreada, fi, ff, entidad, estado, tipo, tutor, estudiante, asesor);
            } else {
                // Modo editar
                ok = ctrl.actualizarPractica(practicaEnEdicion.getIdPractica(), fi, ff, entidad, estado, tipo, tutor,
                        estudiante, asesor);
            }

            if (ok) {
                if (practicaEnEdicion == null) {
                    limpiarFormulario();
                    actualizarCombos();
                    portal.mostrarPracticaCreada(idCreada);
                } else {
                    cancelar(); // Limpia y resetea estado
                    portal.mostrarPanel("Practicas");
                }
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                    "Fecha inválida. Por favor selecciona la fecha usando el calendario.",
                    "Error de fecha", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void cargarEdicion(Practica p) {
        this.practicaEnEdicion = p;
        btnGuardar.setText("Guardar Cambios");

        // Refrescar los combos para que incluyan al estudiante/tutor/asesor actuales de
        // esta práctica en edición
        actualizarCombos();

        if (p.getEntidad() != null)
            txtEntidad.setText(p.getEntidad());
        if (p.getFechaInicio() != null)
            txtFechaInicio.setText(p.getFechaInicio().toString());
        if (p.getFechaFinal() != null)
            txtFechaFinal.setText(p.getFechaFinal().toString());

        seleccionarComboPorId(comboTipoPractica, p.getIdTipopractica());
        
        // Cargar estudiante y luego filtrar tutores por su programa
        seleccionarComboPorId(comboEstudiante, p.getNumDocEstudiante());
        sincronizarCombosPrograma(false, true);
        
        seleccionarComboPorId(comboTutor, p.getNumDocTutor());
        seleccionarComboPorId(comboAsesor, p.getNumDocAsesor());
        
        comboTipoPractica.setEnabled(false);
        comboEstudiante.setEnabled(false);
    }

    private void cancelar() {
        if (practicaEnEdicion != null) {
            practicaEnEdicion = null;
            btnGuardar.setText("Guardar");
            limpiarFormulario();
            actualizarCombos();
            portal.mostrarPanel("Practicas");
        } else {
            limpiarFormulario();
            actualizarCombos();
        }
    }

    public void actualizarCombos() {
        isAdjustingCombos = true;
        // Guardar selecciones actuales
        String selectedTutor = (String) comboTutor.getSelectedItem();
        String selectedEstudiante = (String) comboEstudiante.getSelectedItem();
        String selectedAsesor = (String) comboAsesor.getSelectedItem();
        String selectedTipo = (String) comboTipoPractica.getSelectedItem();

        // Actualizar modelos cargando datos frescos de la BD (sin filtro inicial)
        comboTutor.setModel(new DefaultComboBoxModel<>(cargarTutores(null)));
        comboEstudiante.setModel(new DefaultComboBoxModel<>(cargarEstudiantes(null)));
        comboAsesor.setModel(new DefaultComboBoxModel<>(cargarAsesores()));
        comboTipoPractica.setModel(new DefaultComboBoxModel<>(cargarTiposPractica()));

        // Restaurar selecciones si aún existen en la lista
        if (selectedTutor != null)
            comboTutor.setSelectedItem(selectedTutor);
        if (selectedEstudiante != null)
            comboEstudiante.setSelectedItem(selectedEstudiante);
        if (selectedAsesor != null)
            comboAsesor.setSelectedItem(selectedAsesor);
        if (selectedTipo != null)
            comboTipoPractica.setSelectedItem(selectedTipo);

        isAdjustingCombos = false;

        // Reaplicar el filtro si es necesario
        if (practicaEnEdicion != null) {
            if (selectedEstudiante != null && !extraerId(selectedEstudiante).isEmpty()) {
                sincronizarCombosPrograma(false, true);
            }
        } else {
            if (selectedTutor != null && !extraerId(selectedTutor).isEmpty()) {
                sincronizarCombosPrograma(true, true);
            } else if (selectedEstudiante != null && !extraerId(selectedEstudiante).isEmpty()) {
                sincronizarCombosPrograma(false, true);
            }
        }
    }

    private void sincronizarCombosPrograma(boolean desdeTutor, boolean silencioso) {
        isAdjustingCombos = true;

        String idTutorSeleccionado = extraerId((String) comboTutor.getSelectedItem());
        String idEstudianteSeleccionado = extraerId((String) comboEstudiante.getSelectedItem());
        String idProgramaFiltro = null;

        if (desdeTutor && !idTutorSeleccionado.isEmpty()) {
            for (TutorAcademico t : ctrl.listarTutores()) {
                if (t.getNumDocumento().equals(idTutorSeleccionado)) {
                    idProgramaFiltro = t.getIdPrograma();
                    break;
                }
            }
            comboEstudiante.setModel(new DefaultComboBoxModel<>(cargarEstudiantes(idProgramaFiltro)));
            seleccionarComboPorId(comboEstudiante, idEstudianteSeleccionado);

            if (comboEstudiante.getSelectedIndex() == 0 && !idEstudianteSeleccionado.isEmpty() && !silencioso) {
                JOptionPane.showMessageDialog(this,
                        "El estudiante previamente seleccionado no pertenece al programa del tutor elegido.",
                        "Aviso de Compatibilidad", JOptionPane.INFORMATION_MESSAGE);
            }
        } else if (!desdeTutor && !idEstudianteSeleccionado.isEmpty()) {
            for (Estudiante est : ctrl.listarEstudiantes()) {
                if (est.getNumDocumento().equals(idEstudianteSeleccionado)) {
                    idProgramaFiltro = est.getIdPrograma();
                    break;
                }
            }
            comboTutor.setModel(new DefaultComboBoxModel<>(cargarTutores(idProgramaFiltro)));
            seleccionarComboPorId(comboTutor, idTutorSeleccionado);

            if (comboTutor.getSelectedIndex() == 0 && !idTutorSeleccionado.isEmpty() && !silencioso) {
                JOptionPane.showMessageDialog(this,
                        "El tutor previamente seleccionado no pertenece al programa del estudiante elegido.",
                        "Aviso de Compatibilidad", JOptionPane.INFORMATION_MESSAGE);
            }
        } else if (idTutorSeleccionado.isEmpty() && idEstudianteSeleccionado.isEmpty()) {
            comboTutor.setModel(new DefaultComboBoxModel<>(cargarTutores(null)));
            comboEstudiante.setModel(new DefaultComboBoxModel<>(cargarEstudiantes(null)));
        }

        isAdjustingCombos = false;
    }

    private void seleccionarComboPorId(JComboBox<String> cb, String id) {
        if (id == null) {
            cb.setSelectedIndex(0);
            return;
        }
        for (int i = 0; i < cb.getItemCount(); i++) {
            if (cb.getItemAt(i).startsWith(id + " |")) {
                cb.setSelectedIndex(i);
                return;
            }
        }
        cb.setSelectedIndex(0);
    }

    private void limpiarFormulario() {
        txtEntidad.setText("");
        txtFechaInicio.setText("");
        txtFechaFinal.setText("");
        comboTipoPractica.setSelectedIndex(0);
        comboTutor.setSelectedIndex(0);
        comboEstudiante.setSelectedIndex(0);
        comboAsesor.setSelectedIndex(0);

        comboTipoPractica.setEnabled(true);
        comboEstudiante.setEnabled(true);
    }

    // ── Carga de datos para combos ────────────────────────────────────────────

    private String[] cargarTiposPractica() {
        try {
            TipopracticaDAO dao = new TipopracticaDAO();
            List<Tipopractica> lista = dao.listarTodos();
            String[] arr = new String[lista.size() + 1];
            arr[0] = "— Seleccione tipo —";
            for (int i = 0; i < lista.size(); i++)
                arr[i + 1] = lista.get(i).getIdTipopractica() + " | " + lista.get(i).getNombre();
            return arr;
        } catch (Exception e) {
            return new String[] { "— Sin datos —" };
        }
    }

    private String[] cargarTutores(String idProgramaFiltro) {
        List<TutorAcademico> lista = ctrl.listarTutores();
        List<TutorAcademico> filtrados = new java.util.ArrayList<>();
        for (TutorAcademico t : lista) {
            boolean matchesProgram = (idProgramaFiltro == null)
                    || (t.getIdPrograma() != null && t.getIdPrograma().equals(idProgramaFiltro));
            if (matchesProgram && ("Activo".equalsIgnoreCase(t.getEstado())
                    || (practicaEnEdicion != null && t.getNumDocumento().equals(practicaEnEdicion.getNumDocTutor())))) {
                filtrados.add(t);
            }
        }

        if (filtrados.isEmpty()) {
            if (idProgramaFiltro != null)
                return new String[] { "— Sin tutores del mismo programa —" };
            return new String[] { "— No hay tutores activos para asignar —" };
        }

        String[] arr = new String[filtrados.size() + 1];
        arr[0] = "— Seleccione tutor —";
        for (int i = 0; i < filtrados.size(); i++)
            arr[i + 1] = filtrados.get(i).getNumDocumento() + " | " + filtrados.get(i).getNombre() + " "
                    + filtrados.get(i).getApellido();
        return arr;
    }

    private String[] cargarEstudiantes(String idProgramaFiltro) {
        List<Estudiante> lista = ctrl.listarEstudiantes();
        List<Practica> practicas = ctrl.listarPracticas();
        java.util.Set<String> estudiantesEnPracticaActiva = new java.util.HashSet<>();

        for (Practica p : practicas) {
            if ("Activo".equalsIgnoreCase(p.getEstado())) {
                if (practicaEnEdicion == null || !p.getIdPractica().equals(practicaEnEdicion.getIdPractica())) {
                    if (p.getNumDocEstudiante() != null) {
                        estudiantesEnPracticaActiva.add(p.getNumDocEstudiante());
                    }
                }
            }
        }

        List<Estudiante> filtrados = new java.util.ArrayList<>();
        for (Estudiante e : lista) {
            boolean matchesProgram = (idProgramaFiltro == null)
                    || (e.getIdPrograma() != null && e.getIdPrograma().equals(idProgramaFiltro));
            if (matchesProgram && "Activo".equalsIgnoreCase(e.getEstado())
                    && !estudiantesEnPracticaActiva.contains(e.getNumDocumento())) {
                filtrados.add(e);
            }
        }

        if (filtrados.isEmpty()) {
            if (idProgramaFiltro != null)
                return new String[] { "— Sin estudiantes del mismo programa —" };
            return new String[] { "— No hay estudiantes activos sin prácticas activas —" };
        }

        String[] arr = new String[filtrados.size() + 1];
        arr[0] = "— Seleccione estudiante —";
        for (int i = 0; i < filtrados.size(); i++)
            arr[i + 1] = filtrados.get(i).getNumDocumento() + " | " + filtrados.get(i).getNombre() + " "
                    + filtrados.get(i).getApellido();
        return arr;
    }

    private String[] cargarAsesores() {
        List<AsesorPedagogico> lista = ctrl.listarAsesores();
        List<AsesorPedagogico> filtrados = new java.util.ArrayList<>();
        for (AsesorPedagogico a : lista) {
            if ("Activo".equalsIgnoreCase(a.getEstado())
                    || (practicaEnEdicion != null && a.getNumDocumento().equals(practicaEnEdicion.getNumDocAsesor()))) {
                filtrados.add(a);
            }
        }

        if (filtrados.isEmpty()) {
            return new String[] { "— No hay asesores activos para asignar —" };
        }

        String[] arr = new String[filtrados.size() + 1];
        arr[0] = "— Seleccione asesor —";
        for (int i = 0; i < filtrados.size(); i++)
            arr[i + 1] = filtrados.get(i).getNumDocumento() + " | " + filtrados.get(i).getNombre() + " "
                    + filtrados.get(i).getApellido();
        return arr;
    }

    private String extraerId(String item) {
        if (item == null || item.startsWith("—"))
            return "";
        return item.contains("|") ? item.split("\\|")[0].trim() : item.trim();
    }

    // ── Helpers de UI ─────────────────────────────────────────────────────────

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
        p.add(Box.createRigidArea(new Dimension(0, 4)));
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
        
        cb.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String val = (String) cb.getSelectedItem();
                if (val != null && !val.startsWith("—")) {
                    cb.setToolTipText(val);
                } else {
                    cb.setToolTipText(null);
                }
            }
        });
        
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
        b.setPreferredSize(new Dimension(140, 38));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JButton createRedButton(String text) {
        JButton b = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(220, 50, 50));
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
        b.setPreferredSize(new Dimension(150, 38));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
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
