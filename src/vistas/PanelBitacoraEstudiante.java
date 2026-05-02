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
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

/**
 * Bitácora del Estudiante (solo lectura para nota/observación).
 * Permite: ver retroalimentaciones, subir archivo evidencia, escribir descripción.
 * Lógica completamente conectada a BD.
 */
public class PanelBitacoraEstudiante extends JPanel {

    private static final Color BG   = new Color(242, 242, 242);
    private static final Color DARK = new Color(45, 45, 45);

    private final BitacoraControlador ctrl;
    private final Estudiante          estudiante;

    // Componentes dinámicos
    private JLabel      lblFecha;
    private JLabel      lblNota;
    private JLabel      lblObservacion;
    private JPanel      feedbackContainer;
    private JPanel      evidenciasContainer;
    private JTextArea   txtDesc;
    private JLabel      lblArchivoActual;
    private Bitacora    bitacoraActual;
    private String      idPracticaActual;
    private byte[]      archivoPendiente;
    private String      nombreArchivoPendiente;

    public PanelBitacoraEstudiante(CardLayout cardLayout, JPanel parentPanel,
                                   Estudiante estudiante, BitacoraControlador ctrl) {
        this.ctrl       = ctrl;
        this.estudiante = estudiante;

        setBackground(BG);
        setLayout(new BorderLayout());

        // ── Panel de contenido (vertical) ──
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(BG);
        content.setBorder(new EmptyBorder(25, 40, 30, 40));

        // Título
        JLabel lblTitulo = new JLabel("Bitácora");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 26));
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(lblTitulo);
        content.add(spacer(20));

        // ── Tarjeta: Fecha | Nota | Observación (solo lectura) ──
        JPanel infoCard = createRoundedPanel(20, Color.WHITE);
        infoCard.setLayout(new GridLayout(1, 3));
        infoCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        lblFecha     = new JLabel("—");
        lblNota      = new JLabel("—");
        lblObservacion = new JLabel("—");

        infoCard.add(createDataField("Fecha de creación", lblFecha));
        infoCard.add(createDataField("Nota Final", lblNota));
        infoCard.add(createDataField("Observación General", lblObservacion));
        content.add(infoCard);
        content.add(spacer(20));

        // ── Retroalimentación ──
        JLabel lblRetro = new JLabel("Retroalimentación:");
        lblRetro.setFont(new Font("SansSerif", Font.BOLD, 17));
        lblRetro.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(lblRetro);
        content.add(spacer(12));

        feedbackContainer = new JPanel();
        feedbackContainer.setLayout(new BoxLayout(feedbackContainer, BoxLayout.Y_AXIS));
        feedbackContainer.setOpaque(false);
        feedbackContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(feedbackContainer);
        content.add(spacer(25));

        // ── Evidencias Subidas ──
        JLabel lblEvidencias = new JLabel("Evidencias subidas:");
        lblEvidencias.setFont(new Font("SansSerif", Font.BOLD, 17));
        lblEvidencias.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(lblEvidencias);
        content.add(spacer(12));

        evidenciasContainer = new JPanel();
        evidenciasContainer.setLayout(new BoxLayout(evidenciasContainer, BoxLayout.Y_AXIS));
        evidenciasContainer.setOpaque(false);
        evidenciasContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(evidenciasContainer);
        content.add(spacer(25));

        // ── Subir Evidencias ──
        JPanel uploadCard = createRoundedPanel(20, Color.WHITE);
        uploadCard.setLayout(new GridBagLayout());
        uploadCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        uploadCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.insets = new Insets(4, 0, 4, 0);

        gbc.gridy = 0; uploadCard.add(createCloudIcon(), gbc);

        lblArchivoActual = new JLabel("Sin archivo subido");
        lblArchivoActual.setFont(new Font("SansSerif", Font.ITALIC, 12));
        lblArchivoActual.setForeground(Color.GRAY);
        gbc.gridy = 1; uploadCard.add(lblArchivoActual, gbc);

        JButton btnUpload = new JButton("Seleccionar archivo");
        btnUpload.setBackground(DARK);
        btnUpload.setForeground(Color.WHITE);
        btnUpload.setFocusPainted(false);
        btnUpload.setBorderPainted(false);
        btnUpload.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnUpload.setPreferredSize(new Dimension(200, 38));
        btnUpload.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnUpload.addActionListener(e -> seleccionarArchivo());
        gbc.gridy = 2; gbc.insets = new Insets(8, 0, 8, 0);
        uploadCard.add(btnUpload, gbc);

        content.add(uploadCard);
        content.add(spacer(20));

        // ── Descripción de lo que realizó ──
        JLabel lblDesc = new JLabel("Descripción de lo que realizó:");
        lblDesc.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblDesc.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(lblDesc);
        content.add(spacer(10));

        JPanel descCard = createRoundedPanel(15, Color.WHITE);
        descCard.setLayout(new BorderLayout());
        descCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtDesc = new JTextArea("Describa aquí las actividades realizadas...");
        txtDesc.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtDesc.setLineWrap(true);
        txtDesc.setWrapStyleWord(true);
        txtDesc.setOpaque(false);
        txtDesc.setBorder(new EmptyBorder(15, 20, 15, 20));
        txtDesc.setRows(5);
        descCard.add(txtDesc, BorderLayout.CENTER);
        content.add(descCard);
        content.add(spacer(12));

        JButton btnSubir = new JButton("Subir Evidencia y Descripción");
        btnSubir.setBackground(DARK);
        btnSubir.setForeground(Color.WHITE);
        btnSubir.setFocusPainted(false);
        btnSubir.setBorderPainted(false);
        btnSubir.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnSubir.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSubir.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnSubir.addActionListener(e -> subirEvidencia());
        content.add(btnSubir);
        content.add(spacer(20));

        // ── Botón Volver ──
        JButton btnVolver = new JButton("\u2190  Volver");
        btnVolver.setBackground(new Color(210, 210, 210));
        btnVolver.setBorderPainted(false);
        btnVolver.setFocusPainted(false);
        btnVolver.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btnVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVolver.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnVolver.addActionListener(e -> cardLayout.show(parentPanel, "MiPractica"));
        content.add(btnVolver);

        // ── Scroll vertical solamente ──
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG);
        wrapper.add(content, BorderLayout.CENTER);

        JScrollPane scroll = new JScrollPane(wrapper);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.getVerticalScrollBar().setUI(new CleanScrollUI());
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
        scroll.getVerticalScrollBar().setBackground(BG);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
    }

    /**
     * Carga los datos de la bitácora y refresca la UI.
     * Llamado desde PanelMiPractica cuando el estudiante hace clic en "Entrar".
     */
    public void cargar(Bitacora b, String idPractica) {
        this.bitacoraActual = b;
        this.idPracticaActual = idPractica;

        archivoPendiente = null;
        nombreArchivoPendiente = null;
        txtDesc.setText("");
        lblArchivoActual.setText("Sin archivo seleccionado");
        evidenciasContainer.removeAll();

        if (b == null) {
            lblFecha.setText("Sin bitácora");
            lblNota.setText("—");
            lblObservacion.setText("—");
            feedbackContainer.removeAll();
            JLabel sin = new JLabel("Sin evidencias. Sube una para iniciar tu bitácora.");
            sin.setFont(new Font("SansSerif", Font.ITALIC, 13));
            sin.setForeground(Color.GRAY);
            evidenciasContainer.add(sin);
        } else {
            lblFecha.setText(b.getFechaCreacion() != null ? b.getFechaCreacion().toString() : "—");
            double nota = b.getNotaFinal();
            lblNota.setText(nota > 0 ? String.valueOf(nota) : "Pendiente");
            String obs = b.getObservacionFinal();
            lblObservacion.setText(obs != null && !obs.isEmpty() ? obs : "Pendiente");

            // Evidencias
            List<Evidencia> evidencias = ctrl.listarEvidencias(b.getIdBitacora());
            if (evidencias.isEmpty()) {
                JLabel sin = new JLabel("No hay evidencias subidas.");
                sin.setFont(new Font("SansSerif", Font.ITALIC, 13));
                sin.setForeground(Color.GRAY);
                evidenciasContainer.add(sin);
            } else {
                for (Evidencia e : evidencias) {
                    JPanel p = createRoundedPanel(12, new Color(235, 235, 235));
                    p.setLayout(new BorderLayout());
                    p.setBorder(new EmptyBorder(10, 15, 10, 15));
                    p.setAlignmentX(Component.LEFT_ALIGNMENT);
                    String file = e.getNombreArchivo() != null ? e.getNombreArchivo() : "Sin archivo";
                    JLabel lTop = new JLabel("Archivo: " + file);
                    lTop.setFont(new Font("SansSerif", Font.BOLD, 12));
                    String desc = e.getDescripcion() != null ? e.getDescripcion() : "(Sin descripción)";
                    JLabel lDesc = new JLabel("<html>" + desc + "</html>");
                    lDesc.setFont(new Font("SansSerif", Font.PLAIN, 13));
                    p.add(lTop, BorderLayout.NORTH);
                    p.add(lDesc, BorderLayout.CENTER);
                    evidenciasContainer.add(p);
                    evidenciasContainer.add(spacer(8));
                }
            }

            // Retroalimentaciones
            feedbackContainer.removeAll();
            List<Retroalimentacion> retros = ctrl.listarRetroalimentaciones(b.getIdBitacora());
            if (retros.isEmpty()) {
                JLabel sinRetro = new JLabel("Aún no hay retroalimentaciones.");
                sinRetro.setFont(new Font("SansSerif", Font.ITALIC, 13));
                sinRetro.setForeground(Color.GRAY);
                sinRetro.setAlignmentX(Component.LEFT_ALIGNMENT);
                feedbackContainer.add(sinRetro);
            } else {
                for (Retroalimentacion r : retros) {
                    String autor = r.getNumDocTutor() != null ? "Tutor" : "Asesor";
                    String fecha = r.getFecha() != null ? r.getFecha().toString() : "";
                    feedbackContainer.add(createFeedbackItem(autor, fecha, r.getComentario()));
                    feedbackContainer.add(spacer(8));
                }
            }
        }

        evidenciasContainer.revalidate(); evidenciasContainer.repaint();
        feedbackContainer.revalidate(); feedbackContainer.repaint();
        revalidate(); repaint();
    }

    private void seleccionarArchivo() {
        JFileChooser chooser = new JFileChooser();
        int res = chooser.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            try {
                archivoPendiente = Files.readAllBytes(f.toPath());
                nombreArchivoPendiente = f.getName();
                lblArchivoActual.setText(nombreArchivoPendiente);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error leyendo archivo.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void subirEvidencia() {
        if (bitacoraActual == null) {
            boolean ok = ctrl.crearBitacora(estudiante.getNumDocumento(), idPracticaActual);
            if (!ok) return;
            bitacoraActual = ctrl.buscarPorEstudianteYPractica(estudiante.getNumDocumento(), idPracticaActual);
        }
        String desc = txtDesc.getText().trim();
        if (desc.isEmpty() && archivoPendiente == null) {
            JOptionPane.showMessageDialog(this, "Debe agregar una descripción o un archivo.");
            return;
        }
        boolean ok = ctrl.guardarEvidencia(bitacoraActual.getIdBitacora(), archivoPendiente, nombreArchivoPendiente, desc);
        if (ok) {
            cargar(bitacoraActual, idPracticaActual);
        }
    }

    // ── Helpers de UI ─────────────────────────────────────────────────────────

    private JPanel createDataField(String titulo, JLabel valorLabel) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(20, 30, 20, 30));
        JLabel t = new JLabel(titulo);
        t.setFont(new Font("SansSerif", Font.BOLD, 14));
        valorLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        valorLabel.setBorder(new EmptyBorder(5, 0, 0, 0));
        p.add(t);
        p.add(valorLabel);
        return p;
    }

    private JPanel createFeedbackItem(String autor, String fecha, String texto) {
        JPanel p = createRoundedPanel(15, new Color(238, 238, 238));
        p.setLayout(new BorderLayout());
        p.setBorder(new EmptyBorder(12, 25, 12, 25));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTop = new JLabel(autor + "   " + fecha);
        lblTop.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblTop.setForeground(Color.GRAY);

        JLabel lblTxt = new JLabel("<html>" + texto + "</html>");
        lblTxt.setFont(new Font("SansSerif", Font.PLAIN, 14));

        p.add(lblTop, BorderLayout.NORTH);
        p.add(lblTxt, BorderLayout.CENTER);
        return p;
    }

    private JPanel createCloudIcon() {
        JPanel icon = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(60, 60, 60));
                g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int cx = getWidth() / 2, cy = getHeight() / 2;
                java.awt.geom.Path2D cloud = new java.awt.geom.Path2D.Float();
                cloud.moveTo(cx + 15, cy + 5);
                cloud.curveTo(cx + 25, cy + 5, cx + 25, cy - 15, cx + 10, cy - 15);
                cloud.curveTo(cx + 10, cy - 30, cx - 15, cy - 30, cx - 15, cy - 10);
                cloud.curveTo(cx - 30, cy - 10, cx - 30, cy + 5, cx - 15, cy + 5);
                cloud.lineTo(cx - 6, cy + 5);
                cloud.moveTo(cx + 6, cy + 5);
                cloud.lineTo(cx + 15, cy + 5);
                g2.draw(cloud);
                g2.drawLine(cx, cy + 12, cx, cy - 8);
                g2.drawLine(cx, cy - 8, cx - 6, cy - 2);
                g2.drawLine(cx, cy - 8, cx + 6, cy - 2);
                g2.dispose();
            }
        };
        icon.setPreferredSize(new Dimension(80, 60));
        icon.setOpaque(false);
        return icon;
    }

    private JPanel createRoundedPanel(int r, Color bg) {
        return new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), r, r));
                g2.dispose();
            }
        };
    }

    private Component spacer(int h) { return Box.createRigidArea(new Dimension(0, h)); }

    static class CleanScrollUI extends BasicScrollBarUI {
        @Override protected void configureScrollBarColors() {
            thumbColor = new Color(200, 200, 200);
            trackColor = new Color(242, 242, 242);
        }
        @Override protected JButton createDecreaseButton(int o) { return zero(); }
        @Override protected JButton createIncreaseButton(int o) { return zero(); }
        private JButton zero() { JButton b = new JButton(); b.setPreferredSize(new Dimension(0,0)); return b; }
    }
}
