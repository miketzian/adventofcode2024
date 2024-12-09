package com.github.dekmetzi.aoc2024;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class Day08Test {


    @ParameterizedTest
    @CsvSource(value={
            "1,1:2,2:0,0:3,3:5:5",
            "2,2:1,1:0,0:3,3:5:5",

             "1,2:2,1:0,3:3,0:5:5",
            "2,1:1,2:0,3:3,0:5:5",

            "1,8:2,5:0,11:3,2:12:12",
            "2,5:1,8:0,11:3,2:12:12"
    }, delimiter = ':')
    public void testAntinodes(Day08.Node n1, Day08.Node n2, Day08.Node a1, Day08.Node a2,
        int xLen, int yLen) {

        List<Day08.Node> aa = n1.antinodes(n2, xLen, yLen, false);

        System.out.println(List.of(n1, n2) + " -> " + aa);
        assertThat(aa.contains(a1)).isTrue();
        assertThat(aa.contains(a1)).isTrue();
    }

    @ParameterizedTest
    @CsvSource(value={
            "1,1:2,2:0,0:2:2",
    }, delimiter = ':')

    public void testAntinodes2(Day08.Node n1, Day08.Node n2, Day08.Node a1,
                              int xLen, int yLen) {

        List<Day08.Node> aa = n1.antinodes(n2, xLen, yLen, false);
        assertThat(aa.contains(a1)).isTrue();
        assertThat(aa.size()).isEqualTo(1);
    }
}
