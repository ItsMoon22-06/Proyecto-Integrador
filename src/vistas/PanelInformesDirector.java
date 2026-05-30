package vistas;

import modelado.InformeDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Panel de Informes del Director.
 * Contiene 5 sub-informes navegables mediante tabs:
 * 1. Informe General de Prácticas
 * 2. Estudiantes por Tipo de Práctica
 * 3. Seguimiento de Bitácoras
 * 4. Retroalimentaciones
 * 5. Entidades Receptoras
 *
 * Diseño coherente con el sistema SPP: fondo #F2F2F2, tarjetas blancas
 * redondeadas, sidebar negro, tipografía SansSerif.
 */
public class PanelInformesDirector extends JPanel {

    // ── Paleta del proyecto ────────────────────────────────────────────────────
    private static final Color BG = new Color(242, 242, 242);
    private static final Color DARK = new Color(30, 30, 30);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color ACTIVE_BG = new Color(232, 238, 246); // azul suave badge
    private static final Color WARN_BG = new Color(255, 235, 205); // naranja suave
    private static final Color CHART_1 = new Color(60, 60, 60);
    private static final Color CHART_2 = new Color(100, 149, 237);
    private static final Color CHART_3 = new Color(102, 187, 106);
    private static final Color CHART_4 = new Color(255, 183, 77);
    private static final Color CHART_5 = new Color(239, 108, 78);

    private static final Color[] CHART_COLORS = { CHART_1, CHART_2, CHART_3, CHART_4, CHART_5,
            new Color(171, 71, 188), new Color(38, 198, 218) };

    private final InformeDAO dao;
    private JPanel contenido;
    private CardLayout cardInformes;
    private final List<JLabel> tabLabels = new ArrayList<>();
    private final String[] TAB_IDS = { "General", "TipoPractica", "Bitacoras", "Retro", "Entidades" };
    private final String[] TAB_NAMES = { "📊 General", "📋 Tipos", "📓 Bitácoras", "💬 Retro", "🏢 Entidades" };
    private int currentTabIndex = 0;

    public PanelInformesDirector() {
        this.dao = new InformeDAO();
        setBackground(BG);
        setLayout(new BorderLayout());
        construirUI();
    }

    // ── Construcción principal ─────────────────────────────────────────────────

