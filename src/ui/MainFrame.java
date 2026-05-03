package ui;

import algorithms.BubbleSort;
import algorithms.SelectionSort;
import algorithms.InsertionSort;
import algorithms.MergeSort;
import algorithms.QuickSort;
import algorithms.BinarySearch;
import algorithms.Sorter;
import db.ResultDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * MainFrame is the root window of the Algorithm Visualizer app.
 * It coordinates the ControlPanel, VisualizerPanel, and the sorting thread.
 */
public class MainFrame extends JFrame {

    private ControlPanel controlPanel;
    private VisualizerPanel visualizerPanel;
    private CodePanel codePanel;
    private SummaryPanel summaryPanel;
    private JLabel statusBar;

    private Sorter currentSorter;
    private Thread sortingThread;

    public MainFrame() {
        setTitle("ALGORITHM VISUALIZER");
        setSize(1280, 780);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(8, 8, 10));
        setLayout(new BorderLayout(0, 0));

        // --- Top Panel (Hero Title + Controls) ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(8, 8, 10));

        // Custom painted hero title banner inspired by Beetle
        JPanel titleBanner = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();

                // Dark gradient background
                GradientPaint bg = new GradientPaint(0, 0, new Color(8, 8, 10), 0, h, new Color(12, 12, 15));
                g2.setPaint(bg);
                g2.fillRect(0, 0, w, h);

                // Draw animated rain/particle lines
                g2.setColor(new Color(255, 255, 255, 15));
                g2.setStroke(new BasicStroke(1f));
                for (int i = 0; i < 40; i++) {
                    int rx = (int)(Math.random() * w);
                    int ry = (int)(Math.random() * h);
                    int len = 8 + (int)(Math.random() * 25);
                    g2.drawLine(rx, ry, rx - 3, ry + len);
                }

                // Version badge - green pill
                int badgeX = 28;
                int badgeY = 12;
                g2.setColor(new Color(34, 197, 94, 30));
                g2.fillRoundRect(badgeX, badgeY, 70, 22, 16, 16);
                g2.setColor(new Color(34, 197, 94));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(badgeX, badgeY, 70, 22, 16, 16);
                // Green dot
                g2.fillOval(badgeX + 8, badgeY + 7, 8, 8);
                g2.setFont(new Font("Monospaced", Font.BOLD, 11));
                g2.drawString("v1.0.0", badgeX + 20, badgeY + 16);

                // Main title — BIG BOLD BLOCKY
                g2.setFont(new Font("Monospaced", Font.BOLD, 42));
                
                // White glow effect behind title
                String title = "ALGORITHM VISUALIZER";
                FontMetrics fm = g2.getFontMetrics();
                int tx = 30;
                int ty = 60;

                // Soft white glow layers
                for (int layer = 3; layer >= 1; layer--) {
                    g2.setColor(new Color(255, 255, 255, 8 * layer));
                    g2.drawString(title, tx - layer, ty);
                    g2.drawString(title, tx + layer, ty);
                    g2.drawString(title, tx, ty - layer);
                    g2.drawString(title, tx, ty + layer);
                }

                // Main white title
                g2.setColor(new Color(255, 255, 255, 240));
                g2.drawString(title, tx, ty);

                // Subtitle
                g2.setFont(new Font("SansSerif", Font.BOLD, 16));
                g2.setColor(new Color(180, 180, 200));
                g2.drawString("Visualize. Learn. Master.", tx + 2, ty + 28);

