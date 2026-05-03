package algorithms;

import ui.VisualizerPanel;
import javax.swing.JSlider;
import java.util.Arrays;

/**
 * Merge Sort Implementation with Divide & Conquer visualization.
 * 
 * During the merge step, switches the visualizer into DC mode
 * showing three arrays: LEFT, RIGHT, and MERGED result.
 */
public class MergeSort extends Sorter {

    public MergeSort(VisualizerPanel visualizer, JSlider speedSlider) {
        super(visualizer, speedSlider);
    }

    @Override
    public void sort(int[] array) {
        long startTime = System.currentTimeMillis();
        mergeSort(array, 0, array.length - 1);
        if (!isStopped) {
            visualizer.setDCMode(false);
            visualizer.setSortedRange(0, array.length - 1);
            visualizer.updateVisuals(array, -1, -1);
        }
        timeTakenMs = System.currentTimeMillis() - startTime;
    }

    private void mergeSort(int[] array, int left, int right) {
        if (isStopped) return;
        if (left < right) {
            int mid = left + (right - left) / 2;

            // Show the current sub-array being split in normal mode
            visualizer.setDCMode(false);
            compare(array, left, right);

            mergeSort(array, left, mid);
            mergeSort(array, mid + 1, right);
            merge(array, left, mid, right);
        }
    }

    private void merge(int[] array, int left, int mid, int right) {
        if (isStopped) return;

        int n1 = mid - left + 1;
        int n2 = right - mid;

        int[] L = new int[n1];
        int[] R = new int[n2];

        System.arraycopy(array, left, L, 0, n1);
        System.arraycopy(array, mid + 1, R, 0, n2);

        // Build the merged result incrementally
        int[] result = new int[0];
        int i = 0, j = 0, k = left;

        // Show initial state in DC mode
        visualizer.updateDCVisuals(L, R, result, 0, 0, -1);
        sleep();

        while (i < n1 && j < n2) {
            if (isStopped) return;

            // Highlight the two elements being compared
            visualizer.updateDCVisuals(L, R, result, i, j, -1);
            comparisons++;
            sleep();

            if (L[i] <= R[j]) {
                array[k] = L[i];
                result = appendToArray(result, L[i]);
                i++;
            } else {
                array[k] = R[j];
                result = appendToArray(result, R[j]);
                j++;
            }
            swaps++;
            k++;

            // Show placement into merged
            visualizer.updateDCVisuals(L, R, result, i < n1 ? i : -1, j < n2 ? j : -1, result.length - 1);
            sleep();
        }

        // Copy remaining from L
        while (i < n1) {
            if (isStopped) return;
            array[k] = L[i];
            result = appendToArray(result, L[i]);
            swaps++;
            i++; k++;
            visualizer.updateDCVisuals(L, R, result, i < n1 ? i : -1, -1, result.length - 1);
            sleep();
        }

        // Copy remaining from R
        while (j < n2) {
            if (isStopped) return;
            array[k] = R[j];
            result = appendToArray(result, R[j]);
            swaps++;
            j++; k++;
            visualizer.updateDCVisuals(L, R, result, -1, j < n2 ? j : -1, result.length - 1);
            sleep();
        }

        // Switch back to normal mode to show updated main array
        visualizer.setDCMode(false);
        visualizer.updateVisuals(array, left, right);
        sleep();
    }

    /**
     * Appends a value to an array, returning a new, larger array.
     */
    private int[] appendToArray(int[] arr, int value) {
        int[] newArr = Arrays.copyOf(arr, arr.length + 1);
        newArr[arr.length] = value;
        return newArr;
    }
}
