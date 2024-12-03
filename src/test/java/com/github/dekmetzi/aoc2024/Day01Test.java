package com.github.dekmetzi.aoc2024;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Predicate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class Day01Test {

    final List<String> TEST_DATA = List.of("""
        3   4
        4   3
        2   5
        1   3
        3   9
        3   3""".split("\n")
    );

    @Test
    public void mainTestCase() {

        var day = Day01.from(TEST_DATA.iterator());

        assertThat(day.l1().size()).isEqualTo(6);
        assertThat(day.l2().size()).isEqualTo(6);

        Predicate<List<Integer>> inOrderPredicate = (data) -> {
            for (int i=1; i<data.size(); i++) {
                if (data.get(i) < data.get(i-1))
                    return false;
            }
            return true;
        };
        assertThat(day.l1()).matches(inOrderPredicate);
        assertThat(day.l2()).matches(inOrderPredicate);

        assertThat(day.totalDistance()).isEqualTo(11);

        assertThat(day.similarityScore()).isEqualTo(31);
    }
}
