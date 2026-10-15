package com.dyedurham.namesorter;

import com.dyedurham.namesorter.app.NameSorterApp;
import com.dyedurham.namesorter.io.*;
import com.dyedurham.namesorter.parsing.DefaultNameParser;
import com.dyedurham.namesorter.parsing.NameParser;
import com.dyedurham.namesorter.sorting.LastNameFirstSorter;
import com.dyedurham.namesorter.sorting.NameSorter;

import java.io.IOException;
import java.util.List;

/**
 * Entry point for the Name Sorter command-line application.
 *
 * <p>
 * This class is intentionally minimal — it only handles CLI argument validation
 * and wires together the application's dependencies. All business logic lives
 * in
 * the domain and service classes.
 * </p>
 *
 * <p>
 * <strong>Usage:</strong>
 * </p>
 * 
 * <pre>
 *   java -jar name-sorter.jar ./unsorted-names-list.txt
 * </pre>
 *
 * <p>
 * <strong>Output:</strong>
 * </p>
 * <ul>
 * <li>Sorted names printed to the console (stdout)</li>
 * <li>Sorted names written to {@code sorted-names-list.txt} in the current
 * directory</li>
 * </ul>
 *
 * <p>
 * Exit codes:
 * </p>
 * <ul>
 * <li>{@code 0} — success</li>
 * <li>{@code 1} — incorrect usage or I/O error</li>
 * </ul>
 */
public class Main {

    /** The file name where sorted output is always written. */
    private static final String OUTPUT_FILE = "sorted-names-list.txt";

    /**
     * Application entry point.
     *
     * @param args command-line arguments; expects exactly one: the path to the
     *             input names file
     */
    public static void main(String[] args) {
        // Validate that exactly one argument (the input file path) was provided
        if (args.length != 1) {
            System.err.println("Usage: name-sorter <path-to-names-file>");
            System.err.println("Example: name-sorter ./unsorted-names-list.txt");
            System.exit(1);
        }

        String inputFilePath = args[0];

        try {
            // Wire up all dependencies — this is the only place that knows
            // about concrete implementations. Everything else works through interfaces.
            NameParser parser = new DefaultNameParser();
            NameReader reader = new FileNameReader(inputFilePath, parser);
            NameSorter sorter = new LastNameFirstSorter();
            NameWriter toFile = new FileNameWriter(OUTPUT_FILE);
            NameWriter toScreen = new ConsoleNameWriter();

            // The app receives both writers — it writes to file AND console simultaneously
            NameSorterApp app = new NameSorterApp(reader, sorter, List.of(toFile, toScreen));
            app.run();

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }
}
