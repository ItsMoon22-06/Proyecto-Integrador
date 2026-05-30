package vistas;

import controlador.DirectorControlador;
import modelado.Director;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Portal principal del Director del Programa.
 * Menú: Crear Práctica | Práctica | Gestionar usuarios | Perfil
 */
public class PortalDirector extends JFrame {

    private JPanel mainContentPanel;
    private CardLayout cardLayout;

    private boolean sidebarExpanded = true;
    private static final int SIDEBAR_EXPANDED = 260;
    private static final int SIDEBAR_COLLAPSED = 62;

    private static final String[] ICONOS = { "➕", "📖", "📊", "📋", "⚙", "👤" };
    private static final String[] TEXTOS = { "Crear Práctica", "Prácticas", "Informes", "Gestionar usuarios", "Gestión Tipo Práctica", "Perfil" };
    private static final String[] CARDS = { "CrearPractica", "Practicas", "Informes", "GestionarUsuarios", "GestionTipoPractica", "Perfil" };

    private JPanel sidebar;
    private JLabel lblSigep;
    private JLabel lblPortal;
    private JLabel lblLogout;
    private final List<JLabel> menuLabels = new ArrayList<>();

    private final DirectorControlador ctrl;

    // Paneles a los que necesitamos acceder desde afuera
    private PanelCrearPractica panelCrear;
    private PanelBitacoraTutor panelBitacora;
    private PanelPracticaDirector panelPractica;
    private PanelInformesDirector panelInformes;
    private PanelGestionTipoPractica panelTipoPractica;

