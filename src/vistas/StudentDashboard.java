package vistas;

import controlador.BitacoraControlador;
import controlador.EstudianteControlador;
import controlador.PracticaControlador;
import modelado.Estudiante;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Portal del Estudiante.
 * Diseño visual conservado. Lógica real conectada a BD.
 */
public class StudentDashboard extends JFrame {

    private JPanel     mainContentPanel;
    private CardLayout cardLayout;

    private final CardLayout practicaCardLayout = new CardLayout();
    private final JPanel     practicaContainer  = new JPanel(practicaCardLayout) {{
        setBackground(new Color(242, 242, 242));
    }};

    private boolean sidebarExpanded = true;
    private static final int SIDEBAR_EXPANDED  = 260;
    private static final int SIDEBAR_COLLAPSED = 62;

    private static final String[] ICONOS = { "\uD83D\uDCD6", "\uD83D\uDC64" };
    private static final String[] TEXTOS = { "Mi Práctica", "Perfil" };
    private static final String[] CARDS  = { "MiPractica",  "Perfil" };

    private JPanel     sidebar;
    private JLabel     lblSigep;
    private JLabel     lblPortal;
    private JLabel     lblLogout;
    private final List<JLabel> menuLabels = new ArrayList<>();

    private final EstudianteControlador estudianteCtrl;
    private final PracticaControlador   practicaCtrl;
    private final BitacoraControlador   bitacoraCtrl;
    private final Estudiante            estudianteActual;

