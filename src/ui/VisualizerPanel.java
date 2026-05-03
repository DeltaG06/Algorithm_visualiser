package ui;

import algorithms.Sorter;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * VisualizerPanel — the heart of the application.
 * Supports normal mode, DC mode, and step-by-step history replay.
 */
public class VisualizerPanel extends JPanel {

    private int[] array;
    private int[] originalArray;
    private int highlightIndex1 = -1;
    private int highlightIndex2 = -1;
    private int sortedStart = -1;
    private int sortedEnd = -1;

    private Sorter currentSorter;

    // DC mode
    private boolean dcMode = false;
    private int[] dcLeft, dcRight, dcMerged;
    private int dcLeftHighlight = -1, dcRightHighlight = -1, dcMergedHighlight = -1;

    // Snapshot history for step-by-step navigation
    private final List<VisualSnapshot> history = new ArrayList<>();
    private int historyIndex = -1;
    private Sorter.Operation replayOperation = null;
    private static final int MAX_HISTORY = 5000;

    // Colors
    private static final Color C_CYAN   = new Color(0, 210, 255);
    private static final Color C_PINK   = new Color(255, 42, 112);
    private static final Color C_GREEN  = new Color(34, 197, 94);
    private static final Color C_GOLD   = new Color(255, 200, 40);
    private static final Color C_PURPLE = new Color(168, 85, 247);

    // ===== Snapshot inner class =====
    public static class VisualSnapshot {
        final int[] array;
        final int idx1, idx2, sStart, sEnd;
        final boolean dcMode;
        final int[] dcL, dcR, dcM;
        final int dcLH, dcRH, dcMH;
        final Sorter.Operation op;

        VisualSnapshot(int[] arr, int i1, int i2, int ss, int se, Sorter.Operation op) {
            this.array = arr != null ? arr.clone() : null;
            idx1 = i1; idx2 = i2; sStart = ss; sEnd = se;
            this.op = op; dcMode = false;
            dcL = null; dcR = null; dcM = null;
            dcLH = -1; dcRH = -1; dcMH = -1;
        }

        VisualSnapshot(int[] l, int[] r, int[] m, int lh, int rh, int mh) {
            dcMode = true;
            dcL = l != null ? l.clone() : null;
            dcR = r != null ? r.clone() : null;
            dcM = m != null ? m.clone() : null;
            dcLH = lh; dcRH = rh; dcMH = mh;
            array = null; idx1 = -1; idx2 = -1;
            sStart = -1; sEnd = -1;
            op = Sorter.Operation.IDLE;
        }
    }

    public VisualizerPanel() {
        setBackground(new Color(8, 8, 10));
        generateRandomArray(25);
    }

    public void setCurrentSorter(Sorter sorter) { this.currentSorter = sorter; }

    // ===== Array management =====

    public void generateRandomArray(int size) {
        array = new int[size];
        Random rand = new Random();
        for (int i = 0; i < size; i++) array[i] = rand.nextInt(100) + 5;
        originalArray = array.clone();
        resetHighlights();
        clearHistory();
        repaint();
    }

    public void setCustomArray(int[] ca) {
        array = ca.clone(); originalArray = ca.clone();
        resetHighlights();
        clearHistory();
        repaint();
    }

    public void resetToOriginal() {
        if (originalArray != null) {
            array = originalArray.clone();
            resetHighlights();
            clearHistory();
            repaint();
        }
    }

    private void resetHighlights() {
        highlightIndex1 = -1; highlightIndex2 = -1;
        sortedStart = -1; sortedEnd = -1;
        dcMode = false; replayOperation = null;
    }

    // ===== Normal mode updates =====

