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
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

/**
 * Bitácora del Estudiante (solo lectura para nota/observación).
 * Permite: ver retroalimentaciones, subir archivo evidencia, escribir
 * descripción.
 * Lógica completamente conectada a BD.
 */
public class PanelBitacoraEstudiante extends JPanel {

    private static final Color BG = new Color(242, 242, 242);
    private static final Color DARK = new Color(45, 45, 45);

    private final BitacoraControlador ctrl;
    private final Estudiante estudiante;

    // Componentes dinámicos
    private JLabel lblFecha;
    private JLabel lblNota;
    private JLabel lblObservacion;
    private JLabel lblEstadoBitacora;
    private String observacionCompleta = "";
    private JPanel feedbackContainer;
    private JPanel evidenciasContainer;
    private JTextArea txtDesc;
    private JLabel lblArchivoActual;
    private Bitacora bitacoraActual;
    private String idPracticaActual;
    private byte[] archivoPendiente;
    private String nombreArchivoPendiente;
    private Evidencia evidenciaEnEdicion;
    private JButton btnCancelar;

    private boolean esFinalizadoActual;
    private JPanel uploadCard;
    private JLabel lblDesc;
    private JPanel descCard;
    private JButton btnSubir;

    public PanelBitacoraEstudiante(CardLayout cardLayout, JPanel parentPanel,
            Estudiante estudiante, BitacoraControlador ctrl) {
        this.ctrl = ctrl;
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

        // ── Tarjeta: Fecha | Nota | Observación | Estado (solo lectura) ──
        JPanel infoCard = createRoundedPanel(20, Color.WHITE);
        infoCard.setLayout(new GridLayout(1, 4));
        infoCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        lblFecha = new JLabel("—");
        lblNota = new JLabel("—");
        lblObservacion = new JLabel("—");
        lblEstadoBitacora = new JLabel("—");

        infoCard.add(createDataField("Fecha de creación", lblFecha));
        infoCard.add(createDataField("Nota Final", lblNota));
        infoCard.add(createDataFieldWithVer("Observación General", lblObservacion));
        infoCard.add(createDataField("Estado Bitácora", lblEstadoBitacora));
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
        uploadCard = createRoundedPanel(20, Color.WHITE);
        uploadCard.setLayout(new GridBagLayout());
        uploadCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        uploadCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(4, 0, 4, 0);

        gbc.gridy = 0;
        uploadCard.add(createCloudIcon(), gbc);

        lblArchivoActual = new JLabel("Sin archivo subido");
        lblArchivoActual.setFont(new Font("SansSerif", Font.ITALIC, 12));
        lblArchivoActual.setForeground(Color.GRAY);
        gbc.gridy = 1;
        uploadCard.add(lblArchivoActual, gbc);

        JButton btnUpload = createRoundedButton("Seleccionar archivo", DARK, Color.WHITE);
        btnUpload.setPreferredSize(new Dimension(200, 38));
        btnUpload.addActionListener(e -> seleccionarArchivo());
        gbc.gridy = 2;
        gbc.insets = new Insets(8, 0, 8, 0);
        uploadCard.add(btnUpload, gbc);

        content.add(uploadCard);
        content.add(spacer(20));

        // ── Descripción de lo que realizó ──
        lblDesc = new JLabel("Descripción de la evidencia:");
        lblDesc.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblDesc.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(lblDesc);
        content.add(spacer(10));

        descCard = createRoundedPanel(15, Color.WHITE);
        descCard.setLayout(new BorderLayout());
        descCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtDesc = new JTextArea();
        txtDesc.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtDesc.setLineWrap(true);
        txtDesc.setWrapStyleWord(true);
        txtDesc.setOpaque(false);
        txtDesc.setBorder(new EmptyBorder(15, 20, 15, 20));
        txtDesc.setRows(5);
        descCard.add(txtDesc, BorderLayout.CENTER);
        content.add(descCard);
        content.add(spacer(12));
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelBotones.setOpaque(false);
        panelBotones.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnSubir = createRoundedButton("Subir Evidencia", DARK, Color.WHITE);
        btnSubir.addActionListener(e -> subirEvidencia());
        panelBotones.add(btnSubir);

        btnCancelar = createRoundedButton("Cancelar", new Color(200, 50, 50), Color.WHITE);
        btnCancelar.addActionListener(e -> {
            evidenciaEnEdicion = null;
            archivoPendiente = null;
            nombreArchivoPendiente = null;
            lblArchivoActual.setText("Sin archivo subido");
            txtDesc.setText("");
            lblDesc.setText("Descripción de la evidencia:");
        });
        panelBotones.add(btnCancelar);

        content.add(panelBotones);
        content.add(spacer(20));

        // ── Botón Volver ──
        JButton btnVolver = createRoundedButton("\u2190  Volver", new Color(210, 210, 210), new Color(45, 45, 45));
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
    public void cargar(Bitacora b, String idPractica, boolean esFinalizado) {
        this.bitacoraActual = b;
        this.idPracticaActual = idPractica;
        this.esFinalizadoActual = esFinalizado;

        boolean isCerrada = (b != null && "Cerrada".equalsIgnoreCase(b.getEstado())) || esFinalizado;
        boolean esSoloLectura = esFinalizado || isCerrada;

        uploadCard.setVisible(!esSoloLectura);
        lblDesc.setVisible(!esSoloLectura);
        descCard.setVisible(!esSoloLectura);
        btnSubir.setVisible(!esSoloLectura);
        btnCancelar.setVisible(!esSoloLectura);

        archivoPendiente = null;
        nombreArchivoPendiente = null;
        txtDesc.setText("");
        lblArchivoActual.setText("Sin archivo seleccionado");
        lblDesc.setText("Descripción de la evidencia:");
        evidenciasContainer.removeAll();

        evidenciaEnEdicion = null;

        if (b == null) {
            lblFecha.setText("Sin bitácora");
            lblNota.setText("—");
            lblObservacion.setText("—");
            lblEstadoBitacora.setText("—");
            observacionCompleta = "";
            feedbackContainer.removeAll();
            JLabel sin = new JLabel("Sin evidencias. Sube una para iniciar tu bitácora.");
            sin.setFont(new Font("SansSerif", Font.ITALIC, 13));
            sin.setForeground(Color.GRAY);
            evidenciasContainer.add(sin);
        } else {
            lblFecha.setText(b.getFechaCreacion() != null ? b.getFechaCreacion().toString() : "—");
            double nota = b.getNotaFinal();
            String noDataText = isCerrada ? "—" : "Pendiente";
            lblNota.setText(nota > 0 ? String.valueOf(nota) : noDataText);
            String obs = b.getObservacionFinal();
            observacionCompleta = (obs != null && !obs.isEmpty()) ? obs : noDataText;
            lblObservacion.setText(truncar(observacionCompleta, 5));

            // Estado de la bitácora
            String estadoBit = isCerrada ? "Cerrada" : (b.getEstado() != null ? b.getEstado() : "Activa");
            lblEstadoBitacora.setText(estadoBit);
            if ("Cerrada".equalsIgnoreCase(estadoBit)) {
                lblEstadoBitacora.setForeground(new Color(200, 50, 50));
            } else {
                lblEstadoBitacora.setForeground(new Color(40, 150, 40));
            }

            // Evidencias
            List<Evidencia> evidencias = ctrl.listarEvidencias(b.getIdBitacora());
            if (evidencias.isEmpty()) {
                JLabel sin = new JLabel("No hay evidencias subidas.");
                sin.setFont(new Font("SansSerif", Font.ITALIC, 13));
                sin.setForeground(Color.GRAY);
                evidenciasContainer.add(sin);
            } else {
                for (Evidencia e : evidencias) {
                    evidenciasContainer.add(createEvidenciaItem(e));
                    evidenciasContainer.add(spacer(8));
                }
            }

            // Retroalimentaciones
            feedbackContainer.removeAll();
            List<RetroalimentacionTutor> retrosT = ctrl.listarRetroalimentacionesTutor(b.getIdBitacora());
            List<RetroalimentacionAsesor> retrosA = ctrl.listarRetroalimentacionesAsesor(b.getIdBitacora());
            if (retrosT.isEmpty() && retrosA.isEmpty()) {
                JLabel sinRetro = new JLabel("Aún no hay retroalimentaciones.");
                sinRetro.setFont(new Font("SansSerif", Font.ITALIC, 13));
                sinRetro.setForeground(Color.GRAY);
                sinRetro.setAlignmentX(Component.LEFT_ALIGNMENT);
                feedbackContainer.add(sinRetro);
            } else {
                for (RetroalimentacionTutor r : retrosT) {
                    String autor = "Tutor";
                    String fecha = r.getFecha() != null ? r.getFecha().toString() : "";
                    feedbackContainer.add(createFeedbackItem(autor, fecha, r.getComentario()));
                    feedbackContainer.add(spacer(8));
                }
                for (RetroalimentacionAsesor r : retrosA) {
                    String autor = "Asesor";
                    String fecha = r.getFecha() != null ? r.getFecha().toString() : "";
                    feedbackContainer.add(createFeedbackItem(autor, fecha, r.getComentario()));
                    feedbackContainer.add(spacer(8));
                }
            }
        }

        evidenciasContainer.revalidate();
        evidenciasContainer.repaint();
        feedbackContainer.revalidate();
        feedbackContainer.repaint();
        revalidate();
        repaint();
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
        String desc = txtDesc.getText().trim();

        if (archivoPendiente == null && desc.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ambos campos deben llenarse.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (archivoPendiente == null) {
            JOptionPane.showMessageDialog(this, "Falta el archivo.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (desc.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Falta la descripción.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (bitacoraActual == null) {
            boolean ok = ctrl.crearBitacora(estudiante.getNumDocumento(), idPracticaActual);
            if (!ok)
                return;
            bitacoraActual = ctrl.buscarPorPractica(idPracticaActual);
        }

        boolean ok;
        if (evidenciaEnEdicion != null) {
            ok = ctrl.modificarEvidencia(evidenciaEnEdicion.getIdEvidencia(), bitacoraActual.getIdBitacora(), archivoPendiente, nombreArchivoPendiente, desc);
            evidenciaEnEdicion = null;
        } else {
            ok = ctrl.guardarEvidencia(bitacoraActual.getIdBitacora(), archivoPendiente, nombreArchivoPendiente, desc);
        }

        if (ok) {
            cargar(bitacoraActual, idPracticaActual, esFinalizadoActual);
        }
    }

    // ── Helpers de UI ─────────────────────────────────────────────────────────

    private JPanel createEvidenciaItem(Evidencia ev) {
        JPanel p = createRoundedPanel(12, new Color(235, 235, 235));
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
                JOptionPane.showMessageDialog(PanelBitacoraEstudiante.this, panel, "Ver Evidencia",
                        JOptionPane.PLAIN_MESSAGE);
            });

            JMenuItem itemMod = new JMenuItem("Modificar", crearIconoLapiz());
            styleMenuItem(itemMod, new Color(30, 30, 30), false);
            itemMod.addActionListener(ev2 -> {
                evidenciaEnEdicion = ev;
                txtDesc.setText(ev.getDescripcion() != null ? ev.getDescripcion() : "");
                archivoPendiente = ev.getArchivo();
                nombreArchivoPendiente = ev.getNombreArchivo();
                lblArchivoActual.setText(ev.getNombreArchivo() != null ? ev.getNombreArchivo() : "Sin archivo");
                lblDesc.setText("Editando evidencia:");
                JOptionPane.showMessageDialog(PanelBitacoraEstudiante.this,
                        "<html>Se cargó la evidencia para editar.<br>Puede cambiar el archivo y/o la descripción.<br>Presione <b>Subir Evidencia</b> para guardar los cambios,<br>o presione <b>Cancelar</b> para anular.</html>",
                        "Modo Modificar", JOptionPane.INFORMATION_MESSAGE);
            });

            JMenuItem itemDel = new JMenuItem("Eliminar", crearIconoBasura());
            styleMenuItem(itemDel, new Color(200, 30, 30), true);
            itemDel.addActionListener(ev2 -> {
                int res = JOptionPane.showConfirmDialog(this, "¿Eliminar evidencia?", "Confirmar",
                        JOptionPane.YES_NO_OPTION);
                if (res == JOptionPane.YES_OPTION) {
                    ctrl.eliminarEvidencia(ev.getIdEvidencia(), this);
                    cargar(bitacoraActual, idPracticaActual, esFinalizadoActual);
                }
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

        p.add(textPanel, BorderLayout.CENTER);
        p.add(east, BorderLayout.EAST);
        return p;
    }

    private void descargarArchivo(String idEvidencia) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle("Seleccione la carpeta donde guardar el archivo original");
        int res = chooser.showSaveDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            File dir = chooser.getSelectedFile();
            if (ctrl.descargarEvidenciaBlob(idEvidencia, dir)) {
                JOptionPane.showMessageDialog(this, "Archivo original descargado y guardado exitosamente.", "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Error al descargar o guardar el archivo desde la BD.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
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

    private JPanel createDataField(String titulo, JLabel valorLabel) {
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

    private JPanel createDataFieldWithVer(String titulo, JLabel valorLabel) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(10, 5, 10, 5));

        JLabel t = new JLabel(titulo);
        t.setFont(new Font("SansSerif", Font.BOLD, 14));
        t.setAlignmentX(Component.CENTER_ALIGNMENT);

        valorLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(5, 0, 0, 0));
        row.add(valorLabel);

        JButton btnVer = new JButton("Ver");
        btnVer.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnVer.setForeground(new Color(60, 120, 220));
        btnVer.setContentAreaFilled(false);
        btnVer.setBorderPainted(false);
        btnVer.setFocusPainted(false);
        btnVer.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVer.addActionListener(e -> {
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
        });
        row.add(btnVer);

        p.add(t);
        p.add(row);
        return p;
    }

    private String truncar(String texto, int max) {
        if (texto == null)
            return "";
        return texto.length() > max ? texto.substring(0, max) + "..." : texto;
    }

    private JPanel createFeedbackItem(String autor, String fecha, String texto) {
        JPanel p = createRoundedPanel(15, new Color(238, 238, 238));
        p.setLayout(new BorderLayout());
        p.setBorder(new EmptyBorder(12, 25, 12, 25));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTop = new JLabel(autor + "   " + fecha);
        lblTop.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblTop.setForeground(Color.GRAY);

        String txtVis = texto.length() > 80 ? texto.substring(0, 80) + "..." : texto;
        JLabel lblTxt = new JLabel("<html>" + txtVis + "</html>");
        lblTxt.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblTxt.setToolTipText(texto);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.add(lblTop);
        center.add(lblTxt);
        p.add(center, BorderLayout.CENTER);

        // Tres puntos con opción "Ver" para leer el texto completo
        JButton btnMenu = createDotsButton();
        btnMenu.addActionListener(e -> {
            JPopupMenu popup = createStyledPopup();
            JMenuItem itemVer = new JMenuItem("Ver", crearIconoOjo());
            styleMenuItem(itemVer, new Color(30, 30, 30), false);
            itemVer.addActionListener(ev2 -> {
                JTextArea ta = new JTextArea(texto);
                ta.setLineWrap(true);
                ta.setWrapStyleWord(true);
                ta.setEditable(false);
                ta.setRows(6);
                ta.setFont(new Font("SansSerif", Font.PLAIN, 14));
                JScrollPane sp = new JScrollPane(ta);
                sp.setPreferredSize(new Dimension(550, 300));
                JOptionPane.showMessageDialog(PanelBitacoraEstudiante.this, sp,
                        "Retroalimentación completa", JOptionPane.PLAIN_MESSAGE);
            });
            popup.add(itemVer);
            popup.show(btnMenu, btnMenu.getWidth() - popup.getPreferredSize().width, btnMenu.getHeight());
        });
        JPanel east = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        east.setOpaque(false);
        east.add(btnMenu);
        p.add(east, BorderLayout.EAST);

        return p;
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

    private JPanel createCloudIcon() {
        JPanel icon = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
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

    static class CleanScrollUI extends BasicScrollBarUI {
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
