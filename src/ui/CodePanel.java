package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.HashMap;
import java.util.Map;

/**
 * CodePanel displays the source code
 * of the currently selected algorithm in multiple languages.
 */
public class CodePanel extends JPanel {

    private JTextArea codeArea;
    private JLabel titleLabel;
    private JComboBox<String> langDropdown;
    
    // codeMap structure: Map<AlgorithmName, Map<Language, Code>>
    private Map<String, Map<String, String>> codeMap;
    
    private String currentAlgorithm = "Bubble Sort";
    private String currentLanguage = "Java";

    public CodePanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(12, 12, 15));
        setPreferredSize(new Dimension(380, 0)); // Slightly wider to fit language dropdown
        setBorder(new EmptyBorder(8, 0, 8, 8));

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(15, 15, 18));
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(40, 40, 55)),
            new EmptyBorder(6, 10, 6, 10)
        ));

        // Title
        titleLabel = new JLabel("  CODE");
        titleLabel.setFont(new Font("Monospaced", Font.BOLD, 13));
        titleLabel.setForeground(new Color(0, 210, 255));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Right side of header (Lang Dropdown + Copy)
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);

        // Language Dropdown
        String[] languages = {"Java", "Python", "C++", "C"};
        langDropdown = new JComboBox<>(languages);
        langDropdown.setFont(new Font("Monospaced", Font.BOLD, 11));
        langDropdown.setPreferredSize(new Dimension(80, 26));
        langDropdown.setBackground(new Color(25, 25, 30));
        langDropdown.setForeground(new Color(0, 210, 255));
        langDropdown.setFocusable(false);
        
        // Simple dark styling for dropdown
        langDropdown.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton btn = new JButton("\u25BC");
                btn.setFont(new Font("SansSerif", Font.PLAIN, 9));
                btn.setForeground(new Color(0, 210, 255));
                btn.setBackground(new Color(25, 25, 30));
                btn.setBorder(BorderFactory.createEmptyBorder());
                return btn;
            }
        });
        
        langDropdown.addActionListener(e -> {
            currentLanguage = (String) langDropdown.getSelectedItem();
            updateCodeView();
        });

        // Copy Button
        JButton copyButton = new JButton("COPY");
        copyButton.setFont(new Font("Monospaced", Font.BOLD, 11));
        copyButton.setForeground(new Color(0, 210, 255));
        copyButton.setBackground(new Color(25, 25, 30));
        copyButton.setFocusPainted(false);
        copyButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(40, 40, 55)),
            new EmptyBorder(4, 10, 4, 10)
        ));
        copyButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        copyButton.addActionListener(e -> {
            String code = codeArea.getText();
            if (code != null && !code.isEmpty()) {
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(code), null);
                copyButton.setText("COPIED!");
                Timer t = new Timer(1500, evt -> copyButton.setText("COPY"));
                t.setRepeats(false);
                t.start();
            }
        });
        
        copyButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { copyButton.setBackground(new Color(35, 35, 45)); }
            public void mouseExited(java.awt.event.MouseEvent evt) { copyButton.setBackground(new Color(25, 25, 30)); }
        });

        rightPanel.add(langDropdown);
        rightPanel.add(copyButton);

        headerPanel.add(rightPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Code area
        codeArea = new JTextArea();
        codeArea.setEditable(false);
        codeArea.setBackground(new Color(15, 15, 18));
        codeArea.setForeground(new Color(255, 60, 80)); // Neon Red
        codeArea.setCaretColor(new Color(255, 60, 80)); 
        codeArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        codeArea.setBorder(new EmptyBorder(12, 14, 12, 14));
        codeArea.setHighlighter(null);

        JScrollPane codeScroll = new JScrollPane(codeArea);
        codeScroll.setBorder(BorderFactory.createLineBorder(new Color(30, 30, 40)));
        codeScroll.getVerticalScrollBar().setBackground(new Color(15, 15, 18));
        codeScroll.getViewport().setBackground(new Color(15, 15, 18));

        add(codeScroll, BorderLayout.CENTER);

        initContentMap();
        setCode("Bubble Sort");
    }

    private void initContentMap() {
        codeMap = new HashMap<>();

        // BUBBLE SORT
        Map<String, String> bubbleMap = new HashMap<>();
        bubbleMap.put("Java", "void bubbleSort(int arr[]) {\n" +
                "    int n = arr.length;\n" +
                "    for (int i = 0; i < n-1; i++) {\n" +
                "        for (int j = 0; j < n-i-1; j++) {\n" +
                "            if (arr[j] > arr[j+1]) {\n" +
                "                int temp = arr[j];\n" +
                "                arr[j] = arr[j+1];\n" +
                "                arr[j+1] = temp;\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "}");
        bubbleMap.put("Python", "def bubble_sort(arr):\n" +
                "    n = len(arr)\n" +
                "    for i in range(n-1):\n" +
                "        for j in range(0, n-i-1):\n" +
                "            if arr[j] > arr[j+1]:\n" +
                "                arr[j], arr[j+1] = arr[j+1], arr[j]");
        bubbleMap.put("C++", "void bubbleSort(vector<int>& arr) {\n" +
                "    int n = arr.size();\n" +
                "    for (int i = 0; i < n-1; i++) {\n" +
                "        for (int j = 0; j < n-i-1; j++) {\n" +
                "            if (arr[j] > arr[j+1]) {\n" +
                "                swap(arr[j], arr[j+1]);\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "}");
        bubbleMap.put("C", "void bubbleSort(int arr[], int n) {\n" +
                "    for (int i = 0; i < n-1; i++) {\n" +
                "        for (int j = 0; j < n-i-1; j++) {\n" +
                "            if (arr[j] > arr[j+1]) {\n" +
                "                int temp = arr[j];\n" +
                "                arr[j] = arr[j+1];\n" +
                "                arr[j+1] = temp;\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "}");
        codeMap.put("Bubble Sort", bubbleMap);

        // SELECTION SORT
        Map<String, String> selectionMap = new HashMap<>();
        selectionMap.put("Java", "void selectionSort(int arr[]) {\n" +
                "    int n = arr.length;\n" +
                "    for (int i = 0; i < n-1; i++) {\n" +
                "        int min_idx = i;\n" +
                "        for (int j = i+1; j < n; j++) {\n" +
                "            if (arr[j] < arr[min_idx])\n" +
                "                min_idx = j;\n" +
                "        }\n" +
                "        int temp = arr[min_idx];\n" +
                "        arr[min_idx] = arr[i];\n" +
                "        arr[i] = temp;\n" +
                "    }\n" +
                "}");
        selectionMap.put("Python", "def selection_sort(arr):\n" +
                "    n = len(arr)\n" +
                "    for i in range(n-1):\n" +
                "        min_idx = i\n" +
                "        for j in range(i+1, n):\n" +
                "            if arr[j] < arr[min_idx]:\n" +
                "                min_idx = j\n" +
                "        arr[i], arr[min_idx] = arr[min_idx], arr[i]");
        selectionMap.put("C++", "void selectionSort(vector<int>& arr) {\n" +
                "    int n = arr.size();\n" +
                "    for (int i = 0; i < n-1; i++) {\n" +
                "        int min_idx = i;\n" +
                "        for (int j = i+1; j < n; j++) {\n" +
                "            if (arr[j] < arr[min_idx])\n" +
                "                min_idx = j;\n" +
                "        }\n" +
                "        swap(arr[i], arr[min_idx]);\n" +
                "    }\n" +
                "}");
        selectionMap.put("C", "void selectionSort(int arr[], int n) {\n" +
                "    for (int i = 0; i < n-1; i++) {\n" +
                "        int min_idx = i;\n" +
                "        for (int j = i+1; j < n; j++) {\n" +
                "            if (arr[j] < arr[min_idx])\n" +
                "                min_idx = j;\n" +
                "        }\n" +
                "        int temp = arr[min_idx];\n" +
                "        arr[min_idx] = arr[i];\n" +
                "        arr[i] = temp;\n" +
                "    }\n" +
                "}");
        codeMap.put("Selection Sort", selectionMap);

        // INSERTION SORT
        Map<String, String> insertionMap = new HashMap<>();
        insertionMap.put("Java", "void insertionSort(int arr[]) {\n" +
                "    int n = arr.length;\n" +
                "    for (int i = 1; i < n; ++i) {\n" +
                "        int key = arr[i];\n" +
                "        int j = i - 1;\n" +
                "        while (j >= 0 && arr[j] > key) {\n" +
                "            arr[j + 1] = arr[j];\n" +
                "            j = j - 1;\n" +
                "        }\n" +
                "        arr[j + 1] = key;\n" +
                "    }\n" +
                "}");
        insertionMap.put("Python", "def insertion_sort(arr):\n" +
                "    for i in range(1, len(arr)):\n" +
                "        key = arr[i]\n" +
                "        j = i - 1\n" +
                "        while j >= 0 and key < arr[j]:\n" +
                "            arr[j + 1] = arr[j]\n" +
                "            j -= 1\n" +
                "        arr[j + 1] = key");
        insertionMap.put("C++", "void insertionSort(vector<int>& arr) {\n" +
                "    int n = arr.size();\n" +
                "    for (int i = 1; i < n; i++) {\n" +
                "        int key = arr[i];\n" +
                "        int j = i - 1;\n" +
                "        while (j >= 0 && arr[j] > key) {\n" +
                "            arr[j + 1] = arr[j];\n" +
                "            j = j - 1;\n" +
                "        }\n" +
                "        arr[j + 1] = key;\n" +
                "    }\n" +
                "}");
        insertionMap.put("C", "void insertionSort(int arr[], int n) {\n" +
                "    for (int i = 1; i < n; i++) {\n" +
                "        int key = arr[i];\n" +
                "        int j = i - 1;\n" +
                "        while (j >= 0 && arr[j] > key) {\n" +
                "            arr[j + 1] = arr[j];\n" +
                "            j = j - 1;\n" +
                "        }\n" +
                "        arr[j + 1] = key;\n" +
                "    }\n" +
                "}");
        codeMap.put("Insertion Sort", insertionMap);

        // BINARY SEARCH
        Map<String, String> binaryMap = new HashMap<>();
        binaryMap.put("Java", "int binarySearch(int arr[], int x) {\n" +
                "    int l = 0, r = arr.length - 1;\n" +
                "    while (l <= r) {\n" +
                "        int m = l + (r - l) / 2;\n" +
                "        if (arr[m] == x) return m;\n" +
                "        if (arr[m] < x) l = m + 1;\n" +
                "        else r = m - 1;\n" +
                "    }\n" +
                "    return -1;\n" +
                "}");
        binaryMap.put("Python", "def binary_search(arr, x):\n" +
                "    l, r = 0, len(arr) - 1\n" +
                "    while l <= r:\n" +
                "        m = l + (r - l) // 2\n" +
                "        if arr[m] == x:\n" +
                "            return m\n" +
                "        elif arr[m] < x:\n" +
                "            l = m + 1\n" +
                "        else:\n" +
                "            r = m - 1\n" +
                "    return -1");
        binaryMap.put("C++", "int binarySearch(vector<int>& arr, int x) {\n" +
                "    int l = 0, r = arr.size() - 1;\n" +
                "    while (l <= r) {\n" +
                "        int m = l + (r - l) / 2;\n" +
                "        if (arr[m] == x) return m;\n" +
                "        if (arr[m] < x) l = m + 1;\n" +
                "        else r = m - 1;\n" +
                "    }\n" +
                "    return -1;\n" +
                "}");
        binaryMap.put("C", "int binarySearch(int arr[], int l, int r, int x) {\n" +
                "    while (l <= r) {\n" +
                "        int m = l + (r - l) / 2;\n" +
                "        if (arr[m] == x) return m;\n" +
                "        if (arr[m] < x) l = m + 1;\n" +
                "        else r = m - 1;\n" +
                "    }\n" +
                "    return -1;\n" +
                "}");
        codeMap.put("Binary Search", binaryMap);

        // MERGE SORT
        Map<String, String> mergeMap = new HashMap<>();
        mergeMap.put("Java",
                "void mergeSort(int arr[], int l, int r) {\n" +
                "    if (l < r) {\n" +
                "        int m = l + (r - l) / 2;\n" +
                "        mergeSort(arr, l, m);\n" +
                "        mergeSort(arr, m + 1, r);\n" +
                "        merge(arr, l, m, r);\n" +
                "    }\n" +
                "}\n\n" +
                "void merge(int arr[], int l, int m, int r) {\n" +
                "    int n1 = m - l + 1, n2 = r - m;\n" +
                "    int L[] = new int[n1], R[] = new int[n2];\n" +
                "    for (int i=0; i<n1; i++) L[i]=arr[l+i];\n" +
                "    for (int j=0; j<n2; j++) R[j]=arr[m+1+j];\n" +
                "    int i=0, j=0, k=l;\n" +
                "    while (i<n1 && j<n2)\n" +
                "        arr[k++] = (L[i]<=R[j]) ? L[i++] : R[j++];\n" +
                "    while (i<n1) arr[k++]=L[i++];\n" +
                "    while (j<n2) arr[k++]=R[j++];\n" +
                "}");
        mergeMap.put("Python",
                "def merge_sort(arr):\n" +
                "    if len(arr) > 1:\n" +
                "        mid = len(arr) // 2\n" +
                "        L = arr[:mid]\n" +
                "        R = arr[mid:]\n" +
                "        merge_sort(L)\n" +
                "        merge_sort(R)\n" +
                "        i = j = k = 0\n" +
                "        while i < len(L) and j < len(R):\n" +
                "            if L[i] <= R[j]:\n" +
                "                arr[k] = L[i]; i += 1\n" +
                "            else:\n" +
                "                arr[k] = R[j]; j += 1\n" +
                "            k += 1\n" +
                "        while i < len(L):\n" +
                "            arr[k] = L[i]; i += 1; k += 1\n" +
                "        while j < len(R):\n" +
                "            arr[k] = R[j]; j += 1; k += 1");
        mergeMap.put("C++",
                "void merge(vector<int>& arr, int l, int m, int r) {\n" +
                "    vector<int> L(arr.begin()+l, arr.begin()+m+1);\n" +
                "    vector<int> R(arr.begin()+m+1, arr.begin()+r+1);\n" +
                "    int i=0, j=0, k=l;\n" +
                "    while (i<L.size() && j<R.size())\n" +
                "        arr[k++] = (L[i]<=R[j]) ? L[i++] : R[j++];\n" +
                "    while (i<L.size()) arr[k++]=L[i++];\n" +
                "    while (j<R.size()) arr[k++]=R[j++];\n" +
                "}\n\n" +
                "void mergeSort(vector<int>& arr, int l, int r) {\n" +
                "    if (l < r) {\n" +
                "        int m = l + (r - l) / 2;\n" +
                "        mergeSort(arr, l, m);\n" +
                "        mergeSort(arr, m+1, r);\n" +
                "        merge(arr, l, m, r);\n" +
                "    }\n" +
                "}");
        mergeMap.put("C",
                "void merge(int arr[], int l, int m, int r) {\n" +
                "    int n1=m-l+1, n2=r-m;\n" +
                "    int L[n1], R[n2];\n" +
                "    for (int i=0;i<n1;i++) L[i]=arr[l+i];\n" +
                "    for (int j=0;j<n2;j++) R[j]=arr[m+1+j];\n" +
                "    int i=0,j=0,k=l;\n" +
                "    while (i<n1 && j<n2)\n" +
                "        arr[k++]=(L[i]<=R[j])?L[i++]:R[j++];\n" +
                "    while (i<n1) arr[k++]=L[i++];\n" +
                "    while (j<n2) arr[k++]=R[j++];\n" +
                "}\n\n" +
                "void mergeSort(int arr[], int l, int r) {\n" +
                "    if (l < r) {\n" +
                "        int m = l + (r-l)/2;\n" +
                "        mergeSort(arr, l, m);\n" +
                "        mergeSort(arr, m+1, r);\n" +
                "        merge(arr, l, m, r);\n" +
                "    }\n" +
                "}");
        codeMap.put("Merge Sort", mergeMap);

        // QUICK SORT
        Map<String, String> quickMap = new HashMap<>();
        quickMap.put("Java",
                "int partition(int arr[], int low, int high) {\n" +
                "    int pivot = arr[high], i = low - 1;\n" +
                "    for (int j = low; j < high; j++) {\n" +
                "        if (arr[j] <= pivot) {\n" +
                "            i++;\n" +
                "            int t=arr[i]; arr[i]=arr[j]; arr[j]=t;\n" +
                "        }\n" +
                "    }\n" +
                "    int t=arr[i+1]; arr[i+1]=arr[high]; arr[high]=t;\n" +
                "    return i + 1;\n" +
                "}\n\n" +
                "void quickSort(int arr[], int low, int high) {\n" +
                "    if (low < high) {\n" +
                "        int pi = partition(arr, low, high);\n" +
                "        quickSort(arr, low, pi - 1);\n" +
                "        quickSort(arr, pi + 1, high);\n" +
                "    }\n" +
                "}");
        quickMap.put("Python",
                "def partition(arr, low, high):\n" +
                "    pivot = arr[high]\n" +
                "    i = low - 1\n" +
                "    for j in range(low, high):\n" +
                "        if arr[j] <= pivot:\n" +
                "            i += 1\n" +
                "            arr[i], arr[j] = arr[j], arr[i]\n" +
                "    arr[i+1], arr[high] = arr[high], arr[i+1]\n" +
                "    return i + 1\n\n" +
                "def quick_sort(arr, low, high):\n" +
                "    if low < high:\n" +
                "        pi = partition(arr, low, high)\n" +
                "        quick_sort(arr, low, pi - 1)\n" +
                "        quick_sort(arr, pi + 1, high)");
        quickMap.put("C++",
                "int partition(vector<int>& arr, int low, int high) {\n" +
                "    int pivot = arr[high], i = low - 1;\n" +
                "    for (int j = low; j < high; j++) {\n" +
                "        if (arr[j] <= pivot)\n" +
                "            swap(arr[++i], arr[j]);\n" +
                "    }\n" +
                "    swap(arr[i+1], arr[high]);\n" +
                "    return i + 1;\n" +
                "}\n\n" +
                "void quickSort(vector<int>& arr, int low, int high) {\n" +
                "    if (low < high) {\n" +
                "        int pi = partition(arr, low, high);\n" +
                "        quickSort(arr, low, pi - 1);\n" +
                "        quickSort(arr, pi + 1, high);\n" +
                "    }\n" +
                "}");
        quickMap.put("C",
                "int partition(int arr[], int low, int high) {\n" +
                "    int pivot=arr[high], i=low-1, t;\n" +
                "    for (int j=low; j<high; j++) {\n" +
                "        if (arr[j] <= pivot) {\n" +
                "            i++;\n" +
                "            t=arr[i]; arr[i]=arr[j]; arr[j]=t;\n" +
                "        }\n" +
                "    }\n" +
                "    t=arr[i+1]; arr[i+1]=arr[high]; arr[high]=t;\n" +
                "    return i + 1;\n" +
                "}\n\n" +
                "void quickSort(int arr[], int low, int high) {\n" +
                "    if (low < high) {\n" +
                "        int pi = partition(arr, low, high);\n" +
                "        quickSort(arr, low, pi - 1);\n" +
                "        quickSort(arr, pi + 1, high);\n" +
                "    }\n" +
                "}");
        codeMap.put("Quick Sort", quickMap);
    }

    public void setCode(String algorithmName) {
        this.currentAlgorithm = algorithmName;
        updateCodeView();
    }
    
    private void updateCodeView() {
        Map<String, String> langMap = codeMap.get(currentAlgorithm);
        if (langMap != null) {
            String code = langMap.getOrDefault(currentLanguage, "// Code not available in " + currentLanguage);
            codeArea.setText(code);
        } else {
            codeArea.setText("// Algorithm not found");
        }
        codeArea.setCaretPosition(0);
        titleLabel.setText("  " + currentAlgorithm.toUpperCase() + " // " + currentLanguage.toUpperCase());
    }
}
