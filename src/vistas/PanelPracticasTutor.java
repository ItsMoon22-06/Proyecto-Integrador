package vistas;

import controlador.BitacoraControlador;
import controlador.PracticaControlador;
import modelado.Bitacora;
import modelado.Estudiante;
import modelado.EstudianteDAO;
import modelado.ConexionBD;
import modelado.Practica;
import modelado.Programa;
import modelado.ProgramaDAO;
import modelado.Tipopractica;
import modelado.TipopracticaDAO;
import modelado.TutorAcademico;
import modelado.TutorAcademicoDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class PanelPracticasTutor extends JPanel {

    private static final Color BG = new Color(242, 242, 242);

    private final boolean esTutor;
    private final String docRevisor;
    private final PracticaControlador practicaCtrl;
    private final BitacoraControlador bitacoraCtrl;
    private final PanelBitacoraTutor panelBitacora;
    
    private final CardLayout mainCardLayout;
    private final JPanel mainContainer;

    private final CardLayout internalCardLayout;
    private final JPanel internalContainer;

    private final JPanel practicesListPanel;
    private final JPanel studentsListPanel;

    public PanelPracticasTutor(CardLayout mainCardLayout, JPanel mainContainer,
            boolean esTutor, String docRevisor,
            PracticaControlador practicaCtrl,
            BitacoraControlador bitacoraCtrl,
            PanelBitacoraTutor panelBitacora) {
        this.esTutor = esTutor;
        this.docRevisor = docRevisor;
        this.practicaCtrl = practicaCtrl;
        this.bitacoraCtrl = bitacoraCtrl;
        this.panelBitacora = panelBitacora;
        this.mainCardLayout = mainCardLayout;
        this.mainContainer = mainContainer;

        setBackground(BG);
        setLayout(new BorderLayout());

        internalCardLayout = new CardLayout();
        internalContainer = new JPanel(internalCardLayout);
        internalContainer.setBackground(BG);

        // -- Vista 1: Lista de Prácticas --
        practicesListPanel = new JPanel();
        practicesListPanel.setLayout(new BoxLayout(practicesListPanel, BoxLayout.Y_AXIS));
        practicesListPanel.setBackground(BG);
        practicesListPanel.setBorder(new EmptyBorder(25, 40, 25, 40));

        JPanel wrapper1 = new JPanel(new BorderLayout());
        wrapper1.setBackground(BG);
        wrapper1.add(practicesListPanel, BorderLayout.NORTH);
        
        JScrollPane scroll1 = createScroll(wrapper1);

        // -- Vista 2: Lista de Estudiantes --
        studentsListPanel = new JPanel();
        studentsListPanel.setLayout(new BoxLayout(studentsListPanel, BoxLayout.Y_AXIS));
        studentsListPanel.setBackground(BG);
        studentsListPanel.setBorder(new EmptyBorder(25, 40, 25, 40));

        JPanel wrapper2 = new JPanel(new BorderLayout());
        wrapper2.setBackground(BG);
        wrapper2.add(studentsListPanel, BorderLayout.NORTH);

        JScrollPane scroll2 = createScroll(wrapper2);

        internalContainer.add(scroll1, "PRACTICAS");
        internalContainer.add(scroll2, "ESTUDIANTES");

        add(internalContainer, BorderLayout.CENTER);

        cargarPracticas();
    }

    private JScrollPane createScroll(JPanel p) {
        JScrollPane scroll = new JScrollPane(p);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.getVerticalScrollBar().setUI(new CleanScrollBarUI());
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
        scroll.getVerticalScrollBar().setBackground(BG);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    private void cargarPracticas() {
        practicesListPanel.removeAll();

        JLabel lblTitulo = new JLabel("Prácticas");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        practicesListPanel.add(lblTitulo);
        practicesListPanel.add(spacer(20));

        List<Practica> practicas;
        if (esTutor) {
            practicas = practicaCtrl.listarPorTutor(docRevisor);
        } else {
            practicas = practicaCtrl.listarTodas();
        }

        if (practicas == null || practicas.isEmpty()) {
            JLabel sin = new JLabel(esTutor ? "No tiene prácticas asignadas." : "No hay prácticas registradas.");
            sin.setFont(new Font("SansSerif", Font.ITALIC, 14));
            sin.setForeground(Color.GRAY);
            sin.setAlignmentX(Component.LEFT_ALIGNMENT);
            practicesListPanel.add(sin);
            practicesListPanel.revalidate();
            practicesListPanel.repaint();
            return;
        }

        ProgramaDAO progDAO = new ProgramaDAO(ConexionBD.getInstancia().getConexion());
        TipopracticaDAO tpDAO = new TipopracticaDAO(ConexionBD.getInstancia().getConexion());
        TutorAcademicoDAO tutorDAO = new TutorAcademicoDAO(ConexionBD.getInstancia().getConexion());

        for (Practica p : practicas) {
            Programa prog = p.getIdPrograma() != null ? progDAO.buscarPorId(p.getIdPrograma()) : null;
            Tipopractica tp = p.getIdTipopractica() != null ? tpDAO.buscarPorId(p.getIdTipopractica()) : null;
            TutorAcademico tutor = p.getNumDocTutor() != null ? tutorDAO.buscarPorDocumento(p.getNumDocTutor()) : null;

            String nombrePrac = tp != null ? tp.getNombre() : (p.getIdTipopractica() != null ? p.getIdTipopractica() : p.getIdPractica());
            String estado = p.getEstado() != null ? p.getEstado() : "—";
            
            // ── Tarjeta de información (estética idéntica a PanelMiPractica) ──
            JPanel card = createRoundedPanel(25, Color.WHITE);
            card.setLayout(new BorderLayout());
            card.setBorder(new EmptyBorder(25, 30, 25, 30));
            card.setAlignmentX(Component.LEFT_ALIGNMENT);

            // Header: título + badge estado
            JPanel cardHeader = new JPanel(new BorderLayout());
            cardHeader.setOpaque(false);
            JLabel infoTitle = new JLabel("Información de Práctica");
            infoTitle.setFont(new Font("SansSerif", Font.BOLD, 18));

            JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            statusPanel.setOpaque(false);
            JLabel statusLabel = new JLabel("Estado");
            statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));

            JPanel badge = createRoundedPanel(15, new Color(178, 235, 242));
            badge.setBorder(new EmptyBorder(5, 15, 5, 15));
            JLabel lblEstadoBadge = new JLabel(estado);
            lblEstadoBadge.setFont(new Font("SansSerif", Font.PLAIN, 14));
            badge.add(lblEstadoBadge);

            statusPanel.add(statusLabel);
            statusPanel.add(badge);
            cardHeader.add(infoTitle, BorderLayout.WEST);
            cardHeader.add(statusPanel, BorderLayout.EAST);

            // Grid 3x3
            JPanel gridInfo = new JPanel(new GridLayout(3, 3, 20, 30));
            gridInfo.setOpaque(false);
            gridInfo.setBorder(new EmptyBorder(30, 0, 30, 0));
            
            gridInfo.add(createFieldBlock("Nombre Práctica", new JLabel(nombrePrac)));
            gridInfo.add(createFieldBlock("Tutor Asignado", new JLabel(tutor != null ? tutor.getNombre() + " " + tutor.getApellido() : "—")));
            gridInfo.add(createFieldBlock("Horas de la práctica", new JLabel(tp != null ? String.valueOf(tp.getHorasRequeridas()) : "120")));
            gridInfo.add(createFieldBlock("Institución", new JLabel(p.getEntidad() != null ? p.getEntidad() : "—")));
            gridInfo.add(new JLabel("")); // celda vacía central
            gridInfo.add(createFieldBlock("Semestre", new JLabel(tp != null ? String.valueOf(tp.getNumSemestre()) : "—")));
            gridInfo.add(createFieldBlock("Nombre Programa", new JLabel(prog != null ? prog.getNombre() : (p.getIdPrograma() != null ? p.getIdPrograma() : "—"))));
            gridInfo.add(createFieldBlock("Fecha", new JLabel(p.getFecha() != null ? p.getFecha().toString() : "—")));
            gridInfo.add(createFieldBlock("Código de la práctica", new JLabel(p.getIdPractica())));

            // Footer con botón Entrar
            JPanel cardFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            cardFooter.setOpaque(false);

            JPanel btnEntrar = createRoundedPanel(10, Color.BLACK);
            btnEntrar.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 8));
            btnEntrar.setPreferredSize(new Dimension(110, 40));
            btnEntrar.setCursor(new Cursor(Cursor.HAND_CURSOR));

            JLabel entrarIcon = new JLabel("\u21A6");
            entrarIcon.setForeground(Color.WHITE);
            entrarIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
            JLabel entrarText = new JLabel("Entrar");
            entrarText.setForeground(Color.WHITE);
            entrarText.setFont(new Font("SansSerif", Font.PLAIN, 14));
            btnEntrar.add(entrarIcon);
            btnEntrar.add(entrarText);

            btnEntrar.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    mostrarEstudiantes(p);
                }
            });

            cardFooter.add(btnEntrar);

            card.add(cardHeader, BorderLayout.NORTH);
            card.add(gridInfo, BorderLayout.CENTER);
            card.add(cardFooter, BorderLayout.SOUTH);

            practicesListPanel.add(card);
            practicesListPanel.add(spacer(30));
        }

        practicesListPanel.revalidate();
        practicesListPanel.repaint();
    }

    private void mostrarEstudiantes(Practica p) {
        studentsListPanel.removeAll();

        // Botón Volver
        JButton btnVolver = new JButton("\u2190  Volver a prácticas");
        btnVolver.setBackground(new Color(210, 210, 210));
        btnVolver.setBorderPainted(false);
        btnVolver.setFocusPainted(false);
        btnVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVolver.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnVolver.setMaximumSize(new Dimension(180, 40));
        btnVolver.addActionListener(e -> internalCardLayout.show(internalContainer, "PRACTICAS"));

        studentsListPanel.add(btnVolver);
        studentsListPanel.add(spacer(20));

        // Título lista de estudiantes
        JLabel lblEstTitle = new JLabel("Estudiantes en la práctica " + p.getIdPractica());
        lblEstTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblEstTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        studentsListPanel.add(lblEstTitle);
        studentsListPanel.add(spacer(15));

        EstudianteDAO estDAO = new EstudianteDAO(ConexionBD.getInstancia().getConexion());
        List<Estudiante> estudiantes = estDAO.listarPorPractica(p.getIdPractica());
        
        if (estudiantes.isEmpty()) {
            JLabel sin = new JLabel("Sin estudiantes asignados.");
            sin.setFont(new Font("SansSerif", Font.ITALIC, 13));
            sin.setForeground(Color.GRAY);
            sin.setAlignmentX(Component.LEFT_ALIGNMENT);
            studentsListPanel.add(sin);
        } else {
            for (Estudiante est : estudiantes) {
                studentsListPanel.add(createStudentRow(est, p));
                studentsListPanel.add(spacer(10));
            }
        }

        studentsListPanel.revalidate();
        studentsListPanel.repaint();
        internalCardLayout.show(internalContainer, "ESTUDIANTES");
    }

    private JPanel createStudentRow(Estudiante est, Practica practica) {
        JPanel row = createRoundedPanel(15, Color.WHITE);
        row.setLayout(new BorderLayout());
        row.setBorder(new EmptyBorder(12, 20, 12, 20));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));
        row.setMinimumSize(new Dimension(100, 65));
        row.setPreferredSize(new Dimension(100, 65));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel lblLabel = new JLabel("Nombre Estudiante");
        lblLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        JLabel lblNombre = new JLabel(est.getNombre() + " " + est.getApellido());
        lblNombre.setFont(new Font("SansSerif", Font.PLAIN, 13));
        textPanel.add(lblLabel);
        textPanel.add(lblNombre);

        JButton btnIngresar = new JButton("Ver Bitácora");
        btnIngresar.setBackground(new Color(210, 210, 210));
        btnIngresar.setBorder(BorderFactory.createEmptyBorder(6, 18, 6, 18));
        btnIngresar.setFocusPainted(false);
        btnIngresar.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btnIngresar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnIngresar.addActionListener(e -> {
            Bitacora b = bitacoraCtrl.buscarPorEstudianteYPractica(est.getNumDocumento(), practica.getIdPractica());
            panelBitacora.cargar(b, est);
            mainCardLayout.show(mainContainer, "BITACORA");
        });

        row.add(textPanel, BorderLayout.CENTER);
        row.add(btnIngresar, BorderLayout.EAST);
        return row;
    }

    private JPanel createFieldBlock(String titulo, JLabel valorLabel) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);

        JLabel t = new JLabel(titulo);
        t.setFont(new Font("SansSerif", Font.BOLD, 13));

        JPanel inputBg = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(232, 238, 246));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
            }
        };
        inputBg.setOpaque(true);
        inputBg.setBackground(new Color(232, 238, 246));
        valorLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        valorLabel.setOpaque(false);
        valorLabel.setBorder(new EmptyBorder(8, 12, 8, 12));
        inputBg.add(valorLabel, BorderLayout.CENTER);
        inputBg.setPreferredSize(new Dimension(100, 36));
        inputBg.setMinimumSize(new Dimension(50, 36));
        inputBg.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        p.add(t);
        p.add(spacer(5));
        p.add(inputBg);
        return p;
    }

    private JPanel createRoundedPanel(int r, Color bg) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), r, r));
                g2.dispose();
            }
        };
        panel.setBackground(bg);
        return panel;
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
