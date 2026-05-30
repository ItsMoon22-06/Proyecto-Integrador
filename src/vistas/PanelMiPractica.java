package vistas;

import controlador.BitacoraControlador;
import controlador.PracticaControlador;
import modelado.AsesorPedagogico;
import modelado.AsesorPedagogicoDAO;
import modelado.Bitacora;
import modelado.Estudiante;
import modelado.Practica;
import modelado.Tipopractica;
import modelado.TipopracticaDAO;
import modelado.TutorAcademico;
import modelado.TutorAcademicoDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

/**
 * Panel "Mi Práctica Pedagógica" del portal del Estudiante.
 * Usa el esquema real de BD: PRACTICA tiene NUMDOCESTUDIANTE (no IDPRACTICA en
 * Estudiante).
 */
public class PanelMiPractica extends JPanel {

    private static final Color BG = new Color(242, 242, 242);

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

        JLabel title = new JLabel("Mi Práctica Pedagógica");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(title);
        content.add(spacer(20));

        List<Practica> lista = practicaCtrl.listarPorEstudiante(estudiante.getNumDocumento());

        if (lista == null || lista.isEmpty()) {
            JLabel sin = new JLabel("No tiene prácticas asignadas.");
            sin.setFont(new Font("SansSerif", Font.ITALIC, 14));
            sin.setForeground(Color.GRAY);
            sin.setAlignmentX(Component.LEFT_ALIGNMENT);
            content.add(sin);
        } else {
            TipopracticaDAO tpDAO = new TipopracticaDAO();
            TutorAcademicoDAO tutorDAO = new TutorAcademicoDAO();
            AsesorPedagogicoDAO asesorDAO = new AsesorPedagogicoDAO();

            for (Practica p : lista) {
                boolean esFinalizado = "Finalizado".equalsIgnoreCase(p.getEstado());
                Color bgBlock = esFinalizado ? new Color(220, 220, 220) : new Color(232, 238, 246);
                Color bgBadge = esFinalizado ? new Color(200, 200, 200) : new Color(178, 235, 242);

                Tipopractica tp = p.getIdTipopractica() != null ? tpDAO.buscarPorId(p.getIdTipopractica()) : null;
                TutorAcademico tutor = p.getNumDocTutor() != null ? tutorDAO.buscarPorDocumento(p.getNumDocTutor())
                        : null;
                AsesorPedagogico asesor = p.getNumDocAsesor() != null
                        ? asesorDAO.buscarPorDocumento(p.getNumDocAsesor())
                        : null;

                String nombrePrac = tp != null ? tp.getNombre()
                        : (p.getIdTipopractica() != null ? p.getIdTipopractica() : p.getIdPractica());
                String strTutor = tutor != null ? tutor.getNombre() + " " + tutor.getApellido() : "—";
                String strHoras = tp != null ? String.valueOf(tp.getHorasRequeridas()) : "—";
                String strInst = p.getEntidad() != null ? p.getEntidad() : "—";
                String strAsesor = asesor != null ? asesor.getNombre() + " " + asesor.getApellido() : "—";
                String strSemestre = tp != null ? String.valueOf(tp.getNumSemestre()) : "—";
                String strFInicio = p.getFechaInicio() != null ? p.getFechaInicio().toString() : "—";
                String strFFinal = p.getFechaFinal() != null ? p.getFechaFinal().toString() : "—";
                String strEstado = p.getEstado() != null ? p.getEstado() : "—";

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
                JLabel statusLabel = new JLabel("Estado");

                JPanel badge = createRoundedPanel(15, bgBadge);
                badge.setBorder(new EmptyBorder(5, 15, 5, 15));
                JLabel lblEstadoBadge = new JLabel(strEstado);
                lblEstadoBadge.setFont(new Font("SansSerif", Font.PLAIN, 14));
                badge.add(lblEstadoBadge);

                statusPanel.add(statusLabel);
                statusPanel.add(badge);
                cardHeader.add(infoTitle, BorderLayout.WEST);
                cardHeader.add(statusPanel, BorderLayout.EAST);

                // Grid de datos
                JPanel gridInfo = new JPanel(new GridLayout(3, 3, 20, 30));
                gridInfo.setOpaque(false);
                gridInfo.setBorder(new EmptyBorder(30, 0, 30, 0));
                gridInfo.add(createInfoBlock("Nombre Práctica", new JLabel(nombrePrac), bgBlock));
                gridInfo.add(createInfoBlock("Tutor Asignado", new JLabel(strTutor), bgBlock));
                gridInfo.add(createInfoBlock("Horas de la práctica", new JLabel(strHoras), bgBlock));
                gridInfo.add(createInfoBlock("Institución", new JLabel(strInst), bgBlock));
                gridInfo.add(createInfoBlock("Asesor", new JLabel(strAsesor), bgBlock));
                gridInfo.add(createInfoBlock("Semestre", new JLabel(strSemestre), bgBlock));
                gridInfo.add(createInfoBlock("Fecha Inicio", new JLabel(strFInicio), bgBlock));
                gridInfo.add(createInfoBlock("Fecha Final", new JLabel(strFFinal), bgBlock));
                gridInfo.add(createInfoBlock("Código de práctica", new JLabel(p.getIdPractica()), bgBlock));

                // Footer: botón Entrar
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
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        Bitacora b = bitacoraCtrl.buscarPorEstudianteYPractica(estudiante.getNumDocumento(),
                                p.getIdPractica());
                        panelBitacora.cargar(b, p.getIdPractica(), esFinalizado);
                        cardLayout.show(parentPanel, "Bitacora");
                    }
                });

                cardFooter.add(btnEntrar);

                card.add(cardHeader, BorderLayout.NORTH);
                card.add(gridInfo, BorderLayout.CENTER);
                card.add(cardFooter, BorderLayout.SOUTH);
                content.add(card);
                content.add(spacer(20)); // Spacer between practices
            }
        }

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG);
        wrapper.add(content, BorderLayout.CENTER);

        JScrollPane scroll = new JScrollPane(wrapper);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel createInfoBlock(String tituloStr, JLabel valorLabel, Color bg) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);

        JLabel t = new JLabel(tituloStr);
        t.setFont(new Font("SansSerif", Font.BOLD, 13));
        t.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel inputBg = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.dispose();
            }
        };
        inputBg.setOpaque(false);
        inputBg.setLayout(new BorderLayout());
        valorLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        valorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        valorLabel.setBorder(new EmptyBorder(8, 12, 8, 12));
        inputBg.add(valorLabel, BorderLayout.CENTER);
        inputBg.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        inputBg.setPreferredSize(new Dimension(0, 36));
        inputBg.setMinimumSize(new Dimension(0, 36));
        inputBg.setAlignmentX(Component.CENTER_ALIGNMENT);

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
}
