package nz.ac.wgtn.swen225.lc.App;

import javax.swing.SwingUtilities;

/**
 * The main class that serves as the entry point for the Chaps Challenge game application.
 * 
 * @author greenthom.
 */
public class Main {
    /**
     * The main method of the Chaps Challenge game application.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Initialize the game using the singleton pattern
            Game.singleton();
        });
    }
}