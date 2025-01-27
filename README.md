# Table of Contents

- [Summary](#summary)
- [Files Overview](#files-overview)
- [Heuristic Score Calculation](#heuristic-score-calculation)
- [Minimax Algorithm](#minimax-algorithm)
  - [Key Parts](#key-parts)
  - [Methods](#methods)
- [GUI Features](#gui-features)
- [Dependencies](#dependencies)
- [Demo Video](#demo-video)

## Summary

This project implements an AI agent for Connect Four using the Minimax algorithm with alpha-beta pruning.
The game features a graphical user interface (GUI) where users can play against the AI,
adjust the search depth, and toggle pruning.

## Files Overview

- **GUI.java:** Handles the game interface, user input, and display. It interacts with the Minimax AI to determine the AI's moves.
- **Minimax.java:** Contains the Minimax algorithm implementation, including heuristic evaluation and alpha-beta pruning.

## Heuristic Score Calculation

The heuristic function evaluates the board state by analyzing all possible sequences of four consecutive cells
(horizontal, vertical, diagonal).
For each sequence:

1. Count AI agent `a` and opponent `o` pieces in the sequence.
2. **Score adjustment:**
   - If the sequence contains only AI pieces:  
     `score += 10^(number of AI pieces)`
   - If the sequence contains only opponent pieces:  
     `score -= 10^(number of opponent pieces)`
   - Mixed/empty sequences do not affect the score.

This prioritizes creating/blocking potential wins and rewards the AI for controlling more cells in a sequence.

## Minimax Algorithm

### Key Parts

- **State Class:** Represents a game state with:

  - **`boardState`:** 42-character string (`a`, `o`, `#` for empty).
  - **`depth`:** Current depth in the search tree.
  - **`maxOrMin`:** Indicates if it's the AI's (maximizer) or opponent's (minimizer) turn.

- **Algorithm Flow**:

  1. **Maximizer:** Selects the move with the highest heuristic value from successors.
  2. **Minimizer:** Selects the move with the lowest heuristic value from successors.
  3. **Depth Limiting:** Stops recursion at a user-defined depth (default: 2) and returns the heuristic score.

- **Alpha-Beta Pruning:** Optimizes Minimax by pruning branches that cannot influence the final decision, reducing computation time.

### Methods

- **`value(State state)`:** Entry point for Minimax, delegates to `maximizer()` or `minimizer()`.
- **`abValue(State state, int alpha, int beta)`:** Alpha-beta variant of `value()`.
- **`getSuccessors(State state)`:** Generates valid next states for all columns.

## GUI Features

- **Interactive Board:** Click "Drop" buttons to place pieces.
- **Settings:**
  - **Depth Adjustment:** Controls how many moves ahead should the AI plans (2–7).
  - **Pruning Toggle:** Enables/disables alpha-beta pruning for faster decisions.
- **Win Detection:** Displays scores and results when the board is full.

## Dependencies

- Java Swing for GUI components.
- Image icons for board visuals (provided in the `src/` directory).

## Demo Video

[Connect-Four-AI-Agent Simulation](video.mp4)
