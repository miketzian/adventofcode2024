package com.github.dekmetzi.aoc2024;

import com.github.dekmetzi.aoc2024.util.PuzzleInput;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public record Day15(Warehouse w, MoveIter mi) {

    private static final Logger logger = LoggerFactory.getLogger("[Day 15] Warehouse Woes");



    public static class Warehouse {

        private final List<char[]> rows;
        private int x = 0;
        private int y = 0;
        final boolean doubleWide;

        public Warehouse(List<char[]> rows, int x, int y) {
            this.rows = rows;
            this.x = x;
            this.y = y;

            if (at(x, y) != '@')
                throw new IllegalStateException();

            doubleWide = rows.get(1)[1] == '#';
        }

        public long boxCoordsSum() {


            logger.debug("rows.[].size={}, rows.size={}", rows.getFirst().length, rows.size());
            long sum = 0;
            for (int i=0; i<rows.size(); i++) {
                for (int j=0; j<rows.get(i).length; j++) {
                    // The GPS coordinate of a box is equal to 100 times its distance from the top edge
                    // of the map plus its distance from the left edge of the map.
                    if (rows.get(i)[j] == 'O') {
                        sum += (i * 100L) + j;
                    }
                    else if (rows.get(i)[j] == '[') {
                        // distance to edge
//                        var lr = Math.min(
//                            j-1,
//                                rows.get(i).length - 3 - j /* minus 2+2 for border, minus 1 for other side */
//                        );
//                        var tb = Math.min(
//                                i, rows.size() - i  /* minus 1 for border, other side is equal */
//                        );
//
//                        int lr = Integer.MAX_VALUE;
//                        int t = 0;
//                        for (int ic=j; ic>0; ic--) {
//                            if (at(j, ))
//
//                        }
//
                        var lr = j;
                        var tb = i;

                        logger.debug("coord {}/{} = ({} * 100) + {} = {}", i, j, tb, lr, (tb * 100) + lr);
                        sum += (tb * 100L) + lr;
                        j++;
                    }
                }
            }
            return sum;
        }

        public char at(int x, int y) {
            return rows.get(y)[x];
        }

        public void swap(int x, int y, int x2, int y2) {
            logger.debug("swap {}/{} ({}) with {}/{} ({})", x, y, at(x, y), x2, y2, at(x2, y2));
            char c = at(x2, y2);
            if ('.' != c) {
                throw new RuntimeException("Cannot swap to occupied space!");
            }
            rows.get(y2)[x2] = at(x, y);
            rows.get(y)[x] = c;
        }

        record Track(int x, int y) {}

        public boolean wideMove(Move m, List<Track> tracks) {
            if (m == Move.LEFT || m == Move.RIGHT)
                throw new IllegalStateException();

            if (!tracks.stream().anyMatch((t) -> at(t.x, t.y) != '.')) {
                // if they are all clear, then return true.
                return true;
            }

            if (tracks.stream().anyMatch((t) -> at(t.x, t.y) == '#')) {
                // if any are blocked, then return false;
                return false;
            }

            char[] nextChars = new char[tracks.size()];
            int ix = 0;
            for (Track t: tracks) {
                nextChars[ix++] = at(t.x, t.y);
            }
            logger.debug("Moving: {}, {} -> {}", new String(nextChars), tracks.getFirst(), tracks.getLast());

            if (m.dX != 0)
                throw new IllegalStateException();

            Iterator<Track> trackIterator = tracks.iterator();
            if (!trackIterator.hasNext())
                throw new IllegalStateException("must have at least one");
            //

            LinkedList<Track> nextTrack = new LinkedList<>();

            Track next = trackIterator.next();
            var nc = at(next.x, next.y + m.dY);
            if (nc == ']') {
                // if the first one has a ], then we need to include one to the left.
                nextTrack.add(new Track(next.x - 1, next.y + m.dY));
            }
            // add the 1st one.
            nc = at(next.x, next.y + m.dY);
            if (nc != '.')
                nextTrack.add(new Track(next.x, next.y + m.dY));

            while (trackIterator.hasNext()) {
                next = trackIterator.next();
                nc = at(next.x, next.y + m.dY);
                if (nc != '.') {
                    Track a;
                    if (nc == ']') {
                        // make sure we have the one left.
                        var maybe = new Track(next.x - 1, next.y + m.dY);

                        // will either be getLast() or the prior one
                        var mi = nextTrack.descendingIterator();

                        if (!( (mi.hasNext() && mi.next().equals(maybe))
                            || (mi.hasNext() && mi.next().equals(maybe))
                        )) {
                            nextTrack.add(maybe);
                        }
                    }
                    a = new Track(next.x, next.y + m.dY);
                    // will be the last one, if there is one.
                    if (nextTrack.isEmpty() || !nextTrack.getLast().equals(a)) {
                        nextTrack.add(a);
                    }
                    if (nc == '[') {
                        nextTrack.add(new Track(next.x + 1, next.y + m.dY));
                    }
                }
            }

            if (!wideMove(m, nextTrack)) {
                // we can't move up/down, so return false.
                return false;
            }

            // at this point, the nextTrack items should be clear.
            for (Track nextC: nextTrack) {
                if (at(nextC.x, nextC.y) != '.')
                    throw new IllegalStateException("not clear");
            }

            for (Track t: tracks) {
                swap(t.x, t.y, t.x, t.y + m.dY);
            }
            return true;
        }

        public boolean move(Move m) {

            int nx = x + m.dX, ny = y + m.dY;
            char nc = at(nx, ny);
            if (nc == '#')
                return false;

            // m = Move.LEFT
            // m == Move.LEFT -> yes/no

            if (doubleWide && !(m == Move.LEFT || m == Move.RIGHT)) {
                Map<Track, List<Track>> widths = new HashMap<>();
                // can we move the blocks.
                if (nc == '[') {
                    if (!wideMove(m, List.of(new Track(nx, ny), new Track(nx + 1, ny)))) {
                        // no move happened.
                        return false;
                    }
                } else if (nc == ']') {
                    if (!wideMove(m, List.of(new Track(nx -1, ny), new Track(nx, ny)))) {
                        // no move happened.
                        return false;
                    }
                }
                if (at(nx, ny) != '.')
                    throw new IllegalStateException();
                swap(x, y, nx, ny);
            } else {
                while (nc != '.') {
                    nx += m.dX;
                    ny += m.dY;
                    nc = at(nx, ny);

                    if (nc == '#')
                        return false;
                }
                // nc == '.'
                // from x,y -> nx,ny is   @. @O. @OO. (etc).

                logger.debug("x={},y={} <- nx={},ny={}", x, y, nx, ny);

                while (nx != x || ny != y) {
                    swap(nx - m.dX, ny - m.dY, nx, ny);
                    nx -= m.dX;
                    ny -= m.dY;
                }
            }

            // now we're back at x, so we just increment where the robot is.

            // robots old place shoudl be blank
            if (at(x, y) != '.') {
                logger.debug("\n{}", this);
                throw new IllegalStateException(String.format("%d/%d was not ., was %c",
                        x, y, at(x, y)));
            }
            x += m.dX;
            y += m.dY;

            // robots new place should contain robot.
            if (at(x, y) != '@')
                throw new IllegalStateException(String.format("%d/%d was not @, was %c",
                        x, y, at(x, y)));

            return true;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            boolean f = true;
            for (char[] row: this.rows) {
                if (f) {
                    f = false;
                } else {
                    sb.append("\n");
                }
                sb.append(row);
            }
            return sb.toString();
        }
    }

    public enum Move{
        LEFT('<', -1, 0),
        RIGHT('>', 1, 0),
        UP('^', 0, -1),
        DOWN('v', 0, 1);

        final char rep;
        final int dX;
        final int dY;
        Move(char rep, int dX, int dY) {
            this.rep = rep;
            this.dX = dX;
            this.dY = dY;
        }

        static Move from(char c) {
            for (var m: values()) {
                if (m.rep == c) {
                    return m;
                }
            }
            throw new NoSuchElementException(String.valueOf(c));
        }
    }

    public record MoveIter(List<char[]> moves) implements Iterable<Move> {

        public Iterator<Move> iterator() {
            return new Iterator<Move>() {
                int x = 0;
                int y = 0;

                @Override
                public boolean hasNext() {
                    return moves.size() != x;
                }

                @Override
                public Move next() {
                    try {
                        var next = Move.from(moves.get(x)[y]);
                        y++;
                        if (y == moves.get(x).length) {
                            x++;
                            y = 0;
                        }
                        return next;
                    } catch (IndexOutOfBoundsException ignored) {
                        throw new NoSuchElementException();
                    }
                }
            };
        }
    }

    public static void part1() {

        List<char[]> map = Lists.newArrayList();
        List<char[]> moves = Lists.newArrayList();

        boolean robot = false;
        int x = 0;
        int y = -1;
        for (char[] s: PuzzleInput.puzzleInput(15, false, String::toCharArray)) {
            if (!robot) {
                if (s.length == 0) {
                    robot = true;
                } else {
                    map.add(s);
                    for (int i = 0; i < s.length; i++) {
                        if ('@' == s[i]) {
                            y = i;
                            break;
                        }
                    }
                }
                if (y == -1)
                    x++;
            } else {
                // robot moves.
                moves.add(s);
            }
        }

        var day = new Day15(new Warehouse(map, x, y), new MoveIter(moves));
        day.processMoves();
        var result = day.boxCoordsSum();

        logger.info("BoxCoords: {}", result);
    }

    public static Day15 from(Iterable<char[]> input) {
        List<char[]> map = Lists.newArrayList();
        List<char[]> moves = Lists.newArrayList();
        boolean robot = false;
        int x = 0;
        int y = -1;
        for (char[] s: input) {
            if (!robot) {
                if (s.length == 0) {
                    robot = true;
                } else {
                    map.add(s);
                    for (int i = 0; i < s.length; i++) {
                        if ('@' == s[i]) {
                            y = i;
                            break;
                        }
                    }
                }
                if (y == -1)
                    x++;
            } else {
                // robot moves.
                moves.add(s);
            }
        }
        return new Day15(new Warehouse(map, y, x), new MoveIter(moves));
    }

    public static void part2(boolean test) {

        List<char[]> map = Lists.newArrayList();
        List<char[]> moves = Lists.newArrayList();

        boolean robot = false;
        int x = 0;
        int y = -1;
        for (char[] s: PuzzleInput.puzzleInput(15, test, String::toCharArray)) {
            if (!robot) {
                if (s.length == 0) {
                    robot = true;
                } else {
                    char[] row = new char[s.length * 2];
                    int rx = 0;
                    for (int i = 0; i < s.length; i++) {
                        switch (s[i]) {
                            case '#':
                            case '.':
                                row[rx++] = s[i];
                                row[rx++] = s[i];
                                break;
                            case 'O':
                                row[rx++] = '[';
                                row[rx++] = ']';
                                break;
                            case '@':
                                y = rx;
                                row[rx++] = '@';
                                row[rx++] = '.';
                                break;
                            default:
                                throw new IllegalStateException("Unexpected: " + s[i]);
                        }
                    }
                    map.add(row);
                    if (y == -1)
                        x++;
                }
            } else {
                // robot moves.
                moves.add(s);
            }
        }

        var day = new Day15(new Warehouse(map, y, x), new MoveIter(moves));

        logger.debug("Initial State: \n{}", day.w());

        day.processMoves();
        var result = day.boxCoordsSum();
//
        logger.debug("Final: \n{}", day.w());

        // 1432998 = too high.

        logger.info("BoxCoords: {}", result);
    }

    public static void main(String[] args) {

        try {
            // part1();
            part2(false);

        } catch (RuntimeException re) {
            logger.error("Problem", re);
        }
    }

    private long boxCoordsSum() {
        return w.boxCoordsSum();
    }

    void processMoves() {
        for (Move m: this.mi) {
            // logger.debug("Move: {}", m);
            w.move(m);
            // logger.debug("\n{}", w);
        }
        logger.debug("{}\n", w);
    }
}
