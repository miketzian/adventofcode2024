package com.github.dekmetzi.aoc2024;

import com.github.dekmetzi.aoc2024.util.PuzzleInput;
import com.github.dekmetzi.aoc2024.util.PuzzleTimer;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class Day09 {

    private static final Logger logger
            = LoggerFactory.getLogger("[Day 9] Disk Fragmenter");

    final List<Value9> flow;
    public Day09(List<Value9> flow) {
        this.flow = flow;
    }

    public static Day09 from(String input) {
        var chars = input.toCharArray();
        List<Value9> flow = new ArrayList<>(chars.length);
        boolean isFile = true;
        int id = 0;
        int ix = 0;
        while (ix < chars.length) {
            char c = chars[ix];
            int v = (int)c - 48;  // 48 = 0
            if (isFile) {
                flow.add(new Data(id++, v));
            } else if (v != 0){
                // isSpace, unless value is zero
                flow.add(new Space(v));
            }
            ix++;
            isFile = !isFile;
        }
        return new Day09(flow);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Value9 o: flow) {
            if (o != null)
                sb.append(o);
        }
        return sb.toString();
    }

    public interface Value9 {
        int value(int ix);
        int size();
    }

    public record Data(int id, int size) implements Value9 {

//        public Data {
//            System.out.println("new data: " + id + " -> size " + size);
//        }

        public int value(int ix) {
            if (ix >= size)
                throw new IllegalArgumentException();
            return id;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            int k = 0;
            while (k < size) {
                sb.append(id);
                k++;
            }
            return sb.toString();
        }
    }

    public static class Space implements Value9 {
        private final Data[] data;
        int writeIx = 0;
        public Space(int chars) {
            data = new Data[chars];
        }

        public boolean maybePut(Data d) {
            // if Data will fit, then put it
            int availSpace = data.length - writeIx;
            if (d.size() > availSpace) {
                return false;
            }
            for (int i=0; i<d.size(); i++) {
                data[writeIx++] = d;
            }
            return true;
        }

        public boolean hasData() {
            return data[0] != null;
        }

        public int value(int ix) {
            if (ix >= data.length)
                throw new IllegalArgumentException();
            if (data[ix] == null)
                return 0;
            return data[ix].id;
        }
        public int size() {
            return data.length;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (Data c: data) {
                if (c == null) {
                    sb.append('.');
                } else {
                    sb.append(c.id);
                }
            }
            return sb.toString();
        }
    }

    public void part2Compact() {

        OUTER:
        for (int flowIx = flow.size()-1; flowIx >=0; flowIx--) {
            Value9 o = flow.get(flowIx);
            if (o instanceof Data data) {
                // searching from the left, find a spot that will fit this.
                // brute
                // improvement: track spaces from the left that are full/scanned ?
                for (int scanIx=0; scanIx<flowIx; scanIx++) {
                    Value9 sO = flow.get(scanIx);
                    if (sO instanceof Space scanSpace) {
                        if (scanSpace.maybePut(data)) {
                            flow.set(flowIx, new Space(o.size()));
                            continue OUTER;
                        }
                    }
                }
            }
        }
    }

    public void part1Compact() {

        Data last = null;
        int lastSize = 0;

        for (int flowIx = 0; flowIx < flow.size(); flowIx++) {
            Value9 o = flow.get(flowIx);
            if (o instanceof Space space) {
                for (int i=0; i<space.data.length; i++) {

                    if (lastSize == 0 && flow.getLast() != o) {
                        Value9 o2 = flow.removeLast();
                        while (o2 instanceof Space skipSpace) {
                            if (skipSpace.hasData()) {
                                throw new IllegalStateException("found");
                            }
                            o2 = flow.removeLast();
                        }
                        if (o2 instanceof Data lastData) {
                            last = lastData;
                            lastSize = last.size;
                        }
                    }
                    if (lastSize != 0)
                    {
                        // use this first
                        space.data[i] = last;
                        lastSize--;
                    }
                }
            }
        }
        // re-add if needed.
        if (lastSize != 0)
            flow.add(new Data(last.id, lastSize));
    }

    public long checksum() {
        long sum = 0L;
        long ix2=0;
        for (Value9 ov: flow) {
            if (ov == null)
                continue;
            for (int i=0; i<ov.size(); i++) {
                long v = ov.value(i), mul = ix2 * v;
                logger.trace("{} * {} = {}", ix2, v, mul);
                sum += mul;
                ix2++;
            }
        }
        return sum;
    }

    public static void main(String[] args) {

        // single line input
        String chars = PuzzleInput.puzzleInput(9, false).iterator().next();

        var timer = PuzzleTimer.start();
        try {
            var day = Day09.from(chars);
            logger.debug("Input: {}", day);

            day.part2Compact();
            // day.part1Compact();
            logger.debug("Output: {}", day);

            long sum = day.checksum();

            // correct 6382875730645
            // pt2     6420913943576

            logger.info("result: {} in {}", sum, timer.stop());

        } catch (RuntimeException e){
            logger.error("Exception after {}", timer.stop(), e);
        }
    }
}
