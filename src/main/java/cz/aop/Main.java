package cz.aop;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main {

    private static final String DOWN = "D";
    private static final String UP = "U";
    private static final String LEFT = "L";
    private static final String RIGHT = "R";

    private static final Map<String, String> DIRECTION_MAP = Map.of("0", RIGHT, "1", DOWN, "2", LEFT, "3", UP);

    public static void main(String[] args) throws IOException {
        String[][] commands = load("input.txt");
        Fence fence = createFence(commands);

        System.out.println("Content of polygon is: " + shoelaceTheorem(fence));
    }


    private static long shoelaceTheorem(Fence fence) {
        List<Point> points = fence.edges;
        long perimeter = fence.perimeter;
        long area = 0;

        for (int i = 0; i < points.size() - 1; i++) {
            Point current = points.get(i);
            Point next = points.get(i + 1);
            long a = (long) current.x * next.y;
            long b = (long) next.x * current.y;
            area += b - a;
        }
        return Math.abs(area / 2) + perimeter / 2 + 1;
    }


    private static Fence createFence(String[][] commands) {
        Point current = new Point(0, 0);
        long perimeter = 0;

        List<Point> coordinates = new ArrayList<>();
        for (String[] instructions : commands) {
            Instruction instruction = parseInstruction(instructions[2]);
            if (DOWN.equals(instruction.direction)) {
                current = new Point(current.x, current.y + instruction.distance);
            } else if (UP.equals(instruction.direction)) {
                current = new Point(current.x, current.y - instruction.distance);
            } else if (RIGHT.equals(instruction.direction)) {
                current = new Point(current.x + instruction.distance, current.y);
            } else {
                current = new Point(current.x - instruction.distance, current.y);
            }
            coordinates.add(current);
            perimeter += instruction.distance;
        }
        return new Fence(coordinates, perimeter);
    }

    private static Instruction parseInstruction(String command) {
        command = command.replaceAll("[()]", "");
        String hex = command.substring(1, 6);
        String directionCode = command.substring(6);

        String direction = DIRECTION_MAP.get(directionCode);
        int distance = Integer.parseInt(hex, 16);
        if (direction == null) {
            throw new RuntimeException("Direction code was not found: " + directionCode + ".");
        }
        return new Instruction(direction, distance);
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

    static class Point {
        int x;
        int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    static class Instruction {
        String direction;
        int distance;

        public Instruction(String direction, int distance) {
            this.direction = direction;
            this.distance = distance;
        }
    }

    public static class Line {
        Point start;
        Point end;

        Line(Point start, Point end) {
            this.start = start;
            this.end = end;
        }
    }

    public static class Fence {
        List<Point> edges;
        long perimeter;

        public Fence(List<Point> edges, long perimeter) {
            this.edges = edges;
            this.perimeter = perimeter;
        }
    }
}