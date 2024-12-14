package com.github.dekmetzi.aoc2024;

import com.github.dekmetzi.aoc2024.util.PuzzleInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;

public class Day13 {

    private static final Logger logger = LoggerFactory.getLogger("[Day 13] Claw Contraption");

    public record LinearResult(long a, long b) {

        public long tokens() {
            return (a*3) + b;
        }
    }

    public static Optional<LinearResult> solve(LinearAdd eq1, LinearAdd eq2) {

        logger.debug("1: {}", eq1);
        logger.debug("2: {}", eq2);

        long lcm = lcm(eq1.a, eq2.a);

        long mul1 = lcm / eq1.a;
        long mul2 = lcm / eq2.a;

        var eq1m = eq1.mul(mul1);
        var eq2m = eq2.mul(mul2);

        logger.debug("1: {}", eq1m);
        logger.debug("2: {}", eq2m);

        var res = eq1m.sub(eq2m);

        logger.debug("3: {}", res);

        if (res.a != 0)
            throw new IllegalStateException();

        double bVal = (double)res.e / res.b;
        if (bVal < 0)
            bVal *= -1;

        logger.debug("bVal: {}", bVal);

        if (Math.floor(bVal) != bVal) {
            return Optional.empty();
        }

        Optional<Long> aVal = eq1.solveA((long)bVal);

        if (aVal.isEmpty()) {
            return Optional.empty();
        }
        var finalResult = new LinearResult(aVal.get(), (long)bVal);

        if (!eq1.solves(finalResult) || !(eq2.solves(finalResult))) {
            throw new IllegalStateException();
        }

        return Optional.of(
                finalResult
        );
    }

    public static long lcm(long number1, long number2) {
        if (number1 == 0 || number2 == 0) {
            return 0;
        }
        long absNumber1 = Math.abs(number1);
        long absNumber2 = Math.abs(number2);
        long absHigherNumber = Math.max(absNumber1, absNumber2);
        long absLowerNumber = Math.min(absNumber1, absNumber2);
        long lcm = absHigherNumber;
        while (lcm % absLowerNumber != 0) {
            lcm += absHigherNumber;
        }
        return lcm;
    }

    public record LinearAdd(long a, long b, long e) {


        public LinearAdd mul(long mul) {
            return new LinearAdd(a * mul, b*mul, e*mul);
        }
        public LinearAdd sub(LinearAdd other) {
            return new LinearAdd(a - other.a, b - other.b, e - other.e);
        }

        public boolean solves(LinearResult res) {
            return (a * res.a) + (b * res.b) == e;
        }

        public Optional<Long> solveA(long bVal) {

            logger.debug("Solve {} when B is {}", this, bVal);
            long bTotal = b * bVal;
            double aValue = (double)(e - bTotal) / a;

            logger.debug("({} - {}) / {} = {}", e, bTotal, a, aValue);

            if (aValue < 0)
                aValue *= -1;

            if (Math.floor(aValue) != aValue)
                return Optional.empty();
            return Optional.of((long)aValue);
        }

        @Override
        public String toString() {
            var sb = new StringBuilder();
            if (a != 0) {
                sb.append(a).append('A');
                if (b != 0)
                    sb.append(" +\t").append(b).append('B').append('\t');
                else {
                    sb.append("\t");
                }
            } else if (b != 0) {
                sb.append("\t\t").append(b).append('B').append("  +  ");
            } else {
                return String.format("%dA + %dB = %d", a, b, e);
            }
            return sb.append("=\t").append(e).toString();
        }
    }

    public static void main(String[] args) {

        long total = 0;

        try {
            for (Optional<LinearResult> result : PuzzleInput.puzzleInput(13, false, new Function<String, Optional<LinearResult>>() {

                final Pattern p = Pattern.compile("Button ([AB]): X\\+(\\d+), Y\\+(\\d+)");
                final Pattern pe = Pattern.compile("Prize: X=(\\d+), Y=(\\d+)");

                long eq1a = -1, eq1b = -1, eq2a = -1, eq2b = -1;

                @Override
                public Optional<LinearResult> apply(String s) {

                    if (s.isEmpty())
                        return Optional.empty();

                    if (eq1a == -1 || eq1b == -1) {
                        var m = p.matcher(s);
                        if (!m.matches())
                            throw new IllegalStateException();

                        if ("A".equals(m.group(1))) {
                            // Button A: X+69, Y+23
                            eq1a = Long.parseLong(m.group(2));
                            eq2a = Long.parseLong(m.group(3));
                        } else {
                            // Button B: X+27, Y+71
                            eq1b = Long.parseLong(m.group(2));
                            eq2b = Long.parseLong(m.group(3));
                        }
                        return Optional.empty();
                    } else {
                        var m = pe.matcher(s);
                        if (!m.matches())
                            throw new IllegalStateException("No PE match: " + s);

                        // Prize: X=18641, Y=10279
                        var eq1 = new LinearAdd(eq1a, eq1b, 10000000000000L + Long.parseLong(m.group(1)));
                        var eq2 = new LinearAdd(eq2a, eq2b, 10000000000000L + Long.parseLong(m.group(2)));

                        eq1a = -1;
                        eq1b = -1;
                        eq2a = -1;
                        eq2b = -1;

                        return solve(eq1, eq2);
                    }
                }

            })) {
                if (result.isEmpty())
                    continue;
                logger.info("Solution: {}", result.get());
                total += result.get().tokens();
            }
            logger.info("Result: {}", total);
        } catch (RuntimeException re) {
            logger.error("Runtime Error", re);
        }
    }
}
