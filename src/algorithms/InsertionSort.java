package algorithms;

import ui.VisualizerPanel;
import javax.swing.JSlider;

public class InsertionSort extends Sorter {

    public InsertionSort(VisualizerPanel visualizer, JSlider speedSlider) {
        super(visualizer, speedSlider);
    }

    @Override
    public void sort(int[] array) {
        int n = array.length;
        long startTime = System.currentTimeMillis();

        for (int i = 1; i < n; i++) {
            if (isStopped) return;
            
            int key = array[i];
            int j = i - 1;

            /* Move elements of array[0..i-1], that are greater than key, 
               to one position ahead of their current position */
            while (j >= 0 && array[j] > key) {
                if (isStopped) return;
                
                compare(array, j, j + 1); // Highlight elements being compared
                
                array[j + 1] = array[j];
                swaps++;
                
                visualizer.updateVisuals(array, j, j + 1);
                sleep();
                
                j = j - 1;
            }
            array[j + 1] = key;
            
            // The sub-array from 0 to i is now sorted
            visualizer.setSortedRange(0, i);
        }

        visualizer.setSortedRange(0, n - 1);
        visualizer.updateVisuals(array, -1, -1);
        
        timeTakenMs = System.currentTimeMillis() - startTime;
    }
}
