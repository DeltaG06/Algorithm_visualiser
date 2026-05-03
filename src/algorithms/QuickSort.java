package algorithms;

import ui.VisualizerPanel;
import javax.swing.JSlider;

/**
 * Quick Sort Implementation.
 * 
 * Picks a pivot element, partitions the array around it
 * (smaller left, larger right), then recursively sorts both sides.
 */
public class QuickSort extends Sorter {

    public QuickSort(VisualizerPanel visualizer, JSlider speedSlider) {
        super(visualizer, speedSlider);
    }

    @Override
    public void sort(int[] array) {
        long startTime = System.currentTimeMillis();
        quickSort(array, 0, array.length - 1);
        if (!isStopped) {
            visualizer.setSortedRange(0, array.length - 1);
            visualizer.updateVisuals(array, -1, -1);
        }
        timeTakenMs = System.currentTimeMillis() - startTime;
    }

    private void quickSort(int[] array, int low, int high) {
        if (isStopped) return;
        if (low < high) {
            int pi = partition(array, low, high);
            // Mark pivot as sorted
            visualizer.setSortedRange(pi, pi);
            quickSort(array, low, pi - 1);
            quickSort(array, pi + 1, high);
        }
    }

    private int partition(int[] array, int low, int high) {
        int pivot = array[high]; // Choose last element as pivot
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (isStopped) return i + 1;
            // Compare current element against the pivot
            compare(array, j, high);
            if (array[j] <= pivot) {
                i++;
                swap(array, i, j);
            }
        }
        // Place pivot in its correct position
        swap(array, i + 1, high);
        return i + 1;
    }
}
