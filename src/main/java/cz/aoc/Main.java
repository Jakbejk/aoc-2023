package cz.aoc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static cz.aoc.Main.Direction.*;

public class Main {

    private static final char START = 'S';
    private static final char GROUND = '.';

    public static Map<Character, Map<Direction, Direction>> DIRECTION_MAP = Map.of('|', Map.of(UP, DOWN, DOWN, UP), '-', Map.of(LEFT, RIGHT, RIGHT, LEFT), 'L', Map.of(UP, RIGHT, RIGHT, UP), 'J', Map.of(UP, LEFT, LEFT, UP), '7', Map.of(DOWN, LEFT, LEFT, DOWN), 'F', Map.of(DOWN, RIGHT, RIGHT, DOWN));

    public static void main(String[] args) throws IOException {
        char[][] data = load("input.txt");
        List<Point> cycle = findCycle(data);
        List<Point> edges = reduceLines(cycle);
        long content = computeArea(cycle, edges);
        System.out.println(content);
    }

    private static long computeArea(List<Point> cycle, List<Point> edges) {
        long area = 0;
        for (int i = 0; i < edges.size() - 1; i++) {
            Point current = edges.get(i);
            Point next = edges.get(i + 1);
            long a = (long) current.x * next.y;
            long b = (long) next.x * current.y;
            area += b - a;
        }
        int perimeter = cycle.size();
        long content = Math.abs(area / 2) + (perimeter / 2) + 1;
        return content - perimeter;
    }

    private static int getIndex(int size, int i) {
        if (i < 0) {
            return i + size;
        } else if (i >= size) {
            return i - size;
        }
        return i;
    }

    private static List<Point> reduceLines(List<Point> points) {
        if (points.size() < 3) {
            return points;
        }
        int size = points.size();
        Point start = points.get(points.size() - 1);
        List<Point> reduced = new ArrayList<>();
        reduced.add(start);
        for (int i = 0; i < points.size(); i++) {
            Point prevPoint = points.get(getIndex(size, i - 1));
            Point currentPoint = points.get(i);
            Point nextPoint = points.get(getIndex(size, i + 1));

            boolean equalX = prevPoint.x == nextPoint.x;
            boolean equalY = prevPoint.y == nextPoint.y;
            if (isCorner(prevPoint, currentPoint, nextPoint)) {
                reduced.add(currentPoint);
            }
        }
        reduced.add(start);
        return reduced;
    }

    private static boolean isCorner(Point prevPoint, Point currentPoint, Point nextPoint) {
        return prevPoint.x != nextPoint.x && prevPoint.y != nextPoint.y;
    }

    private static Point computeNewPoint(DirectedPoint point, Direction direction) {
        return new Point(point.x + direction.getXOffset(), point.y + direction.getYOffset());
    }

    private static List<Point> findCycle(char[][] data) {
        int height = data.length;
        int width = data[0].length;

        List<Point> points = new ArrayList<>();
        Stack<DirectedPoint> stack = new Stack<>();
        boolean[][] visited = new boolean[height][width];

        Point start = getStart(data);
        stack.push(new DirectedPoint(start, UP, 0));

        while (!stack.isEmpty()) {
            DirectedPoint point = stack.pop();

            if (visited[point.y][point.x]) {
                continue;
            }

            visited[point.y][point.x] = true;
            Set<DirectedPoint> neighbours = getNeighbours(data, point);

            for (DirectedPoint neighbour : neighbours) {
                if (!visited[neighbour.y][neighbour.x]) {
                    stack.push(neighbour);
                }
            }

            if (!neighbours.isEmpty()) {
                points.add(point);
            }
        }

        return points;
    }

    private static Set<DirectedPoint> getNeighbours(char[][] data, DirectedPoint point) {
        Set<DirectedPoint> neighbours = new HashSet<>();

        int height = data.length;
        int width = data[0].length;
        char cell = data[point.y][point.x];

        if (cell == GROUND) {
            return new HashSet<>();
        } else if (cell == START) {
            for (Direction cellTarget : Direction.values()) {
                Direction neighbourSource = getOpposite(cellTarget);
                Point newPoint = computeNewPoint(point, cellTarget);
                if (notOverflow(height, width, newPoint)) {
                    char neighbourCell = data[newPoint.y][newPoint.x];
                    if (validCell(neighbourCell) && canAccept(neighbourCell, neighbourSource)) {
                        Direction neighbourTarget = DIRECTION_MAP.get(neighbourCell).get(neighbourSource);
                        neighbours.add(new DirectedPoint(newPoint, neighbourTarget, point.distance + 1));
                    }
                }
            }
        } else {
            Direction cellTarget = point.next;
            Direction neighbourSource = getOpposite(cellTarget);
            Point newPoint = computeNewPoint(point, cellTarget);
            if (notOverflow(height, width, newPoint)) {
                char neighbourCell = data[newPoint.y][newPoint.x];
                if (validCell(neighbourCell)) {
                    if (neighbourCell == START) {
                        neighbours.add(new DirectedPoint(newPoint, UP, point.distance + 1));
                    } else {
                        Direction neighbourTarget = DIRECTION_MAP.get(neighbourCell).get(neighbourSource);
                        if (canAccept(neighbourCell, neighbourSource)) {
                            neighbours.add(new DirectedPoint(newPoint, neighbourTarget, point.distance + 1));
                        }
                    }
                }
            }
        }
        return neighbours;
    }

    private static Direction getOpposite(Direction direction) {
        switch (direction) {
            case LEFT -> {
                return RIGHT;
            }
            case RIGHT -> {
                return LEFT;
            }
            case UP -> {
                return DOWN;
            }
            case DOWN -> {
                return UP;
            }
            default -> throw new RuntimeException("Invalid direction");
        }
    }

    private static boolean validCell(char cell) {
        return cell != GROUND;
    }

    private static boolean notOverflow(int height, int width, Point point) {
        return point.x >= 0 && point.x < width && point.y >= 0 && point.y < height;
    }

    private static boolean canAccept(char cell, Direction source) {
        return DIRECTION_MAP.get(cell).containsKey(source);
    }

    private static Point getStart(char[][] data) {
        for (int y = 0; y < data.length; y++) {
            for (int x = 0; x < data[y].length; x++) {
                if (data[y][x] == START) {
                    return new Point(x, y);
                }
            }
        }
        throw new RuntimeException("No start found");
    }

    private static char[][] load(String path) throws IOException {
        return Files.readAllLines(Paths.get(path)).stream().map(String::toCharArray).toArray(char[][]::new);
    }

    public enum Direction {
        UP(new int[]{0, -1}), DOWN(new int[]{0, +1}), LEFT(new int[]{-1, 0}), RIGHT(new int[]{+1, 0});

        final int[] direction;

        Direction(int[] direction) {
            this.direction = direction;
        }

        public int getXOffset() {
            return direction[0];
        }

        public int getYOffset() {
            return direction[1];
        }


    }

    static class DirectedPoint extends Point {
        Direction next;
        int distance;

        public DirectedPoint(Point point, Direction next, int distance) {
            super(point.x, point.y);
            this.next = next;
            this.distance = distance;
        }
    }

    static class Point {
        int x;
        int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}