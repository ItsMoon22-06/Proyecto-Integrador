package vistas;

import controlador.DirectorControlador;
import modelado.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

/**
 * Panel "Práctica" del Portal Director.
 * Lista todas las prácticas con acciones: Editar, Eliminar, Entrar (ver
 * estudiantes).
 */
public class PanelPracticaDirector extends JPanel {

    private static final Color BG = new Color(242, 242, 242);

    private final DirectorControlador ctrl;
    private final PortalDirector portal;
    private JPanel listPanel;
    private JScrollPane scrollPracticas;

    public PanelPracticaDirector(DirectorControlador ctrl, PortalDirector portal) {
        this.ctrl = ctrl;
        this.portal = portal;
        setBackground(BG);
        setLayout(new BorderLayout());
        construirUI();
    }

    private void construirUI() {
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(BG);
        content.setBorder(new EmptyBorder(25, 40, 40, 40));

        JLabel title = new JLabel("Prácticas");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        content.add(title, BorderLayout.NORTH);

        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(BG);
        listPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        scrollPracticas = new JScrollPane(listPanel);
        scrollPracticas.setBorder(null);
        scrollPracticas.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPracticas.getVerticalScrollBar().setUnitIncrement(16);
        content.add(scrollPracticas, BorderLayout.CENTER);

        add(content, BorderLayout.CENTER);
        cargarPracticas();
    }

    public void cargarPracticas() {
        listPanel.removeAll();
        List<Practica> practicas = ctrl.listarPracticas();

        if (practicas.isEmpty()) {
            JLabel sin = new JLabel("No hay prácticas registradas.");
            sin.setFont(new Font("SansSerif", Font.ITALIC, 14));
            sin.setForeground(Color.GRAY);
            sin.setBorder(new EmptyBorder(10, 0, 0, 0));
            listPanel.add(sin);
        } else {
            for (Practica p : practicas) {
                listPanel.add(crearTarjetaPractica(p));
                listPanel.add(Box.createRigidArea(new Dimension(0, 15)));
            }
        }
        listPanel.revalidate();
        listPanel.repaint();
    }

    public void cargarPracticasYScrollA(String idPractica) {
        cargarPracticas();
        SwingUtilities.invokeLater(() -> {
            if (scrollPracticas != null && idPractica != null) {
                for (Component c : listPanel.getComponents()) {
                    if (c instanceof JPanel && idPractica.equals(c.getName())) {
                        Rectangle bounds = c.getBounds();
                        listPanel.scrollRectToVisible(bounds);
                        break;
                    }
                }
            }
        });
    }

    private JPanel crearTarjetaPractica(Practica p) {
        // Obtener nombres relacionados
        TipopracticaDAO tpDAO = new TipopracticaDAO();
        TutorAcademicoDAO tutorDAO = new TutorAcademicoDAO();
        AsesorPedagogicoDAO asesorDAO = new AsesorPedagogicoDAO();

        Tipopractica tp = p.getIdTipopractica() != null ? tpDAO.buscarPorId(p.getIdTipopractica()) : null;
        TutorAcademico tutor = p.getNumDocTutor() != null ? tutorDAO.buscarPorDocumento(p.getNumDocTutor()) : null;
        AsesorPedagogico asesor = p.getNumDocAsesor() != null ? asesorDAO.buscarPorDocumento(p.getNumDocAsesor())
                : null;

        JPanel card = createRoundedPanel(25, Color.WHITE);
        card.setName(p.getIdPractica());
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(25, 30, 25, 30));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Encabezado: título + estado
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel lblTitulo = new JLabel("Información de Práctica");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        header.add(lblTitulo, BorderLayout.WEST);

        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightHeader.setOpaque(false);

        // Badge de estado
        boolean esFinalizado = "Finalizado".equalsIgnoreCase(p.getEstado());
        Color bgBlock = esFinalizado ? new Color(220, 220, 220) : new Color(232, 238, 246);
        Color bgBadge = esFinalizado ? new Color(200, 200, 200) : new Color(178, 235, 242);

