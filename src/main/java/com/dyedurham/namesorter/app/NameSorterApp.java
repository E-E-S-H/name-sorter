package com.dyedurham.namesorter.app;

import com.dyedurham.namesorter.io.NameReader;
import com.dyedurham.namesorter.io.NameWriter;
import com.dyedurham.namesorter.model.Name;
import com.dyedurham.namesorter.sorting.NameSorter;

import java.io.IOException;
import java.util.List;

/**
 * Orchestrates the name sorting pipeline.
 *
 * <p>
 * This class is the glue between all the independent components.
 * Its one job is to coordinate the following steps in order:
 * </p>
 * <ol>
 * <li><strong>Read</strong> — load names from the source via
 * {@link NameReader}</li>
 * <li><strong>Sort</strong> — sort the names via {@link NameSorter}</li>
 * <li><strong>Write</strong> — send sorted names to all configured destinations
 * via {@link NameWriter}</li>
 * </ol>
 *
 * <p>
 * <strong>SOLID principles applied here:</strong>
 * </p>
 * <ul>
 * <li><strong>Single Responsibility:</strong> This class only orchestrates — it
 * does
 * not read files, sort names, or format output itself.</li>
 * <li><strong>Dependency Inversion:</strong> This class depends on the
 * {@link NameReader}, {@link NameSorter}, and {@link NameWriter} interfaces,
 * not on any concrete implementations. The concrete classes are injected
 * from outside (in {@link com.dyedurham.namesorter.Main}).</li>
 * <li><strong>Open/Closed:</strong> Adding a new output destination (e.g. a
 * database
 * writer) requires only adding it to the {@code writers} list — no changes
 * here.</li>
 * </ul>
 */
public class NameSorterApp {

    /** Provides the list of names to be sorted. */
    private final NameReader reader;

    /** Determines the sort order applied to the names. */
    private final NameSorter sorter;

    /**
     * All destinations where the sorted names will be written.
     * Using a list allows writing to multiple destinations simultaneously
     * (e.g. both a file and the console).
     */
    private final List<NameWriter> writers;

    /**
     * Constructs the application with all required dependencies.
     *
     * <p>
     * All dependencies are injected rather than instantiated internally.
     * This makes the class fully testable — test code can supply mock or
     * in-memory implementations of each dependency.
     * </p>
     *
     * @param reader  the source from which names are read
     * @param sorter  the strategy used to sort the names
     * @param writers the destinations where sorted names are written
     */
    public NameSorterApp(NameReader reader, NameSorter sorter, List<NameWriter> writers) {
        this.reader = reader;
        this.sorter = sorter;
        this.writers = writers;
    }

    /**
     * Executes the full name sorting pipeline: read → sort → write.
     *
     * <p>
     * All writers in the list are called in order. If one writer fails,
     * the exception propagates and subsequent writers are not called.
     * This is intentional — a partial write is worse than no write.
     * </p>
     *
     * @throws IOException if reading or writing fails
     */
    public void run() throws IOException {
        // Step 1: Read all names from the source
        List<Name> names = reader.read();

        // Step 2: Sort the names using the configured sorting strategy
        List<Name> sortedNames = sorter.sort(names);

        // Step 3: Write the sorted names to every configured destination
        for (NameWriter writer : writers) {
            writer.write(sortedNames);
        }
    }
}