    public PortalDirector(Director director) {
        this.ctrl = new DirectorControlador();

        setTitle("SPP - Portal Director");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 680);
        setMinimumSize(new Dimension(750, 520));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ── SIDEBAR ──────────────────────────────────────────────────────────
        sidebar = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
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
        lblSigep = new JLabel("SPP");
        lblSigep.setForeground(Color.WHITE);
        lblSigep.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblPortal = new JLabel("Portal Director");
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
                @Override
                public void mouseEntered(MouseEvent e) {
                    lbl.setOpaque(true);
                    lbl.setBackground(new Color(60, 60, 60));
                    lbl.repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    lbl.setOpaque(false);
                    lbl.repaint();
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    mostrarPanel(card);
                }
            });
            menuItemsPanel.add(lbl);
            menuItemsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        }
        sidebar.add(menuItemsPanel, BorderLayout.CENTER);

        // Cerrar Sesión
        JPanel logoutPanel = new JPanel(new BorderLayout());
        logoutPanel.setOpaque(false);
        logoutPanel.setBorder(new EmptyBorder(10, 0, 20, 0));
        lblLogout = crearEtiquetaMenu("\uD83D\uDEAA", "Cerrar Sesión");
        lblLogout.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                LoginWindow lw = new LoginWindow();
                lw.setExtendedState(PortalDirector.this.getExtendedState());
                if (PortalDirector.this.getExtendedState() != JFrame.MAXIMIZED_BOTH)
                    lw.setBounds(PortalDirector.this.getBounds());
                new controlador.LoginControlador(lw);
                lw.setVisible(true);
                dispose();
            }
        });
        logoutPanel.add(lblLogout, BorderLayout.CENTER);
        sidebar.add(logoutPanel, BorderLayout.SOUTH);
        add(sidebar, BorderLayout.WEST);

        menuToggle.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                sidebarExpanded = !sidebarExpanded;
                actualizarSidebar();
            }
        });

        // ── TOP BAR ──────────────────────────────────────────────────────────
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(242, 242, 242));
        topBar.setBorder(new EmptyBorder(8, 20, 8, 20));
        JPanel profilePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        profilePanel.setOpaque(false);
        JLabel nameLabel = new JLabel(director.getNombre() + " " + director.getApellido());
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        JLabel profileCircle = new JLabel("\uD83D\uDC64", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(60, 60, 60));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        profileCircle.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        profileCircle.setForeground(Color.WHITE);
        profileCircle.setPreferredSize(new Dimension(36, 36));
        profileCircle.setOpaque(false);
        profilePanel.add(nameLabel);
        profilePanel.add(profileCircle);
        topBar.add(profilePanel, BorderLayout.EAST);

        // ── CONTENIDO ─────────────────────────────────────────────────────────
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(new Color(242, 242, 242));

        // Splash
        JPanel splash = new JPanel(new GridBagLayout());
        splash.setBackground(new Color(242, 242, 242));
        JPanel logoC = new JPanel(new BorderLayout());
        logoC.setOpaque(false);
        logoC.add(UIUtils.crearLogo(120, 60), BorderLayout.CENTER);
        JLabel mainTitle = new JLabel("SPP", SwingConstants.CENTER);
        mainTitle.setFont(new Font("SansSerif", Font.BOLD, 70));
        mainTitle.setForeground(new Color(60, 60, 60));
        logoC.add(mainTitle, BorderLayout.SOUTH);
        splash.add(logoC);
        mainContentPanel.add(splash, "Splash");

        // Paneles del director
        panelCrear = new PanelCrearPractica(ctrl, this);
        panelPractica = new PanelPracticaDirector(ctrl, this);
        panelInformes = new PanelInformesDirector();
        PanelGestionarUsuarios panelUsuarios = new PanelGestionarUsuarios(ctrl, this);
        panelTipoPractica = new PanelGestionTipoPractica(ctrl, this);

        // El director usa la misma vista de bitácora que tutor/asesor, pero en modo
        // solo lectura total
        controlador.BitacoraControlador bitCtrl = new controlador.BitacoraControlador();
        panelBitacora = new PanelBitacoraTutor(cardLayout, mainContentPanel, false, "DIRECTOR_READ_ONLY", bitCtrl);

        mainContentPanel.add(panelCrear, "CrearPractica");
        mainContentPanel.add(panelPractica, "Practicas");
        mainContentPanel.add(panelInformes, "Informes");
        mainContentPanel.add(panelUsuarios, "GestionarUsuarios");
        mainContentPanel.add(panelTipoPractica, "GestionTipoPractica");
        mainContentPanel.add(panelBitacora, "Bitacora");
        mainContentPanel.add(new PanelPerfil(
                "Director",
                director.getNombre(), director.getApellido(),
                director.getTipoDocumento(), director.getNumDocumento(),
                director.getCorreoInst(), director.getEstado()), "Perfil");

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(topBar, BorderLayout.NORTH);
        rightPanel.add(mainContentPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.CENTER);

        cardLayout.show(mainContentPanel, "Splash");
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    public void mostrarPanel(String card) {
        if ("Practicas".equals(card) && panelPractica != null) {
            panelPractica.cargarPracticas();
        }
        if ("CrearPractica".equals(card) && panelCrear != null) {
            panelCrear.actualizarCombos();
        }
        if ("GestionTipoPractica".equals(card)) {
            panelTipoPractica.cargarLista();
        }
        cardLayout.show(mainContentPanel, card);
    }

    public void mostrarPracticaCreada(String idPractica) {
        if (panelPractica != null) {
            panelPractica.cargarPracticasYScrollA(idPractica);
        }
        cardLayout.show(mainContentPanel, "Practicas");
    }

    public void editarPractica(modelado.Practica p) {
        panelCrear.cargarEdicion(p);
        mostrarPanel("CrearPractica");
    }

    public void actualizarCombosCrearPractica() {
        if (panelCrear != null) {
            panelCrear.actualizarCombos();
        }
    }

    public void verBitacora(modelado.Bitacora b, modelado.Estudiante est,
            boolean esFinalizado) {
        panelBitacora.cargar(b, est, esFinalizado);
        mostrarPanel("Bitacora");
    }

    private void actualizarSidebar() {
        if (sidebarExpanded) {
            sidebar.setPreferredSize(new Dimension(SIDEBAR_EXPANDED, getHeight()));
            lblSigep.setVisible(true);
            lblPortal.setVisible(true);
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
            lblSigep.setVisible(false);
            lblPortal.setVisible(false);
            for (int i = 0; i < menuLabels.size(); i++) {
                menuLabels.get(i).setText(ICONOS[i]);
                menuLabels.get(i).setBorder(new EmptyBorder(12, 0, 12, 0));
                menuLabels.get(i).setHorizontalAlignment(SwingConstants.CENTER);
            }
            lblLogout.setText("\uD83D\uDEAA");
            lblLogout.setBorder(new EmptyBorder(12, 0, 12, 0));
            lblLogout.setHorizontalAlignment(SwingConstants.CENTER);
        }
        sidebar.revalidate();
        sidebar.repaint();
        revalidate();
        repaint();
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
}
