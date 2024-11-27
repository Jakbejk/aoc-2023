package cz.aop;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class Main {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(Files.newBufferedReader(Paths.get("input.txt")));
        String[] lines = br.lines().toArray(String[]::new);
        String[][] lineParts = new String[lines.length][];
        for (int i = 0; i < lines.length; i++) {
            lineParts[i] = lines[i].split(" ");
        }
        int x = 0;
        int y = 0;
        int maxX = 0;
        int maxY = 0;
        int minX = 0;
        int minY = 0;
        Set<int[]> coordinates = new HashSet<>();
        for (String[] linePart : lineParts) {
            String direction = linePart[0];
            int distance = Integer.parseInt(linePart[1]);
            for (int i = 0; i < distance; i++) {
                coordinates.add(new int[]{y, x});
                if ("D".equals(direction)) {
                    y++;
                } else if ("U".equals(direction)) {
                    y--;
                } else if ("R".equals(direction)) {
                    x++;
                } else if ("L".equals(direction)) {
                    x--;
                }
                maxX = Math.max(maxX, x);
                maxY = Math.max(maxY, y);
                minY = Math.min(minY, y);
                minX = Math.min(minX, x);
            }
        }
        final int xxx = -minX;
        final int yyy = -minY;
        maxY += yyy;
        maxX += xxx;

        boolean[][] grid = new boolean[maxY + 1][maxX + 1];

        coordinates.forEach(coordinate -> {
            coordinate[0] += yyy;
            coordinate[1] += xxx;
        });
        coordinates.forEach(coordinate -> grid[coordinate[0]][coordinate[1]] = true);
        print(grid);
        System.out.println(count(grid));
    }

    private static void print(boolean[][] grid) {
        Point point = findStartPoint(grid);
        assert point != null;
        populate(grid, point.x + 1, point.y + 1);
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                System.out.print(grid[i][j] ? "#" : ".");
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
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j]) {
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