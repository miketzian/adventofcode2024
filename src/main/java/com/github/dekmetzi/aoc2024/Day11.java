package com.github.dekmetzi.aoc2024;

import com.github.dekmetzi.aoc2024.util.PuzzleInput;
import com.github.dekmetzi.aoc2024.util.PuzzleTimer;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.Buffer;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class Day11 {

    private static final Logger logger
            = LoggerFactory.getLogger("[Day 11] Plutonian Pebbles");

    Map<String, Long> data = new HashMap<>();

    public Day11(String input) {
        for (String s: input.split(" ")) {
            data.compute(s, (k, v) -> v != null ? v + 1L : 1L);
        }
    }

    static Day11 input(boolean test) {
        if (!test) {
            return new Day11("5 89749 6061 43 867 1965860 0 206250");
        } else {
            return new Day11("125 17");
        }
    }

    public long sum() {
        return data.values().stream().mapToLong((L) -> L).sum();
    }

     record Combine<T>(long value) implements BiFunction<T, Long, Long> {
        @Override
        public Long apply(T s, Long aLong) {
            if (aLong != null) {
                return aLong + value;
            }
            return value;
        }
    }

    public void blink() {

        // create the list of updates, then apply them
        // otherwise, earlier updates can conflict ie
        // 67, 7
        // -> 6x1 14168x2
        Map<String, Long> updates = new HashMap<>();

        for (var e: data.entrySet()) {

            var update = new Combine<String>(e.getValue());

            if ("0".equals(e.getKey())) {
                updates.compute("1", update);

            } else if (e.getKey().length() % 2 == 0) {
                var s = e.getKey();

                var k1 = s.substring(0, s.length() / 2);
                var k2 = s.substring(s.length() / 2);
                while (k2.startsWith("0") && !"0".equals(k2))
                    k2 = k2.substring(1);

                updates.compute(k1, update);
                updates.compute(k2, update);
            } else {
                updates.compute(String.valueOf(Long.parseLong(e.getKey()) * 2024), update);
            }
        }
        data = updates;
    }

    public static void main(String[] args) {

        var timer = PuzzleTimer.start();
        try {
            long sum = 0;
            {
                var input = input(false);
                logger.debug("Init: ({}) {}", input.sum(), input.data);

                for (int i=1; i<76; i++) {
                    input.blink();
                    if (i % 25 == 0)
                        logger.debug("{}: ({}) {}", i, input.sum(), input.data);
                }

                sum = input.sum();
            }
            logger.info("result: {} in {}", sum, timer.stop());

        } catch (RuntimeException e){
            logger.error("Exception after {}", timer.stop(), e);
        }
    }
}
