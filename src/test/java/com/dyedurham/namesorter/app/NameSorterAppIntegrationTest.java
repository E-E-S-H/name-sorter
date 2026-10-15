package com.dyedurham.namesorter.app;

import com.dyedurham.namesorter.io.*;
import com.dyedurham.namesorter.parsing.DefaultNameParser;
import com.dyedurham.namesorter.parsing.NameParser;
import com.dyedurham.namesorter.sorting.LastNameFirstSorter;
import com.dyedurham.namesorter.sorting.NameSorter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration tests for the full name sorting pipeline via {@link NameSorterApp}.
 *
 * <p>Unlike unit tests, integration tests exercise all real components working together:
 * {@link FileNameReader} → {@link LastNameFirstSorter} → {@link FileNameWriter}.
 * These tests provide high confidence that the system works end-to-end.</p>
 *
 * <p>{@link TempDir} ensures each test gets a fresh directory and all created
 * files are cleaned up automatically after the test completes.</p>
 */
@DisplayName("NameSorterApp (Integration)")
class NameSorterAppIntegrationTest {

    @Test
    @DisplayName("should sort names and write correct output to file (assessment specification)")
    void shouldSortAndWriteCorrectOutputMatchingSpecification(@TempDir Path tempDir) throws IOException {
        // Arrange — write the exact input from the assessment spec to a temp file
        Path inputFile = tempDir.resolve("unsorted-names-list.txt");
        Files.write(inputFile, List.of(
            "Janet Parsons",
            "Vaughn Lewis",
            "Adonis Julius Archer",
            "Shelby Nathan Yoder",
            "Marin Alvarez",
            "London Lindsey",
            "Beau Tristan Bentley",
            "Leo Gardner",
            "Hunter Uriah Mathew Clarke",
            "Mikayla Lopez",
            "Frankie Conner Ritter"
        ));

        Path outputFile = tempDir.resolve("sorted-names-list.txt");

        // Act — wire up the full real pipeline (no mocks)
        NameParser parser   = new DefaultNameParser();
        NameReader reader   = new FileNameReader(inputFile.toString(), parser);
        NameSorter sorter   = new LastNameFirstSorter();
        NameWriter toFile   = new FileNameWriter(outputFile.toString());
        NameWriter toScreen = new ConsoleNameWriter(); // safe to use — stdout doesn't affect assertions

        new NameSorterApp(reader, sorter, List.of(toFile, toScreen)).run();

        // Assert — output file must contain the sorted names in the exact expected order
        List<String> result = Files.readAllLines(outputFile);
        assertThat(result).containsExactly(
            "Marin Alvarez",
            "Adonis Julius Archer",
            "Beau Tristan Bentley",
            "Hunter Uriah Mathew Clarke",
            "Leo Gardner",
            "Vaughn Lewis",
            "London Lindsey",
            "Mikayla Lopez",
            "Janet Parsons",
            "Frankie Conner Ritter",
            "Shelby Nathan Yoder"
        );
    }

    @Test
    @DisplayName("should overwrite an existing sorted-names-list.txt file")
    void shouldOverwriteExistingOutputFile(@TempDir Path tempDir) throws IOException {
        // Arrange — pre-create the output file with stale content
        Path inputFile  = tempDir.resolve("unsorted-names-list.txt");
        Path outputFile = tempDir.resolve("sorted-names-list.txt");
        Files.write(inputFile, List.of("Janet Parsons", "Marin Alvarez"));
        Files.write(outputFile, List.of("STALE CONTENT FROM PREVIOUS RUN"));

        // Act
        NameParser parser  = new DefaultNameParser();
        NameReader reader  = new FileNameReader(inputFile.toString(), parser);
        NameSorter sorter  = new LastNameFirstSorter();
        NameWriter toFile  = new FileNameWriter(outputFile.toString());

        new NameSorterApp(reader, sorter, List.of(toFile, new ConsoleNameWriter())).run();

        // Assert — stale content is gone; file contains fresh sorted output
        List<String> result = Files.readAllLines(outputFile);
        assertThat(result).containsExactly(
            "Marin Alvarez",
            "Janet Parsons"
        );
    }

    @Test
    @DisplayName("should produce an empty output file when the input file is empty")
    void shouldProduceEmptyOutputForEmptyInput(@TempDir Path tempDir) throws IOException {
        Path inputFile  = tempDir.resolve("unsorted-names-list.txt");
        Path outputFile = tempDir.resolve("sorted-names-list.txt");
        Files.createFile(inputFile); // empty file

        NameParser parser = new DefaultNameParser();
        NameReader reader = new FileNameReader(inputFile.toString(), parser);
        NameSorter sorter = new LastNameFirstSorter();
        NameWriter toFile = new FileNameWriter(outputFile.toString());

        new NameSorterApp(reader, sorter, List.of(toFile, new ConsoleNameWriter())).run();

        assertThat(Files.readAllLines(outputFile)).isEmpty();
    }

    @Test
    @DisplayName("should gracefully skip invalid lines and sort the remaining valid names")
    void shouldSkipInvalidLinesAndSortRemainder(@TempDir Path tempDir) throws IOException {
        Path inputFile  = tempDir.resolve("unsorted-names-list.txt");
        Path outputFile = tempDir.resolve("sorted-names-list.txt");
        Files.write(inputFile, List.of(
            "Janet Parsons",
            "Madonna",          // invalid — only one word, should be skipped
            "",                 // blank line — should be skipped
            "Marin Alvarez"
        ));

        NameParser parser = new DefaultNameParser();
        NameReader reader = new FileNameReader(inputFile.toString(), parser);
        NameSorter sorter = new LastNameFirstSorter();
        NameWriter toFile = new FileNameWriter(outputFile.toString());

        new NameSorterApp(reader, sorter, List.of(toFile, new ConsoleNameWriter())).run();

        // Only the 2 valid names should appear in the output
        assertThat(Files.readAllLines(outputFile)).containsExactly(
            "Marin Alvarez",
            "Janet Parsons"
        );
    }
}
