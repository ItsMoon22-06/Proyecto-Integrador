package vistas;

import controlador.BitacoraControlador;
import controlador.PracticaControlador;
import modelado.Bitacora;
import modelado.Estudiante;
import modelado.Practica;
import modelado.Programa;
import modelado.ProgramaDAO;
import modelado.Tipopractica;
import modelado.TipopracticaDAO;
import modelado.TutorAcademico;
import modelado.TutorAcademicoDAO;
import modelado.ConexionBD;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class PanelMiPractica extends JPanel {

    private static final Color BG = new Color(242, 242, 242);

    // Labels dinámicos
    private JLabel lblNombrePractica;
    private JLabel lblTutorAsignado;
    private JLabel lblHoras;
    private JLabel lblInstitucion;
    private JLabel lblSemestre;
    private JLabel lblPrograma;
    private JLabel lblFecha;
    private JLabel lblCodigo;
    private JLabel lblEstadoBadge;

    public PanelMiPractica(CardLayout cardLayout, JPanel parentPanel,
                            Estudiante estudiante,
                            PracticaControlador practicaCtrl,
                            BitacoraControlador bitacoraCtrl,
                            PanelBitacoraEstudiante panelBitacora) {
        setLayout(new BorderLayout());
        setBackground(BG);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(BG);
        content.setBorder(new EmptyBorder(25, 40, 40, 40));

        // Título
        JLabel title = new JLabel("Mi Práctica Pedagógica");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(title);
        content.add(spacer(20));

        // ── Tarjeta de información ──
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
        lblEstadoBadge = new JLabel("—");
        lblEstadoBadge.setFont(new Font("SansSerif", Font.PLAIN, 14));
        badge.add(lblEstadoBadge);

        statusPanel.add(statusLabel);
        statusPanel.add(badge);
        cardHeader.add(infoTitle, BorderLayout.WEST);
        cardHeader.add(statusPanel, BorderLayout.EAST);

        // Grid 3x3
        lblNombrePractica = new JLabel("—");
        lblTutorAsignado  = new JLabel("—");
        lblHoras          = new JLabel("—");
        lblInstitucion    = new JLabel("—");
        lblSemestre       = new JLabel("—");
        lblPrograma       = new JLabel("—");
        lblFecha          = new JLabel("—");
        lblCodigo         = new JLabel("—");

        JPanel gridInfo = new JPanel(new GridLayout(3, 3, 20, 30));
        gridInfo.setOpaque(false);
        gridInfo.setBorder(new EmptyBorder(30, 0, 30, 0));
        gridInfo.add(createInfoBlock("Nombre Práctica",      lblNombrePractica));
        gridInfo.add(createInfoBlock("Tutor Asignado",       lblTutorAsignado));
        gridInfo.add(createInfoBlock("Horas de la práctica", lblHoras));
        gridInfo.add(createInfoBlock("Institución",          lblInstitucion));
        gridInfo.add(new JLabel(""));
        gridInfo.add(createInfoBlock("Semestre",             lblSemestre));
        gridInfo.add(createInfoBlock("Nombre Programa",      lblPrograma));
        gridInfo.add(createInfoBlock("Fecha",                lblFecha));
        gridInfo.add(createInfoBlock("Código de la práctica",lblCodigo));

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
                List<Practica> practicas = practicaCtrl.listarPorEstudiante(estudiante.getIdPractica());
                Bitacora b = null;
                String idPractica = null;
                if (!practicas.isEmpty()) {
                    idPractica = practicas.get(0).getIdPractica();
                    b = bitacoraCtrl.buscarPorEstudianteYPractica(estudiante.getNumDocumento(), idPractica);
                }
                panelBitacora.cargar(b, idPractica);
                cardLayout.show(parentPanel, "Bitacora");
            }
        });

        cardFooter.add(btnEntrar);

        card.add(cardHeader, BorderLayout.NORTH);
        card.add(gridInfo, BorderLayout.CENTER);
        card.add(cardFooter, BorderLayout.SOUTH);

        content.add(card);

        // ── Scroll vertical solamente ──
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG);
        wrapper.add(content, BorderLayout.CENTER);

        JScrollPane scroll = new JScrollPane(wrapper);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        // Cargar datos reales
        cargarDatos(estudiante, practicaCtrl);
    }

    private void cargarDatos(Estudiante est, PracticaControlador ctrl) {
        List<Practica> lista = ctrl.listarPorEstudiante(est.getIdPractica());
        if (lista == null || lista.isEmpty()) {
            lblNombrePractica.setText("Sin práctica asignada");
            return;
        }
        Practica p = lista.get(0);

        ProgramaDAO progDAO = new ProgramaDAO(ConexionBD.getInstancia().getConexion());
        TipopracticaDAO tpDAO = new TipopracticaDAO(ConexionBD.getInstancia().getConexion());

        Programa prog = p.getIdPrograma() != null ? progDAO.buscarPorId(p.getIdPrograma()) : null;
        Tipopractica tp = p.getIdTipopractica() != null ? tpDAO.buscarPorId(p.getIdTipopractica()) : null;
        
        TutorAcademicoDAO tutorDAO = new TutorAcademicoDAO(ConexionBD.getInstancia().getConexion());
        TutorAcademico tutor = p.getNumDocTutor() != null ? tutorDAO.buscarPorDocumento(p.getNumDocTutor()) : null;

        lblNombrePractica.setText(tp != null ? tp.getNombre() : (p.getIdTipopractica() != null ? p.getIdTipopractica() : p.getIdPractica()));
        lblTutorAsignado.setText(tutor != null ? tutor.getNombre() + " " + tutor.getApellido() : "—");
        lblHoras.setText(tp != null ? String.valueOf(tp.getHorasRequeridas()) : "120");
        lblInstitucion.setText(p.getEntidad() != null ? p.getEntidad() : "—");
        lblSemestre.setText(tp != null ? String.valueOf(tp.getNumSemestre()) : "—");
        lblPrograma.setText(prog != null ? prog.getNombre() : (p.getIdPrograma() != null ? p.getIdPrograma() : "—"));
        lblFecha.setText(p.getFecha() != null ? p.getFecha().toString() : "—");
        lblCodigo.setText(p.getIdPractica());
        String estado = p.getEstado() != null ? p.getEstado() : "—";
        lblEstadoBadge.setText(estado);
    }

    private JPanel createInfoBlock(String tituloStr, JLabel valorLabel) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);

        JLabel t = new JLabel(tituloStr);
        t.setFont(new Font("SansSerif", Font.BOLD, 13));

        JPanel inputBg = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(232, 238, 246)); // Tono azul claro estético
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.dispose();
            }
        };
        inputBg.setOpaque(false);
        inputBg.setLayout(new BorderLayout());
        valorLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        valorLabel.setBorder(new EmptyBorder(8, 12, 8, 12));
        inputBg.add(valorLabel, BorderLayout.CENTER);
        inputBg.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        inputBg.setPreferredSize(new Dimension(0, 36));

        p.add(t);
        p.add(spacer(5));
        p.add(inputBg);
        return p;
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
}
