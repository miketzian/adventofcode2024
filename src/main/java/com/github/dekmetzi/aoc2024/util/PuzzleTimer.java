package com.github.dekmetzi.aoc2024.util;


import java.text.DecimalFormat;
import java.time.Instant;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class PuzzleTimer {

    private final Instant start;
    // would AtomicReference be better ?
    private Optional<Instant> end = Optional.empty();

    private PuzzleTimer() {
        this.start = Instant.now();
    }

    public PuzzleTimer stop() {
        var when = Instant.now();
        if (end.isPresent())
            throw new IllegalStateException("already stopped");
        end = Optional.of(when);
        return this;
    }

    public Duration duration() {
        if (end.isEmpty())
            throw new IllegalStateException("still running");
        return Duration.between(start, end.get());
    }

    // would not compile without an argument..
    public static PuzzleTimer start() {
        return new PuzzleTimer();
    }

    @Override
    public String toString() {

        if (end.isEmpty())
        {
            return "[running]";
        }
        var duration = duration();

        var nsString = new DecimalFormat( "###,###" ).format(duration.toNanosPart());

        return String.format("%dms (%sns)",
                // duration.toSecondsPart(),
                duration.toMillisPart(), nsString);
    }
}
