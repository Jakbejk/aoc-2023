package cz.aoc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main {

    private static final char START = 'S';
    private static final char OCCUPIED = '#';

    public static void main(String[] args) throws IOException {
        char[][] grid = load("input.txt");
        visit(grid, 64);
    }

    private static Point getStartPoint(char[][] grid) {
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[y].length; x++) {
                if (grid[y][x] == START) {
                    return new Point(x, y);
                }
            }
        }
        throw new RuntimeException("No start point found");
    }

    private static void visit(char[][] grid, int desiredSteps) {
        int size = grid.length;
        Point start = getStartPoint(grid);

        Set<Point> points = new HashSet<>();
        points.add(start);

        for (int y = 0; y < desiredSteps; y++) {
            Set<Point> children = new HashSet<>();
            for (Point point : points) {
                Point right = new Point(point.x + 1, point.y);
                Point left = new Point(point.x - 1, point.y);
                Point up = new Point(point.x, point.y - 1);
                Point down = new Point(point.x, point.y + 1);
                if (isValid(grid, size, right)) {
                    children.add(right);
                }
                if (isValid(grid, size, left)) {
                    children.add(left);
                }
                if (isValid(grid, size, up)) {
                    children.add(up);
                }
                if (isValid(grid, size, down)) {
                    children.add(down);
                }
            }
            points = children;
        }

        System.out.println(points.size());

    }

    private static boolean isValid(char[][] grid, int size, Point point) {
        if (point.x < 0 || point.x >= size || point.y < 0 || point.y >= size) {
            return false;
        }
        return grid[point.y][point.x] != OCCUPIED;
    }

    private static char[][] load(String path) throws IOException {
        return Files.readAllLines(Paths.get(path)).stream().map(String::toCharArray).toArray(char[][]::new);
    }

    static class Point {
        int x;
        int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Point other) {
                return other.x == x && other.y == y;
            }
            return false;
        }
    }
}
