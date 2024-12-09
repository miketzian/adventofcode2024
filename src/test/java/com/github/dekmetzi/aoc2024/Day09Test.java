package com.github.dekmetzi.aoc2024;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class Day09Test {


    @ParameterizedTest
    @CsvSource(value={
            "12345:0..111....22222",
            "2333133121414131402:00...111...2...333.44.5555.6666.777.888899",
    }, delimiter = ':')
    public void testParse(Day09 input, String toString) {
        assertThat(input.toString()).isEqualTo(toString);
    }

    @ParameterizedTest
    @CsvSource(value={
            "2333133121414131402:0099811188827773336446555566:1928",
    }, delimiter = ':')
    public void testCompress(Day09 input, String compressed, long checksum) {

        input.part1Compact();
        assertThat(input.toString()).isEqualTo(compressed);
        assertThat(input.checksum()).isEqualTo(checksum);
    }


    @ParameterizedTest
    @CsvSource(value={
            "2333133121414131402:00992111777.44.333....5555.6666.....8888..:2858",
    }, delimiter = ':')
    public void testPart2Compact(Day09 input, String compressed, long checksum) {

        input.part2Compact();
        assertThat(input.toString()).isEqualTo(compressed);
        assertThat(input.checksum()).isEqualTo(checksum);
    }
}