    public void updateVisuals(int[] currentArray, int idx1, int idx2) {
        this.array = currentArray;
        this.highlightIndex1 = idx1;
        this.highlightIndex2 = idx2;
        this.dcMode = false;
        this.replayOperation = null;
        Sorter.Operation op = (currentSorter != null) ? currentSorter.currentOp : Sorter.Operation.IDLE;
        addSnapshot(new VisualSnapshot(currentArray, idx1, idx2, sortedStart, sortedEnd, op));
        repaint();
    }

    public void setSortedRange(int start, int end) {
        this.sortedStart = start; this.sortedEnd = end;
        repaint();
    }

    // ===== DC mode updates =====

    public void setDCMode(boolean mode) { this.dcMode = mode; repaint(); }

    public void updateDCVisuals(int[] left, int[] right, int[] merged, int lh, int rh, int mh) {
        dcMode = true;
        dcLeft = left; dcRight = right; dcMerged = merged;
        dcLeftHighlight = lh; dcRightHighlight = rh; dcMergedHighlight = mh;
        replayOperation = null;
        addSnapshot(new VisualSnapshot(left, right, merged, lh, rh, mh));
        repaint();
    }

    // ===== Snapshot history =====

    private void addSnapshot(VisualSnapshot snap) {
        synchronized (history) {
            if (history.size() >= MAX_HISTORY) history.remove(0);
            history.add(snap);
            historyIndex = history.size() - 1;
        }
    }

    public void clearHistory() {
        synchronized (history) { history.clear(); historyIndex = -1; }
    }

    public boolean stepBackward() {
        synchronized (history) {
            if (historyIndex > 0) {
                historyIndex--;
                replaySnapshot(history.get(historyIndex));
                return true;
            }
        }
        return false;
    }

    public boolean stepForwardInHistory() {
        synchronized (history) {
            if (historyIndex < history.size() - 1) {
                historyIndex++;
                replaySnapshot(history.get(historyIndex));
                return true;
            }
        }
        return false;
    }

    public int getHistoryIndex() { return historyIndex; }
    public int getHistorySize() { synchronized (history) { return history.size(); } }

    public String getStepInfo() {
        return "Step " + (historyIndex + 1) + " / " + getHistorySize();
    }

    private void replaySnapshot(VisualSnapshot s) {
        if (s.dcMode) {
            dcMode = true;
            dcLeft = s.dcL; dcRight = s.dcR; dcMerged = s.dcM;
            dcLeftHighlight = s.dcLH; dcRightHighlight = s.dcRH; dcMergedHighlight = s.dcMH;
        } else {
            dcMode = false;
            array = s.array;
            highlightIndex1 = s.idx1; highlightIndex2 = s.idx2;
            sortedStart = s.sStart; sortedEnd = s.sEnd;
        }
        replayOperation = s.op;
        repaint();
    }

    // ===== Painting =====

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        int w = getWidth(), h = getHeight();

        g2.setColor(new Color(255, 255, 255, 6));
        for (int x = 20; x < w; x += 35)
            for (int y = 20; y < h; y += 35)
                g2.fillOval(x, y, 2, 2);

