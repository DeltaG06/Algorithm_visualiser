package algorithms;

import ui.VisualizerPanel;
import javax.swing.JSlider;

/**
 * Bubble Sort Implementation.
 * 
 * WHY Bubble Sort?
 * It's one of the simplest sorting algorithms. It repeatedly steps through the list, 
 * compares adjacent elements, and swaps them if they are in the wrong order.
 * The largest elements "bubble" to the top (end of the array) first.
 */
public class BubbleSort extends Sorter {

    public BubbleSort(VisualizerPanel visualizer, JSlider speedSlider) {
        super(visualizer, speedSlider);
    }

    @Override
    public void sort(int[] array) {
        int n = array.length;
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < n - 1; i++) {
            // isStopped allows us to cancel the sort if the user clicks Reset
            if (isStopped) return; 

            for (int j = 0; j < n - i - 1; j++) {
                if (isStopped) return;

                // Highlight elements being compared
                compare(array, j, j + 1);

                if (array[j] > array[j + 1]) {
                    // Highlight and swap if they are out of order
                    swap(array, j, j + 1);
                }
            }
            // After each full pass, the last element evaluated is guaranteed to be sorted.
            // We tell the visualizer to turn it neon green!
            visualizer.setSortedRange(n - 1 - i, n - 1);
        }
        
        // Mark the whole array as sorted when loop finishes
        visualizer.setSortedRange(0, n - 1); 
        
        // Clear active highlights
        visualizer.updateVisuals(array, -1, -1);
        
        timeTakenMs = System.currentTimeMillis() - startTime;
    }
}
