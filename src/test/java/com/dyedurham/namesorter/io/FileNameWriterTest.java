package com.dyedurham.namesorter.io;

import com.dyedurham.namesorter.model.Name;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for {@link FileNameWriter}.
 *
 * <p>Uses {@link TempDir} so every test gets a clean temporary directory.
 * No test leaves any files on disk after it completes.</p>
 */
@DisplayName("FileNameWriter")
class FileNameWriterTest {

    // ─── Happy Path ───────────────────────────────────────────────────────────

    @Nested
    @DisplayName("when writing to a file")
    class WritingToFile {

        @Test
        @DisplayName("should create the output file when it does not already exist")
        void shouldCreateFileIfNotExists(@TempDir Path tempDir) throws IOException {
            Path outputFile = tempDir.resolve("sorted-names-list.txt");
            List<Name> names = List.of(
                new Name(List.of("Marin"), "Alvarez"),
                new Name(List.of("Janet"), "Parsons")
            );

            new FileNameWriter(outputFile.toString()).write(names);

            assertThat(outputFile).exists();
        }

        @Test
        @DisplayName("should write each name on its own line")
        void shouldWriteEachNameOnSeparateLine(@TempDir Path tempDir) throws IOException {
            Path outputFile = tempDir.resolve("sorted-names-list.txt");
            List<Name> names = List.of(
                new Name(List.of("Marin"), "Alvarez"),
                new Name(List.of("Adonis", "Julius"), "Archer")
            );

            new FileNameWriter(outputFile.toString()).write(names);

            List<String> lines = Files.readAllLines(outputFile);
            assertThat(lines).containsExactly(
                "Marin Alvarez",
                "Adonis Julius Archer"
            );
        }

        @Test
        @DisplayName("should overwrite the file when it already exists")
        void shouldOverwriteExistingFile(@TempDir Path tempDir) throws IOException {
            Path outputFile = tempDir.resolve("sorted-names-list.txt");

            // First write
            new FileNameWriter(outputFile.toString()).write(
                List.of(new Name(List.of("Old"), "Name"))
            );

            // Second write — must completely replace the first
            new FileNameWriter(outputFile.toString()).write(
                List.of(new Name(List.of("New"), "Name"))
            );

            List<String> lines = Files.readAllLines(outputFile);
            assertThat(lines)
                .hasSize(1)
                .containsExactly("New Name");
        }

        @Test
        @DisplayName("should create an empty file when given an empty list")
        void shouldWriteEmptyFileForEmptyList(@TempDir Path tempDir) throws IOException {
            Path outputFile = tempDir.resolve("sorted-names-list.txt");

            new FileNameWriter(outputFile.toString()).write(List.of());

            assertThat(outputFile).exists();
            assertThat(Files.readAllLines(outputFile)).isEmpty();
        }
    }
}