    private void construirUI() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG);
        wrapper.setBorder(new EmptyBorder(25, 40, 40, 40));

        // Encabezado
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel title = new JLabel("Informes y Estadísticas");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(DARK);

        JLabel subtitle = new JLabel("Visión general del estado de las prácticas profesionales");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(new Color(110, 110, 110));

        JPanel titles = new JPanel();
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        titles.setOpaque(false);
        titles.add(title);
        titles.add(Box.createRigidArea(new Dimension(0, 4)));
        titles.add(subtitle);

        JButton btnActualizar = crearBotonAccion("⟳  Actualizar datos");
        btnActualizar.addActionListener(e -> recargarInformeActual());
        header.add(titles, BorderLayout.WEST);
        header.add(btnActualizar, BorderLayout.EAST);
        wrapper.add(header, BorderLayout.NORTH);

        // Tabs de navegación
        JPanel tabsPanel = construirTabs();
        wrapper.add(tabsPanel, BorderLayout.CENTER);

        // Contenido de cada tab
        cardInformes = new CardLayout();
        contenido = new JPanel(cardInformes);
        contenido.setBackground(BG);

        contenido.add(construirInformeGeneral(), "General");
        contenido.add(construirInformeTipos(), "TipoPractica");
        contenido.add(construirInformeBitacoras(), "Bitacoras");
        contenido.add(construirInformeRetro(), "Retro");
        contenido.add(construirInformeEntidades(), "Entidades");

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.add(tabsPanel, BorderLayout.NORTH);
        center.add(contenido, BorderLayout.CENTER);

        // Reemplazar el center en wrapper
        wrapper.remove(tabsPanel);
        wrapper.add(center, BorderLayout.CENTER);

        add(wrapper, BorderLayout.CENTER);
        seleccionarTab(0);
    }

    // ── Tabs ───────────────────────────────────────────────────────────────────

    private JPanel construirTabs() {
        JPanel tabs = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tabs.setOpaque(false);
        tabs.setBorder(new EmptyBorder(0, 0, 16, 0));

        for (int i = 0; i < TAB_NAMES.length; i++) {
            final int idx = i;
            JLabel tab = new JLabel(TAB_NAMES[i]);
            tab.setFont(new Font("SansSerif", Font.PLAIN, 13));
            tab.setForeground(new Color(100, 100, 100));
            tab.setBorder(new EmptyBorder(8, 18, 8, 18));
            tab.setCursor(new Cursor(Cursor.HAND_CURSOR));
            tab.setOpaque(true);
            tab.setBackground(BG);

            tab.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    seleccionarTab(idx);
                }

                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    if (!tab.getBackground().equals(Color.WHITE))
                        tab.setBackground(new Color(230, 230, 230));
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    if (!tab.getBackground().equals(Color.WHITE))
                        tab.setBackground(BG);
                }
            });

            tabLabels.add(tab);
            tabs.add(tab);
            if (i < TAB_NAMES.length - 1) {
                JLabel sep = new JLabel("|");
                sep.setForeground(new Color(200, 200, 200));
                sep.setBorder(new EmptyBorder(8, 2, 8, 2));
                tabs.add(sep);
            }
        }
        return tabs;
    }

    private void seleccionarTab(int idx) {
        this.currentTabIndex = idx;
        for (int i = 0; i < tabLabels.size(); i++) {
            JLabel t = tabLabels.get(i);
            if (i == idx) {
                t.setFont(new Font("SansSerif", Font.BOLD, 13));
                t.setForeground(DARK);
                t.setBackground(Color.WHITE);
                t.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 2, 0, DARK),
                        new EmptyBorder(8, 18, 6, 18)));
            } else {
                t.setFont(new Font("SansSerif", Font.PLAIN, 13));
                t.setForeground(new Color(100, 100, 100));
                t.setBackground(BG);
                t.setBorder(new EmptyBorder(8, 18, 8, 18));
            }
        }
        cardInformes.show(contenido, TAB_IDS[idx]);
    }

    private void recargarInformeActual() {
        // Reconstruir contenido
        contenido.removeAll();
        contenido.add(construirInformeGeneral(), "General");
        contenido.add(construirInformeTipos(), "TipoPractica");
        contenido.add(construirInformeBitacoras(), "Bitacoras");
        contenido.add(construirInformeRetro(), "Retro");
        contenido.add(construirInformeEntidades(), "Entidades");

        // Restaurar la tab activa
        cardInformes.show(contenido, TAB_IDS[currentTabIndex]);

        contenido.revalidate();
        contenido.repaint();
    }

    // ══════════════════════════════════════════════════════════════════════════
    // INFORME 1 – GENERAL
    // ══════════════════════════════════════════════════════════════════════════

    private JPanel construirInformeGeneral() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);

        JPanel scroll = new JPanel();
        scroll.setLayout(new BoxLayout(scroll, BoxLayout.Y_AXIS));
        scroll.setBackground(BG);
        scroll.setBorder(new EmptyBorder(4, 0, 20, 0));

        // ── KPIs ──
        Map<String, Integer> porEstado = dao.contarPorEstado();
        int total = dao.totalPracticas();
        int activas = porEstado.getOrDefault("Activo", porEstado.getOrDefault("ACTIVO", 0));
        int finalizadas = porEstado.getOrDefault("Finalizado", porEstado.getOrDefault("FINALIZADO", 0));
        int canceladas = total - activas - finalizadas;

        JPanel kpis = new JPanel(new GridLayout(1, 4, 16, 0));
        kpis.setOpaque(false);
        kpis.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        kpis.add(kpiCard("Total Prácticas", String.valueOf(total), "📋", new Color(232, 238, 246)));
        kpis.add(kpiCard("Activas", String.valueOf(activas), "✅", new Color(210, 240, 220)));
        kpis.add(kpiCard("Finalizadas", String.valueOf(finalizadas), "🏁", new Color(255, 245, 200)));
        kpis.add(kpiCard("Otras", String.valueOf(canceladas), "⚠️", new Color(255, 225, 215)));
        scroll.add(kpis);
        scroll.add(Box.createRigidArea(new Dimension(0, 20)));

        // ── Fila inferior: gráfico de estados + top entidades + top tutores ──
        JPanel fila2 = new JPanel(new GridLayout(1, 3, 16, 0));
        fila2.setOpaque(false);
        fila2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 320));

        // Gráfico de barras estados
        Map<String, Integer> estadosData = new LinkedHashMap<>();
        estadosData.put("Activas", activas);
        estadosData.put("Finalizadas", finalizadas);
        if (canceladas > 0)
            estadosData.put("Otras", canceladas);
        fila2.add(tarjetaConGrafico("Estado de Prácticas", estadosData,
                new Color[] { CHART_2, CHART_3, CHART_5 }));

        // Top 5 entidades
        List<Object[]> topEnt = dao.topEntidades(5);
        fila2.add(tarjetaRanking("🏢 Top Entidades", topEnt, total));

        // Top 5 tutores
        List<Object[]> topTut = dao.topTutores(5);
        fila2.add(tarjetaRanking("👤 Top Tutores", topTut, total));

        scroll.add(fila2);

        JScrollPane sp = new JScrollPane(scroll);
        sp.setBorder(null);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        root.add(sp, BorderLayout.CENTER);
        return root;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // INFORME 2 – TIPOS DE PRÁCTICA
    // ══════════════════════════════════════════════════════════════════════════

    private JPanel construirInformeTipos() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);

        JPanel scroll = new JPanel();
        scroll.setLayout(new BoxLayout(scroll, BoxLayout.Y_AXIS));
        scroll.setBackground(BG);
        scroll.setBorder(new EmptyBorder(4, 0, 20, 0));

        List<Object[]> tipos = dao.estudiantesPorTipo();
        int totalT = tipos.stream().mapToInt(r -> (int) r[1]).sum();

        // Tarjeta grande con gráfico de barras horizontales
        JPanel card = crearCard();
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(25, 30, 25, 30));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 380));

        JLabel tit = new JLabel("Distribución por Tipo de Práctica");
        tit.setFont(new Font("SansSerif", Font.BOLD, 16));
        card.add(tit, BorderLayout.NORTH);

        // Panel gráfico barras horizontales
        JPanel grafico = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (tipos.isEmpty())
                    return;
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int maxVal = tipos.stream().mapToInt(r -> (int) r[1]).max().orElse(1);
                int barH = 36;
                int gap = 18;
                int labelW = 200;
                int barMaxW = getWidth() - labelW - 80;
                int y = 20;
                for (int i = 0; i < tipos.size() && i < CHART_COLORS.length; i++) {
                    Object[] row = tipos.get(i);
                    String nombre = (String) row[0];
                    int val = (int) row[1];
                    int barW = maxVal > 0 ? (int) ((double) val / maxVal * barMaxW) : 0;
                    double pct = totalT > 0 ? (double) val / totalT * 100 : 0;
                    Color c = CHART_COLORS[i % CHART_COLORS.length];

                    // Etiqueta
                    g2.setFont(new Font("SansSerif", Font.PLAIN, 13));
                    g2.setColor(DARK);
                    String label = nombre.length() > 28 ? nombre.substring(0, 25) + "..." : nombre;
                    g2.drawString(label, 0, y + barH / 2 + 5);

                    // Barra
                    g2.setColor(c);
                    g2.fillRoundRect(labelW, y, Math.max(barW, 4), barH, 8, 8);

                    // Valor
                    g2.setColor(DARK);
                    g2.setFont(new Font("SansSerif", Font.BOLD, 12));
                    g2.drawString(val + "  (" + String.format("%.1f", pct) + "%)",
                            labelW + barW + 10, y + barH / 2 + 5);

                    y += barH + gap;
                }
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(0, tipos.size() * 54 + 40);
            }
        };
        grafico.setBackground(Color.WHITE);
        grafico.setBorder(new EmptyBorder(16, 0, 0, 0));

        JScrollPane sp2 = new JScrollPane(grafico);
        sp2.setBorder(null);
        sp2.getVerticalScrollBar().setUnitIncrement(12);
        card.add(sp2, BorderLayout.CENTER);

        scroll.add(card);
        scroll.add(Box.createRigidArea(new Dimension(0, 20)));

        // Tabla resumen
        JPanel tabla = crearTablaResumenTipos(tipos, totalT);
        scroll.add(tabla);

        JScrollPane sp = new JScrollPane(scroll);
        sp.setBorder(null);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        root.add(sp, BorderLayout.CENTER);
        return root;
    }

    private JPanel crearTablaResumenTipos(List<Object[]> tipos, int total) {
        JPanel card = crearCard();
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(25, 30, 25, 30));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));

        JLabel tit = new JLabel("Tabla Resumen");
        tit.setFont(new Font("SansSerif", Font.BOLD, 16));
        card.add(tit, BorderLayout.NORTH);

        String[] cols = { "Tipo de Práctica", "Estudiantes", "% Participación" };
        Object[][] data = new Object[tipos.size()][3];
        for (int i = 0; i < tipos.size(); i++) {
            Object[] r = tipos.get(i);
            double pct = total > 0 ? (double) (int) r[1] / total * 100 : 0;
            data[i][0] = r[0];
            data[i][1] = r[1];
            data[i][2] = String.format("%.1f%%", pct);
        }

        JTable table = new JTable(data, cols) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setRowHeight(32);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(245, 245, 245));
        table.setGridColor(new Color(230, 230, 230));
        table.setShowGrid(true);
        table.setBackground(Color.WHITE);
        table.setBorder(null);
        table.setSelectionBackground(ACTIVE_BG);

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(null);
        sp.setBorder(new EmptyBorder(12, 0, 0, 0));
        card.add(sp, BorderLayout.CENTER);
        return card;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // INFORME 3 – BITÁCORAS
    // ══════════════════════════════════════════════════════════════════════════

    private JPanel construirInformeBitacoras() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);

        JPanel scroll = new JPanel();
        scroll.setLayout(new BoxLayout(scroll, BoxLayout.Y_AXIS));
        scroll.setBackground(BG);
        scroll.setBorder(new EmptyBorder(4, 0, 20, 0));

        int totalBit = dao.totalBitacoras();
        double promEv = dao.promedioEvidencias();
        Map<String, Integer> bitEstados = dao.bitacorasPorEstado();
        List<Object[]> sinEvid = dao.practicasSinEvidencias();

        // KPIs
        JPanel kpis = new JPanel(new GridLayout(1, 3, 16, 0));
        kpis.setOpaque(false);
        kpis.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        kpis.add(kpiCard("Total Bitácoras", String.valueOf(totalBit), "📓", new Color(232, 238, 246)));
        kpis.add(kpiCard("Sin Evidencias", String.valueOf(sinEvid.size()), "⚠️", WARN_BG));
        kpis.add(kpiCard("Prom. Evidencias", String.format("%.1f", promEv), "📎", new Color(210, 240, 220)));
        scroll.add(kpis);
        scroll.add(Box.createRigidArea(new Dimension(0, 20)));

        // Gráfico estados de bitácoras
        JPanel fila2 = new JPanel(new GridLayout(1, 2, 16, 0));
        fila2.setOpaque(false);
        fila2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        fila2.add(tarjetaConGrafico("Estados de Bitácoras", bitEstados, CHART_COLORS));

        // Estudiantes sin actividad
        JPanel cardSin = crearCard();
        cardSin.setLayout(new BorderLayout());
        cardSin.setBorder(new EmptyBorder(25, 30, 25, 30));
        JLabel titSin = new JLabel("⚠️ Prácticas sin Evidencias  (" + sinEvid.size() + ")");
        titSin.setFont(new Font("SansSerif", Font.BOLD, 14));
        cardSin.add(titSin, BorderLayout.NORTH);
        if (sinEvid.isEmpty()) {
            JLabel ok = new JLabel("✅ Todos los estudiantes tienen evidencias registradas.");
            ok.setFont(new Font("SansSerif", Font.PLAIN, 13));
            ok.setForeground(new Color(60, 130, 80));
            ok.setBorder(new EmptyBorder(16, 0, 0, 0));
            cardSin.add(ok, BorderLayout.CENTER);
        } else {
            String[] cols = { "ID Práctica", "Entidad", "Estudiante" };
            Object[][] data = sinEvid.stream()
                    .map(r -> new Object[] { r[0], r[1], r[2] })
                    .toArray(Object[][]::new);
            cardSin.add(crearTablaSimple(data, cols), BorderLayout.CENTER);
        }
        fila2.add(cardSin);
        scroll.add(fila2);

        JScrollPane sp = new JScrollPane(scroll);
        sp.setBorder(null);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        root.add(sp, BorderLayout.CENTER);
        return root;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // INFORME 4 – RETROALIMENTACIONES
    // ══════════════════════════════════════════════════════════════════════════

    private JPanel construirInformeRetro() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);

        JPanel scroll = new JPanel();
        scroll.setLayout(new BoxLayout(scroll, BoxLayout.Y_AXIS));
        scroll.setBackground(BG);
        scroll.setBorder(new EmptyBorder(4, 0, 20, 0));

        int totalTutor = dao.totalRetroTutor();
        int totalAsesor = dao.totalRetroAsesor();
        List<Object[]> sinRetro = dao.bitacorasSinRetroTutor();
        List<Object[]> topTut = dao.topTutoresPorRetro(5);
        List<Object[]> topAse = dao.topAsesoresPorRetro(5);

        // KPIs
        JPanel kpis = new JPanel(new GridLayout(1, 3, 16, 0));
        kpis.setOpaque(false);
        kpis.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        kpis.add(kpiCard("Retro. Tutores", String.valueOf(totalTutor), "👨‍🏫", new Color(210, 240, 220)));
        kpis.add(kpiCard("Retro. Asesores", String.valueOf(totalAsesor), "👩‍💼", new Color(232, 238, 246)));
        kpis.add(kpiCard("Sin Retro. Tutor", String.valueOf(sinRetro.size()), "⚠️", WARN_BG));
        scroll.add(kpis);
        scroll.add(Box.createRigidArea(new Dimension(0, 20)));

        // Rankings
        JPanel fila2 = new JPanel(new GridLayout(1, 2, 16, 0));
        fila2.setOpaque(false);
        fila2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 290));
        fila2.add(tarjetaRanking("👨‍🏫 Tutores más activos", topTut, totalTutor));
        fila2.add(tarjetaRanking("👩‍💼 Asesores más activos", topAse, totalAsesor));
        scroll.add(fila2);
        scroll.add(Box.createRigidArea(new Dimension(0, 20)));

        // Tabla sin retroalimentación
        JPanel cardSin = crearCard();
        cardSin.setLayout(new BorderLayout());
        cardSin.setBorder(new EmptyBorder(25, 30, 25, 30));
        cardSin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 280));
        JLabel titSin = new JLabel("⚠️ Bitácoras sin Retroalimentación de Tutor  (" + sinRetro.size() + ")");
        titSin.setFont(new Font("SansSerif", Font.BOLD, 14));
        cardSin.add(titSin, BorderLayout.NORTH);
        if (sinRetro.isEmpty()) {
            JLabel ok = new JLabel("✅ Todos los tutores han dado retroalimentación.");
            ok.setFont(new Font("SansSerif", Font.PLAIN, 13));
            ok.setForeground(new Color(60, 130, 80));
            ok.setBorder(new EmptyBorder(16, 0, 0, 0));
            cardSin.add(ok, BorderLayout.CENTER);
        } else {
            String[] cols = { "ID Práctica", "Entidad", "Estudiante" };
            Object[][] data = sinRetro.stream()
                    .map(r -> new Object[] { r[0], r[1], r[2] })
                    .toArray(Object[][]::new);
            cardSin.add(crearTablaSimple(data, cols), BorderLayout.CENTER);
        }
        scroll.add(cardSin);

        JScrollPane sp = new JScrollPane(scroll);
        sp.setBorder(null);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        root.add(sp, BorderLayout.CENTER);
        return root;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // INFORME 5 – ENTIDADES RECEPTORAS
    // ══════════════════════════════════════════════════════════════════════════

    private JPanel construirInformeEntidades() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);

        JPanel scroll = new JPanel();
        scroll.setLayout(new BoxLayout(scroll, BoxLayout.Y_AXIS));
        scroll.setBackground(BG);
        scroll.setBorder(new EmptyBorder(4, 0, 20, 0));

        List<Object[]> entidades = dao.estadisticasEntidades();
        int totalEnt = dao.totalEntidades();
        int entActivas = (int) entidades.stream().filter(r -> (int) r[2] > 0).count();

        // KPIs
        JPanel kpis = new JPanel(new GridLayout(1, 3, 16, 0));
        kpis.setOpaque(false);
        kpis.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        kpis.add(kpiCard("Total Entidades", String.valueOf(totalEnt), "🏢", new Color(232, 238, 246)));
        kpis.add(kpiCard("Con Activos", String.valueOf(entActivas), "✅", new Color(210, 240, 220)));
        kpis.add(kpiCard("Solo Finalizadas", String.valueOf(totalEnt - entActivas), "🏁", WARN_BG));
        scroll.add(kpis);
        scroll.add(Box.createRigidArea(new Dimension(0, 20)));

        // Top entidades con gráfico
        Map<String, Integer> topData = new LinkedHashMap<>();
        entidades.stream().limit(6).forEach(r -> topData.put(
                ((String) r[0]).length() > 20 ? ((String) r[0]).substring(0, 18) + "…" : (String) r[0],
                (int) r[1]));
        JPanel card1 = tarjetaConGrafico("Entidades con Más Practicantes", topData, CHART_COLORS);
        card1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 340));
        scroll.add(card1);
        scroll.add(Box.createRigidArea(new Dimension(0, 20)));

        // Tabla completa de entidades
        JPanel cardTabla = crearCard();
        cardTabla.setLayout(new BorderLayout());
        cardTabla.setBorder(new EmptyBorder(25, 30, 25, 30));
        cardTabla.setMaximumSize(new Dimension(Integer.MAX_VALUE, 340));

        JLabel tit = new JLabel("Detalle de Entidades Receptoras");
        tit.setFont(new Font("SansSerif", Font.BOLD, 16));
        cardTabla.add(tit, BorderLayout.NORTH);

        String[] cols = { "Entidad", "Total", "Activos", "Finalizados" };
        Object[][] data = entidades.stream()
                .map(r -> new Object[] { r[0], r[1], r[2], r[3] })
                .toArray(Object[][]::new);
        cardTabla.add(crearTablaSimple(data, cols), BorderLayout.CENTER);
        scroll.add(cardTabla);

        JScrollPane sp = new JScrollPane(scroll);
        sp.setBorder(null);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        root.add(sp, BorderLayout.CENTER);
        return root;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // COMPONENTES REUTILIZABLES
    // ══════════════════════════════════════════════════════════════════════════

    /** Tarjeta KPI con icono, valor grande y etiqueta */
    private JPanel kpiCard(String label, String valor, String emoji, Color bgColor) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(20, 22, 20, 22));

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        JLabel emojiLabel = new JLabel(emoji);
        emojiLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        JPanel badge = new JPanel();
        badge.setBackground(bgColor);
        badge.setOpaque(true);
        badge.setLayout(new FlowLayout(FlowLayout.CENTER, 6, 4));
        badge.add(emojiLabel);
        badge.setPreferredSize(new Dimension(44, 44));
        topRow.add(badge, BorderLayout.WEST);

        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("SansSerif", Font.BOLD, 30));
        lblValor.setForeground(DARK);
        lblValor.setHorizontalAlignment(SwingConstants.RIGHT);
        topRow.add(lblValor, BorderLayout.EAST);

        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblLabel.setForeground(new Color(100, 100, 100));
        lblLabel.setBorder(new EmptyBorder(8, 0, 0, 0));

        card.add(topRow, BorderLayout.NORTH);
        card.add(lblLabel, BorderLayout.SOUTH);
        return card;
    }

    /** Tarjeta con gráfico de barras verticales */
    private JPanel tarjetaConGrafico(String titulo, Map<String, Integer> data, Color[] colors) {
        JPanel card = crearCard();
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(22, 25, 22, 25));

        JLabel tit = new JLabel(titulo);
        tit.setFont(new Font("SansSerif", Font.BOLD, 14));
        card.add(tit, BorderLayout.NORTH);

        int total = data.values().stream().mapToInt(Integer::intValue).sum();
        String[] keys = data.keySet().toArray(new String[0]);
        int[] vals = data.values().stream().mapToInt(Integer::intValue).toArray();

        JPanel grafico = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (keys.length == 0)
                    return;
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int n = keys.length;
                int w = getWidth();
                int h = getHeight() - 50;
                int maxV = Arrays.stream(vals).max().orElse(1);
                int barW = Math.max(20, (w - 40) / n - 10);
                int startX = (w - n * (barW + 10)) / 2;

                for (int i = 0; i < n; i++) {
                    int barH = maxV > 0 ? (int) ((double) vals[i] / maxV * h) : 0;
                    int x = startX + i * (barW + 10);
                    int y = h - barH + 10;
                    Color c = colors[i % colors.length];
                    g2.setColor(c);
                    g2.fillRoundRect(x, y, barW, barH, 8, 8);

                    // Valor encima
                    g2.setFont(new Font("SansSerif", Font.BOLD, 11));
                    g2.setColor(DARK);
                    String txt = String.valueOf(vals[i]);
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(txt, x + (barW - fm.stringWidth(txt)) / 2, y - 4);

                    // Etiqueta abajo
                    g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
                    g2.setColor(new Color(80, 80, 80));
                    String k = keys[i].length() > 10 ? keys[i].substring(0, 9) + "…" : keys[i];
                    int kw = g2.getFontMetrics().stringWidth(k);
                    g2.drawString(k, x + (barW - kw) / 2, h + 28);
                }

                // Línea base
                g2.setColor(new Color(220, 220, 220));
                g2.drawLine(0, h + 10, w, h + 10);
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(0, 220);
            }
        };
        grafico.setBackground(Color.WHITE);
        grafico.setBorder(new EmptyBorder(14, 0, 0, 0));
        card.add(grafico, BorderLayout.CENTER);

        // Leyenda pequeña abajo
        if (total > 0) {
            JLabel leyenda = new JLabel("Total: " + total);
            leyenda.setFont(new Font("SansSerif", Font.PLAIN, 11));
            leyenda.setForeground(new Color(120, 120, 120));
            leyenda.setHorizontalAlignment(SwingConstants.RIGHT);
            card.add(leyenda, BorderLayout.SOUTH);
        }
        return card;
    }

    /** Tarjeta de ranking con barra de progreso interna */
    private JPanel tarjetaRanking(String titulo, List<Object[]> items, int total) {
        JPanel card = crearCard();
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(22, 25, 22, 25));

        JLabel tit = new JLabel(titulo);
        tit.setFont(new Font("SansSerif", Font.BOLD, 14));
        card.add(tit, BorderLayout.NORTH);

        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setOpaque(false);
        list.setBorder(new EmptyBorder(12, 0, 0, 0));

        if (items.isEmpty()) {
            JLabel empty = new JLabel("Sin datos disponibles");
            empty.setFont(new Font("SansSerif", Font.ITALIC, 12));
            empty.setForeground(Color.GRAY);
            list.add(empty);
        } else {
            int maxV = items.stream().mapToInt(r -> (int) r[1]).max().orElse(1);
            for (int i = 0; i < items.size(); i++) {
                Object[] row = items.get(i);
                String nombre = (String) row[0];
                int val = (int) row[1];
                double pct = maxV > 0 ? (double) val / maxV : 0;
                Color barColor = CHART_COLORS[i % CHART_COLORS.length];

                JPanel item = new JPanel(new BorderLayout());
                item.setOpaque(false);
                item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));

                JPanel header = new JPanel(new BorderLayout());
                header.setOpaque(false);
                JLabel lNom = new JLabel("#" + (i + 1) + " " + nombre);
                lNom.setFont(new Font("SansSerif", Font.PLAIN, 12));
                JLabel lVal = new JLabel(val + "");
                lVal.setFont(new Font("SansSerif", Font.BOLD, 12));
                header.add(lNom, BorderLayout.WEST);
                header.add(lVal, BorderLayout.EAST);

                // Barra progreso
                final double pctF = pct;
                final Color bc = barColor;
                JPanel barra = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        Graphics2D g2 = (Graphics2D) g;
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(new Color(230, 230, 230));
                        g2.fillRoundRect(0, 2, getWidth(), getHeight() - 4, 6, 6);
                        g2.setColor(bc);
                        g2.fillRoundRect(0, 2, (int) (getWidth() * pctF), getHeight() - 4, 6, 6);
                    }
                };
                barra.setOpaque(false);
                barra.setPreferredSize(new Dimension(0, 10));

                item.add(header, BorderLayout.NORTH);
                item.add(barra, BorderLayout.SOUTH);
                list.add(item);
                list.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }
        card.add(list, BorderLayout.CENTER);
        return card;
    }

    /** Tabla estilizada simple */
    private JScrollPane crearTablaSimple(Object[][] data, String[] cols) {
        JTable table = new JTable(data, cols) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(245, 245, 245));
        table.getTableHeader().setForeground(DARK);
        table.setGridColor(new Color(235, 235, 235));
        table.setBackground(Color.WHITE);
        table.setSelectionBackground(ACTIVE_BG);
        table.setShowGrid(true);
        table.setBorder(null);

        // Alternar filas
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                if (!sel)
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(249, 249, 249));
                return c;
            }
        });

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(new EmptyBorder(12, 0, 0, 0));
        sp.getViewport().setBackground(Color.WHITE);
        return sp;
    }

    /** Panel blanco redondeado (tarjeta base) */
    private JPanel crearCard() {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Sombra suave
                g2.setColor(new Color(0, 0, 0, 18));
                g2.fillRoundRect(3, 5, getWidth() - 6, getHeight() - 6, 22, 22);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        p.setOpaque(false);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        return p;
    }

    /** Botón de acción estilo SPP */
    private JButton crearBotonAccion(String texto) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(70, 70, 70), 0, getHeight(),
                        new Color(20, 20, 20));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(170, 38));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
