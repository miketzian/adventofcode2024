package com.github.dekmetzi.aoc2024;

import com.github.dekmetzi.aoc2024.util.PuzzleInput;
import com.github.dekmetzi.aoc2024.util.PuzzleTimer;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Day08 {

    private static final Logger logger
            = LoggerFactory.getLogger("[Day 8] Resonant Collinearity");

    public record Node(int x, int y) {

        public static Node from(String x) {
            var y = x.split(",", 2);
            return new Node(Integer.parseInt(y[0]), Integer.parseInt(y[1]));
        }

        public List<Node> antinodes(Node other, int xLen, int yLen, boolean cont) {

            var xDiff = Math.abs(other.x - this.x);
            var yDiff = Math.abs(other.y - this.y); // bc 0 based

            var xMin = Math.min(this.x, other.x);
            var yMin = Math.min(this.y, other.y);

            int x1d = this.x == xMin ? -xDiff : xDiff;
            int y1d = this.y == yMin ? -yDiff : yDiff;

            int x1 = this.x + x1d;
            int y1 = this.y + y1d;

            List<Node> antinodes = Lists.newArrayListWithCapacity(2);

            if (cont) {
                while (x1 < xLen && y1 < yLen && x1 >= 0 && y1 >= 0) {
                    antinodes.add(new Node(x1, y1));
                    x1 += x1d;
                    y1 += y1d;
                }
            } else if (x1 < xLen && y1 < yLen && x1 >= 0 && y1 >= 0) {
                antinodes.add(new Node(x1, y1));
            }

            int x2d = other.x == xMin ? -xDiff :  xDiff;
            int y2d = other.y == yMin ? -yDiff : yDiff;

            int x2 = other.x + x2d;
            int y2 = other.y + y2d;

            if (cont) {
                while (x2 < xLen && y2 < yLen && x2 >= 0 && y2 >= 0) {
                    antinodes.add(new Node(x2, y2));
                    x2 += x2d;
                    y2 += y2d;
                }
            } else if (x2 < xLen && y2 < yLen && x2 >= 0 && y2 >= 0)
                antinodes.add(new Node(x2, y2));

            return antinodes;
        }
    }

    public static record ListCombiner(List<Node> nodes) implements Iterable<Node[]> {

        public ListCombiner {
            if (nodes.size() < 2)
                throw new IllegalArgumentException();
        }

        public Iterator<Node[]> iterator() {

            return new Iterator<Node[]>() {

                int ix1=0;
                int ix2=1;

                @Override
                public boolean hasNext() {
                    if (ix2 == nodes.size()) {
                        ix1++;
                        ix2 = ix1 + 1;
                    }
                    return ix1 != nodes.size() && ix2 != nodes.size();
                }

                @Override
                public Node[] next() {
                    return new Node[]{
                            nodes.get(ix1),
                            nodes.get(ix2++)
                    };
                }
            };
        }
    }


    public static void main(String[] args) {

        var timer = PuzzleTimer.start();
        try {
            long sum = 0;

            Map<Character, List<Node>> chars = new HashMap<>();

            // Set<Node> cNodes = new HashSet<>();

            int x = 0;
            int yLen = -1;

            List<char[]> debug = Lists.newArrayList();

            for (char[] s: PuzzleInput.puzzleInput(8, false, (S) -> S.toCharArray())) {
                debug.add(s);
                if (yLen == -1)
                    yLen = s.length;

                for (int i=0; i<s.length; i++) {
                    if (s[i] != '.') {
                        final var r = new Node(x, i);
                        // cNodes.add(r);
                        chars.compute(s[i], (key, value) -> {
                            if (value == null) {
                                return Lists.newArrayList(r);
                            }
                            value.add(r);
                            return value;
                        });
                    }
                }
                x++;
            }
            var xLen = x;

            Set<Node> antinodes = new HashSet<>();

            for (var e: chars.entrySet()) {

                // for each pair of nodes
                for (Node[] comb: new ListCombiner(e.getValue())) {
                    for (Node x2: comb[0].antinodes(comb[1], xLen, yLen, true)) {
                        antinodes.add(x2);
                    }
                }
            }

            // part 1 doesn't care about this.
            // antinodes.removeAll(cNodes);

            for (Node n: antinodes) {
                System.out.println(n);
                if (debug.get(n.x)[n.y] == '.')
                    debug.get(n.x)[n.y] = '#';
            }

            for (List<Node> cc: chars.values()) {
                if (cc.size() > 1)
                    antinodes.addAll(cc);
            }

            System.out.println("== done ==");
            for (var line: debug) {
                System.out.println(new String(line));
            }
            // part 1
            // 277 ? too high

            // 260 - too low
            logger.info("result: {} in {}", antinodes.size(), timer.stop());

        } catch (RuntimeException e){
            logger.error("Exception after {}", timer.stop(), e);
        }
    }
}