        if (dcMode && dcLeft != null && dcRight != null && dcMerged != null)
            paintDCMode(g2, w, h);
        else
            paintNormalMode(g2, w, h);
    }

    // ===== Normal mode rendering =====

    private void paintNormalMode(Graphics2D g2, int width, int height) {
        if (array == null || array.length == 0) return;

        int maxVal = 0;
        for (int v : array) maxVal = Math.max(maxVal, v);
        if (maxVal == 0) maxVal = 1;

        int topM = 55, botM = 40, sideP = 50;
        int usableW = width - sideP * 2;
        double bw = (double) usableW / array.length;
        double sp = Math.max(3.0, bw * 0.22);
        double abw = bw - sp;
        int maxBH = height - topM - botM;

        Sorter.Operation op = Sorter.Operation.IDLE;
        if (replayOperation != null) op = replayOperation;
        else if (currentSorter != null) op = currentSorter.currentOp;

        double[] bxp = new double[array.length];
        double[] byp = new double[array.length];

        for (int i = 0; i < array.length; i++) {
            double x = sideP + i * bw + sp / 2;
            double bh = ((double) array[i] / maxVal) * maxBH * 0.55;
            double y = height - bh - botM;
            bxp[i] = x; byp[i] = y;

            Color bc = C_CYAN;
            boolean hl = (i == highlightIndex1 || i == highlightIndex2);
            if (hl) bc = (op == Sorter.Operation.SWAPPING) ? C_GOLD : C_PINK;
            else if (i >= sortedStart && i <= sortedEnd && sortedStart != -1) bc = C_GREEN;

            drawBar(g2, x, y, abw, bh, bc, hl, array[i], i, height - botM + 15, abw);
        }

        // Arrows
        if (highlightIndex1 >= 0 && highlightIndex2 >= 0
                && highlightIndex1 < array.length && highlightIndex2 < array.length
                && highlightIndex1 != highlightIndex2) {
            int i1 = Math.min(highlightIndex1, highlightIndex2);
            int i2 = Math.max(highlightIndex1, highlightIndex2);
            double x1 = bxp[i1] + abw / 2, x2 = bxp[i2] + abw / 2;
            double ay = Math.min(byp[i1], byp[i2]) - 20;
            Color ac = (op == Sorter.Operation.SWAPPING) ? C_GOLD : C_PINK;

            g2.setColor(new Color(ac.getRed(), ac.getGreen(), ac.getBlue(), 180));
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            double ah = Math.min(18 + (i2 - i1) * 3, 50);
            double mx = (x1 + x2) / 2;
            Path2D arc = new Path2D.Double();
            arc.moveTo(x1, ay);
            arc.curveTo(mx, ay - ah, mx, ay - ah, x2, ay);
            g2.draw(arc);
            drawArrowhead(g2, x1, ay, 7, true);
            drawArrowhead(g2, x2, ay, 7, true);

            if (op == Sorter.Operation.SWAPPING) {
                g2.setColor(C_GOLD);
                g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                double iy = ay - ah - 5, is = 8;
                g2.drawLine((int)(mx-is),(int)iy,(int)(mx+is),(int)iy);
                g2.drawLine((int)(mx+is),(int)iy,(int)(mx+is-4),(int)(iy-4));
                g2.drawLine((int)(mx+is),(int)iy,(int)(mx+is-4),(int)(iy+4));
                double iy2 = iy + 10;
                g2.drawLine((int)(mx+is),(int)iy2,(int)(mx-is),(int)iy2);
                g2.drawLine((int)(mx-is),(int)iy2,(int)(mx-is+4),(int)(iy2-4));
                g2.drawLine((int)(mx-is),(int)iy2,(int)(mx-is+4),(int)(iy2+4));
            }

            String label = "";
            if (op == Sorter.Operation.COMPARING) label = "COMPARING";
            else if (op == Sorter.Operation.SWAPPING) label = "SWAPPING";
            if (!label.isEmpty()) {
                g2.setFont(new Font("Monospaced", Font.BOLD, 11));
                FontMetrics fm = g2.getFontMetrics();
                int lw = fm.stringWidth(label);
                float lx = (float)(mx - lw / 2);
                float ly = (float)(ay - ah - (op == Sorter.Operation.SWAPPING ? 22 : 8));
                g2.setColor(new Color(ac.getRed(), ac.getGreen(), ac.getBlue(), 30));
                g2.fillRoundRect((int)lx-8,(int)ly-12,lw+16,18,12,12);
                g2.setColor(new Color(ac.getRed(), ac.getGreen(), ac.getBlue(), 100));
                g2.drawRoundRect((int)lx-8,(int)ly-12,lw+16,18,12,12);
                g2.setColor(ac);
                g2.drawString(label, lx, ly);
            }
        }

        // Single highlight (Binary Search found)
        if (highlightIndex1 >= 0 && highlightIndex1 == highlightIndex2 && highlightIndex1 < array.length) {
            double cx = bxp[highlightIndex1] + abw / 2;
            double cy = byp[highlightIndex1] - 25;
            g2.setFont(new Font("Monospaced", Font.BOLD, 11));
            FontMetrics fm = g2.getFontMetrics();
            String fl = "FOUND"; int flw = fm.stringWidth(fl);
            g2.setColor(new Color(C_PINK.getRed(), C_PINK.getGreen(), C_PINK.getBlue(), 35));
            g2.fillRoundRect((int)(cx-flw/2-8),(int)cy-12,flw+16,18,12,12);
            g2.setColor(C_PINK);
            g2.drawRoundRect((int)(cx-flw/2-8),(int)cy-12,flw+16,18,12,12);
            g2.drawString(fl, (float)(cx-flw/2), (float)cy);
            drawArrowhead(g2, cx, cy+8, 6, true);
        }
    }

    // ===== DC mode rendering =====

    private void paintDCMode(Graphics2D g2, int width, int height) {
        int maxVal = 1;
        for (int v : dcLeft) maxVal = Math.max(maxVal, v);
        for (int v : dcRight) maxVal = Math.max(maxVal, v);
        for (int v : dcMerged) maxVal = Math.max(maxVal, v);

        int secH = height / 3, pad = 40;

        drawDCSection(g2, "LEFT SUBARRAY", dcLeft, dcLeftHighlight, C_CYAN, pad, width-pad*2, 0, secH, maxVal);
        g2.setColor(new Color(60,60,80,120)); g2.setStroke(new BasicStroke(1f));
        g2.drawLine(pad, secH, width-pad, secH);

        drawDCSection(g2, "RIGHT SUBARRAY", dcRight, dcRightHighlight, C_PURPLE, pad, width-pad*2, secH, secH, maxVal);
        g2.setColor(new Color(60,60,80,120));
        g2.drawLine(pad, secH*2, width-pad, secH*2);

        drawDCSection(g2, "MERGED RESULT", dcMerged, dcMergedHighlight, C_GREEN, pad, width-pad*2, secH*2, secH, maxVal);

        if (dcMergedHighlight >= 0 && dcMerged.length > 0) {
            int total = dcLeft.length + dcRight.length;
            double mbw = (double)(width-pad*2)/total, msp = Math.max(2.0,mbw*0.2), maw = mbw-msp;
            double tx = pad + dcMergedHighlight*mbw + msp/2 + maw/2;
            double ty = secH*2 + 28;
            g2.setColor(new Color(C_GOLD.getRed(),C_GOLD.getGreen(),C_GOLD.getBlue(),200));
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine((int)tx,(int)(ty-20),(int)tx,(int)(ty-4));
            drawArrowhead(g2, tx, ty, 7, true);
            g2.setFont(new Font("Monospaced", Font.BOLD, 10));
            FontMetrics fm = g2.getFontMetrics();
            String pl = "PLACING"; int plw = fm.stringWidth(pl);
            float plx = (float)(tx-plw/2), ply = (float)(ty-26);
            g2.setColor(new Color(C_GOLD.getRed(),C_GOLD.getGreen(),C_GOLD.getBlue(),35));
            g2.fillRoundRect((int)plx-6,(int)ply-10,plw+12,15,10,10);
            g2.setColor(C_GOLD);
            g2.drawRoundRect((int)plx-6,(int)ply-10,plw+12,15,10,10);
            g2.drawString(pl, plx, ply);
        }
    }

    private void drawDCSection(Graphics2D g2, String title, int[] data, int hl,
                               Color base, int xOff, int uw, int yOff, int sh, int maxVal) {
        g2.setFont(new Font("Monospaced", Font.BOLD, 11));
        g2.setColor(new Color(base.getRed(),base.getGreen(),base.getBlue(),200));
        g2.drawString("\u25C7 " + title, xOff+5, yOff+18);

        int tp = 26, bp = 20, bah = sh - tp - bp;
        if (data == null || data.length == 0) {
            g2.setColor(new Color(50,50,70,80));
            g2.setFont(new Font("Monospaced", Font.ITALIC, 11));
            g2.drawString("(empty)", xOff + uw/2 - 25, yOff + sh/2 + 5);
            return;
        }

        double bw = (double)uw / data.length, sp = Math.max(2.0,bw*0.2), abw = bw-sp;
        for (int i = 0; i < data.length; i++) {
            double x = xOff + i*bw + sp/2;
            double bh = Math.max(4, ((double)data[i]/maxVal)*bah*0.75);
            double y = yOff + tp + bah - bh;
            drawBar(g2, x, y, abw, bh, (i==hl)?C_GOLD:base, i==hl, data[i], -1, -1, abw);
        }
    }

    // ===== Shared bar rendering =====

    private void drawBar(Graphics2D g2, double x, double y, double w, double h,
                         Color c, boolean hl, int val, int idx, double idxY, double abw) {
        g2.setColor(new Color(0,0,0,40));
        g2.fill(new RoundRectangle2D.Double(x+2,y+3,w,h,6,6));

        g2.setPaint(new GradientPaint((float)x,(float)y,
                new Color(c.getRed()/3,c.getGreen()/3,c.getBlue()/3,160),
                (float)x,(float)(y+h),
                new Color(c.getRed(),c.getGreen(),c.getBlue(),210)));
        RoundRectangle2D.Double r = new RoundRectangle2D.Double(x,y,w,h,6,6);
        g2.fill(r);

        g2.setColor(new Color(c.getRed(),c.getGreen(),c.getBlue(),hl?255:120));
        g2.setStroke(new BasicStroke(hl?2f:1f));
        g2.draw(r);

        g2.setColor(new Color(255,255,255,hl?240:140));
        g2.fill(new RoundRectangle2D.Double(x,y,w,3,3,3));

        if (hl) {
            g2.setColor(new Color(c.getRed(),c.getGreen(),c.getBlue(),25));
            g2.fill(new RoundRectangle2D.Double(x-3,y-3,w+6,h+6,10,10));
        }

        if (w >= 14) {
            int fs = Math.min(13, Math.max(9,(int)(w/2.5)));
            g2.setFont(new Font("Monospaced", Font.BOLD, fs));
            String vs = String.valueOf(val);
            FontMetrics fm = g2.getFontMetrics();
            float tx = (float)(x+(w-fm.stringWidth(vs))/2);
            if (h > fm.getAscent()+15) {
                g2.setColor(new Color(10,10,14)); // Dark text for contrast inside the bright bar
                g2.drawString(vs, tx, (float)(y + h - 8)); // Drawn at the bottom of the bar
            } else {
                g2.setColor(new Color(200,200,220));
                g2.drawString(vs, tx, (float)(y-5));
            }
        }

        if (idx >= 0 && idxY > 0 && abw >= 14) {
            g2.setFont(new Font("Monospaced", Font.PLAIN, 9));
            g2.setColor(new Color(80,80,100));
            String is = String.valueOf(idx);
            FontMetrics fm2 = g2.getFontMetrics();
            g2.drawString(is, (float)(x+(w-fm2.stringWidth(is))/2), (float)idxY);
        }
    }

    private void drawArrowhead(Graphics2D g2, double x, double y, double s, boolean down) {
        Path2D a = new Path2D.Double();
        if (down) { a.moveTo(x-s/2,y-s); a.lineTo(x,y); a.lineTo(x+s/2,y-s); }
        else { a.moveTo(x-s/2,y+s); a.lineTo(x,y); a.lineTo(x+s/2,y+s); }
        g2.fill(a);
    }

    public int[] getArray() { return array; }
}
