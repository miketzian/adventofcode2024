package com.github.dekmetzi.aoc2024.util;

import java.text.DecimalFormat;
import java.time.Instant;
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

    public long ns() {
        if (end.isEmpty())
            throw new IllegalStateException("still running");
        return end.get().getNano() - start.getNano();
    }

    public long ms() {
        return TimeUnit.NANOSECONDS.toMillis(ns());
    }

    // would not compile without an argument..
    public static PuzzleTimer start() {
        return new PuzzleTimer();
    }

    @Override
    public String toString() {
        long ns = ns();
        var nsString = new DecimalFormat( "###,###" ).format(ns);
        long ms = TimeUnit.NANOSECONDS.toMillis(ns);
        return String.format("%dms (%sns)", ms, nsString);
    }
}
