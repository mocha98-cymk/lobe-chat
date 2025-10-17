package com.lobehub.snake;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.prefs.Preferences;

/**
 * Core game logic including movement, collisions, scoring, and food generation.
 */
public class SnakeGameEngine {

    private static final String HIGH_SCORE_KEY = "snake.highScore";

    private final int columns;
    private final int rows;
    private final Random random = new Random();
    private final Preferences preferences;

    private Snake snake;
    private Position food;
    private boolean running;
    private boolean paused;
    private int score;
    private int highScore;
    private Direction queuedDirection;

    public SnakeGameEngine(int columns, int rows) {
        if (columns < 5 || rows < 5) {
            throw new IllegalArgumentException("Board size is too small");
        }
        this.columns = columns;
        this.rows = rows;
        this.preferences = Preferences.userNodeForPackage(SnakeGameEngine.class);
        this.highScore = preferences.getInt(HIGH_SCORE_KEY, 0);
        reset();
    }

    public void reset() {
        this.snake = Snake.createDefault(new Position(columns / 2, rows / 2));
        this.score = 0;
        this.running = true;
        this.paused = false;
        this.queuedDirection = null;
        spawnFood();
    }

    public void update() {
        if (!running || paused) {
            return;
        }

        applyQueuedDirection();

        Position nextHead = snake.nextHeadPosition();
        boolean willGrow = Objects.equals(nextHead, food);

        if (isOutOfBounds(nextHead) || snake.willCollideWithSelf(nextHead, willGrow)) {
            running = false;
            paused = false;
            updateHighScore();
            return;
        }

        snake.move(nextHead, willGrow);

        if (willGrow) {
            score += 10;
            if (snake.length() >= columns * rows) {
                running = false;
                updateHighScore();
            } else {
                spawnFood();
            }
        }
    }

    public void queueDirection(Direction direction) {
        if (!running || direction == null) {
            return;
        }
        if (!snake.canChangeTo(direction)) {
            return;
        }
        queuedDirection = direction;
    }

    public void togglePause() {
        if (!running) {
            return;
        }
        paused = !paused;
    }

    public List<Position> getSnakeSegments() {
        return snake.segments();
    }

    public Position getFoodPosition() {
        return food;
    }

    public int getScore() {
        return score;
    }

    public int getHighScore() {
        return highScore;
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isPaused() {
        return paused;
    }

    public int getSnakeLength() {
        return snake.length();
    }

    private void applyQueuedDirection() {
        if (queuedDirection != null) {
            snake.changeDirection(queuedDirection);
            queuedDirection = null;
        }
    }

    private boolean isOutOfBounds(Position position) {
        return position.x() < 0 || position.x() >= columns || position.y() < 0 || position.y() >= rows;
    }

    private void spawnFood() {
        if (snake.length() >= columns * rows) {
            food = null;
            return;
        }
        Set<Position> occupied = new HashSet<>(snake.segments());
        Position candidate;
        do {
            candidate = new Position(random.nextInt(columns), random.nextInt(rows));
        } while (occupied.contains(candidate));
        food = candidate;
    }

    private void updateHighScore() {
        if (score > highScore) {
            highScore = score;
            preferences.putInt(HIGH_SCORE_KEY, highScore);
        }
    }
}