    public StudentDashboard(Estudiante estudiante) {
        this.estudianteActual = estudiante;
        this.estudianteCtrl   = new EstudianteControlador();
        this.practicaCtrl     = new PracticaControlador();
        this.bitacoraCtrl     = new BitacoraControlador();

        setTitle("SIGEP - Portal Estudiante");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 650);
        setMinimumSize(new Dimension(700, 500));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ── SIDEBAR ──
        sidebar = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new GradientPaint(0, 0, Color.BLACK, 0, getHeight(), new Color(30, 30, 30)));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        sidebar.setPreferredSize(new Dimension(SIDEBAR_EXPANDED, getHeight()));

        JPanel sidebarHeader = new JPanel(new BorderLayout());
        sidebarHeader.setOpaque(false);
        sidebarHeader.setBorder(new EmptyBorder(12, 15, 12, 12));

        JPanel sidebarTitles = new JPanel();
        sidebarTitles.setLayout(new BoxLayout(sidebarTitles, BoxLayout.Y_AXIS));
        sidebarTitles.setOpaque(false);
        lblSigep = new JLabel("SIGEP");
        lblSigep.setForeground(Color.WHITE);
        lblSigep.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblPortal = new JLabel("Portal Estudiante");
        lblPortal.setForeground(Color.LIGHT_GRAY);
        lblPortal.setFont(new Font("SansSerif", Font.PLAIN, 12));
        sidebarTitles.add(lblSigep);
        sidebarTitles.add(lblPortal);

        JLabel menuToggle = new JLabel("\u2261");
        menuToggle.setFont(new Font("SansSerif", Font.BOLD, 26));
        menuToggle.setForeground(Color.WHITE);
        menuToggle.setCursor(new Cursor(Cursor.HAND_CURSOR));
        menuToggle.setVerticalAlignment(SwingConstants.CENTER);
        menuToggle.setHorizontalAlignment(SwingConstants.CENTER);
        sidebarHeader.add(sidebarTitles, BorderLayout.CENTER);
        sidebarHeader.add(menuToggle, BorderLayout.EAST);
        sidebar.add(sidebarHeader, BorderLayout.NORTH);

        JPanel menuItemsPanel = new JPanel();
        menuItemsPanel.setLayout(new BoxLayout(menuItemsPanel, BoxLayout.Y_AXIS));
        menuItemsPanel.setOpaque(false);
        menuItemsPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        for (int i = 0; i < ICONOS.length; i++) {
            JLabel lbl = crearEtiquetaMenu(ICONOS[i], TEXTOS[i]);
            menuLabels.add(lbl);
            final String card = CARDS[i];
            lbl.addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { lbl.setOpaque(true); lbl.setBackground(new Color(60,60,60)); lbl.repaint(); }
                @Override public void mouseExited(MouseEvent e)  { lbl.setOpaque(false); lbl.repaint(); }
                @Override public void mouseClicked(MouseEvent e) {
                    if (card.equals("MiPractica")) practicaCardLayout.show(practicaContainer, "MiPractica");
                    cardLayout.show(mainContentPanel, card);
                }
            });
            menuItemsPanel.add(lbl);
            menuItemsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        }
        sidebar.add(menuItemsPanel, BorderLayout.CENTER);

        JPanel logoutPanel = new JPanel(new BorderLayout());
        logoutPanel.setOpaque(false);
        logoutPanel.setBorder(new EmptyBorder(10, 0, 20, 0));
        lblLogout = crearEtiquetaMenu("\uD83D\uDEAA", "Cerrar Sesión");
        lblLogout.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                LoginWindow lw = new LoginWindow();
                lw.setExtendedState(StudentDashboard.this.getExtendedState());
                if (StudentDashboard.this.getExtendedState() != JFrame.MAXIMIZED_BOTH) {
                    lw.setBounds(StudentDashboard.this.getBounds());
                }
                new controlador.LoginControlador(lw);
                lw.setVisible(true);
                dispose();
            }
        });
        logoutPanel.add(lblLogout, BorderLayout.CENTER);
        sidebar.add(logoutPanel, BorderLayout.SOUTH);
        add(sidebar, BorderLayout.WEST);

        menuToggle.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { sidebarExpanded = !sidebarExpanded; actualizarSidebar(); }
        });

        // ── TOP BAR ──
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(242, 242, 242));
        topBar.setBorder(new EmptyBorder(8, 20, 8, 20));
        JPanel profilePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        profilePanel.setOpaque(false);
        JLabel studentNameLabel = new JLabel(estudiante.getNombre() + " " + estudiante.getApellido());
        studentNameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        JLabel profileCircle = new JLabel("\uD83D\uDC64", SwingConstants.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(60,60,60)); g2.fillOval(0,0,getWidth(),getHeight());
                g2.setColor(new Color(40,40,40)); g2.setStroke(new BasicStroke(2)); g2.drawOval(1,1,getWidth()-2,getHeight()-2);
                g2.dispose(); super.paintComponent(g);
            }
        };
        profileCircle.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        profileCircle.setForeground(Color.WHITE);
        profileCircle.setPreferredSize(new Dimension(36, 36));
        profileCircle.setOpaque(false);
        profilePanel.add(studentNameLabel);
        profilePanel.add(profileCircle);
        topBar.add(profilePanel, BorderLayout.EAST);

        // ── CONTENIDO ──
        cardLayout       = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(new Color(242, 242, 242));

        // Bienvenida
        JPanel panelBienvenida = new JPanel(new GridBagLayout());
        panelBienvenida.setBackground(new Color(242, 242, 242));
        JPanel logoContainer = new JPanel(new BorderLayout());
        logoContainer.setOpaque(false);
        logoContainer.add(UIUtils.crearLogo(120, 60), BorderLayout.CENTER);
        JLabel mainTitle = new JLabel("SIGEP", SwingConstants.CENTER);
        mainTitle.setFont(new Font("SansSerif", Font.BOLD, 70));
        mainTitle.setForeground(new Color(60, 60, 60));
        logoContainer.add(mainTitle, BorderLayout.SOUTH);
        panelBienvenida.add(logoContainer);
        mainContentPanel.add(panelBienvenida, "Bienvenida");

        // Mi Práctica con sub-CardLayout
        PanelBitacoraEstudiante panelBitacora = new PanelBitacoraEstudiante(
            practicaCardLayout, practicaContainer, estudiante, bitacoraCtrl);
        PanelMiPractica panelMiPractica = new PanelMiPractica(
            practicaCardLayout, practicaContainer,
            estudiante, practicaCtrl, bitacoraCtrl, panelBitacora);

        practicaContainer.add(panelMiPractica, "MiPractica");
        practicaContainer.add(panelBitacora,   "Bitacora");
        practicaCardLayout.show(practicaContainer, "MiPractica");
        mainContentPanel.add(practicaContainer, "MiPractica");

        // Perfil
        mainContentPanel.add(new PanelPerfil(
            "Estudiante",
            estudiante.getNombre(), estudiante.getApellido(),
            estudiante.getTipoDocumento(), estudiante.getNumDocumento(),
            estudiante.getCorreoInst(), estudiante.getEstado()
        ), "Perfil");

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(topBar, BorderLayout.NORTH);
        rightPanel.add(mainContentPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.CENTER);

        cardLayout.show(mainContentPanel, "Bienvenida");
    }

    private void actualizarSidebar() {
        if (sidebarExpanded) {
            sidebar.setPreferredSize(new Dimension(SIDEBAR_EXPANDED, getHeight()));
            lblSigep.setVisible(true); lblPortal.setVisible(true);
            for (int i = 0; i < menuLabels.size(); i++) {
                menuLabels.get(i).setText(ICONOS[i] + "   " + TEXTOS[i]);
                menuLabels.get(i).setBorder(new EmptyBorder(12, 25, 12, 20));
                menuLabels.get(i).setHorizontalAlignment(SwingConstants.LEFT);
            }
            lblLogout.setText("\uD83D\uDEAA   Cerrar Sesión");
            lblLogout.setBorder(new EmptyBorder(12, 25, 12, 20));
            lblLogout.setHorizontalAlignment(SwingConstants.LEFT);
        } else {
            sidebar.setPreferredSize(new Dimension(SIDEBAR_COLLAPSED, getHeight()));
            lblSigep.setVisible(false); lblPortal.setVisible(false);
            for (int i = 0; i < menuLabels.size(); i++) {
                menuLabels.get(i).setText(ICONOS[i]);
                menuLabels.get(i).setBorder(new EmptyBorder(12, 0, 12, 0));
                menuLabels.get(i).setHorizontalAlignment(SwingConstants.CENTER);
            }
            lblLogout.setText("\uD83D\uDEAA");
            lblLogout.setBorder(new EmptyBorder(12, 0, 12, 0));
            lblLogout.setHorizontalAlignment(SwingConstants.CENTER);
        }
        sidebar.revalidate(); sidebar.repaint(); revalidate(); repaint();
    }

    private JLabel crearEtiquetaMenu(String icono, String texto) {
        JLabel label = new JLabel(icono + "   " + texto);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("SansSerif", Font.PLAIN, 14));
        label.setBorder(new EmptyBorder(12, 25, 12, 20));
        label.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return label;
    }

    public EstudianteControlador getEstudianteCtrl()  { return estudianteCtrl; }
    public PracticaControlador   getPracticaCtrl()    { return practicaCtrl; }
    public BitacoraControlador   getBitacoraCtrl()    { return bitacoraCtrl; }
    public Estudiante            getEstudianteActual(){ return estudianteActual; }
}
