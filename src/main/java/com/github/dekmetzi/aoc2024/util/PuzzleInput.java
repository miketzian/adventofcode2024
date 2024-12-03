package com.github.dekmetzi.aoc2024.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.ref.Cleaner;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Function;

public class PuzzleInput {

    private static final Cleaner cleaner = Cleaner.create();

    public static Iterable<String> puzzleInput(int day, boolean test) {
        return puzzleInput(day, test, Function.identity());
    }
    public static <T> Iterable<T> puzzleInput(int day, boolean test, Function<String, T> transform) {
        var fileName = String.format(test ? "2024_Day%02dTest.txt" : "2024_Day%02d.txt", day);
        return new ClassFileInput<>(fileName, transform);
    }

    public static record ClassFileInput<T>(String fileName, Function<String, T> transform) implements Iterable<T> {

        @Override
        public Iterator<T> iterator() {
            return new WrappingIterator<>(
                    new InputStreamReader(
                            Objects.requireNonNull(
                                    PuzzleInput.class.getClassLoader().getResourceAsStream(fileName),
                                    () -> String.format("%s was not found on the classpath", fileName)
                            )
                    ), transform);
        }
    }

    public static class WrappingIterator<T> implements Iterator<T> {

        private final BufferedReader _wrap;
        private final Function<String, T> transform;

        public WrappingIterator(Reader r, Function<String, T> transform) {
            this._wrap = new BufferedReader(r);
            this.transform = transform;
            cleaner.register(this, this::close);
        }

        public void close() {
            try {
                _wrap.close();
            } catch (IOException ioe) {
                // well, we tried..
            }
        }

        @Override
        public boolean hasNext() {
            try {
                if (!_wrap.ready()) {
                    // cleaner.close is supposed to call close()
                    // however I did not observe close being called in my testing.
                    this.close();
                    return false;
                }
                return true;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public T next() {
            try {
                return this.transform.apply(_wrap.readLine());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
