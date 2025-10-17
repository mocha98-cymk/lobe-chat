package com.lobehub.snake;

import javax.swing.JFrame;

/**
 * Main application window that hosts the game panel.
 */
public class GameFrame extends JFrame {

    public GameFrame() {
        super("LobeHub Snake");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        GamePanel gamePanel = new GamePanel();
        add(gamePanel);
        pack();
        setLocationRelativeTo(null);
    }
}
