# Java Snake Game 🐍

A classic Snake implementation built with Java Swing. The project demonstrates a clean object-oriented design that separates rendering, engine logic, and domain models.

## Features

- **Responsive Swing-based game window** with keyboard controls (arrow keys or WASD)
- **Smooth movement loop** driven by a Swing `Timer`
- **Random food generation** that avoids the snake's body
- **Automatic growth and scoring** when food is collected
- **Collision detection** against walls and the snake itself
- **Pause and resume** with `P`/space, restart with `R` or `Enter`
- **Persistent high score** stored via `java.util.prefs.Preferences`
- **Progressive difficulty** - speed increases as the snake grows

## Getting Started

Compile and run with any modern JDK (17 or newer recommended):

```bash
cd apps/java-snake-game

# Compile
javac -d out $(find src/main/java -name "*.java")

# Run
java -cp out com.lobehub.snake.SnakeGame
```

## Controls

- **Arrow keys / WASD** – change direction
- **P** or **Space** – pause or resume
- **R** or **Enter** – restart after game over

## Architecture

The project follows clean separation of concerns:

- **SnakeGame** – Application entry point
- **GameFrame** – Main window wrapper
- **GamePanel** – Rendering and input handling
- **SnakeGameEngine** – Core game logic (collisions, scoring, state)
- **Snake** – Snake entity behavior
- **Direction** – Movement direction enum
- **Position** – Immutable coordinate record

## Game Mechanics

- **Grid**: 30×30 cells, each 20 pixels
- **Initial snake**: 3 segments
- **Base speed**: 130ms per frame
- **Speed increase**: 2ms faster per segment (capped at 55ms minimum)
- **Score**: 10 points per food eaten

Enjoy the game! 🎉
