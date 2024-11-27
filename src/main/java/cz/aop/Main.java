package cz.aop;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class Main {

    private static final String DOWN = "D";
    private static final String UP = "U";
    private static final String LEFT = "L";
    private static final String RIGHT = "R";

    public static void main(String[] args) throws IOException {
        String[][] commands = load("input.txt");
        boolean[][] fence = createFence(commands);
        print(fence);
        System.out.println(count(fence));
    }

    private static boolean[][] createFence(String[][] commands) {
        int x = 0;
        int y = 0;
        Point max = new Point(Integer.MIN_VALUE, Integer.MIN_VALUE);
        Point min = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);

        Set<Point> coordinates = new HashSet<>();
        for (String[] instructions : commands) {
            String direction = instructions[0];
            int distance = Integer.parseInt(instructions[1]);
            for (int i = 0; i < distance; i++) {
                coordinates.add(new Point(x, y));
                if (DOWN.equals(direction)) {
                    y++;
                } else if (UP.equals(direction)) {
                    y--;
                } else if (RIGHT.equals(direction)) {
                    x++;
                } else if (LEFT.equals(direction)) {
                    x--;
                }
                max = new Point(Math.max(max.x, x), Math.max(max.y, y));
                min = new Point(Math.min(min.x, x), Math.min(min.y, y));
            }
        }

        offsetPoint(max, min);
        boolean[][] grid = new boolean[max.y + 1][max.x + 1];

        if (min.x < 0 || min.y < 0) {
            for (Point coordinate : coordinates) {
                offsetPoint(coordinate, min);
            }
        }
        coordinates.forEach(coordinate -> grid[coordinate.y][coordinate.x] = true);
        return grid;
    }

    private static void offsetPoint(Point target, Point offset) {
        if (offset.x < 0) {
            target.x -= offset.x;
        }
        if (offset.y < 0) {
            target.y -= offset.y;
        }
    }

    private static String[][] load(String path) throws IOException {
        BufferedReader br = new BufferedReader(Files.newBufferedReader(Paths.get(path)));
        String[] lines = br.lines().toArray(String[]::new);
        String[][] lineParts = new String[lines.length][];
        for (int i = 0; i < lines.length; i++) {
            lineParts[i] = lines[i].split(" ");
        }
        return lineParts;
    }

    private static void print(boolean[][] grid) {
        Point point = findStartPoint(grid);
        assert point != null;
        populate(grid, point.x + 1, point.y + 1);
        for (boolean[] row : grid) {
            for (boolean cell : row) {
                System.out.print(cell ? "#" : ".");
            }
            System.out.println();
        }
    }

    private static Point findStartPoint(boolean[][] grid) {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j]) {
                    return new Point(i, j);
                }
            }
        }
        return null;
    }

    private static int count(boolean[][] grid) {
        int count = 0;
        for (boolean[] row : grid) {
            for (boolean cell : row) {
                if (cell) {
                    count++;
                }
            }
        }
        return count;
    }

    private static void populate(boolean[][] grid, int x, int y) {
        if (x < 0 || y < 0 || x >= grid.length || y >= grid[0].length) {
            return;
        }
        Stack<Point> points = new Stack<>();
        points.push(new Point(x, y));

        do {
            Point point = points.pop();
            if (!grid[point.x][point.y]) {
                grid[point.x][point.y] = true;
                points.push(new Point(point.x + 1, point.y));
                points.push(new Point(point.x - 1, point.y));
                points.push(new Point(point.x, point.y + 1));
                points.push(new Point(point.x, point.y - 1));
            }
        } while (!points.empty());
    }

    private static class Point {
        int x;
        int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

}