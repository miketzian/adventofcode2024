package com.github.dekmetzi.aoc2024;

import com.github.dekmetzi.aoc2024.util.PuzzleInput;
import com.github.dekmetzi.aoc2024.util.PuzzleTimer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Day02{

    private static final Logger logger
            = LoggerFactory.getLogger("[Day 2] Red-Nosed Reports");

    public static boolean isReportSafe(IReport r, boolean enableDampening) {
        if (r.isSafe()) {
            return true;
        } else if (enableDampening) {
            for (int ix=0; ix<r.length(); ix++) {
                if (r.without(ix).isSafe()) {
                    return true;
                }
            }
        }
        return false;
    }

    public interface IReport {
        int get(int ix);
        int length();
        default boolean isSafe() {
            int countIncr = 0, countDecr = 0;
            for (int i=1; i<length(); i++) {
                var diff = get(i) - get(i-1);
                if (diff > 0) {
                    if (countDecr != 0) {
                        return false;
                    }
                    countIncr++;
                } else if (diff == 0) {
                    return false;
                } else {
                    // diff < 0
                    if (countIncr != 0) {
                        return false;
                    }
                    countDecr++;
                }
                if (Math.abs(diff) > 3) {
                    return false;
                }
            }
            return true;
        }
        default IReport without(int dataPoint) {
            return new WithoutReport(this, dataPoint);
        }
    }

    public record WithoutReport(IReport wrapped, int dataPoint) implements IReport {

        public WithoutReport {
            if (dataPoint <0 || dataPoint >= wrapped.length())
                throw new IllegalArgumentException("dataPoint not in range");
        }

        @Override
        public int get(int ix) {
            return ix < dataPoint ? wrapped.get(ix) : wrapped.get(ix + 1);
        }

        @Override
        public int length() {
            return wrapped.length() - 1;
        }
    }

    public record Report(int[] data) implements IReport {

        public int get(int ix) {
            return data[ix];
        }
        public int length() {
            return data.length;
        }

        public static Report from(String str) {
            return new Report(Arrays.stream(str.split(" +")).mapToInt(Integer::parseInt).toArray());
        }
    }

    public static void main(String[] args) {

        for (Object r : PuzzleInput.puzzleInput(2, false, Report::from)) {
            // TBD why reading / parsing the input makes the subsequent code faster
            // presumably something loaded/cached somewhere
        }

        var timer = PuzzleTimer.start();
        try {
            var safe = new int[2];
            for (Report r : PuzzleInput.puzzleInput(2, false, Report::from)) {
                if (r.isSafe()) {
                    safe[0]++;
                    safe[1]++;
                } else if (isReportSafe(r, true)) {
                    safe[1]++;
                }
            }

            timer.stop();
            logger.info("Safe reports w/o dampening: {}", safe[0]);
            logger.info("Safe reports with dampening enabled: {}", safe[1]);
            logger.info("Completed in {}", timer);

        } catch (RuntimeException e){
            logger.error("Exception after {}", timer.stop(), e);
        }
    }
}
