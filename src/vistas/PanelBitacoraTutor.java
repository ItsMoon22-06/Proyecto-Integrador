package vistas;

import controlador.BitacoraControlador;
import modelado.Bitacora;
import modelado.Estudiante;
import modelado.Evidencia;
import modelado.Retroalimentacion;

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

    private final boolean esTutor;
    private final String docRevisor;
    private final BitacoraControlador ctrl;

    // Componentes dinámicos
    private JLabel lblStudentName;
    private JLabel lblFecha;
    private JTextField txtNota;
    private JTextField txtObservacion;
    private JPanel evidenciasContainer;
    private JPanel retroContainer;
    private JTextField txtComment;
    private Bitacora bitacoraActual;
    @SuppressWarnings("unused")
    private Estudiante estudianteActual;

    public PanelBitacoraTutor(CardLayout cardLayout, JPanel container,
            boolean esTutor, String docRevisor, BitacoraControlador ctrl) {
        this.esTutor = esTutor;
        this.docRevisor = docRevisor;
        this.ctrl = ctrl;

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

        // ── Fecha / Nota / Observación ──
        JPanel infoCard = createRoundedPanel(15, Color.WHITE);
        infoCard.setBorder(new EmptyBorder(15, 20, 15, 20));
        infoCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        lblFecha = new JLabel("—");

        if (esTutor) {
            infoCard.setLayout(new GridLayout(1, 3, 20, 0));
            txtNota = new JTextField();
            txtObservacion = new JTextField();
            infoCard.add(createFieldStatic("Fecha de creación", lblFecha));
            infoCard.add(createFieldEditable("Nota Final (0-5)", txtNota));
            infoCard.add(createFieldEditable("Observación General", txtObservacion));
        } else {
            infoCard.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
            infoCard.add(createFieldStatic("Fecha de creación", lblFecha));
        }
        content.add(infoCard);
        content.add(spacer(12));

        // Botón cerrar bitácora (solo tutor)
        if (esTutor) {
            JButton btnCerrar = new JButton("Guardar y Cerrar Bitácora");
            btnCerrar.setBackground(COLOR_DARK);
            btnCerrar.setForeground(Color.WHITE);
            btnCerrar.setFocusPainted(false);
            btnCerrar.setBorderPainted(false);
            btnCerrar.setFont(new Font("SansSerif", Font.BOLD, 13));
            btnCerrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
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
        JPanel retroCard = createRoundedPanel(15, Color.WHITE);
        retroCard.setLayout(new BorderLayout(10, 0));
        retroCard.setBorder(new EmptyBorder(15, 20, 15, 20));
        retroCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        retroCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        retroCard.setMinimumSize(new Dimension(100, 80));

        JPanel commentLeft = new JPanel(new BorderLayout());
        commentLeft.setOpaque(false);
        JLabel lblAddComment = new JLabel("Añadir Comentario:");
        lblAddComment.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txtComment = new JTextField();
        txtComment.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txtComment.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_GRAY_IN),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        commentLeft.add(lblAddComment, BorderLayout.NORTH);
        commentLeft.add(txtComment, BorderLayout.CENTER);

        JButton btnSend = new JButton("Enviar");
        btnSend.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnSend.setBackground(COLOR_DARK);
        btnSend.setForeground(Color.WHITE);
        btnSend.setBorderPainted(false);
        btnSend.setFocusPainted(false);
        btnSend.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSend.setPreferredSize(new Dimension(80, 40));
        btnSend.addActionListener(e -> enviarComentario());

        retroCard.add(commentLeft, BorderLayout.CENTER);
        retroCard.add(btnSend, BorderLayout.EAST);
        content.add(retroCard);
        content.add(spacer(20));

        // ── Botón Volver ──
        JButton btnVolver = new JButton("\u2190  Volver");
        btnVolver.setBackground(new Color(210, 210, 210));
        btnVolver.setBorderPainted(false);
        btnVolver.setFocusPainted(false);
        btnVolver.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btnVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVolver.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnVolver.addActionListener(e -> cardLayout.show(container, "LISTA"));
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
    public void cargar(Bitacora b, Estudiante est) {
        this.bitacoraActual = b;
        this.estudianteActual = est;

        String nomEst = est != null ? est.getNombre() + " " + est.getApellido() : "—";
        lblStudentName.setText(nomEst);

        if (b == null) {
            lblFecha.setText("Sin bitácora");
            if (esTutor) {
                txtNota.setText("");
                txtObservacion.setText("");
            }
            evidenciasContainer.removeAll();
            retroContainer.removeAll();
        } else {
            lblFecha.setText(b.getFechaCreacion() != null ? b.getFechaCreacion().toString() : "—");

            if (esTutor) {
                txtNota.setText(b.getNotaFinal() > 0 ? String.valueOf(b.getNotaFinal()) : "");
                String obs = b.getObservacionFinal();
                txtObservacion.setText(obs != null ? obs : "");
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
                    JPanel p = createRoundedPanel(12, new Color(248, 248, 248));
                    p.setLayout(new BorderLayout());
                    p.setBorder(new EmptyBorder(10, 15, 10, 15));
                    p.setAlignmentX(Component.LEFT_ALIGNMENT);

                    JPanel headerRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
                    headerRow.setOpaque(false);
                    if (ev.getArchivo() != null && ev.getArchivo().length > 0) {
                        headerRow.add(createPDFIcon());
                        String fileName = ev.getNombreArchivo() != null ? ev.getNombreArchivo() : "archivo";
                        JButton btnDescargar = new JButton("Descargar " + fileName);
                        btnDescargar.setFont(new Font("SansSerif", Font.PLAIN, 12));
                        btnDescargar.setCursor(new Cursor(Cursor.HAND_CURSOR));
                        btnDescargar.addActionListener(e -> descargarArchivo(ev.getArchivo(), fileName));
                        headerRow.add(btnDescargar);
                    } else {
                        JLabel lSin = new JLabel("Sin archivo");
                        lSin.setFont(new Font("SansSerif", Font.BOLD, 12));
                        headerRow.add(lSin);
                    }

                    String desc = ev.getDescripcion() != null ? ev.getDescripcion() : "(Sin descripción)";
                    JLabel lDesc = new JLabel("<html>" + desc + "</html>");
                    lDesc.setFont(new Font("SansSerif", Font.PLAIN, 13));

                    p.add(headerRow, BorderLayout.NORTH);
                    p.add(spacer(5), BorderLayout.CENTER);
                    p.add(lDesc, BorderLayout.SOUTH);
                    evidenciasContainer.add(p);
                    evidenciasContainer.add(spacer(8));
                }
            }

            // Retroalimentaciones
            retroContainer.removeAll();
            List<Retroalimentacion> retros = ctrl.listarRetroalimentaciones(b.getIdBitacora());
            if (retros.isEmpty()) {
                JLabel sin = new JLabel("Sin retroalimentaciones aún.");
                sin.setFont(new Font("SansSerif", Font.ITALIC, 13));
                sin.setForeground(Color.GRAY);
                sin.setAlignmentX(Component.LEFT_ALIGNMENT);
                retroContainer.add(sin);
            } else {
                for (Retroalimentacion r : retros) {
                    String autor = r.getNumDocTutor() != null ? "Tutor" : "Asesor";
                    String fecha = r.getFecha() != null ? r.getFecha().toString() : "";
                    retroContainer.add(createRetroItem(autor, fecha, r.getComentario()));
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

    private void descargarArchivo(byte[] bytes, String defaultName) {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File(defaultName));
        int res = chooser.showSaveDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            try {
                java.nio.file.Files.write(f.toPath(), bytes);
                JOptionPane.showMessageDialog(this, "Archivo guardado exitosamente.", "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al guardar el archivo: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
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
        if (esTutor)
            ok = ctrl.agregarRetroalimentacionTutor(bitacoraActual.getIdBitacora(), texto, docRevisor);
        else
            ok = ctrl.agregarRetroalimentacionAsesor(bitacoraActual.getIdBitacora(), texto, docRevisor);
        if (ok) {
            txtComment.setText("");
            // Recargar retroalimentaciones
            retroContainer.removeAll();
            List<Retroalimentacion> retros = ctrl.listarRetroalimentaciones(bitacoraActual.getIdBitacora());
            for (Retroalimentacion r : retros) {
                String autor = r.getNumDocTutor() != null ? "Tutor" : "Asesor";
                String fecha = r.getFecha() != null ? r.getFecha().toString() : "";
                retroContainer.add(createRetroItem(autor, fecha, r.getComentario()));
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
        ctrl.cerrarBitacora(bitacoraActual.getIdBitacora(), nota, obs);
    }

    // ── Helpers de UI ─────────────────────────────────────────────────────────

    private JPanel createFieldStatic(String titulo, JLabel valorLabel) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(0, 0, 0, 20));
        JLabel t = new JLabel(titulo);
        t.setFont(new Font("SansSerif", Font.BOLD, 14));
        valorLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        valorLabel.setBorder(new EmptyBorder(5, 0, 0, 0));
        p.add(t);
        p.add(valorLabel);
        return p;
    }

    private JPanel createFieldEditable(String titulo, JTextField field) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(0, 0, 0, 20));
        JLabel t = new JLabel(titulo);
        t.setFont(new Font("SansSerif", Font.BOLD, 14));
        field.setFont(new Font("SansSerif", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_GRAY_IN),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        p.add(t);
        p.add(Box.createRigidArea(new Dimension(0, 5)));
        p.add(field);
        return p;
    }

    private JPanel createRetroItem(String autor, String fecha, String texto) {
        JPanel p = createRoundedPanel(12, new Color(238, 238, 238));
        p.setLayout(new BorderLayout());
        p.setBorder(new EmptyBorder(10, 20, 10, 20));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lblTop = new JLabel(autor + "   " + fecha);
        lblTop.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblTop.setForeground(Color.GRAY);
        JLabel lblTxt = new JLabel("<html>" + texto + "</html>");
        lblTxt.setFont(new Font("SansSerif", Font.PLAIN, 13));
        p.add(lblTop, BorderLayout.NORTH);
        p.add(lblTxt, BorderLayout.CENTER);
        return p;
    }

    private JPanel createPDFIcon() {
        JPanel icon = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int cx = getWidth() / 2, pageW = 36, pageH = 44;
                int px = cx - pageW / 2, py = 4;
                g2.setColor(new Color(110, 130, 160));
                g2.fillRoundRect(px, py, pageW, pageH, 6, 6);
                g2.setColor(new Color(200, 210, 230));
                for (int i = 0; i < 3; i++)
                    g2.fillRect(px + 6, py + 9 + i * 8, pageW - 12, 3);
                g2.setColor(new Color(40, 50, 70));
                g2.fillRect(px, py + pageH - 14, pageW, 14);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 8));
                g2.drawString("FILE", px + 8, py + pageH - 3);
                g2.dispose();
            }
        };
        icon.setPreferredSize(new Dimension(50, 56));
        icon.setOpaque(false);
        return icon;
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
