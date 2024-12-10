package com.github.dekmetzi.aoc2024;

import com.github.dekmetzi.aoc2024.util.PuzzleInput;
import com.github.dekmetzi.aoc2024.util.PuzzleTimer;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Day10 {

    private static final Logger logger
            = LoggerFactory.getLogger("[Day 10] Hoof It");

    final List<List<Node>> map;
    final List<Node> trailheads = new LinkedList<>();
    final List<Node> summits = new LinkedList<>();

    public Day10(List<List<Node>> map) {

        // Node[][] grid -> Node (with value)
        this.map = map;
        for (List<Node> e : map) {
            for (Node v : e) {
                if (v.v == 0) {
                    trailheads.add(v);
                }
                if (v.v == 9) {
                    summits.add(v);
                }
            }
        }
        if (trailheads.isEmpty() || summits.isEmpty())
            throw new IllegalArgumentException("trailheads=" + trailheads + ", summits=" + summits);
    }

    public Set<Node> neighbors(Node n) {
        var result = new HashSet<Node>(4);
        if (n.x > 0)
            result.add( map.get(n.x-1).get(n.y));
        if (n.y > 0)
            result.add( map.get(n.x).get(n.y-1));
        if (n.x + 1 < map.size())
            result.add( map.get(n.x+1).get(n.y));
        if (n.y + 1 < map.get(n.x).size())
            result.add( map.get(n.x).get(n.y+1));

        return result;
    }


    static class RouteNode<T extends Node> implements Comparable<RouteNode<T>> {
        private final T current;
        private T previous;
        private double routeScore;
        private double estimatedScore;

        RouteNode(T current) {
            this(current, null, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        }

        RouteNode(T current, T previous, double routeScore, double estimatedScore) {
            this.current = current;
            this.previous = previous;
            this.routeScore = routeScore;
            this.estimatedScore = estimatedScore;
        }

        // used to drive the priority queue
        // so that better-scored items come first
        @Override
        public int compareTo(RouteNode other) {
            return Double.compare(this.estimatedScore, other.estimatedScore);
            /*
            if (this.estimatedScore > other.estimatedScore) {
                return 1;
            } else if (this.estimatedScore < other.estimatedScore) {
                return -1;
            } else {
                return 0;
            }
            */
        }
    }

    public record Node(int x, int y, int v)  {
        int distance(Node other) {
            return Math.abs(this.x - other.x) + Math.abs(this.y - other.y);
        }

        Set<Node> connections(Day10 grid) {
            return grid.neighbors(this)
                    .stream().filter((h) -> h.v - v == 1)
                    .collect(Collectors.toSet());
        }
    }

    static List<List<Node>> input(boolean test) {

        final AtomicInteger ai = new AtomicInteger(0);

        return Lists.newArrayList(PuzzleInput.puzzleInput(10, test,
                (String S) -> {
                    List<Node> out = new ArrayList<>(S.length());
                    var chars = S.toCharArray();
                    var i = ai.getAndIncrement();
                    for (int j=0; j<chars.length; j++) {
                    // for (char c: S.toCharArray()) {
                        // out.add((int)c-48);
                        out.add(new Node(i, j, (int)chars[j]-48));
                    }
                    return out;
                }).iterator());
    }

    public record RouteFinder<T extends Node> (Day10 graph) {

        /**
         * actually this was overkill but hey, we used a*
         */
        public List<T> findRoute(T from, T to) {

            Queue<RouteNode<T>> openSet = new PriorityQueue<>();
            Map<Node, RouteNode<T>> allNodes = new HashMap<>();

            RouteNode<T> start = new RouteNode<>(from, null, 0d, from.distance(to));
            openSet.add(start);
            allNodes.put(from, start);

            while (!openSet.isEmpty()) {
                RouteNode<T> next = openSet.poll();
                if (next.current.equals(to)) {
                    List<T> route = new ArrayList<>();
                    RouteNode<T> current = next;
                    do {
                        route.addFirst(current.current);
                        current = allNodes.get(current.previous);
                    } while (current != null);
                    return route;
                }

                (next.current.connections(graph)).forEach( (c) -> {
                    RouteNode<T> nextNode = allNodes.getOrDefault(c, new RouteNode<>((T)c));
                    allNodes.put(c, nextNode);

                    double newScore = next.routeScore + 1;
                    if (newScore < nextNode.routeScore) {
                        nextNode.previous = next.current;
                        nextNode.routeScore = newScore;
                        nextNode.estimatedScore = newScore + c.distance(to);
                        openSet.add(nextNode);
                    }
                });
            }

            throw new IllegalStateException("No route found");
        }

        /**
         * we actually don't need the path in this case, only the # of
         * available paths.
         * Since the route always goes up, we don't need to check
         * for loops or double-back either.
         */
        public int findRoutes(Node from, Node to) {
            int found = 0;
            for (var next: from.connections(graph)) {
                if (next.equals(to)) {
                    // we are at the end.
                    found++;
                } else {
                    found += findRoutes(next, to);
                }
            }
            return found;
        }
    }

    public static void main(String[] args) {

        var day = new Day10(input(false));

        var timer = PuzzleTimer.start();
        try {

            RouteFinder<Node> rf = new RouteFinder<>(day);
            {
                long sum = 0;
                for (var th : day.trailheads) {

                    int reachable = 0;
                    int score = 0;

                    for (var sm : day.summits) {
                        try {
                            logger.trace("route: {}", rf.findRoute(th, sm));
                            reachable++;
                        } catch (IllegalStateException ignored) {
                            // no route
                        }
                    }
                    // logger.debug("th {} -> {} (score={})", th, reachable, score);
                    sum += reachable;
                }

                logger.info("part1 result: {}  in {}", sum, timer.stop());
            }

            timer = PuzzleTimer.start();
            {
                long sumScores = 0;

                for (var th : day.trailheads) {

                    int score = 0;

                    for (var sm : day.summits) {
                        var routes = rf.findRoutes(th, sm);
                        logger.trace("routes: {} from {} to {}", routes, th, sm);
                        score += routes;
                    }
                    logger.trace("th {} score={}", th, score);
                    sumScores += score;
                }

                logger.info("part2 result: {}  in {}", sumScores, timer.stop());
            }
        } catch (RuntimeException e){
            logger.error("Exception after {}", timer.stop(), e);
        }
    }
}
