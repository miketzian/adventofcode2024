package com.github.dekmetzi.aoc2024;

import com.github.dekmetzi.aoc2024.util.PuzzleInput;
import com.github.dekmetzi.aoc2024.util.PuzzleTimer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Day06 {

    private static final Logger logger
            = LoggerFactory.getLogger("[Day 6] Guard Gallivant");

    // actual grid that is checked/updated
    final List<char[]> grid;

    // used only for copies.
    static List<char[]> orig_grid = null;

    public Day06(List<char[]> grid) {
        this.grid = Collections.unmodifiableList(grid);
        if (orig_grid == null)
            orig_grid = grid.stream()
                .map((arr) -> {
                    var newA = new char[arr.length];
                    System.arraycopy(arr, 0, newA, 0, arr.length);
                    return newA;
                }).toList();
    }

    public boolean wouldBlock(int x, int y, int px, int py, Direction d) {

        var sim = new Day06(orig_grid.stream()
                .map((arr) -> {
                    var newA = new char[arr.length];
                    System.arraycopy(arr, 0, newA, 0, arr.length);
                    return newA;
                })
                .toList());
        try {
            sim.grid.get(x)[y] = '#';
            var simGuard = new Guard(px, py, d);
            Set<CoordinateWithDirection> seen = new HashSet<>();

            int max = 1_000_000;
            while (--max != 0) {
                if (!seen.add(new CoordinateWithDirection(simGuard.x, simGuard.y, simGuard.d))) {
                    // we are in a loop
                    return true;
                }
                simGuard.move(sim);
            }
            throw new IllegalStateException("ran out of moves");
        } catch (GuardLeavesException ignored) {
            return false;
        } finally {
            sim.grid.get(x)[y] = '.';
        }
    }

    public char grid(int x, int y) {
        try {
            return this.grid.get(x)[y];
        } catch (IndexOutOfBoundsException ignored) {
            return 'X';
        }
    }

    public record Coordinate(int x, int y) {}
    public record CoordinateWithDirection(int x, int y, Direction d) {}

    public enum Direction {
        U("R", '^'), D("L", 'v'), L("U", '<'), R("D", '>');

        public final String r90;
        public final char dir;

        Direction(String r90, char dir) {
            this.r90 = r90;
            this.dir = dir;
        }

        public Direction r90() {
            return Direction.valueOf(this.r90);
        }
    }

    public boolean noObstacle(int x, int y) throws GuardLeavesException {
        try {
            if (this.grid.get(x)[y] != '#') {
                return true;
            }
        } catch (IndexOutOfBoundsException ignored) {
            throw new GuardLeavesException();
        }
        return false;
    }

    public static class GuardLeavesException extends Exception {
        public GuardLeavesException() {
            super();
        }
    }

    static class Guard {
        int x;
        int y;
        Direction d;
        public Guard(int x, int y, Direction facing) {
            this.x = x;
            this.y = y;
            this.d = facing;
        }

        public CoordinateWithDirection next(Day06 main) throws GuardLeavesException {
            switch (d) {
                case U:
                    // cannot go straight
                    if (main.noObstacle(x - 1, y)) {
                        return new CoordinateWithDirection(this.x-1, this.y, this.d);
                    }
                    break;
                case D:
                    if (main.noObstacle(x + 1, y)) {
                        return new CoordinateWithDirection(this.x+1, this.y, this.d);
                    }
                    break;
                case L:
                    if (main.noObstacle(x, y - 1)) {
                        return new CoordinateWithDirection(this.x, this.y-1, this.d);
                    }
                    break;
                case R:
                    if (main.noObstacle(x, y + 1)) {
                        return new CoordinateWithDirection(this.x, this.y+1, this.d);
                    }
            }
            // obstacle case
            return new CoordinateWithDirection(this.x, this.y, this.d.r90());
        }

        public void move(Day06 main) throws GuardLeavesException {

            int ox = this.x, oy = this.y;

            var nn = this.next(main);
            this.x = nn.x;
            this.y = nn.y;
            this.d = nn.d;

            main.grid.get(this.x)[this.y] = this.d.dir;
        }
    }

    public static void main(String[] args) {

        // horizontal, vertical, diagonal, written backwards, or even overlapping other words

        List<char[]> lines = new ArrayList<>();

        Guard g = null;
        for (String s: PuzzleInput.puzzleInput(6, false)) {
            int gx = s.indexOf("^");
            if (gx != -1) {
                if (g != null)
                    throw new IllegalStateException("two guards");
                g = new Guard(lines.size(), gx, Direction.U);
            }
            lines.add(s.toCharArray());
        }
        Objects.requireNonNull(g);
        int sx = g.x, sy = g.y;

        var timer = PuzzleTimer.start();
        try {
            if (g == null)
                throw new IllegalStateException("no guard");

            var day = new Day06(lines);

            Set<Coordinate> seenNodes = new HashSet<>();

            // prevent runaway loop
            int max = 50000;
            try {
                while (max != 0) {
                    // the guard moves, and is updated with it's
                    // new position and direction
                    g.move(day);
                    seenNodes.add(new Coordinate(g.x, g.y));
                    max--;
                }
                throw new IllegalStateException("no moves left");
            } catch (GuardLeavesException ignored) {

            }

            // for all the nodes in the path, check if blocking it would
            // result in a loop.
            long loopFormingNodeCount = seenNodes.parallelStream()
                    .filter((cc) -> day.wouldBlock(cc.x, cc.y, sx, sy, Direction.U))
                    .count();

            logger.info("guard moved to {} unique places, could turn {}, in {}",
                    seenNodes.size() + 1 /* start */,
                    loopFormingNodeCount, timer.stop());

            // 23:34:41.286 INFO  [Day 6] Guard Gallivant - guard moved to 5177 unique places, could turn 1686, in 400ms (400,855,000ns)

        } catch (RuntimeException e){
            logger.error("Exception after {}", timer.stop(), e);
        }
    }


    public String toString() {
        var x = new StringBuilder();
        for (var y: this.grid) {
            x.append(y).append('\n');
        }
        return x.toString();
    }
}
