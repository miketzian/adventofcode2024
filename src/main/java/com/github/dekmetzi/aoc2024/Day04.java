package com.github.dekmetzi.aoc2024;

import com.github.dekmetzi.aoc2024.util.PuzzleInput;
import com.github.dekmetzi.aoc2024.util.PuzzleTimer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class Day04 {

    private static final Logger logger
            = LoggerFactory.getLogger("[Day 4] Ceres Search");

    public int horizontalXmas(List<char[]> lines, int i, int j) {
        int c = 0;
        char[] line = lines.get(i);

        // XMAS horizontally forwards
        if (j+4 <= line.length
            && line[j+1] == 'M'
                && line[j+2] == 'A'
                && line[j+3] == 'S') {
            logger.debug("horizontal at {}, {}", i, j);
            c += 1;
        }

        // XMAS reversed
        if (j-3 >= 0
                && line[j-1] == 'M'
                && line[j-2] == 'A'
                && line[j-3] == 'S') {
            logger.debug("rev horizontal at {}, {}", i, j);
            c += 1;
        }
        return c;
    }

    public int verticalXmas(List<char[]> lines, int i, int j) {

        int c = 0;
        if (i-3 >= 0
            && lines.get(i-1)[j] == 'M'
            && lines.get(i-2)[j] == 'A'
            && lines.get(i-3)[j] == 'S'
        ) {
            logger.debug("reverse vertical at {}, {}", i, j);
            c+= 1;
        }

        if (i+3 < lines.size()
            && lines.get(i+1)[j] == 'M'
            && lines.get(i+2)[j] == 'A'
            && lines.get(i+3)[j] == 'S'
        ) {
            logger.debug("vertical at {}, {}", i, j);
            c+= 1;
        }
        return c;
    }

    public int diagonalXmas(List<char[]> lines, int i, int j) {

        int[][] dirs = {
                {-1, -1},
                {-1, 1},
                {1, -1},
                {1, 1}
        };

        int c = 0;

        for (int[]dir: dirs) {
            int x=i, y=j; // X
            try {
                x += dir[0];
                y += dir[1];
                if (lines.get(x)[y] != 'M')
                    continue;

                x += dir[0];
                y += dir[1];
                if (lines.get(x)[y] != 'A')
                    continue;

                x += dir[0];
                y += dir[1];
                if (lines.get(x)[y] != 'S')
                    continue;

                logger.debug("diagonal using {}/{} at {}/{}", dir[0], dir[1], i, j);
                c += 1;

            } catch (IndexOutOfBoundsException ignore) {
                // if the indexes don't exist in a given direction
                // then skip to the next one.
            }
        }

        if (c != 0)
            logger.debug("found {} diagonals total at {}/{}", c, i, j);

        return c;
    }


    public int findXShapedMas(List<char[]> lines) {

        int found = 0;
        for (int i=0; i<lines.size(); i++) {
            for (int j=0; j<lines.get(i).length; j++) {
                if (lines.get(i)[j] == 'A') {
                    // forward
                    try {
                        String m1 = new String(new char[]{
                                lines.get(i-1)[j-1],
                                lines.get(i)[j],
                                lines.get(i+1)[j+1]
                        }), m2 = new String(new char[]{
                                lines.get(i+1)[j-1],
                                lines.get(i)[j],
                                lines.get(i-1)[j+1]
                        });

                        if ("MAS".equals(m1) || "SAM".equals(m1)) {
                            if ("MAS".equals(m2) || "SAM".equals(m2)) {
                                logger.debug("line {}, line {} at {}/{}", m1, m2, i, j);
                                found +=1;
                            }
                        }
                    } catch (IndexOutOfBoundsException ignore) {
                        // skip if we can't make the X
                    }
                }
            }
        }
        return found;
    }

    public int findXmas(List<char[]> lines) {

        //long h = 0, v=0, d=0;

        int found = 0;
        for (int i=0; i<lines.size(); i++) {
            for (int j=0; j<lines.get(i).length; j++) {
                if (lines.get(i)[j] == 'X') {
                    //var t = PuzzleTimer.start();
                    found += horizontalXmas(lines, i, j);
                    //h += t.stop().ns();
                    //t = PuzzleTimer.start();
                    found += verticalXmas(lines, i, j);
                    //v += t.stop().ns();
                    //t = PuzzleTimer.start();
                    found += diagonalXmas(lines, i, j);
                    //d += t.stop().ns();
                }
            }
        }

        //logger.info("h={}, v={}, d={}", h, v, d);
        return found;
    }

    public static void main(String[] args) {

        // horizontal, vertical, diagonal, written backwards, or even overlapping other words

        List<char[]> lines = new ArrayList<>();
        for (String s: PuzzleInput.puzzleInput(4, false)) {
            lines.add(s.toCharArray());
        }

        var timer = PuzzleTimer.start();
        try {

            var day = new Day04();

            int found = day.findXmas(lines);
            timer.stop();
            logger.info("Found {} XMAS in {}", found, timer);

            timer = PuzzleTimer.start();
            found = day.findXShapedMas(lines);
            timer.stop();
            logger.info("Found {} Xes in {}", found, timer);

        } catch (RuntimeException e){
            logger.error("Exception after {}", timer.stop(), e);
        }
    }

}
