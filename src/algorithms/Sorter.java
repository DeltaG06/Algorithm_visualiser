package algorithms;

import ui.VisualizerPanel;
import javax.swing.JSlider;

/**
 * Abstract base class for all sorting algorithms.
 * 
 * WHY an abstract class?
 * Every sorting algorithm needs to do three things:
 * 1. Track comparisons and swaps.
 * 2. Update the VisualizerPanel.
 * 3. Pause/sleep so the user can see the animation.
 * By putting this logic here, we don't have to rewrite it for every algorithm!
 */
public abstract class Sorter {

    public enum Operation { IDLE, COMPARING, SWAPPING }

    protected VisualizerPanel visualizer;
    protected JSlider speedSlider;
    
    // Metrics for Step 5 (Database)
    public int comparisons = 0;
    public int swaps = 0;
    public long timeTakenMs = 0;

    // Current operation state (used by the visualizer for drawing indicators)
    public volatile Operation currentOp = Operation.IDLE;

    // Control flags for pausing and stopping the thread
    public volatile boolean isPaused = false;
    public volatile boolean isStopped = false;

    // Step-by-step execution support
    public volatile boolean stepSignal = false;

    public void requestStep() {
        stepSignal = true;
    }

    public Sorter(VisualizerPanel visualizer, JSlider speedSlider) {
        this.visualizer = visualizer;
        this.speedSlider = speedSlider;
    }

    /**
     * The main sort method that child classes will implement.
     */
    public abstract void sort(int[] array);

    /**
     * Swaps two elements in the array and updates the visuals.
     */
    protected void swap(int[] array, int i, int j) {
        if (isStopped) return;
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
        swaps++;
        
        currentOp = Operation.SWAPPING;
        visualizer.updateVisuals(array, i, j);
        sleep();
        currentOp = Operation.IDLE;
    }

    /**
     * Highlights two elements being compared without swapping.
     */
    protected void compare(int[] array, int i, int j) {
        if (isStopped) return;
        comparisons++;
        currentOp = Operation.COMPARING;
        visualizer.updateVisuals(array, i, j);
        sleep();
        currentOp = Operation.IDLE;
    }

    /**
     * Handles the animation delay and pausing mechanism.
     * 
     * WHY sleep?
     * Computers sort arrays in milliseconds. If we don't pause the thread,
     * the sort will finish instantly and we won't see the animation!
     */
    protected void sleep() {
        try {
            // If the user clicks pause, we loop here doing nothing until unpaused
            // or until a step signal is received
            while (isPaused && !isStopped) {
                if (stepSignal) {
                    stepSignal = false;
                    return; // Allow exactly one step to proceed
                }
                Thread.sleep(50);
            }
            if (isStopped) return;

            // Sleep based on the speed slider's value
            Thread.sleep(speedSlider.getValue());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void pause() {
        isPaused = true;
    }

    public void resume() {
        isPaused = false;
    }

    public void stop() {
        isStopped = true;
    }
}
