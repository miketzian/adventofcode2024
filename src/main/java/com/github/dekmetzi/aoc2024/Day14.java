package com.github.dekmetzi.aoc2024;

import com.github.dekmetzi.aoc2024.util.PuzzleInput;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Day14{

    private static final Logger logger = LoggerFactory.getLogger("[Day 14] Restroom Redoubt");

    // width
    private final int w;
    // height
    private final int h;

    private final int seconds;

    private final Map<Quadrant, Integer> quadrantRobotMap = Maps.newEnumMap(Quadrant.class);
    private final Map<Quadrant, Point[]> boundaries = Maps.newEnumMap(Quadrant.class);
    private final Map<Robot, Point> robots = new HashMap<>();

    public Day14(int w, int h, int seconds) {
        this.w = w;
        this.h = h;
        this.seconds = seconds;

        int qw = (w / 2);
        int qh = (h / 2);

        boundaries.put(Quadrant.LT,
                new Point[]{new Point(0, 0), new Point(qw-1, qh-1) });
        boundaries.put(Quadrant.LB,
                new Point[]{new Point(0, h - qh), new Point(qw-1, h-1)});
        boundaries.put(Quadrant.RT,
                new Point[]{new Point(w - qw , 0), new Point(w-1, qh-1) });
        boundaries.put(Quadrant.RB,
                new Point[]{new Point(w - qw , h - qh), new Point(w-1, h-1) });

        // NEXT TIME take the time to render the boundaries to the output to make sure this is right 1st time.
    }

    public enum Quadrant { LT, LB, RT, RB };

    public Optional<Quadrant> where (Point loc) {

        if (loc.x < 0 || loc.x > w || loc.y < 0 || loc.y > h)
            throw new IllegalStateException();

        for (var e: boundaries.entrySet()) {
            if (loc.inRange(e.getValue())) {
                return Optional.of(e.getKey());
            }
        }
        return Optional.empty();
    }

    public void moveRobots(int seconds) {

        for (var e: robots.entrySet()) {
            var r = e.getKey();
            int finalX = e.getValue().x + (seconds * r.vx);
            int finalY = e.getValue().y + (seconds * r.vy);

            if (finalX > 0) {
                finalX = finalX % w;
            } else if (finalX != 0) {
                double s1 = (double)Math.abs(finalX) / w;
                int s2 = (int)Math.ceil(s1) * w;
                finalX = finalX + s2;
            }
            if (finalY > 0) {
                finalY = finalY % h;
            } else if (finalY != 0) {
                double s1 = (double)Math.abs(finalY) / h;
                int s2 = (int)Math.ceil(s1) * h;
                finalY = finalY + s2;
            }
            e.setValue(new Point(finalX, finalY));

            // this doesn't update the quadrant map
        }
    }

    /**
     * Adds a robot and moves it the initial # of seconds.
     */
    public void addRobot(Robot r) {

        int finalX = r.px + (seconds * r.vx);
        int finalY = r.py + (seconds * r.vy);

        if (finalX > 0) {
            finalX = finalX % w;
        } else if (finalX != 0) {
            double s1 = (double)Math.abs(finalX) / w;
            int s2 = (int)Math.ceil(s1) * w;
            finalX = finalX + s2;
        }
        if (finalY > 0) {
            finalY = finalY % h;
        } else if (finalY != 0) {
            double s1 = (double)Math.abs(finalY) / h;
            int s2 = (int)Math.ceil(s1) * h;
            finalY = finalY + s2;
        }

        robots.put(r, new Point(finalX, finalY));

        // find the quadrant for this point
        where(new Point(finalX, finalY))
                // if it's in a quadrant, then
                .ifPresent(quadrant ->
                        // update the quadrant map to set where this robot is.
                        quadrantRobotMap.compute(quadrant, (key, value) -> value != null ? value + 1 : 1));
    }

    public String toString() {
        List<char[]> lines = new ArrayList<>(h);
        for (int i=0; i<h; i++) {
            char[] line = new char[w];
            Arrays.fill(line, '.');
            lines.add(line);
        }

        for (Point l: robots.values()) {
            var c = lines.get(l.y)[l.x];
            if (c == '.') {
                lines.get(l.y)[l.x] = '1';
            } else {
                lines.get(l.y)[l.x]++;
            }
        }

        StringBuilder out = new StringBuilder();
        out.append("\n");
        for (char[] line: lines) {
            out.append(line).append("\n");
        }
        return out.toString();
    }


    public record Point(int x, int y) {

        public boolean inRange(Point[] range) {

            if (x < range[0].x || y < range[0].y)
                return false;

            return x <= range[1].x && y <= range[1].y;
        }
    }

    // x = left wall, y = right wall ?
    public record Robot (int px, int py, int vx, int vy) {

        final static Pattern p = Pattern.compile("p=([-\\d]+),([-\\d]+) v=([-\\d]+),([-\\d]+)");

        public static Robot from(String s) {

            var pm = p.matcher(s);
            if (!pm.matches())
                throw new IllegalArgumentException(String.format("Invalid Robot: %s", s));

            return new Robot(
                    Integer.parseInt(pm.group(1)),
                    Integer.parseInt(pm.group(2)),
                    Integer.parseInt(pm.group(3)),
                    Integer.parseInt(pm.group(4))
            );
        }
    }

    public static Stream<String> testInput() {
        return Stream.of("""
                p=0,4 v=3,-3
                p=6,3 v=-1,-3
                p=10,3 v=-1,2
                p=2,0 v=2,-1
                p=0,0 v=1,3
                p=3,0 v=-2,-2
                p=7,6 v=-1,-3
                p=3,0 v=-1,-2
                p=9,3 v=2,3
                p=7,3 v=-1,2
                p=2,4 v=2,-3
                p=9,5 v=-3,-3""".split("\n"));
    }

    public static void main(String[] args) {

        try {
            var test1 = new Day14(11, 7, 100);
            testInput().map(Robot::from).forEach(test1::addRobot);

            for (var e: test1.boundaries.entrySet()) {
                logger.debug("Test Bounds: {}: {} -> {}", e.getKey(), e.getValue()[0], e.getValue()[1]);
            }

            int testPart1 = test1.quadrantRobotMap.values().stream().reduce(1, (acc, v) -> acc * v);
            logger.debug("TestResult: {}", testPart1);

            var main = new Day14(101, 103, 100);
            for (Robot r: PuzzleInput.puzzleInput(14, false, Robot::from)) {
                main.addRobot(r);
            }
            for (var e: main.boundaries.entrySet()) {
                logger.debug("Bounds: {}: {} -> {}", e.getKey(), e.getValue()[0], e.getValue()[1]);
            }

            int mainPart1 = main.quadrantRobotMap.values().stream().reduce(1, (acc, v) -> acc * v);
            logger.debug("Part 1 Result: {}", mainPart1);

            // rgb data is one byte array representing all rows.
            DataBuffer rgbData = new DataBufferInt(main.w * main.h);
            WritableRaster raster = Raster.createPackedRaster(rgbData, main.w, main.h, main.w,
                    new int[]{0xff0000, 0xff00, 0xff},
                    null);

            ColorModel colorModel = new DirectColorModel(24, 0xff0000, 0xff00, 0xff);

            for (int i=100; i<10000 ; i++) {
                String fileName = String.format("tmp/%d-seconds.png", i);
                for (Point p: main.robots.values()) {
                    rgbData.setElem((main.w * p.y) + p.x, Color.WHITE.getRGB());
                }
                BufferedImage img = new BufferedImage(colorModel, raster, false, null);
                try {
                    ImageIO.write(img, "png", new File(fileName));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                for (Point p: main.robots.values()) {
                    rgbData.setElem((main.w * p.y) + p.x, Color.BLACK.getRGB());
                }
                main.moveRobots(1);
            }

            // writing ascii to file also works, but having the OS render thumbnails of PNG is nicer.
//            try (var bw = new java.io.BufferedWriter(new java.io.FileWriter("out.txt"))) {
//                for (int i=100; i<7700; i++) {
//                    test.moveRobots(1);
//                    bw.write(String.valueOf(i));
//                    bw.write(":\n");
//                    bw.write(test.toString());
//                    // logger.debug("{}: {}", 100+i, test.toString());
//                }
//            } catch (IOException ioe) {
//                logger.error("Error writing file", ioe);
//            }

        } catch (RuntimeException re) {
            logger.error("Problem", re);
        }
    }
}
