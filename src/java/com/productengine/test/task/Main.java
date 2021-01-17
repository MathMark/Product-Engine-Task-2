package com.productengine.test.task;

import com.productengine.test.task.navigator.ParallelIteratorNavigator;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Enter root path: ");
        String rootPath = reader.readLine();
        File file = new File(rootPath);
        if (!file.exists()) {
            throw new IllegalArgumentException("Incorrect file path. Directory does not exist");
        }
        if (!file.isDirectory()) {
            throw new IllegalArgumentException("You need to enter a path to a directory.");
        }

        System.out.print("Enter depth: ");
        int depth = Integer.parseInt(reader.readLine());
        if (depth < 0) {
            throw new IllegalArgumentException("Depth must not be negative!");
        }
        System.out.print("Enter mask: ");
        String mask = reader.readLine();

        ParallelIteratorNavigator navigator = new ParallelIteratorNavigator();
        navigator.getFilesThatMatch(rootPath, depth, mask, System.out::println);

    }
}
