package vistas;

import controlador.BitacoraControlador;
import modelado.Bitacora;
import modelado.Estudiante;
import modelado.Evidencia;
import modelado.RetroalimentacionTutor;
import modelado.RetroalimentacionAsesor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.*;
import java.util.List;

/**
 * Vista de Bitácora usada por Tutor Académico y Asesor Pedagógico.
 * esTutor=true → campos editables nota/observacion + botón "Cerrar Bitácora".
 * esTutor=false → solo lectura.
 * Lógica completamente conectada a BD.
 */
public class PanelBitacoraTutor extends JPanel {

    private static final Color BG = new Color(242, 242, 242);
    private static final Color COLOR_DARK = new Color(45, 45, 45);
    private static final Color COLOR_GRAY_IN = new Color(225, 225, 225);
    private final boolean esDirector;

    private final boolean esTutor;
    private final String docRevisor;
    private final BitacoraControlador ctrl;

    // Componentes dinámicos
    private JLabel lblStudentName;
    private JLabel lblFecha;
    private JTextField txtNota;
    private JTextField txtObservacion;
    private JLabel lblEstadoBitacora;
    private String observacionCompleta = "";
    private JPanel evidenciasContainer;
    private JPanel retroContainer;
    private JTextArea txtComment;
    private Bitacora bitacoraActual;
    private Estudiante estudianteActual;

    private boolean esFinalizadoActual;
    private JLabel lblAddComment;
    private JPanel retroCard;
    private JPanel panelBotones;
    private JButton btnCerrar;
    private Object retroEnEdicion;
    private JButton btnCancelarRetro;
    private JButton btnVerTutor;

