package com.github.dekmetzi.aoc2024;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class Day03Test {

    @ParameterizedTest
    @CsvSource(value={
            "xmul(2,4)%&mul[3,7]!@^do_not_mul(5,5)+mul(32,64]then(mul(11,8)mul(8,5)):false:161",
            "xmul(2,4)&mul[3,7]!^don't()_mul(5,5)+mul(32,64](mul(11,8)undo()?mul(8,5)):true:48",
    }, delimiter = ':')
    public void testParts(String input, boolean part2, int expected) {

        assertThat(
                new Day03().calculateResults(List.of(input).iterator(), part2)
        ).isEqualTo(expected);
    }
}
