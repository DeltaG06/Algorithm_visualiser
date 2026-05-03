package algorithms;

import ui.VisualizerPanel;
import javax.swing.JSlider;
import java.util.Arrays;
import java.util.Random;

public class BinarySearch extends Sorter {

    private int target = -1;
    private boolean targetFound = false;

    public BinarySearch(VisualizerPanel visualizer, JSlider speedSlider) {
        super(visualizer, speedSlider);
    }

    /**
     * Set the target value to search for.
     */
    public void setTarget(int target) {
        this.target = target;
    }

    public boolean wasFound() {
        return targetFound;
    }

    public int getTarget() {
        return target;
    }

    @Override
    public void sort(int[] array) {
        // Binary search requires a sorted array!
        Arrays.sort(array);
        visualizer.setSortedRange(0, array.length - 1);
        visualizer.updateVisuals(array, -1, -1);
        
        try {
            Thread.sleep(500); // Brief pause before search begins
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        if (isStopped) return;

        long startTime = System.currentTimeMillis();
        
        // If no target was set by the user, pick a random one
        if (target == -1) {
            target = array[new Random().nextInt(array.length)];
        }
        
        int left = 0;
        int right = array.length - 1;
        targetFound = false;
        
        while (left <= right) {
            if (isStopped) return;
            
            int mid = left + (right - left) / 2;
            
            compare(array, mid, right); 
            
            if (array[mid] == target) {
                visualizer.updateVisuals(array, mid, mid);
                targetFound = true;
                break;
            }
            
            if (array[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        
        // Final delay to show the result
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        
        timeTakenMs = System.currentTimeMillis() - startTime;
    }
}
