package com.github.dekmetzi.aoc2024;

import com.github.dekmetzi.aoc2024.util.PuzzleInput;
import com.github.dekmetzi.aoc2024.util.PuzzleTimer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Day05 {

    private static final Logger logger
            = LoggerFactory.getLogger("[Day 5] Print Queue");

    final List<OrderRule> rules;

    public Day05(List<OrderRule> rules) {
        this.rules = Collections.unmodifiableList(rules);
    }

    /**
     * Compare based on what the rules suggest
     */
    public Comparator<Integer> ruleSort() {
        return (a, b) -> {
            for (OrderRule rule: this.rules) {
                if (a == rule.a && b == rule.b) {
                    // a should come before b
                    return -1;
                } else if (a == rule.b && b == rule.a) {
                    // a should come after b
                    return 1;
                }
            }
            return 0;
        };
    }

    public record OrderRule(int a, int b) {

        public boolean ok(List<Integer> list) {
            int ax = list.indexOf(a), bx = list.indexOf(b);
            if (ax == -1 || bx == -1)
                return true;
            return ax < bx;
        }

        public static OrderRule from(String s) {
            var x = s.split("\\|", 2);
            return new OrderRule(Integer.parseInt(x[0]), Integer.parseInt(x[1]));
        }
    }

    public static List<Integer> findCorrect(List<Integer> update, final List<OrderRule> rules) {


        logger.debug("Input was not ok: {}", update);

        // a negative integer -> the first argument is less than the second
        // a positive integer -> the first argument is greater than the second.

        update = update.stream().sorted((a, b) -> {
            for (var rule: rules) {
                if (a == rule.a && b == rule.b) {
                    // a should come before b
                    return -1;
                } else if (a == rule.b && b == rule.a) {
                    // a should come after b
                    return 1;
                }
            }
            return 0;
        }).toList();

        for (OrderRule rule: rules) {
            if (!rule.ok(update)) {
                logger.debug("updated: {}", update);
                throw new RuntimeException("failed: " + rule);
            }
        }
        return update;
    }

    public static void main(String[] args) {

        List<OrderRule> rules = new LinkedList<>();

        var inputIterator = PuzzleInput.puzzleInput(5, false).iterator();

        while (inputIterator.hasNext()) {
            var v = inputIterator.next();
            if (v.isEmpty())
                break;
            rules.add(OrderRule.from(v));
        }

        logger.info("loaded {} rules", rules.size());

        var timer = PuzzleTimer.start();
        try {
            var day = new Day05(rules);
            int ok = 0;

            int sum = 0;
            int incorrectSum = 0;

            while (inputIterator.hasNext()) {
                // these are the test cases.
                var update = Arrays.stream(inputIterator.next().split(","))
                        .map(Integer::parseInt)
                        .toList();

                if (rules.stream().allMatch((rule) -> rule.ok(update))) {
                    var ix = ((update.size() - 1) / 2);
                    sum += update.get(ix);
                    logger.debug("ok: {}, mid: {}", update, update.get(ix));
                    ok++;
                } else {
                    // this entry is no good, we need to sort it.
                    var corrected = update.stream().sorted(day.ruleSort()).toList();
                    var ix = ((update.size() - 1) / 2);
                    logger.debug("corrected: {}, mid: {}", corrected, ix);
                    incorrectSum += corrected.get(ix);
                }
            }
            timer.stop();
            logger.info("Found {} first-pass-ok in {}, sum: {} ./ corrected-sum {}", ok, timer, sum, incorrectSum);

        } catch (RuntimeException e){
            logger.error("Exception after {}", timer.stop(), e);
        }
    }

}
