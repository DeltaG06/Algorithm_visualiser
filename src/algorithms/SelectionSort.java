package algorithms;

import ui.VisualizerPanel;
import javax.swing.JSlider;

public class SelectionSort extends Sorter {

    public SelectionSort(VisualizerPanel visualizer, JSlider speedSlider) {
        super(visualizer, speedSlider);
    }

    @Override
    public void sort(int[] array) {
        int n = array.length;
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < n - 1; i++) {
            if (isStopped) return;

            // Find the minimum element in unsorted array
            int minIdx = i;
            for (int j = i + 1; j < n; j++) {
                if (isStopped) return;
                
                compare(array, j, minIdx);
                
                if (array[j] < array[minIdx]) {
                    minIdx = j;
                }
            }

            // Swap the found minimum element with the first element
            if (minIdx != i) {
                swap(array, minIdx, i);
            }
            
            // Mark the sorted portion (left to right)
            visualizer.setSortedRange(0, i);
        }

        // Entire array is sorted
        visualizer.setSortedRange(0, n - 1);
        visualizer.updateVisuals(array, -1, -1);
        
        timeTakenMs = System.currentTimeMillis() - startTime;
    }
}
