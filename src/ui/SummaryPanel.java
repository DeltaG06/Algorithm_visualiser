package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * SummaryPanel displays the educational summary and complexity
 * of the currently selected algorithm at the bottom of the screen.
 */
public class SummaryPanel extends JPanel {

    private JTextArea descArea;
    private Map<String, String> descMap;

    public SummaryPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(10, 10, 12));
        setPreferredSize(new Dimension(0, 120));
        setBorder(new EmptyBorder(4, 8, 4, 8));

        // --- Description area ---
        descArea = new JTextArea();
        descArea.setEditable(false);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setBackground(new Color(15, 15, 18));
        descArea.setForeground(new Color(190, 190, 210));
        descArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
        descArea.setBorder(new EmptyBorder(10, 14, 10, 14));
        descArea.setHighlighter(null);

        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(40, 40, 55)),
            BorderFactory.createLineBorder(new Color(30, 30, 40))
        ));
        descScroll.getVerticalScrollBar().setBackground(new Color(15, 15, 18));
        descScroll.getViewport().setBackground(new Color(15, 15, 18));

        add(descScroll, BorderLayout.CENTER);

        initContentMap();
        setSummary("Bubble Sort");
    }

    private void initContentMap() {
        descMap = new HashMap<>();

        descMap.put("Bubble Sort", 
            "BUBBLE SORT  —  Bubble Sort is one of the simplest sorting algorithms. It repeatedly steps through the list, compares adjacent elements, and swaps them if they are in the wrong order. The largest elements 'bubble' up to the end of the array first. While easy to understand, it is too slow for real-world use on large datasets.\n\n" +
            "Time Complexity:  Best O(n)  |  Average O(n\u00b2)  |  Worst O(n\u00b2)     Space: O(1)"
        );

        descMap.put("Selection Sort", 
            "SELECTION SORT  —  Selection Sort divides the array into a sorted section at the front and an unsorted section at the back. It scans the unsorted section to find the smallest element, then swaps it into the sorted section. It makes fewer swaps than Bubble Sort, but still scans the entire array repeatedly.\n\n" +
            "Time Complexity:  Best O(n\u00b2)  |  Average O(n\u00b2)  |  Worst O(n\u00b2)     Space: O(1)"
        );

        descMap.put("Insertion Sort", 
            "INSERTION SORT  —  Insertion Sort builds the final sorted array one item at a time. It takes an element, searches the sorted portion for its correct spot, and inserts it by shifting larger elements. It is highly efficient for small or nearly-sorted data sets.\n\n" +
            "Time Complexity:  Best O(n)  |  Average O(n\u00b2)  |  Worst O(n\u00b2)     Space: O(1)"
        );

        descMap.put("Binary Search", 
            "BINARY SEARCH  —  Binary Search is a fast search algorithm that only works on sorted arrays. It looks at the middle element. If the target is smaller, it ignores the right half; if larger, it ignores the left half. It keeps halving the search area until the target is found or confirmed absent.\n\n" +
            "Time Complexity:  Best O(1)  |  Average O(log n)  |  Worst O(log n)     Space: O(1)"
        );

        descMap.put("Merge Sort",
            "MERGE SORT  —  Merge Sort is a divide-and-conquer algorithm. It splits the array in half recursively until it has subarrays of one element, which are trivially sorted. It then merges the sorted halves back together in order. It guarantees O(n log n) in all cases, making it excellent for large, unordered datasets.\n\n" +
            "Time Complexity:  Best O(n log n)  |  Average O(n log n)  |  Worst O(n log n)     Space: O(n)"
        );

        descMap.put("Quick Sort",
            "QUICK SORT  —  Quick Sort is a highly efficient divide-and-conquer algorithm. It picks a 'pivot' element and partitions the array so all smaller elements go left and all larger go right. It then recursively sorts both sides. Despite a poor worst-case, its average-case performance and low memory overhead make it the fastest in practice.\n\n" +
            "Time Complexity:  Best O(n log n)  |  Average O(n log n)  |  Worst O(n\u00b2)     Space: O(log n)"
        );
    }

    public void setSummary(String algorithmName) {
        String desc = descMap.getOrDefault(algorithmName, "Description not found.");
        descArea.setText(desc);
        descArea.setCaretPosition(0);
    }
}