        JPanel badge = createRoundedPanel(15, bgBadge);
        badge.setBorder(new EmptyBorder(5, 15, 5, 15));
        JLabel lblEstado = new JLabel(p.getEstado() != null ? p.getEstado() : "—");
        lblEstado.setFont(new Font("SansSerif", Font.PLAIN, 14));
        badge.add(lblEstado);

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        statusPanel.setOpaque(false);
        statusPanel.add(new JLabel("Estado"));
        statusPanel.add(badge);

        rightHeader.add(statusPanel);

        // Botón de 3 puntos
        JButton btnMenu = createDotsButton();
        btnMenu.addActionListener(e -> {
            JPopupMenu popup = createStyledPopup();

            JMenuItem itemEntrar = new JMenuItem("Entrar", crearIconoOjo());
            styleMenuItem(itemEntrar, new Color(30, 30, 30), false);
            itemEntrar.addActionListener(ev -> abrirEstudiantes(p));
            popup.add(itemEntrar);

            if (!esFinalizado) {
                popup.addSeparator();

                JMenuItem itemMod = new JMenuItem("Editar", crearIconoLapiz());
                styleMenuItem(itemMod, new Color(30, 30, 30), false);
                itemMod.addActionListener(ev -> portal.editarPractica(p));
                popup.add(itemMod);

                popup.addSeparator();

                JMenuItem itemDel = new JMenuItem("Eliminar", crearIconoBasura());
                styleMenuItem(itemDel, new Color(200, 30, 30), true);

                controlador.BitacoraControlador bCtrl = new controlador.BitacoraControlador();
                if (bCtrl.buscarPorPractica(p.getIdPractica()) != null) {
                    itemDel.setEnabled(false);
                    itemDel.setToolTipText("No se puede eliminar porque ya tiene una bitácora creada.");
                } else {
                    itemDel.addActionListener(ev -> {
                        int res = JOptionPane.showConfirmDialog(this, "¿Eliminar práctica?", "Confirmar",
                                JOptionPane.YES_NO_OPTION);
                        if (res == JOptionPane.YES_OPTION) {
                            boolean ok = ctrl.eliminarPractica(p.getIdPractica(), this);
                            if (ok)
                                cargarPracticas();
                        }
                    });
                }
                popup.add(itemDel);
            }
            popup.show(btnMenu, btnMenu.getWidth() - popup.getPreferredSize().width, btnMenu.getHeight());
        });

        rightHeader.add(btnMenu);
        header.add(rightHeader, BorderLayout.EAST);

        // Grid de datos
        JPanel grid = new JPanel(new GridLayout(3, 3, 20, 30));
        grid.setOpaque(false);
        grid.setBorder(new EmptyBorder(30, 0, 10, 0));

        String nombrePractica = tp != null ? tp.getNombre()
                : (p.getIdTipopractica() != null ? p.getIdTipopractica() : p.getIdPractica());
        String nombreTutor = tutor != null ? tutor.getNombre() + " " + tutor.getApellido()
                : (p.getNumDocTutor() != null ? p.getNumDocTutor() : "—");
        String nombreAsesor = asesor != null ? asesor.getNombre() + " " + asesor.getApellido()
                : (p.getNumDocAsesor() != null ? p.getNumDocAsesor() : "—");
        String horas = tp != null ? String.valueOf(tp.getHorasRequeridas()) : "—";
        String semestre = tp != null ? String.valueOf(tp.getNumSemestre()) : "—";

        grid.add(infoBlock("Nombre Práctica", nombrePractica, bgBlock));
        grid.add(infoBlock("Tutor Asignado", nombreTutor, bgBlock));
        grid.add(infoBlock("Horas de la práctica", horas, bgBlock));
        grid.add(infoBlock("Institución", p.getEntidad() != null ? p.getEntidad() : "—", bgBlock));
        grid.add(infoBlock("Asesor Asignado", nombreAsesor, bgBlock));
        grid.add(infoBlock("Semestre", semestre, bgBlock));
        grid.add(infoBlock("Fecha Inicio", p.getFechaInicio() != null ? p.getFechaInicio().toString() : "—", bgBlock));
        grid.add(infoBlock("Fecha Final", p.getFechaFinal() != null ? p.getFechaFinal().toString() : "—", bgBlock));
        grid.add(infoBlock("Código de práctica", p.getIdPractica(), bgBlock));

