package com.github.dekmetzi.aoc2024;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class Day04Test {

    @Test
    public void testParts() {
        var lines = List.of("XMASYSAMXMAS".toCharArray());

        assertThat(new Day04().horizontalXmas(lines, 0, 0)).isEqualTo(1);
        assertThat(new Day04().horizontalXmas(lines, 0, 8)).isEqualTo(2);
    }
}
