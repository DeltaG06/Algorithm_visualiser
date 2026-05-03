package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class ControlPanel extends JPanel {

    public JComboBox<String> algorithmDropdown;
    public JButton startButton;
    public JButton pauseButton;
    public JButton stepBackButton;
    public JButton stepFwdButton;
    public JButton resetButton;
    public JButton randomButton;
    public JButton customButton;
    public JButton statsButton;
    public JSlider speedSlider;
    public JLabel speedLabel;

    // Accent colors
    private static final Color ACCENT_GREEN  = new Color(34, 197, 94);
    private static final Color ACCENT_ORANGE = new Color(251, 146, 60);
    private static final Color ACCENT_SLATE  = new Color(100, 116, 139);
    private static final Color ACCENT_BLUE   = new Color(59, 130, 246);
    private static final Color ACCENT_GOLD   = new Color(234, 179, 8);
    private static final Color ACCENT_PURPLE = new Color(168, 85, 247);
    private static final Color ACCENT_CYAN   = new Color(0, 210, 255);
    private static final Color ACCENT_PINK   = new Color(255, 42, 112);

    public ControlPanel() {
        setOpaque(false);
        setBorder(new EmptyBorder(10, 20, 10, 20));
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 4));

        // ===== ALGORITHM DROPDOWN =====
        String[] algorithms = {
            "\u2500 SORTING \u2500",
            "Bubble Sort",
            "Selection Sort",
            "Insertion Sort",
            "\u2500 DIVIDE & CONQUER \u2500",
            "Merge Sort",
            "Quick Sort",
            "\u2500 SEARCH \u2500",
            "Binary Search"
        };
        algorithmDropdown = new JComboBox<>(algorithms);
        algorithmDropdown.setSelectedItem("Bubble Sort");
        algorithmDropdown.setFont(new Font("Monospaced", Font.BOLD, 13));
        algorithmDropdown.setFocusable(false);
        algorithmDropdown.setPreferredSize(new Dimension(230, 34));
        
        // Track last valid selection to revert if user clicks a separator
        final String[] lastValid = {"Bubble Sort"};

        // Custom dark dropdown renderer
        algorithmDropdown.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton btn = new JButton() {
                    @Override
                    public void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g;
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(new Color(18, 18, 22));
                        g2.fillRect(0, 0, getWidth(), getHeight());
                        // Draw chevron
                        g2.setColor(ACCENT_PINK);
                        g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                        int cx = getWidth() / 2;
                        int cy = getHeight() / 2;
                        g2.drawLine(cx - 5, cy - 2, cx, cy + 3);
                        g2.drawLine(cx, cy + 3, cx + 5, cy - 2);
                    }
                };
                btn.setBorder(BorderFactory.createEmptyBorder());
                btn.setPreferredSize(new Dimension(30, 34));
                return btn;
            }

            @Override
            protected ComboPopup createPopup() {
                BasicComboPopup popup = (BasicComboPopup) super.createPopup();
                popup.getList().setBackground(new Color(18, 18, 22));
                popup.getList().setForeground(ACCENT_PINK);
                popup.getList().setSelectionBackground(new Color(255, 42, 112, 40));
                popup.getList().setSelectionForeground(ACCENT_PINK);
                popup.getList().setFont(new Font("Monospaced", Font.BOLD, 13));
                popup.setBorder(BorderFactory.createLineBorder(new Color(50, 50, 70)));
                return popup;
            }
        });

        algorithmDropdown.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                String text = (value != null) ? value.toString() : "";
                
                if (text.startsWith("\u2500")) {
                    // Category separator — smaller, dimmer, centered
                    label.setFont(new Font("Monospaced", Font.BOLD, 10));
                    label.setForeground(new Color(0, 210, 255, 180));
                    label.setBackground(new Color(12, 12, 16));
                    label.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(40, 40, 60)),
                        new EmptyBorder(5, 10, 5, 10)
                    ));
                    label.setHorizontalAlignment(SwingConstants.CENTER);
                } else {
                    // Normal algorithm item
                    label.setFont(new Font("Monospaced", Font.BOLD, 13));
                    label.setBorder(new EmptyBorder(6, 16, 6, 10));
                    label.setHorizontalAlignment(SwingConstants.LEFT);
                    if (isSelected) {
                        label.setBackground(new Color(255, 42, 112, 30));
                        label.setForeground(ACCENT_PINK);
                    } else {
                        label.setBackground(new Color(18, 18, 22));
                        label.setForeground(ACCENT_PINK);
                    }
                }
                return label;
            }
        });
        
        // Prevent selecting category separator items
        algorithmDropdown.addActionListener(e -> {
            String selected = (String) algorithmDropdown.getSelectedItem();
            if (selected != null && selected.startsWith("\u2500")) {
                algorithmDropdown.setSelectedItem(lastValid[0]);
            } else if (selected != null) {
                lastValid[0] = selected;
            }
        });

        algorithmDropdown.setBackground(new Color(18, 18, 22));
        algorithmDropdown.setForeground(ACCENT_PINK);
        algorithmDropdown.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(50, 50, 70), 1),
            BorderFactory.createEmptyBorder(2, 8, 2, 2)
        ));

        // ===== ANIMATED PILL BUTTONS =====
        startButton    = new GlowButton("START",  ACCENT_GREEN);
        pauseButton    = new GlowButton("PAUSE",  ACCENT_ORANGE);
        stepBackButton = new GlowButton("STEP \u25C0", ACCENT_CYAN); // Left arrow
        stepFwdButton  = new GlowButton("STEP \u25B6", ACCENT_CYAN); // Right arrow
        resetButton    = new GlowButton("RESET",  ACCENT_SLATE);
        randomButton   = new GlowButton("RANDOM", ACCENT_BLUE);
        customButton   = new GlowButton("CUSTOM", ACCENT_GOLD);
        statsButton    = new GlowButton("STATS",  ACCENT_PURPLE);

        pauseButton.setEnabled(false);
        stepBackButton.setEnabled(false);
        stepFwdButton.setEnabled(false);

        // ===== SPEED SLIDER =====
        JLabel sliderLabel = new JLabel("SPEED");
        sliderLabel.setForeground(new Color(120, 120, 150));
        sliderLabel.setFont(new Font("Monospaced", Font.BOLD, 11));

        speedSlider = new JSlider(JSlider.HORIZONTAL, 2, 2000, 300);
        speedSlider.setOpaque(false);
        speedSlider.setForeground(ACCENT_CYAN);
        speedSlider.setPreferredSize(new Dimension(120, 30));
        speedSlider.setFocusable(false);

        speedLabel = new JLabel("300ms");
        speedLabel.setForeground(ACCENT_CYAN);
        speedLabel.setFont(new Font("Monospaced", Font.PLAIN, 11));
        speedLabel.setPreferredSize(new Dimension(48, 22));

        speedSlider.addChangeListener(e -> {
            speedLabel.setText(speedSlider.getValue() + "ms");
        });

        // ===== ASSEMBLE =====
        add(algorithmDropdown);
        addGap(6);
        add(startButton);
        add(pauseButton);
        add(stepBackButton);
        add(stepFwdButton);
        add(resetButton);
        addGap(6);
        add(randomButton);
        add(customButton);
        add(statsButton);
        addGap(6);
        add(sliderLabel);
        add(speedSlider);
        add(speedLabel);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        int w = getWidth();
        int h = getHeight();

        // Gradient background
        GradientPaint gp = new GradientPaint(0, 0, new Color(12, 12, 15), 0, h, new Color(8, 8, 10));
        g2.setPaint(gp);
        g2.fillRect(0, 0, w, h);

        // Top border line (subtle)
        g2.setColor(new Color(40, 40, 55, 100));
        g2.drawLine(0, 0, w, 0);

        // Bottom glow line
        g2.setColor(new Color(40, 40, 55, 80));
        g2.drawLine(0, h - 1, w, h - 1);

        g2.dispose();
    }

    private void addGap(int width) {
        JPanel gap = new JPanel();
        gap.setOpaque(false);
        gap.setPreferredSize(new Dimension(width, 1));
        add(gap);
    }

    /**
     * Custom button with smooth glow animation on hover.
     */
    private class GlowButton extends JButton {
        private Color accent;
        private float glow = 0f;
        private Timer glowTimer;
        private boolean hovering = false;

        public GlowButton(String text, Color accent) {
            super(text);
            this.accent = accent;

            setFont(new Font("Monospaced", Font.BOLD, 11));
            setForeground(accent);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(90, 30));

            glowTimer = new Timer(16, e -> {
                if (hovering && glow < 1f) {
                    glow = Math.min(1f, glow + 0.12f);
                    repaint();
                } else if (!hovering && glow > 0f) {
                    glow = Math.max(0f, glow - 0.08f);
                    repaint();
                } else {
                    glowTimer.stop();
                }
            });

            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    if (isEnabled()) { hovering = true; glowTimer.start(); }
                }
                public void mouseExited(MouseEvent e) {
                    hovering = false; glowTimer.start();
                }
            });
        }

        @Override
        public void setEnabled(boolean b) {
            super.setEnabled(b);
            if (!b) { hovering = false; glow = 0f; }
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            if (!isEnabled()) {
                g2.setColor(new Color(22, 22, 28));
                g2.fillRoundRect(0, 0, w - 1, h - 1, 18, 18);
                g2.setColor(new Color(50, 50, 60));
                g2.drawRoundRect(0, 0, w - 1, h - 1, 18, 18);
                g2.setColor(new Color(80, 80, 90));
                drawCenter(g2, getText(), w, h);
                g2.dispose();
                return;
            }

            // Background fill interpolation
            int r = lerp(15, accent.getRed(), glow);
            int gn = lerp(15, accent.getGreen(), glow);
            int b = lerp(15, accent.getBlue(), glow);
            g2.setColor(new Color(r, gn, b));
            g2.fillRoundRect(0, 0, w - 1, h - 1, 18, 18);

            // Outer glow (shadow)
            if (glow > 0.2f) {
                g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), (int)(30 * glow)));
                g2.fillRoundRect(-2, -1, w + 3, h + 2, 22, 22);
                // Re-draw main rect on top
                g2.setColor(new Color(r, gn, b));
                g2.fillRoundRect(0, 0, w - 1, h - 1, 18, 18);
            }

            // Border
            g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 120 + (int)(135 * glow)));
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(0, 0, w - 1, h - 1, 18, 18);

            // Text
            int tr = lerp(accent.getRed(), 10, glow);
            int tg = lerp(accent.getGreen(), 10, glow);
            int tb = lerp(accent.getBlue(), 10, glow);
            g2.setColor(new Color(tr, tg, tb));
            drawCenter(g2, getText(), w, h);

            g2.dispose();
        }

        private int lerp(int a, int b, float t) {
            return Math.max(0, Math.min(255, (int)(a + (b - a) * t)));
        }

        private void drawCenter(Graphics2D g, String text, int w, int h) {
            FontMetrics fm = g.getFontMetrics();
            g.drawString(text, (w - fm.stringWidth(text)) / 2, (h + fm.getAscent()) / 2 - 2);
        }
    }
}
