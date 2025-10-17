package com.lobehub.snake;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

/**
 * Represents the snake entity, including its body segments and heading direction.
 */
public final class Snake {

    private final Deque<Position> body;
    private Direction direction;

    private Snake(Deque<Position> body, Direction direction) {
        this.body = body;
        this.direction = direction;
    }

    public static Snake createDefault(Position headPosition) {
        Deque<Position> segments = new ArrayDeque<>();
        segments.addFirst(headPosition);
        segments.addLast(new Position(headPosition.x() - 1, headPosition.y()));
        segments.addLast(new Position(headPosition.x() - 2, headPosition.y()));
        return new Snake(segments, Direction.RIGHT);
    }

    public void changeDirection(Direction newDirection) {
        if (canChangeTo(newDirection)) {
            this.direction = newDirection;
        }
    }

    public boolean canChangeTo(Direction newDirection) {
        return newDirection != null && !direction.isOpposite(newDirection);
    }

    public Position nextHeadPosition() {
        Position head = body.peekFirst();
        return head.translate(direction.dx(), direction.dy());
    }

    public void move(Position nextHead, boolean grow) {
        body.addFirst(nextHead);
        if (!grow) {
            body.removeLast();
        }
    }

    public boolean willCollideWithSelf(Position candidate, boolean willGrow) {
        int index = 0;
        int size = body.size();
        for (Position segment : body) {
            boolean isTail = index == size - 1;
            if (!willGrow && isTail) {
                break;
            }
            if (Objects.equals(segment, candidate)) {
                return true;
            }
            index++;
        }
        return false;
    }

    public List<Position> segments() {
        return List.copyOf(body);
    }

    public int length() {
        return body.size();
    }
}
