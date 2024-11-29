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
        solve(data);
    }

    private static void solve(char[][] data) {
        Point start = getStart(data);

        Queue<DirectedPoint> stack = new LinkedList<>();

        stack.add(new DirectedPoint(start, UP, 0));
        int path = 0;
        while (!stack.isEmpty()) {
            DirectedPoint point = stack.remove();
            path = Math.max(path, point.distance);
            Set<DirectedPoint> points = getNeighbours(data, point);
            stack.addAll(points);
            erase(data, point);
        }
        System.out.println(path);
    }

    private static Set<DirectedPoint> getNeighbours(char[][] data, DirectedPoint point) {
        Set<DirectedPoint> neighbours = new HashSet<>();

        int size = data.length;
        char cell = data[point.y][point.x];

        if (cell == GROUND) {
            return new HashSet<>();
        } else if (cell == START) {
            for (Direction cellTarget : Direction.values()) {
                Direction neighbourSource = getOpposite(cellTarget);
                Point newPoint = new Point(point.x + cellTarget.getXOffset(), point.y + cellTarget.getYOffset());
                if (notOverflow(size, newPoint)) {
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
            Point newPoint = new Point(point.x + cellTarget.getXOffset(), point.y + cellTarget.getYOffset());
            if (notOverflow(size, newPoint)) {
                char neighbourCell = data[newPoint.y][newPoint.x];
                if (validCell(neighbourCell)) {
                    Direction neighbourTarget = DIRECTION_MAP.get(neighbourCell).get(neighbourSource);
                    if (canAccept(neighbourCell, neighbourSource)) {
                        neighbours.add(new DirectedPoint(newPoint, neighbourTarget, point.distance + 1));
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

    private static boolean notOverflow(int size, Point point) {
        return point.x >= 0 && point.x < size && point.y >= 0 && point.y < size;
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

    private static void erase(char[][] data, Point p) {
        data[p.y][p.x] = GROUND;
    }

    private static char[][] load(String path) throws IOException {
        return Files.readAllLines(Paths.get(path)).stream().map(String::toCharArray).toArray(char[][]::new);
    }

    static enum Direction {
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