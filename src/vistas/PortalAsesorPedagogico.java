package vistas;

import controlador.AsesorControlador;
import controlador.BitacoraControlador;
import controlador.PracticaControlador;
import modelado.AsesorPedagogico;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class PortalAsesorPedagogico extends JFrame {

    private final JPanel mainContentPanel;
    private final CardLayout cardLayout;

    private boolean sidebarExpanded = true;
    private static final int SIDEBAR_EXPANDED = 260;
    private static final int SIDEBAR_COLLAPSED = 62;

    private static final String[] ICONOS = { "\uD83D\uDCD6", "\uD83D\uDC64" };
    private static final String[] TEXTOS = { "Práctica Académica", "Perfil" };
    private static final String[] CARDS = { "PracticaAcademica", "Perfil" };

    private JPanel sidebar;
    private JLabel lblSigep;
    private JLabel lblPortal;
    private JLabel lblLogout;
    private final List<JLabel> menuLabels = new ArrayList<>();

    private final AsesorControlador asesorCtrl;
    private final PracticaControlador practicaCtrl;
    private final BitacoraControlador bitacoraCtrl;
    private final AsesorPedagogico asesorActual;

    public PortalAsesorPedagogico(AsesorPedagogico asesor) {
        this.asesorActual = asesor;
        this.asesorCtrl = new AsesorControlador();
        this.practicaCtrl = new PracticaControlador();
        this.bitacoraCtrl = new BitacoraControlador();

        setTitle("SPP - Portal Asesor Pedagógico");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 650);
        setMinimumSize(new Dimension(700, 500));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        buildSidebar();

        // ── TOP BAR ──
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(242, 242, 242));
        topBar.setBorder(new EmptyBorder(8, 20, 8, 20));
        JPanel profilePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        profilePanel.setOpaque(false);
        JLabel nameLabel = new JLabel(asesor.getNombre() + " " + asesor.getApellido());
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        JLabel profileCircle = new JLabel("\uD83D\uDC64", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(60, 60, 60));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(40, 40, 40));
                g2.setStroke(new BasicStroke(2));
                g2.drawOval(1, 1, getWidth() - 2, getHeight() - 2);
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

        // ── CONTENIDO ──
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(new Color(242, 242, 242));

        // Bienvenida
        JPanel panelBienvenida = new JPanel(new GridBagLayout());
        panelBienvenida.setBackground(new Color(242, 242, 242));
        JPanel logoContainer = new JPanel(new BorderLayout());
        logoContainer.setOpaque(false);
        logoContainer.add(UIUtils.crearLogo(120, 60), BorderLayout.CENTER);
        JLabel mainTitle = new JLabel("SPP", SwingConstants.CENTER);
        mainTitle.setFont(new Font("SansSerif", Font.BOLD, 65));
        mainTitle.setForeground(new Color(50, 50, 50));
        logoContainer.add(mainTitle, BorderLayout.SOUTH);
        panelBienvenida.add(logoContainer);
        mainContentPanel.add(panelBienvenida, "Bienvenida");

        // Práctica con sub-CardLayout (esTutor=false → bitácora solo lectura, sin
        // cerrar)
        CardLayout practicaCardLayout = new CardLayout();
        JPanel practicaContainer = new JPanel(practicaCardLayout);
        practicaContainer.setBackground(new Color(242, 242, 242));

        PanelBitacoraTutor panelBitacora = new PanelBitacoraTutor(
                practicaCardLayout, practicaContainer, false,
                asesor.getNumDocumento(), bitacoraCtrl);
        PanelPracticasTutor panelLista = new PanelPracticasTutor(
                practicaCardLayout, practicaContainer, false,
                asesor.getNumDocumento(), practicaCtrl, bitacoraCtrl, panelBitacora);

        practicaContainer.add(panelLista, "LISTA");
        practicaContainer.add(panelBitacora, "BITACORA");
        practicaCardLayout.show(practicaContainer, "LISTA");
        mainContentPanel.add(practicaContainer, "PracticaAcademica");

        // Perfil
        mainContentPanel.add(new PanelPerfil(
                "Asesor Pedagógico",
                asesor.getNombre(), asesor.getApellido(),
                asesor.getTipoDocumento(), asesor.getNumDocumento(),
                asesor.getCorreoInst(), asesor.getEstado()), "Perfil");

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(topBar, BorderLayout.NORTH);
        rightPanel.add(mainContentPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.CENTER);

        cardLayout.show(mainContentPanel, "Bienvenida");
    }

    private void buildSidebar() {
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

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(12, 15, 12, 12));
        JPanel titles = new JPanel();
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        titles.setOpaque(false);
        lblSigep = new JLabel("SPP");
        lblSigep.setForeground(Color.WHITE);
        lblSigep.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblPortal = new JLabel("Portal Asesor Pedagógico");
        lblPortal.setForeground(Color.LIGHT_GRAY);
        lblPortal.setFont(new Font("SansSerif", Font.PLAIN, 12));
        titles.add(lblSigep);
        titles.add(lblPortal);

        JLabel toggle = new JLabel("\u2261");
        toggle.setFont(new Font("SansSerif", Font.BOLD, 26));
        toggle.setForeground(Color.WHITE);
        toggle.setCursor(new Cursor(Cursor.HAND_CURSOR));
        toggle.setVerticalAlignment(SwingConstants.CENTER);
        toggle.setHorizontalAlignment(SwingConstants.CENTER);
        header.add(titles, BorderLayout.CENTER);
        header.add(toggle, BorderLayout.EAST);
        sidebar.add(header, BorderLayout.NORTH);

        JPanel items = new JPanel();
        items.setLayout(new BoxLayout(items, BoxLayout.Y_AXIS));
        items.setOpaque(false);
        items.setBorder(new EmptyBorder(10, 0, 0, 0));
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
                    cardLayout.show(mainContentPanel, card);
                }
            });
            items.add(lbl);
            items.add(Box.createRigidArea(new Dimension(0, 5)));
        }
        sidebar.add(items, BorderLayout.CENTER);

        JPanel logoutPanel = new JPanel(new BorderLayout());
        logoutPanel.setOpaque(false);
        logoutPanel.setBorder(new EmptyBorder(10, 0, 20, 0));
        lblLogout = crearEtiquetaMenu("\uD83D\uDEAA", "Cerrar Sesión");
        lblLogout.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                LoginWindow lw = new LoginWindow();
                lw.setExtendedState(PortalAsesorPedagogico.this.getExtendedState());
                if (PortalAsesorPedagogico.this.getExtendedState() != JFrame.MAXIMIZED_BOTH) {
                    lw.setBounds(PortalAsesorPedagogico.this.getBounds());
                }
                new controlador.LoginControlador(lw);
                lw.setVisible(true);
                dispose();
            }
        });
        logoutPanel.add(lblLogout, BorderLayout.CENTER);
        sidebar.add(logoutPanel, BorderLayout.SOUTH);
        add(sidebar, BorderLayout.WEST);

        toggle.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                sidebarExpanded = !sidebarExpanded;
                actualizarSidebar();
            }
        });
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

    public AsesorControlador getAsesorCtrl() {
        return asesorCtrl;
    }

    public PracticaControlador getPracticaCtrl() {
        return practicaCtrl;
    }

    public BitacoraControlador getBitacoraCtrl() {
        return bitacoraCtrl;
    }

    public AsesorPedagogico getAsesorActual() {
        return asesorActual;
    }
}
