package vistas;

import controlador.BitacoraControlador;
import controlador.PracticaControlador;
import modelado.AsesorPedagogico;
import modelado.AsesorPedagogicoDAO;
import modelado.Bitacora;
import modelado.Estudiante;
import modelado.EstudianteDAO;
import modelado.Practica;
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

/**
 * Panel de prácticas para Tutor y Asesor.
 * Usa el esquema real: PRACTICA.NUMDOCESTUDIANTE, sin IDPROGRAMA en PRACTICA.
 */
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

        practicesListPanel = new JPanel();
        practicesListPanel.setLayout(new BoxLayout(practicesListPanel, BoxLayout.Y_AXIS));
        practicesListPanel.setBackground(BG);
        practicesListPanel.setBorder(new EmptyBorder(25, 40, 25, 40));

        studentsListPanel = new JPanel();
        studentsListPanel.setLayout(new BoxLayout(studentsListPanel, BoxLayout.Y_AXIS));
        studentsListPanel.setBackground(BG);
        studentsListPanel.setBorder(new EmptyBorder(25, 40, 25, 40));

        JPanel w1 = new JPanel(new BorderLayout());
        w1.setBackground(BG);
        w1.add(practicesListPanel, BorderLayout.NORTH);
        JPanel w2 = new JPanel(new BorderLayout());
        w2.setBackground(BG);
        w2.add(studentsListPanel, BorderLayout.NORTH);

        internalContainer.add(crearScroll(w1), "PRACTICAS");
        internalContainer.add(crearScroll(w2), "ESTUDIANTES");
        add(internalContainer, BorderLayout.CENTER);
        cargarPracticas();
    }

    private JScrollPane crearScroll(JPanel p) {
        JScrollPane s = new JScrollPane(p);
        s.setBorder(null);
        s.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        s.getVerticalScrollBar().setUI(new CleanScrollBarUI());
        s.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
        s.getVerticalScrollBar().setBackground(BG);
        s.getVerticalScrollBar().setUnitIncrement(16);
        return s;
    }

    private void cargarPracticas() {
        practicesListPanel.removeAll();

        JLabel lblTitulo = new JLabel("Prácticas");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        practicesListPanel.add(lblTitulo);
        practicesListPanel.add(spacer(20));

        List<Practica> practicas = esTutor
                ? practicaCtrl.listarPorTutor(docRevisor)
                : practicaCtrl.listarPorAsesor(docRevisor);

        if (practicas == null || practicas.isEmpty()) {
            JLabel sin = new JLabel("No tiene prácticas asignadas.");
            sin.setFont(new Font("SansSerif", Font.ITALIC, 14));
            sin.setForeground(Color.GRAY);
            sin.setAlignmentX(Component.LEFT_ALIGNMENT);
            practicesListPanel.add(sin);
            practicesListPanel.revalidate();
            practicesListPanel.repaint();
            return;
        }

        TipopracticaDAO tpDAO = new TipopracticaDAO();
        TutorAcademicoDAO tutorDAO = new TutorAcademicoDAO();
        AsesorPedagogicoDAO aDAO = new AsesorPedagogicoDAO();

        for (Practica p : practicas) {
            boolean esFinalizado = "Finalizado".equalsIgnoreCase(p.getEstado());
            Color bgBlock = esFinalizado ? new Color(220, 220, 220) : new Color(232, 238, 246);
            Color bgBadge = esFinalizado ? new Color(200, 200, 200) : new Color(178, 235, 242);

            Tipopractica tp = p.getIdTipopractica() != null ? tpDAO.buscarPorId(p.getIdTipopractica()) : null;
            TutorAcademico t = p.getNumDocTutor() != null ? tutorDAO.buscarPorDocumento(p.getNumDocTutor()) : null;
            AsesorPedagogico a = p.getNumDocAsesor() != null ? aDAO.buscarPorDocumento(p.getNumDocAsesor()) : null;

            String nombrePrac = tp != null ? tp.getNombre()
                    : (p.getIdTipopractica() != null ? p.getIdTipopractica() : p.getIdPractica());
            String estado = p.getEstado() != null ? p.getEstado() : "—";

            JPanel card = createRoundedPanel(25, Color.WHITE);
            card.setLayout(new BorderLayout());
            card.setBorder(new EmptyBorder(25, 30, 25, 30));
            card.setAlignmentX(Component.LEFT_ALIGNMENT);

            // Header
            JPanel cardHeader = new JPanel(new BorderLayout());
            cardHeader.setOpaque(false);
            JLabel infoTitle = new JLabel("Información de Práctica");
            infoTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
            JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            statusPanel.setOpaque(false);
            JPanel badge = createRoundedPanel(15, bgBadge);
            badge.setBorder(new EmptyBorder(5, 15, 5, 15));
            JLabel lblBadge = new JLabel(estado);
            lblBadge.setFont(new Font("SansSerif", Font.PLAIN, 14));
            badge.add(lblBadge);
            statusPanel.add(new JLabel("Estado"));
            statusPanel.add(badge);
            cardHeader.add(infoTitle, BorderLayout.WEST);
            cardHeader.add(statusPanel, BorderLayout.EAST);

            // Grid
            JPanel gridInfo = new JPanel(new GridLayout(3, 3, 20, 30));
            gridInfo.setOpaque(false);
            gridInfo.setBorder(new EmptyBorder(30, 0, 30, 0));
            gridInfo.add(createFieldBlock("Nombre Práctica", new JLabel(nombrePrac), bgBlock));
            gridInfo.add(createFieldBlock("Tutor Asignado",
                    new JLabel(t != null ? t.getNombre() + " " + t.getApellido() : "—"), bgBlock));
            gridInfo.add(createFieldBlock("Horas de la práctica",
                    new JLabel(tp != null ? String.valueOf(tp.getHorasRequeridas()) : "—"), bgBlock));
            gridInfo.add(createFieldBlock("Institución", new JLabel(p.getEntidad() != null ? p.getEntidad() : "—"),
                    bgBlock));
            gridInfo.add(createFieldBlock("Asesor Asignado",
                    new JLabel(a != null ? a.getNombre() + " " + a.getApellido() : "—"), bgBlock));
            gridInfo.add(createFieldBlock("Semestre",
                    new JLabel(tp != null ? String.valueOf(tp.getNumSemestre()) : "—"), bgBlock));
            gridInfo.add(createFieldBlock("Fecha Inicio",
                    new JLabel(p.getFechaInicio() != null ? p.getFechaInicio().toString() : "—"), bgBlock));
            gridInfo.add(createFieldBlock("Fecha Final",
                    new JLabel(p.getFechaFinal() != null ? p.getFechaFinal().toString() : "—"), bgBlock));
            gridInfo.add(createFieldBlock("Código de práctica", new JLabel(p.getIdPractica()), bgBlock));

            // Footer botón Entrar
            JPanel cardFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            cardFooter.setOpaque(false);
            JPanel btnEntrar = createRoundedPanel(10, Color.BLACK);
            btnEntrar.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 8));
            btnEntrar.setPreferredSize(new Dimension(110, 40));
            btnEntrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
            JLabel eIcon = new JLabel("\u21A6");
            eIcon.setForeground(Color.WHITE);
            eIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
            JLabel eTxt = new JLabel("Entrar");
            eTxt.setForeground(Color.WHITE);
            eTxt.setFont(new Font("SansSerif", Font.PLAIN, 14));
            btnEntrar.add(eIcon);
            btnEntrar.add(eTxt);
            btnEntrar.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    mostrarEstudiante(p, esFinalizado);
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

    private void mostrarEstudiante(Practica p, boolean esFinalizado) {
        studentsListPanel.removeAll();

        JButton btnVolver = new JButton("\u2190  Volver a prácticas");
        btnVolver.setBackground(new Color(210, 210, 210));
        btnVolver.setBorderPainted(false);
        btnVolver.setFocusPainted(false);
        btnVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVolver.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnVolver.setMaximumSize(new Dimension(200, 40));
        btnVolver.addActionListener(e -> internalCardLayout.show(internalContainer, "PRACTICAS"));
        studentsListPanel.add(btnVolver);
        studentsListPanel.add(spacer(20));

        JLabel lblTitle = new JLabel("Estudiante — Práctica " + p.getIdPractica());
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        studentsListPanel.add(lblTitle);
        studentsListPanel.add(spacer(15));

        // En el esquema real, cada práctica tiene UN solo estudiante (NUMDOCESTUDIANTE)
        EstudianteDAO estDAO = new EstudianteDAO();
        Estudiante est = p.getNumDocEstudiante() != null ? estDAO.buscarPorDocumento(p.getNumDocEstudiante()) : null;

        if (est == null) {
            JLabel sin = new JLabel("Sin estudiante asignado a esta práctica.");
            sin.setFont(new Font("SansSerif", Font.ITALIC, 13));
            sin.setForeground(Color.GRAY);
            sin.setAlignmentX(Component.LEFT_ALIGNMENT);
            studentsListPanel.add(sin);
        } else {
            studentsListPanel.add(createStudentRow(est, p, esFinalizado));
        }

        studentsListPanel.revalidate();
        studentsListPanel.repaint();
        internalCardLayout.show(internalContainer, "ESTUDIANTES");
    }

    private JPanel createStudentRow(Estudiante est, Practica practica, boolean esFinalizado) {
        JPanel row = createRoundedPanel(15, Color.WHITE);
        row.setLayout(new BorderLayout());
        row.setBorder(new EmptyBorder(12, 20, 12, 20));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        JLabel lblLabel = new JLabel("Nombre Estudiante");
        lblLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        JLabel lblNombre = new JLabel(est.getNombre() + " " + est.getApellido());
        lblNombre.setFont(new Font("SansSerif", Font.PLAIN, 13));
        textPanel.add(lblLabel);
        textPanel.add(lblNombre);

        JButton btnBitacora = new JButton("Ver Bitácora");
        btnBitacora.setBackground(new Color(210, 210, 210));
        btnBitacora.setBorderPainted(false);
        btnBitacora.setFocusPainted(false);
        btnBitacora.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBitacora.addActionListener(e -> {
            Bitacora b = bitacoraCtrl.buscarPorPractica(practica.getIdPractica());
            panelBitacora.cargar(b, est, esFinalizado);
            mainCardLayout.show(mainContainer, "BITACORA");
        });

        row.add(textPanel, BorderLayout.CENTER);
        row.add(btnBitacora, BorderLayout.EAST);
        return row;
    }

    private JPanel createFieldBlock(String titulo, JLabel valorLabel, Color bg) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        JLabel t = new JLabel(titulo);
        t.setFont(new Font("SansSerif", Font.BOLD, 13));
        t.setAlignmentX(Component.CENTER_ALIGNMENT);
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
        valorLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        valorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        valorLabel.setBorder(new EmptyBorder(8, 12, 8, 12));
        inputBg.add(valorLabel, BorderLayout.CENTER);
        inputBg.setPreferredSize(new Dimension(0, 36));
        inputBg.setMinimumSize(new Dimension(0, 36));
        inputBg.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        p.add(t);
        p.add(spacer(5));
        p.add(inputBg);
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
