# Pepse Game Project - README

This project implements a dynamic 2D game world using the Danogl engine. 
The world includes terrain generation, animated weather, day-night cycles, 
an avatar with energy mechanics, and interactive objects such as trees and collectible fruits.

===============================
MODULE OVERVIEW
===============================

1. Terrain
----------
- Dynamically generates terrain blocks using Perlin noise.
- Exposes `createInRange(minX, maxX)` to create blocks in a specific X range.
- Each block is represented by the `Block` class.
- The terrain's base height is determined by the y-coordinate of the window.

2. Trees (Flora)
----------------
- The `Flora` class manages tree creation.
- Each `Tree` is composed of:
  - A trunk (stack of blocks).
  - Several `Leaf` objects (animated with wind sway).
  - One or more `Fruit` objects (collectible and regenerating).
- Leaves and fruits are placed in randomized positions near the trunk.

3. Weather (Clouds and Rain)
----------------------------
- The `CloudsManager` creates animated `Cloud` objects that float across the screen.
- On avatar jump, rain drops are triggered from all clouds using a repeating `ScheduledTask`.
- Drops fall with opacity fade-out to simulate rain animation.

4. Avatar and Energy System
---------------------------
- The `Avatar` class handles movement and jump mechanics, as well as energy consumption and regeneration.
- The `EnergyPanel` displays current energy level in the UI using colored rectangles.

5. Day-Night Cycle
------------------
- Includes three components:
  - `Sun`: moves across the sky.
  - `SunHalo`: halo effect that follows the sun.
  - `Night`: semi-transparent dark layer that simulates nighttime.
- The full cycle duration is configurable (default: 30 seconds).

===============================
DESIGN PATTERNS USED
===============================

- Component-based design: Most game objects are composed rather than extended.
  For example, trees are built from trunk, leaf, and fruit components.
- Observer pattern: `CloudsManager` implements `JumpObserver` and listens to avatar jumps.
  When the avatar jumps, the manager triggers rain using a repeating `ScheduledTask`.

===============================
HOW TO RUN THE GAME
===============================

1. Run `PepseGameManager.main()` from your IDE or terminal.
2. Use arrow keys (or configured controls) to move and jump.
3. Observe:
   - Avatar energy depletion while moving.
   - Rain when jumping.
   - Day-night transitions every 30 seconds.
   - Collectible fruits near trees that replenish energy.

===============================
NOTES
===============================

- All objects respect their coordinate space (world vs camera).
- Magic numbers were minimized using constants for maintainability.
- Each class is documented with JavaDoc or inline comments.
- Code style follows the university's coding style guide.

===============================
AUTHORS
===============================

Developed by: Zvi Marmor, Adi Zuarets
Course: Object-Oriented Programming, Hebrew University
Year: 2025
