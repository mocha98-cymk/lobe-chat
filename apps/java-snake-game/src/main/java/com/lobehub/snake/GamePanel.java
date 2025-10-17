package com.lobehub.snake;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

/**
 * Primary rendering surface for the Snake game. Handles drawing and user input.
 */
public class GamePanel extends JPanel implements ActionListener {

    private static final int GRID_COLUMNS = 30;
    private static final int GRID_ROWS = 30;
    private static final int CELL_SIZE = 20;
    private static final int PANEL_WIDTH = GRID_COLUMNS * CELL_SIZE;
    private static final int PANEL_HEIGHT = GRID_ROWS * CELL_SIZE;
    private static final int BASE_DELAY = 130;

    private final SnakeGameEngine engine = new SnakeGameEngine(GRID_COLUMNS, GRID_ROWS);
    private final Timer timer;

    public GamePanel() {
        setBackground(Color.BLACK);
        setFocusable(true);
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        addKeyListener(new SnakeKeyAdapter());

        timer = new Timer(BASE_DELAY, this);
        timer.start();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        requestFocusInWindow();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawScene((Graphics2D) g);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (engine.isRunning() && !engine.isPaused()) {
            engine.update();
            adjustTimerSpeed();
        }
        repaint();
    }

    private void drawScene(Graphics2D g2d) {
        Graphics2D g = (Graphics2D) g2d.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawGrid(g);
        drawFood(g);
        drawSnake(g);
        drawHud(g);

        if (engine.isPaused()) {
            drawPauseOverlay(g);
        }
        if (!engine.isRunning()) {
            drawGameOver(g);
        }

        g.dispose();
    }

    private void drawGrid(Graphics2D g) {
        g.setColor(new Color(50, 50, 50));
        for (int x = 0; x <= PANEL_WIDTH; x += CELL_SIZE) {
            g.drawLine(x, 0, x, PANEL_HEIGHT);
        }
        for (int y = 0; y <= PANEL_HEIGHT; y += CELL_SIZE) {
            g.drawLine(0, y, PANEL_WIDTH, y);
        }
    }

    private void drawFood(Graphics2D g) {
        Position food = engine.getFoodPosition();
        if (food == null) {
            return;
        }
        int x = toPixels(food.x());
        int y = toPixels(food.y());

        g.setColor(new Color(220, 68, 90));
        g.fillRoundRect(x, y, CELL_SIZE, CELL_SIZE, 8, 8);
        g.setColor(Color.WHITE);
        g.drawRoundRect(x, y, CELL_SIZE, CELL_SIZE, 8, 8);
    }

    private void drawSnake(Graphics2D g) {
        List<Position> segments = engine.getSnakeSegments();
        if (segments.isEmpty()) {
            return;
        }
        Position head = segments.get(0);
        g.setColor(new Color(0, 200, 140));
        g.fillRect(toPixels(head.x()), toPixels(head.y()), CELL_SIZE, CELL_SIZE);

        g.setColor(new Color(0, 150, 105));
        for (int i = 1; i < segments.size(); i++) {
            Position segment = segments.get(i);
            g.fillRect(toPixels(segment.x()), toPixels(segment.y()), CELL_SIZE, CELL_SIZE);
        }
    }

    private void drawHud(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 16));
        String scoreText = "Score: " + engine.getScore();
        String highScoreText = "Best: " + engine.getHighScore();

        g.drawString(scoreText, 16, 24);
        g.drawString(highScoreText, 16, 48);

        g.setFont(new Font("SansSerif", Font.PLAIN, 13));
        g.drawString("Arrows or WASD to move", 16, PANEL_HEIGHT - 48);
        g.drawString("Press P to pause/resume", 16, PANEL_HEIGHT - 30);
        g.drawString("Press R to restart", 16, PANEL_HEIGHT - 12);
    }

    private void drawPauseOverlay(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 170));
        g.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);

        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 36));
        String paused = "Paused";
        int textWidth = g.getFontMetrics().stringWidth(paused);
        g.drawString(paused, (PANEL_WIDTH - textWidth) / 2, PANEL_HEIGHT / 2);
    }

    private void drawGameOver(Graphics2D g) {
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.75f));
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);

        g.setComposite(AlphaComposite.SrcOver);
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 42));
        String gameOver = "Game Over";
        int textWidth = g.getFontMetrics().stringWidth(gameOver);
        g.drawString(gameOver, (PANEL_WIDTH - textWidth) / 2, PANEL_HEIGHT / 2 - 20);

        g.setFont(new Font("SansSerif", Font.PLAIN, 20));
        String restart = "Press Enter or R to play again";
        int restartWidth = g.getFontMetrics().stringWidth(restart);
        g.drawString(restart, (PANEL_WIDTH - restartWidth) / 2, PANEL_HEIGHT / 2 + 20);
    }

    private void adjustTimerSpeed() {
        int length = engine.getSnakeLength();
        int boost = Math.min(60, Math.max(0, (length - 3) * 2));
        int newDelay = Math.max(55, BASE_DELAY - boost);
        if (timer.getDelay() != newDelay) {
            timer.setDelay(newDelay);
        }
    }

    private int toPixels(int gridCoordinate) {
        return gridCoordinate * CELL_SIZE;
    }

    private class SnakeKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT, KeyEvent.VK_A -> engine.queueDirection(Direction.LEFT);
                case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> engine.queueDirection(Direction.RIGHT);
                case KeyEvent.VK_UP, KeyEvent.VK_W -> engine.queueDirection(Direction.UP);
                case KeyEvent.VK_DOWN, KeyEvent.VK_S -> engine.queueDirection(Direction.DOWN);
                case KeyEvent.VK_P, KeyEvent.VK_SPACE -> engine.togglePause();
                case KeyEvent.VK_R -> restartGame();
                case KeyEvent.VK_ENTER -> {
                    if (!engine.isRunning()) {
                        restartGame();
                    }
                }
                default -> {
                    // no-op
                }
            }
        }

        private void restartGame() {
            engine.reset();
            timer.setDelay(BASE_DELAY);
            if (!timer.isRunning()) {
                timer.start();
            }
        }
    }
}
