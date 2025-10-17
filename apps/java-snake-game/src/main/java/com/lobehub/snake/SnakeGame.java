package com.lobehub.snake;

import javax.swing.SwingUtilities;

/**
 * Application entry point. Launches the Snake game on the Event Dispatch Thread.
 */
public final class SnakeGame {

    private SnakeGame() {
        // Utility class
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameFrame frame = new GameFrame();
            frame.setVisible(true);
        });
    }
}
