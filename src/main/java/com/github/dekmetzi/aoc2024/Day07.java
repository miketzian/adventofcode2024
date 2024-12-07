package com.github.dekmetzi.aoc2024;

import com.github.dekmetzi.aoc2024.util.PuzzleInput;
import com.github.dekmetzi.aoc2024.util.PuzzleTimer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Day07 {

    private static final Logger logger
            = LoggerFactory.getLogger("[Day 6] Bridge Repair");

    enum Operator {
        ADD, MUL, CAT;

        public long apply(long a, long b) {
            switch (this) {
                case ADD:
                    return a + b;
                case MUL:
                    return a * b;
                case CAT:
                    return Long.parseLong(String.format("%d%d", a, b));
            }
            throw new IllegalStateException("no matches");
        }
    }

    static record Calibration(long testValue, List<Long> inputs) {

        static Calibration value(String input) {

            var x = input.split(": ");

            var vals = Arrays.stream(x[1].split(" "))
                    .map(Long::parseLong)
                    .toList();

            return new Calibration(Long.parseLong(x[0]), vals);
        }

        boolean isValid() {
            return isEqual(inputs.getFirst(), 1);
        }

        boolean isEqual(long current, int currentIx) {

            long next = inputs.get(currentIx++);
            boolean complete = currentIx == inputs.size();

            for (var op: Operator.values()) {
                long opValue = op.apply(current, next);
                // overall this was slower to include this check
                // even though if opValue > testValue it cannot ever return true.
                //if (opValue > testValue) {
                //    return false;
                //}
                if (complete && testValue == opValue) {
                    return true;
                } else if (!complete && isEqual(opValue, currentIx)){
                    return true;
                }
            }
            return false;
        }
    }

    public static void main(String[] args) {

        var timer = PuzzleTimer.start();
        try {
            long sum = 0;

            for (Calibration c: PuzzleInput.puzzleInput(7, false, Calibration::value)) {
                if (c.isValid())
                    sum += c.testValue();
            }
            logger.info("result: {} in {}", sum, timer.stop());

        } catch (RuntimeException e){
            logger.error("Exception after {}", timer.stop(), e);
        }
    }
}