                // Subtle bottom glow line
                GradientPaint glow = new GradientPaint(0, h - 2, new Color(0, 210, 255, 80), w, h - 2, new Color(200, 70, 255, 80));
                g2.setPaint(glow);
                g2.fillRect(0, h - 2, w, 2);
            }
        };
        titleBanner.setPreferredSize(new Dimension(0, 100));
        titleBanner.setBackground(new Color(8, 8, 10));
        topPanel.add(titleBanner, BorderLayout.NORTH);

        controlPanel = new ControlPanel();
        topPanel.add(controlPanel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // --- Central Visualization Panel ---
        visualizerPanel = new VisualizerPanel();

        // --- East Code Panel ---
        codePanel = new CodePanel();

        // --- Draggable Split Pane ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, visualizerPanel, codePanel);
        splitPane.setResizeWeight(1.0); // Extra space goes to the visualizer panel
        splitPane.setContinuousLayout(true);
        splitPane.setDividerSize(6); // Thicker divider to make it easily grab-able
        splitPane.setBorder(BorderFactory.createEmptyBorder());

        // Custom UI for the split pane divider to match the dark theme
        splitPane.setUI(new javax.swing.plaf.basic.BasicSplitPaneUI() {
            @Override
            public javax.swing.plaf.basic.BasicSplitPaneDivider createDefaultDivider() {
                return new javax.swing.plaf.basic.BasicSplitPaneDivider(this) {
                    @Override
                    public void paint(Graphics g) {
                        g.setColor(new Color(30, 30, 40));
                        g.fillRect(0, 0, getSize().width, getSize().height);
                        
                        // Draw vertical grab handles
                        g.setColor(new Color(100, 100, 120));
                        int midY = getSize().height / 2;
                        g.drawLine(2, midY - 10, 2, midY + 10);
                        g.drawLine(4, midY - 10, 4, midY + 10);
                    }
                };
            }
        });

        add(splitPane, BorderLayout.CENTER);

        // --- South Panel (Summary + Status) ---
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setBackground(new Color(8, 8, 10));
        
        summaryPanel = new SummaryPanel();
        southPanel.add(summaryPanel, BorderLayout.CENTER);

        // Status bar
        statusBar = new JLabel("  Ready. Select an algorithm and press Start.");
        statusBar.setFont(new Font("Monospaced", Font.PLAIN, 12));
        statusBar.setForeground(new Color(120, 120, 150));
        statusBar.setBorder(BorderFactory.createEmptyBorder(6, 15, 6, 0));
        statusBar.setOpaque(true);
        statusBar.setBackground(new Color(10, 10, 12));
        
        southPanel.add(statusBar, BorderLayout.SOUTH);
        add(southPanel, BorderLayout.SOUTH);

        // --- Wire up event listeners ---
        setupListeners();

        setVisible(true);
    }

    /**
     * Wires the buttons from the ControlPanel to the actual logic.
     */
    private void setupListeners() {
        
        // --- RANDOM BUTTON ---
        controlPanel.randomButton.addActionListener(e -> {
            if (currentSorter != null) {
                currentSorter.stop();
            }
            visualizerPanel.generateRandomArray(25);
            statusBar.setText("  Generated new random array.");
            controlPanel.startButton.setEnabled(true);
            controlPanel.pauseButton.setText("\u23f8 PAUSE");
            controlPanel.pauseButton.setEnabled(false);
            controlPanel.stepBackButton.setEnabled(false);
            controlPanel.stepFwdButton.setEnabled(false);
            controlPanel.algorithmDropdown.setEnabled(true);
        });

        // --- START BUTTON ---
        controlPanel.startButton.addActionListener(e -> {
            startSorting();
        });

        // --- PAUSE/RESUME BUTTON ---
        controlPanel.pauseButton.addActionListener(e -> {
            if (currentSorter != null) {
                if (controlPanel.pauseButton.getText().toUpperCase().contains("PAUSE")) {
                    currentSorter.pause();
                    controlPanel.pauseButton.setText("\u25b6 RESUME");
                    controlPanel.stepBackButton.setEnabled(true);
                    controlPanel.stepFwdButton.setEnabled(true);
                    statusBar.setText("  Paused. " + visualizerPanel.getStepInfo());
                } else {
                    currentSorter.resume();
                    controlPanel.pauseButton.setText("\u23f8 PAUSE");
                    controlPanel.stepBackButton.setEnabled(false);
                    controlPanel.stepFwdButton.setEnabled(false);
                    statusBar.setText("  Sorting in progress...");
                }
            }
        });

        // --- STEP BACK BUTTON ---
        controlPanel.stepBackButton.addActionListener(e -> {
            if (currentSorter != null && currentSorter.isPaused) {
                if (visualizerPanel.stepBackward()) {
                    statusBar.setText("  Stepped backward. " + visualizerPanel.getStepInfo());
                }
            }
        });

        // --- STEP FORWARD BUTTON ---
        controlPanel.stepFwdButton.addActionListener(e -> {
            if (currentSorter != null && currentSorter.isPaused) {
                // If we are at the end of the history, we let the thread advance by one step
                if (visualizerPanel.getHistoryIndex() == visualizerPanel.getHistorySize() - 1) {
                    currentSorter.requestStep();
                    statusBar.setText("  Stepped forward. " + visualizerPanel.getStepInfo());
                } else {
                    // Otherwise, we just replay the next snapshot in history
                    if (visualizerPanel.stepForwardInHistory()) {
                        statusBar.setText("  Stepped forward. " + visualizerPanel.getStepInfo());
                    }
                }
            }
        });

        // --- RESET BUTTON ---
        controlPanel.resetButton.addActionListener(e -> {
            if (currentSorter != null) {
                currentSorter.stop();
            }
            visualizerPanel.resetToOriginal();
            controlPanel.startButton.setEnabled(true);
            controlPanel.pauseButton.setEnabled(false);
            controlPanel.stepBackButton.setEnabled(false);
            controlPanel.stepFwdButton.setEnabled(false);
            controlPanel.pauseButton.setText("\u23f8 PAUSE");
            controlPanel.algorithmDropdown.setEnabled(true);
            statusBar.setText("  Reset to original array state.");
        });

        // --- STATS BUTTON ---
        controlPanel.statsButton.addActionListener(e -> {
            showStatsDialog();
        });

        // --- CUSTOM ARRAY BUTTON ---
        controlPanel.customButton.addActionListener(e -> {
            UIManager.put("OptionPane.background", new Color(20, 20, 25));
            UIManager.put("Panel.background", new Color(20, 20, 25));
            UIManager.put("OptionPane.messageForeground", Color.WHITE);
            
            String input = JOptionPane.showInputDialog(
                this, 
                "Enter comma-separated integers (e.g. 50, 20, 80, 10, 100):", 
                "Custom Array Input", 
                JOptionPane.PLAIN_MESSAGE
            );

            if (input != null && !input.trim().isEmpty()) {
                try {
                    String[] parts = input.split(",");
                    if (parts.length == 0) throw new NumberFormatException();
                    
                    int[] customArray = new int[parts.length];
                    for (int i = 0; i < parts.length; i++) {
                        int val = Integer.parseInt(parts[i].trim());
                        if (val < 1) val = 1;
                        customArray[i] = val;
                    }
                    
                    if (currentSorter != null) currentSorter.stop();
                    
                    visualizerPanel.setCustomArray(customArray);
                    statusBar.setText("  Loaded custom array of size " + customArray.length + ".");
                    
                    controlPanel.startButton.setEnabled(true);
                    controlPanel.pauseButton.setText("\u23f8 PAUSE");
                    controlPanel.pauseButton.setEnabled(false);
                    controlPanel.stepBackButton.setEnabled(false);
                    controlPanel.stepFwdButton.setEnabled(false);
                    controlPanel.algorithmDropdown.setEnabled(true);

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid input! Please enter only numbers separated by commas.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // --- ALGORITHM DROPDOWN ---
        controlPanel.algorithmDropdown.addActionListener(e -> {
            String selected = (String) controlPanel.algorithmDropdown.getSelectedItem();
            codePanel.setCode(selected);
            summaryPanel.setSummary(selected);
        });
    }

    /**
     * Creates and displays a dialog with a JTable showing all past results.
     */
    private void showStatsDialog() {
        Object[][] data = ResultDAO.getAllResults();
        String[] columnNames = {"ID", "Algorithm", "Array Size", "Time", "Comparisons", "Swaps", "Date"};

        JTable table = new JTable(new DefaultTableModel(data, columnNames)) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Dark themed table
        table.setBackground(new Color(18, 18, 22));
        table.setForeground(new Color(200, 200, 220));
        table.setGridColor(new Color(40, 40, 50));
        table.setFont(new Font("Monospaced", Font.PLAIN, 12));
        table.setRowHeight(28);
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));

        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(25, 25, 35));
        header.setForeground(new Color(0, 210, 255));
        header.setFont(new Font("Monospaced", Font.BOLD, 12));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(0, 210, 255, 60)));

        // Cell renderer for center alignment
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(new Color(18, 18, 22));
        scrollPane.setPreferredSize(new Dimension(700, 300));
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(40, 40, 60)));

        JOptionPane.showMessageDialog(this, scrollPane, "Performance History", JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * Initializes and starts the sorting process in a background thread.
     */
    private void startSorting() {
        String selectedAlgo = (String) controlPanel.algorithmDropdown.getSelectedItem();

        if ("Bubble Sort".equals(selectedAlgo)) {
            currentSorter = new BubbleSort(visualizerPanel, controlPanel.speedSlider);
        } else if ("Selection Sort".equals(selectedAlgo)) {
            currentSorter = new SelectionSort(visualizerPanel, controlPanel.speedSlider);
        } else if ("Insertion Sort".equals(selectedAlgo)) {
            currentSorter = new InsertionSort(visualizerPanel, controlPanel.speedSlider);
        } else if ("Merge Sort".equals(selectedAlgo)) {
            currentSorter = new MergeSort(visualizerPanel, controlPanel.speedSlider);
        } else if ("Quick Sort".equals(selectedAlgo)) {
            currentSorter = new QuickSort(visualizerPanel, controlPanel.speedSlider);
        } else if ("Binary Search".equals(selectedAlgo)) {
            BinarySearch bs = new BinarySearch(visualizerPanel, controlPanel.speedSlider);

            // Ask the user for the target element
            UIManager.put("OptionPane.background", new Color(20, 20, 25));
            UIManager.put("Panel.background", new Color(20, 20, 25));
            UIManager.put("OptionPane.messageForeground", Color.WHITE);

            String targetInput = JOptionPane.showInputDialog(
                this,
                "Enter the value to search for\n(leave blank for a random target):",
                "Binary Search Target",
                JOptionPane.PLAIN_MESSAGE
            );

            if (targetInput == null) return; // User cancelled

            if (!targetInput.trim().isEmpty()) {
                try {
                    int target = Integer.parseInt(targetInput.trim());
                    bs.setTarget(target);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid number! Using a random target.", "Warning", JOptionPane.WARNING_MESSAGE);
                }
            }
            currentSorter = bs;
        }

        controlPanel.startButton.setEnabled(false);
        controlPanel.pauseButton.setEnabled(true);
        controlPanel.algorithmDropdown.setEnabled(false);
        statusBar.setText("  Running " + selectedAlgo + "...");

        // Let the visualizer know which sorter is active so it can read operation state
        visualizerPanel.setCurrentSorter(currentSorter);

        sortingThread = new Thread(() -> {
            currentSorter.sort(visualizerPanel.getArray());

            SwingUtilities.invokeLater(() -> {
                if (!currentSorter.isStopped) {
                    ResultDAO.saveResult(selectedAlgo, visualizerPanel.getArray().length, 
                                         currentSorter.timeTakenMs, currentSorter.comparisons, currentSorter.swaps);
                }

                controlPanel.pauseButton.setEnabled(false);
                controlPanel.startButton.setEnabled(false);
                controlPanel.algorithmDropdown.setEnabled(true);

                // Special status message for Binary Search
                if (currentSorter instanceof BinarySearch) {
                    BinarySearch bs = (BinarySearch) currentSorter;
                    if (bs.wasFound()) {
                        statusBar.setText("  Found " + bs.getTarget() + "! | Comparisons: " + 
                                          bs.comparisons + " | Time: " + bs.timeTakenMs + " ms");
                    } else {
                        statusBar.setText("  " + bs.getTarget() + " NOT found in array. | Comparisons: " + 
                                          bs.comparisons + " | Time: " + bs.timeTakenMs + " ms");
                    }
                } else {
                    statusBar.setText("  Finished " + selectedAlgo + " | Comparisons: " + 
                                      currentSorter.comparisons + " | Swaps: " + currentSorter.swaps + 
                                      " | Time: " + currentSorter.timeTakenMs + " ms");
                }
            });
        });

        sortingThread.start();
    }
}
