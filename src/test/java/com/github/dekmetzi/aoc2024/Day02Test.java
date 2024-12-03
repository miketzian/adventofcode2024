package com.github.dekmetzi.aoc2024;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

public class Day02Test {


    @ParameterizedTest
    @CsvSource({
            "7 6 4 2 1,true",
            "1 2 7 8 9,false",
            "9 7 6 2 1,false",
            "1 3 2 4 5,false",
            "8 6 4 4 1,false",
            "1 3 6 7 9,true",
    })
    public void testNoDampening(Day02.Report report, boolean safe) {

        assertThat(report.isSafe()).isEqualTo(safe);
    }

    @ParameterizedTest
    @CsvSource({
            "7 6 4 2 1,true",
            "1 2 7 8 9,false",
            "9 7 6 2 1,false",
            "1 3 2 4 5,true",
            "8 6 4 4 1,true",
            "1 3 6 7 9,true",
    })
    public void testWithDampening(Day02.Report report, boolean safe) {

        assertThat(Day02.isReportSafe(report, true)).isEqualTo(safe);
    }
}