        card.add(header, BorderLayout.NORTH);
        card.add(grid, BorderLayout.CENTER);
        return card;
    }

    private void abrirEstudiantes(Practica p) {
        // Muestra info del estudiante asignado a la práctica
        EstudianteDAO estDAO = new EstudianteDAO();
        Estudiante est = p.getNumDocEstudiante() != null ? estDAO.buscarPorDocumento(p.getNumDocEstudiante()) : null;

        JDialog dlg = new JDialog(portal, "Práctica - Estudiante Asignado", true);
        dlg.setSize(400, 160);
        dlg.setLocationRelativeTo(portal);
        dlg.setBackground(Color.WHITE);
        dlg.setLayout(new BorderLayout());

        JPanel info = new JPanel(new GridLayout(2, 2, 10, 10));
        info.setBorder(new EmptyBorder(20, 25, 20, 25));
        info.setBackground(Color.WHITE);

        if (est != null) {
            info.add(new JLabel("Nombre:"));
            info.add(new JLabel(est.getNombre() + " " + est.getApellido()));
            info.add(new JLabel("Correo:"));
            info.add(new JLabel(est.getCorreoInst()));
        } else {
            info.add(new JLabel("Sin estudiante asignado a esta práctica."));
        }

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottom.setBackground(Color.WHITE);

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dlg.dispose());

        if (est != null) {
            JButton btnBitacora = new JButton("Ver Bitácora") {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(45, 45, 45));
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                    super.paintComponent(g);
                    g2.dispose();
                }
            };
            btnBitacora.setForeground(Color.WHITE);
            btnBitacora.setContentAreaFilled(false);
            btnBitacora.setBorderPainted(false);
            btnBitacora.setFocusPainted(false);
            btnBitacora.setFont(new Font("SansSerif", Font.BOLD, 13));
            btnBitacora.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnBitacora.addActionListener(e -> {
                controlador.BitacoraControlador bCtrl = new controlador.BitacoraControlador();
                modelado.Bitacora b = bCtrl.buscarPorPractica(p.getIdPractica());
                dlg.dispose();
                boolean esFinalizado = "Finalizado".equalsIgnoreCase(p.getEstado());
                portal.verBitacora(b, est, esFinalizado);
            });
            bottom.add(btnBitacora);
        }
        bottom.add(btnCerrar);

        dlg.add(info, BorderLayout.CENTER);
        dlg.add(bottom, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private JPanel infoBlock(String titulo, String valor, Color bg) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);

        JLabel t = new JLabel(titulo);
        t.setFont(new Font("SansSerif", Font.BOLD, 13));
        t.setAlignmentX(Component.CENTER_ALIGNMENT);

        String txt = (valor != null && !valor.isEmpty()) ? valor : "—";
        JLabel v = new JLabel(txt, SwingConstants.CENTER);
        v.setFont(new Font("SansSerif", Font.PLAIN, 13));
        v.setForeground(new Color(30, 30, 30));
        v.setBorder(new EmptyBorder(8, 12, 8, 12));

        JPanel inputBg = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.dispose();
            }
        };
        inputBg.setOpaque(false);
        inputBg.setAlignmentX(Component.CENTER_ALIGNMENT);
        inputBg.add(v, BorderLayout.CENTER);
        inputBg.setPreferredSize(new Dimension(0, 36));
        inputBg.setMinimumSize(new Dimension(0, 36));
        inputBg.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        p.add(t);
        p.add(Box.createRigidArea(new Dimension(0, 5)));
        p.add(inputBg);
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
}
