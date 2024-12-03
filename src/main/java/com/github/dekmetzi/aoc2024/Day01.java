package com.github.dekmetzi.aoc2024;

import com.github.dekmetzi.aoc2024.util.PuzzleInput;
import com.github.dekmetzi.aoc2024.util.PuzzleTimer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;


public record Day01(List<Integer> l1, List<Integer> l2) {
    private static final Logger logger
            = LoggerFactory.getLogger("[Day 1] Historian Hysteria");

    public Day01 {
        if (l1.size() != l2.size())
            throw new IllegalArgumentException("lists do not match");
    }

    public static Day01 from(Iterator<String> data) {

        List<Integer> l1 = new ArrayList<>();
        List<Integer> l2 = new ArrayList<>();

        while (data.hasNext()) {
//            Arrays.stream(data.next().split(" +", 2))
//                    .map(Integer::parseInt)
//                    .forEachOrdered((i) -> {
//                        if (l1.size() == l2.size())
//                            l1.add(i);
//                        else
//                            l2.add(i);
//                    });

            // ~2ms faster than the above.
            String next = data.next();
            String[] intStrings = next.split(" +", 2);
            l1.add(Integer.parseInt(intStrings[0]));
            l2.add(Integer.parseInt(intStrings[1]));
        }

        // only slightly faster than the stream version
        l1.sort(Integer::compareTo);
        l2.sort(Integer::compareTo);

        // l1 = l1.stream().sorted().collect(Collectors.toUnmodifiableList());
        // l2 = l2.stream().sorted().collect(Collectors.toUnmodifiableList());

        // lists will always be the same size
        return new Day01(Collections.unmodifiableList(l1), Collections.unmodifiableList(l2));
    }

    public int totalDistance() {
        int totalDistance = 0;

        // both lists are same size()
        for (Iterator<Integer> i1 = l1.iterator(), i2=l2.iterator(); i1.hasNext() /* && i2.hasNext() */; ) {
            totalDistance += Math.abs(i1.next() - i2.next());
        }
        return totalDistance;
    }

    // account for possibility that previous
    public int similarityScore() {

        Iterator<Integer> matching = l2.iterator();
        var curr = matching.next();

        int prev = -1;
        int prevCount = 0;

        int score = 0;
        for (Integer i: l1) {

            // account for repeated numbers in the l1 list
            if (i == prev) {
                score += (prevCount * prev);
                continue;
            }
            prev = -1;
            prevCount = 0;

            // if the l1 value is > than the value from l2, then iterate until it isn't
            while (i > curr) {
                if (matching.hasNext())
                    curr = matching.next();
                else
                    break;
            }
            // count the number of matches in the l2 list
            while (i.compareTo(curr) == 0) {
                prev = curr;
                prevCount += 1;
                if (matching.hasNext())
                    curr = matching.next();
                else
                    break;
            }
            if (prevCount != 0) {
                score += (prevCount * prev);
            }
        }
        return score;
    }

    public static void main(String[] args) throws IOException {
        var timer = PuzzleTimer.start();
        try {
            var day = Day01.from(PuzzleInput.puzzleInput(1, false).iterator());

            var distance = day.totalDistance();
            var similarity = day.similarityScore();

            timer.stop();

            logger.info("Total Distance: {}, Similarity Score: {}", distance, similarity);
            logger.info("Completed in {}", timer);

        } catch (RuntimeException e) {
            logger.error("Exception after {}", timer.stop(), e);
        }
    }
}
