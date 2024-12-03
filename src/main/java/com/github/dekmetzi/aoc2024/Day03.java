package com.github.dekmetzi.aoc2024;

import com.github.dekmetzi.aoc2024.util.PuzzleInput;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.github.dekmetzi.aoc2024.util.PuzzleTimer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Day03 {

    private static final Logger logger
            = LoggerFactory.getLogger("[Day 3] Mull It Over");

    final Pattern part1Pattern = Pattern.compile("(mul\\((\\d+),(\\d+)\\))");

    final Pattern part2Pattern = Pattern.compile("(mul\\((\\d+),(\\d+)\\))|(do\\(\\))|(don't\\(\\))");

    public int calculateResults(Iterator<String> inputs, boolean part2) {

        int result = 0;
        boolean enabled = true;

        while (inputs.hasNext()) {
            var m = (part2 ? part2Pattern : part1Pattern).matcher(inputs.next());
            while(m.find()) {
                if (part2) {
                    if (m.group(5) != null) {
                        enabled = false;
                        continue;
                    }
                    else if (m.group(4) != null) {
                        enabled = true;
                        continue;
                    }
                }
                if (enabled)
                    result += Integer.parseInt(m.group(2)) * Integer.parseInt(m.group(3));
            }
        }
        return result;
    }

    public static void main(String[] args) {

        for (Object o: PuzzleInput.puzzleInput(3, false)) {

        }

        var timer = PuzzleTimer.start();
        try {
            var parser = new Day03();

            var part1 = parser.calculateResults(
                    PuzzleInput.puzzleInput(3, false).iterator(),
                    false);

            var part2 = parser.calculateResults(
                    PuzzleInput.puzzleInput(3, false).iterator(),
                    true);

            timer.stop();
            logger.info("Multiplication Result: {}", part1);
            logger.info("Multiplication Result with do/don't: {}", part2);
            logger.info("Completed in {}", timer);

        } catch (RuntimeException e){
            logger.error("Exception after {}", timer.stop(), e);
        }
    }

}