    public PanelBitacoraTutor(CardLayout cardLayout, JPanel container,
            boolean esTutor, String docRevisor, BitacoraControlador ctrl) {
        this.esTutor = esTutor;
        this.docRevisor = docRevisor;
        this.ctrl = ctrl;
        this.esDirector = "DIRECTOR_READ_ONLY".equals(docRevisor);

        setBackground(BG);
        setLayout(new BorderLayout());

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(BG);
        content.setBorder(new EmptyBorder(25, 40, 25, 40));

        // Título
        JLabel lblTitulo = new JLabel("Bitácora del Estudiante");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(lblTitulo);
        content.add(spacer(15));

        // ── Nombre del estudiante ──
        JPanel studentCard = createRoundedPanel(15, Color.WHITE);
        studentCard.setLayout(new BoxLayout(studentCard, BoxLayout.Y_AXIS));
        studentCard.setBorder(new EmptyBorder(15, 20, 15, 20));
        studentCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        studentCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));
        JLabel lbl1 = new JLabel("Nombre Estudiante");
        lbl1.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblStudentName = new JLabel("—");
        lblStudentName.setFont(new Font("SansSerif", Font.PLAIN, 14));
        studentCard.add(lbl1);
        studentCard.add(lblStudentName);
        content.add(studentCard);
        content.add(spacer(12));

        // ── Fecha / Nota / Observación / Estado ──
        JPanel infoCard = createRoundedPanel(15, Color.WHITE);
        infoCard.setBorder(new EmptyBorder(15, 20, 15, 20));
        infoCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        lblFecha = new JLabel("—");
        txtNota = new JTextField(5);
        txtObservacion = new JTextField(10);
        lblEstadoBitacora = new JLabel("—");

        if (esTutor) {
            // Tutor: Fecha + Nota editable + Observación editable + Estado
            infoCard.setLayout(new GridLayout(1, 4, 20, 0));
            infoCard.add(createFieldStatic("Fecha de creación", lblFecha));
            infoCard.add(createFieldEditable("Nota Final (0-5)", txtNota));
            infoCard.add(createFieldEditableWithVer("Observación General", txtObservacion));
            infoCard.add(createFieldStatic("Estado Bitácora", lblEstadoBitacora));
        } else if (esDirector) {
            // Director: Fecha + Nota (solo lectura) + Observación (solo lectura) + Estado
            infoCard.setLayout(new GridLayout(1, 4, 20, 0));
            infoCard.add(createFieldStatic("Fecha de creación", lblFecha));
            infoCard.add(createFieldStaticReadOnly("Nota Final (0-5)", txtNota));
            infoCard.add(createFieldStaticReadOnlyWithVer("Observación General", txtObservacion));
            infoCard.add(createFieldStatic("Estado Bitácora", lblEstadoBitacora));
        } else {
            // Asesor: Fecha + Estado
            infoCard.setLayout(new GridLayout(1, 2, 20, 0));
            infoCard.add(createFieldStatic("Fecha de creación", lblFecha));
            infoCard.add(createFieldStatic("Estado Bitácora", lblEstadoBitacora));
        }
        content.add(infoCard);
        content.add(spacer(12));

        // Botón cerrar bitácora (solo tutor)
        if (esTutor) {
            btnCerrar = createRoundedButton("Guardar y Cerrar Bitácora", COLOR_DARK, Color.WHITE);
            btnCerrar.setAlignmentX(Component.LEFT_ALIGNMENT);
            btnCerrar.addActionListener(e -> cerrarBitacora());
            content.add(btnCerrar);
            content.add(spacer(12));
        }

        // ── Evidencias ──
        JLabel lblEvidenciasTitle = new JLabel("Evidencias del Estudiante:");
        lblEvidenciasTitle.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblEvidenciasTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(lblEvidenciasTitle);
        content.add(spacer(8));

        evidenciasContainer = new JPanel();
        evidenciasContainer.setLayout(new BoxLayout(evidenciasContainer, BoxLayout.Y_AXIS));
        evidenciasContainer.setOpaque(false);
        evidenciasContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(evidenciasContainer);
        content.add(spacer(12));

        // ── Retroalimentaciones existentes ──
        JLabel lblRetroTitle = new JLabel("Retroalimentaciones:");
        lblRetroTitle.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblRetroTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(lblRetroTitle);
        content.add(spacer(8));

        retroContainer = new JPanel();
        retroContainer.setLayout(new BoxLayout(retroContainer, BoxLayout.Y_AXIS));
        retroContainer.setOpaque(false);
        retroContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(retroContainer);
        content.add(spacer(12));

        // ── Añadir comentario ──
        lblAddComment = new JLabel("Añadir Comentario:");
        lblAddComment.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblAddComment.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(lblAddComment);
        content.add(spacer(10));

        retroCard = createRoundedPanel(15, Color.WHITE);
        retroCard.setLayout(new BorderLayout());
        retroCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtComment = new JTextArea();
        txtComment.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtComment.setLineWrap(true);
        txtComment.setWrapStyleWord(true);
        txtComment.setOpaque(false);
        txtComment.setBorder(new EmptyBorder(15, 20, 15, 20));
        txtComment.setRows(5);
        retroCard.add(txtComment, BorderLayout.CENTER);
        content.add(retroCard);
        content.add(spacer(12));

        panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelBotones.setOpaque(false);
        panelBotones.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnSend = createRoundedButton("Enviar Retroalimentación", COLOR_DARK, Color.WHITE);
        btnSend.addActionListener(e -> enviarComentario());
        panelBotones.add(btnSend);

        btnCancelarRetro = createRoundedButton("Cancelar", new Color(200, 50, 50), Color.WHITE);
        btnCancelarRetro.addActionListener(e -> {
            retroEnEdicion = null;
            txtComment.setText("");
            lblAddComment.setText("Añadir Comentario:");
        });
        panelBotones.add(btnCancelarRetro);

        content.add(panelBotones);
        content.add(spacer(20));

        // Ocultar sección de comentarios si el director está viendo
        if ("DIRECTOR_READ_ONLY".equals(docRevisor)) {
            lblAddComment.setVisible(false);
            retroCard.setVisible(false);
            panelBotones.setVisible(false);
        }

        // ── Botón Volver ──
        JButton btnVolver = createRoundedButton("\u2190  Volver", new Color(210, 210, 210), new Color(45, 45, 45));
        btnVolver.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnVolver.addActionListener(e -> {
            if ("DIRECTOR_READ_ONLY".equals(docRevisor)) {
                cardLayout.show(container, "Practicas");
            } else {
                cardLayout.show(container, "LISTA");
            }
        });
        content.add(btnVolver);

        // ── Scroll vertical solamente ──
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG);
        wrapper.add(content, BorderLayout.CENTER);

        JScrollPane scroll = new JScrollPane(wrapper);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.getVerticalScrollBar().setUI(new CleanScrollBarUI());
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
        scroll.getVerticalScrollBar().setBackground(BG);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
    }

    /**
     * Recarga la UI con los datos de una bitácora y estudiante específicos.
     * Llamado desde PanelPracticasTutor al hacer clic en "Ingresar".
     */
    public void cargar(Bitacora b, Estudiante est, boolean esFinalizado) {
        this.bitacoraActual = b;
        this.estudianteActual = est;
        this.esFinalizadoActual = esFinalizado;

        boolean isCerrada = (b != null && "Cerrada".equalsIgnoreCase(b.getEstado())) || esFinalizado;
        boolean readOnly = b == null || esDirector || isCerrada;

        if (lblAddComment != null)
            lblAddComment.setVisible(!readOnly);
        if (retroCard != null)
            retroCard.setVisible(!readOnly);
        if (panelBotones != null)
            panelBotones.setVisible(!readOnly);
        if (btnCerrar != null)
            btnCerrar.setVisible(!readOnly && esTutor);
        if (btnVerTutor != null)
            btnVerTutor.setVisible(readOnly);

        if (esTutor) {
            txtNota.setEditable(!readOnly);
            txtObservacion.setEditable(!readOnly);
            if (readOnly) {
                txtNota.setBorder(null);
                txtNota.setOpaque(false);
                txtNota.setForeground(Color.DARK_GRAY);
                txtObservacion.setBorder(null);
                txtObservacion.setOpaque(false);
                txtObservacion.setForeground(Color.DARK_GRAY);
            } else {
                txtNota.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(COLOR_GRAY_IN),
                        BorderFactory.createEmptyBorder(4, 8, 4, 8)));
                txtNota.setOpaque(true);
                txtNota.setForeground(Color.BLACK);
                txtObservacion.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(COLOR_GRAY_IN),
                        BorderFactory.createEmptyBorder(4, 8, 4, 8)));
                txtObservacion.setOpaque(true);
                txtObservacion.setForeground(Color.BLACK);
            }
        }

        if (txtComment != null)
            txtComment.setText("");

        String nomEst = est != null ? est.getNombre() + " " + est.getApellido() : "—";
        lblStudentName.setText(nomEst);

        if (b == null) {
            lblFecha.setText("Sin bitácora");
            lblEstadoBitacora.setText("—");
            observacionCompleta = "";
            txtNota.setText("—");
            txtObservacion.setText("—");
            evidenciasContainer.removeAll();
            retroContainer.removeAll();
        } else {
            lblFecha.setText(b.getFechaCreacion() != null ? b.getFechaCreacion().toString() : "—");

            // Estado de la bitácora
            String estadoBit = isCerrada ? "Cerrada" : (b.getEstado() != null ? b.getEstado() : "Activa");
            lblEstadoBitacora.setText(estadoBit);
            if ("Cerrada".equalsIgnoreCase(estadoBit)) {
                lblEstadoBitacora.setForeground(new Color(200, 50, 50));
            } else {
                lblEstadoBitacora.setForeground(new Color(40, 150, 40));
            }

            String noDataText = isCerrada ? "—" : "Pendiente";

            if (esDirector) {
                txtNota.setText(b.getNotaFinal() > 0 ? String.valueOf(b.getNotaFinal()) : noDataText);
                String obs = b.getObservacionFinal();
                observacionCompleta = (obs != null && !obs.isEmpty()) ? obs : noDataText;
                txtObservacion.setText(truncar(observacionCompleta, 5));
            } else {
                txtNota.setText(b.getNotaFinal() > 0 ? String.valueOf(b.getNotaFinal()) : (readOnly ? noDataText : ""));
                String obs = b.getObservacionFinal();
                observacionCompleta = (obs != null && !obs.isEmpty()) ? obs : (readOnly ? noDataText : "");
                if (readOnly) {
                    txtObservacion.setText(truncar(observacionCompleta, 5));
                } else {
                    txtObservacion.setText(observacionCompleta);
                }
            }

            // Evidencias
            evidenciasContainer.removeAll();
            List<Evidencia> evidencias = ctrl.listarEvidencias(b.getIdBitacora());
            if (evidencias.isEmpty()) {
                JLabel sin = new JLabel("No hay evidencias subidas.");
                sin.setFont(new Font("SansSerif", Font.ITALIC, 13));
                sin.setForeground(Color.GRAY);
                evidenciasContainer.add(sin);
            } else {
                for (Evidencia ev : evidencias) {
                    evidenciasContainer.add(createEvidenciaItem(ev));
                    evidenciasContainer.add(spacer(8));
                }
            }

            // Retroalimentaciones
            retroContainer.removeAll();
            List<RetroalimentacionTutor> retrosT = ctrl.listarRetroalimentacionesTutor(b.getIdBitacora());
            List<RetroalimentacionAsesor> retrosA = ctrl.listarRetroalimentacionesAsesor(b.getIdBitacora());
            if (retrosT.isEmpty() && retrosA.isEmpty()) {
                JLabel sin = new JLabel("Sin retroalimentaciones.");
                sin.setFont(new Font("SansSerif", Font.ITALIC, 13));
                sin.setForeground(Color.GRAY);
                sin.setAlignmentX(Component.LEFT_ALIGNMENT);
                retroContainer.add(sin);
            } else {
                for (RetroalimentacionTutor r : retrosT) {
                    retroContainer.add(createRetroItem(r.getIdRetroalimentacion(), r.getComentario(), r.getFecha(), r.getNumDocTutor(), true, r));
                    retroContainer.add(spacer(6));
                }
                for (RetroalimentacionAsesor r : retrosA) {
                    retroContainer.add(createRetroItem(r.getIdRetroalimentacion(), r.getComentario(), r.getFecha(), r.getNumDocAsesor(), false, r));
                    retroContainer.add(spacer(6));
                }
            }
        }

        retroContainer.revalidate();
        retroContainer.repaint();
        evidenciasContainer.revalidate();
        evidenciasContainer.repaint();
        revalidate();
        repaint();
    }

    private void descargarArchivo(String idEvidencia) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle("Seleccione la carpeta donde guardar el archivo original");
        int res = chooser.showSaveDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            File dir = chooser.getSelectedFile();
            if (ctrl.descargarEvidenciaBlob(idEvidencia, dir)) {
                JOptionPane.showMessageDialog(this, "Archivo original descargado y guardado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Error al descargar o guardar el archivo desde la BD.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void enviarComentario() {
        if (bitacoraActual == null) {
            JOptionPane.showMessageDialog(this, "No hay bitácora cargada.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String texto = txtComment.getText().trim();
        if (texto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Escribe un comentario.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        boolean ok;
        if (retroEnEdicion != null) {
            // Modo edición: actualizar la retroalimentación existente
            if (retroEnEdicion instanceof RetroalimentacionTutor) {
                ctrl.actualizarRetroalimentacionTutor(((RetroalimentacionTutor)retroEnEdicion).getIdRetroalimentacion(), texto);
            } else if (retroEnEdicion instanceof RetroalimentacionAsesor) {
                ctrl.actualizarRetroalimentacionAsesor(((RetroalimentacionAsesor)retroEnEdicion).getIdRetroalimentacion(), texto);
            }
            retroEnEdicion = null;
            lblAddComment.setText("Añadir Comentario:");
            ok = true;
        } else {
            // Modo nuevo: crear nueva retroalimentación
            if (esTutor)
                ok = ctrl.agregarRetroalimentacionTutor(bitacoraActual.getIdBitacora(), texto, docRevisor);
            else
                ok = ctrl.agregarRetroalimentacionAsesor(bitacoraActual.getIdBitacora(), texto, docRevisor);
        }
        if (ok) {
            JOptionPane.showMessageDialog(this, "Retroalimentación subida correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            txtComment.setText("");
            // Recargar retroalimentaciones
            retroContainer.removeAll();
            List<RetroalimentacionTutor> retrosT = ctrl.listarRetroalimentacionesTutor(bitacoraActual.getIdBitacora());
            for (RetroalimentacionTutor r : retrosT) {
                retroContainer.add(createRetroItem(r.getIdRetroalimentacion(), r.getComentario(), r.getFecha(), r.getNumDocTutor(), true, r));
                retroContainer.add(spacer(6));
            }
            List<RetroalimentacionAsesor> retrosA = ctrl.listarRetroalimentacionesAsesor(bitacoraActual.getIdBitacora());
            for (RetroalimentacionAsesor r : retrosA) {
                retroContainer.add(createRetroItem(r.getIdRetroalimentacion(), r.getComentario(), r.getFecha(), r.getNumDocAsesor(), false, r));
                retroContainer.add(spacer(6));
            }
            retroContainer.revalidate();
            retroContainer.repaint();
        }
    }

    private void cerrarBitacora() {
        if (bitacoraActual == null) {
            JOptionPane.showMessageDialog(this, "No hay bitácora cargada.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String nota = txtNota.getText().trim();
        String obs = txtObservacion.getText().trim();
        boolean ok = ctrl.cerrarBitacora(bitacoraActual.getIdBitacora(), nota, obs);
        if (ok) {
            try {
                bitacoraActual.setNotaFinal(Double.parseDouble(nota));
            } catch (NumberFormatException e) {
                // Should not happen, already validated in controlador
            }
            bitacoraActual.setObservacionFinal(obs);
            bitacoraActual.setEstado("Cerrada");
            cargar(bitacoraActual, estudianteActual, esFinalizadoActual);
        }
    }

    // ── Helpers de UI ─────────────────────────────────────────────────────────

    private JPanel createFieldStatic(String titulo, JLabel valorLabel) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(10, 5, 10, 5));

        JLabel t = new JLabel(titulo);
        t.setFont(new Font("SansSerif", Font.BOLD, 14));
        t.setAlignmentX(Component.CENTER_ALIGNMENT);

        valorLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        valorLabel.setBorder(new EmptyBorder(5, 0, 0, 0));
        valorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        p.add(t);
        p.add(valorLabel);
        return p;
    }

    private JPanel createFieldEditable(String titulo, JTextField field) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(10, 5, 10, 5));

        JLabel t = new JLabel(titulo);
        t.setFont(new Font("SansSerif", Font.BOLD, 14));
        t.setAlignmentX(Component.CENTER_ALIGNMENT);

        field.setFont(new Font("SansSerif", Font.PLAIN, 13));
        field.setHorizontalAlignment(JTextField.CENTER);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_GRAY_IN),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        p.add(t);
        p.add(Box.createRigidArea(new Dimension(0, 5)));
        p.add(field);
        return p;
    }

    private JPanel createFieldEditableWithVer(String titulo, JTextField field) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(10, 5, 10, 5));

        JLabel t = new JLabel(titulo);
        t.setFont(new Font("SansSerif", Font.BOLD, 14));
        t.setAlignmentX(Component.CENTER_ALIGNMENT);

        field.setFont(new Font("SansSerif", Font.PLAIN, 13));
        field.setHorizontalAlignment(JTextField.CENTER);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_GRAY_IN),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        JPanel row = new JPanel(new BorderLayout(5, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        row.add(field, BorderLayout.CENTER);

        JButton btnVer = new JButton("Ver");
        btnVer.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnVer.setForeground(new Color(60, 120, 220));
        btnVer.setContentAreaFilled(false);
        btnVer.setBorderPainted(false);
        btnVer.setFocusPainted(false);
        btnVer.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVer.addActionListener(e -> mostrarObservacionCompleta());
        row.add(btnVer, BorderLayout.EAST);

        btnVerTutor = btnVer; // Guardar referencia para ocultar si se puede editar

        p.add(t);
        p.add(Box.createRigidArea(new Dimension(0, 5)));
        p.add(row);
        return p;
    }

    private JPanel createFieldStaticReadOnly(String titulo, JTextField field) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(10, 5, 10, 5));

        JLabel t = new JLabel(titulo);
        t.setFont(new Font("SansSerif", Font.BOLD, 14));
        t.setAlignmentX(Component.CENTER_ALIGNMENT);

        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setHorizontalAlignment(JTextField.CENTER);
        field.setEditable(false);
        field.setOpaque(false);
        field.setBorder(null);
        field.setForeground(Color.DARK_GRAY);

        p.add(t);
        p.add(Box.createRigidArea(new Dimension(0, 5)));
        p.add(field);
        return p;
    }

    private JPanel createFieldStaticReadOnlyWithVer(String titulo, JTextField field) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(10, 5, 10, 5));

        JLabel t = new JLabel(titulo);
        t.setFont(new Font("SansSerif", Font.BOLD, 14));
        t.setAlignmentX(Component.CENTER_ALIGNMENT);

        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setHorizontalAlignment(JTextField.CENTER);
        field.setEditable(false);
        field.setOpaque(false);
        field.setBorder(null);
        field.setForeground(Color.DARK_GRAY);

        JPanel row = new JPanel(new BorderLayout(5, 0));
        row.setOpaque(false);
        row.add(field, BorderLayout.CENTER);

        JButton btnVer = new JButton("Ver");
        btnVer.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnVer.setForeground(new Color(60, 120, 220));
        btnVer.setContentAreaFilled(false);
        btnVer.setBorderPainted(false);
        btnVer.setFocusPainted(false);
        btnVer.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVer.addActionListener(e -> mostrarObservacionCompleta());
        row.add(btnVer, BorderLayout.EAST);

        p.add(t);
        p.add(Box.createRigidArea(new Dimension(0, 5)));
        p.add(row);
        return p;
    }

    private void mostrarObservacionCompleta() {
        String texto = observacionCompleta;
        if (texto == null || texto.isEmpty())
            texto = "Sin observación.";
        JTextArea ta = new JTextArea(texto);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setEditable(false);
        ta.setRows(6);
        ta.setFont(new Font("SansSerif", Font.PLAIN, 14));
        JScrollPane sp = new JScrollPane(ta);
        sp.setPreferredSize(new Dimension(550, 300));
        JOptionPane.showMessageDialog(this, sp, "Observación General", JOptionPane.PLAIN_MESSAGE);
    }

    private String truncar(String texto, int max) {
        if (texto == null)
            return "";
        return texto.length() > max ? texto.substring(0, max) + "..." : texto;
    }

    private JPanel createEvidenciaItem(Evidencia ev) {
        JPanel p = createRoundedPanel(12, new Color(248, 248, 248));
        p.setLayout(new BorderLayout());
        p.setBorder(new EmptyBorder(10, 15, 10, 15));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);

        String file = ev.getNombreArchivo() != null ? ev.getNombreArchivo() : "Sin archivo";
        JLabel lTop = new JLabel("Archivo: " + file);
        lTop.setFont(new Font("SansSerif", Font.BOLD, 12));

        String descFull = ev.getDescripcion() != null ? ev.getDescripcion() : "(Sin descripción)";
        String descVis = descFull.length() > 60 ? descFull.substring(0, 60) + "…" : descFull;
        JLabel lDesc = new JLabel(descVis);
        lDesc.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lDesc.setToolTipText(descFull);

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.add(lTop);
        textPanel.add(Box.createRigidArea(new Dimension(0, 2)));
        textPanel.add(lDesc);

        JButton btnMenu = createDotsButton();
        btnMenu.addActionListener(e -> {
            JPopupMenu popup = createStyledPopup();

            JMenuItem itemVer = new JMenuItem("Ver", crearIconoOjo());
            styleMenuItem(itemVer, new Color(30, 30, 30), false);
            itemVer.addActionListener(ev2 -> {
                JPanel panel = new JPanel(new BorderLayout(0, 8));
                panel.setBorder(new EmptyBorder(8, 8, 8, 8));
                JLabel lNombre = new JLabel("<html><b>Archivo:</b> " + file + "</html>");
                JTextArea taDesc = new JTextArea(descFull);
                taDesc.setLineWrap(true);
                taDesc.setWrapStyleWord(true);
                taDesc.setEditable(false);
                taDesc.setRows(4);
                taDesc.setFont(new Font("SansSerif", Font.PLAIN, 14));
                JScrollPane sp = new JScrollPane(taDesc);
                sp.setPreferredSize(new Dimension(550, 300));
                panel.add(lNombre, BorderLayout.NORTH);
                panel.add(sp, BorderLayout.CENTER);
                // Se deja el botón visible siempre para que pueda descargar desde la BD
                JButton btnDl = new JButton("⬇ Descargar archivo");
                btnDl.addActionListener(dl -> descargarArchivo(ev.getIdEvidencia()));
                panel.add(btnDl, BorderLayout.SOUTH);
                JOptionPane.showMessageDialog(PanelBitacoraTutor.this, panel, "Ver Evidencia",
                        JOptionPane.PLAIN_MESSAGE);
            });

            popup.add(itemVer);
            popup.show(btnMenu, btnMenu.getWidth() - popup.getPreferredSize().width, btnMenu.getHeight());
        });

        JPanel east = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        east.setOpaque(false);
        east.add(btnMenu);

        p.add(textPanel, BorderLayout.CENTER);
        p.add(east, BorderLayout.EAST);
        return p;
    }

    private JPanel createRetroItem(String idRetro, String textoFull, java.sql.Date fechaObj, String numDoc, boolean isTutorItem, Object originalObj) {
        String autor = isTutorItem ? "Tutor" : "Asesor";
        String fecha = fechaObj != null ? fechaObj.toString() : "";

        JPanel p = createRoundedPanel(12, new Color(238, 238, 238));
        p.setLayout(new BorderLayout());
        p.setBorder(new EmptyBorder(10, 20, 10, 20));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTop = new JLabel(autor + "   " + fecha);
        lblTop.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblTop.setForeground(Color.GRAY);

        String txtVis = textoFull.length() > 60 ? textoFull.substring(0, 60) + "..." : textoFull;
        JLabel lblTxt = new JLabel("<html>" + txtVis + "</html>");
        lblTxt.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblTxt.setToolTipText(textoFull);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.add(lblTop);
        center.add(lblTxt);
        p.add(center, BorderLayout.CENTER);

        // Añadir 3 puntos solo para tutor si es su propio comentario
        boolean soyAutor = (esTutor && isTutorItem && numDoc != null && numDoc.equals(docRevisor)) ||
                (!esTutor && !isTutorItem && numDoc != null && numDoc.equals(docRevisor));

        if (soyAutor) {
            JButton btnMenu = createDotsButton();
            btnMenu.addActionListener(e -> {
                JPopupMenu popup = createStyledPopup();

                JMenuItem itemVer = new JMenuItem("Ver", crearIconoOjo());
                styleMenuItem(itemVer, new Color(30, 30, 30), false);
                itemVer.addActionListener(ev2 -> {
                    JTextArea ta = new JTextArea(textoFull);
                    ta.setLineWrap(true);
                    ta.setWrapStyleWord(true);
                    ta.setEditable(false);
                    ta.setRows(6);
                    ta.setFont(new Font("SansSerif", Font.PLAIN, 14));
                    JScrollPane sp = new JScrollPane(ta);
                    sp.setPreferredSize(new Dimension(550, 300));
                    JOptionPane.showMessageDialog(PanelBitacoraTutor.this, sp, "Retroalimentación completa",
                            JOptionPane.PLAIN_MESSAGE);
                });

                JMenuItem itemMod = new JMenuItem("Modificar", crearIconoLapiz());
                styleMenuItem(itemMod, new Color(30, 30, 30), false);
                itemMod.addActionListener(ev2 -> {
                    retroEnEdicion = originalObj;
                    txtComment.setText(textoFull);
                    lblAddComment.setText("Editando Comentario:");
                    txtComment.requestFocusInWindow();
                    JOptionPane.showMessageDialog(PanelBitacoraTutor.this,
                            "<html>Se cargó la retroalimentación para editar.<br>Puede modificar el texto del comentario.<br>Presione <b>Enviar Retroalimentación</b> para guardar los cambios,<br>o presione <b>Cancelar</b> para anular.</html>",
                            "Modo Modificar", JOptionPane.INFORMATION_MESSAGE);
                });

                JMenuItem itemDel = new JMenuItem("Eliminar", crearIconoBasura());
                styleMenuItem(itemDel, new Color(200, 30, 30), true);
                itemDel.addActionListener(ev2 -> {
                    boolean deleted = false;
                    if (isTutorItem) deleted = ctrl.eliminarRetroalimentacionTutor(idRetro, this);
                    else deleted = ctrl.eliminarRetroalimentacionAsesor(idRetro, this);
                    if (deleted)
                        cargar(bitacoraActual, estudianteActual, esFinalizadoActual);
                });

                popup.add(itemVer);
                if (!esFinalizadoActual
                        && !(bitacoraActual != null && "Cerrada".equalsIgnoreCase(bitacoraActual.getEstado()))) {
                    popup.addSeparator();
                    popup.add(itemMod);
                    popup.addSeparator();
                    popup.add(itemDel);
                }
                popup.show(btnMenu, btnMenu.getWidth() - popup.getPreferredSize().width, btnMenu.getHeight());
            });
            JPanel east = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            east.setOpaque(false);
            east.add(btnMenu);
            p.add(east, BorderLayout.EAST);
        } else {
            // Si no es mío, solo Ver
            JButton btnMenu = createDotsButton();
            btnMenu.addActionListener(e -> {
                JPopupMenu popup = createStyledPopup();
                JMenuItem itemVer = new JMenuItem("Ver", crearIconoOjo());
                styleMenuItem(itemVer, new Color(30, 30, 30), false);
                itemVer.addActionListener(ev2 -> {
                    JTextArea ta = new JTextArea(textoFull);
                    ta.setLineWrap(true);
                    ta.setWrapStyleWord(true);
                    ta.setEditable(false);
                    ta.setRows(6);
                    ta.setFont(new Font("SansSerif", Font.PLAIN, 14));
                    JScrollPane sp = new JScrollPane(ta);
                    sp.setPreferredSize(new Dimension(550, 300));
                    JOptionPane.showMessageDialog(PanelBitacoraTutor.this, sp, "Retroalimentación completa",
                            JOptionPane.PLAIN_MESSAGE);
                });
                popup.add(itemVer);
                popup.show(btnMenu, btnMenu.getWidth() - popup.getPreferredSize().width, btnMenu.getHeight());
            });
            JPanel east = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            east.setOpaque(false);
            east.add(btnMenu);
            p.add(east, BorderLayout.EAST);
        }

        return p;
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

    private JButton createRoundedButton(String text, Color bg, Color fg) {
        JButton b = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setBackground(bg);
        b.setForeground(fg);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setOpaque(false);
        b.setFont(new Font("SansSerif", Font.BOLD, 13));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private Component spacer(int h) {
        return Box.createRigidArea(new Dimension(0, h));
    }

    static class CleanScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            thumbColor = new Color(200, 200, 200);
            trackColor = new Color(242, 242, 242);
        }

        @Override
        protected JButton createDecreaseButton(int o) {
            return zero();
        }

        @Override
        protected JButton createIncreaseButton(int o) {
            return zero();
        }

        private JButton zero() {
            JButton b = new JButton();
            b.setPreferredSize(new Dimension(0, 0));
            return b;
        }
    }
}
