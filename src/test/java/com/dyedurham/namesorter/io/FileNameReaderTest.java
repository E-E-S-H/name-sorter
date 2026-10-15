package com.dyedurham.namesorter.io;

import com.dyedurham.namesorter.model.Name;
import com.dyedurham.namesorter.parsing.DefaultNameParser;
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
 * Unit tests for {@link FileNameReader}.
 *
 * <p>Uses JUnit 5's {@link TempDir} to create and automatically clean up
 * a temporary directory for each test. This prevents test pollution and
 * requires no manual file cleanup code.</p>
 */
@DisplayName("FileNameReader")
class FileNameReaderTest {

    // ─── Happy Path ───────────────────────────────────────────────────────────

    @Nested
    @DisplayName("when reading a valid file")
    class ValidFiles {

        @Test
        @DisplayName("should read and parse all valid names from the file")
        void shouldReadAllValidNames(@TempDir Path tempDir) throws IOException {
            // Arrange: write a temp input file with known names
            Path inputFile = tempDir.resolve("names.txt");
            Files.write(inputFile, List.of(
                "Janet Parsons",
                "Adonis Julius Archer",
                "Marin Alvarez"
            ));

            // Act
            FileNameReader reader = new FileNameReader(inputFile.toString(), new DefaultNameParser());
            List<Name> result = reader.read();

            // Assert
            assertThat(result)
                .extracting(Name::toString)
                .containsExactly(
                    "Janet Parsons",
                    "Adonis Julius Archer",
                    "Marin Alvarez"
                );
        }

        @Test
        @DisplayName("should return an empty list when the file is empty")
        void shouldReturnEmptyListForEmptyFile(@TempDir Path tempDir) throws IOException {
            Path emptyFile = tempDir.resolve("empty.txt");
            Files.createFile(emptyFile);

            FileNameReader reader = new FileNameReader(emptyFile.toString(), new DefaultNameParser());
            List<Name> result = reader.read();

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("should skip blank lines silently without throwing")
        void shouldSkipBlankLines(@TempDir Path tempDir) throws IOException {
            Path inputFile = tempDir.resolve("names.txt");
            Files.write(inputFile, List.of(
                "Janet Parsons",
                "",           // blank line — should be skipped
                "   ",        // whitespace line — should be skipped
                "Marin Alvarez"
            ));

            FileNameReader reader = new FileNameReader(inputFile.toString(), new DefaultNameParser());
            List<Name> result = reader.read();

            // Only 2 valid names; blank lines are silently skipped
            assertThat(result).hasSize(2);
            assertThat(result)
                .extracting(Name::toString)
                .containsExactly("Janet Parsons", "Marin Alvarez");
        }

        @Test
        @DisplayName("should skip invalid name lines with a warning instead of crashing")
        void shouldSkipInvalidLinesWithWarning(@TempDir Path tempDir) throws IOException {
            // "Madonna" is invalid (only one word) — reader should skip it, not crash
            Path inputFile = tempDir.resolve("names.txt");
            Files.write(inputFile, List.of(
                "Janet Parsons",
                "Madonna",           // invalid — only one word
                "Marin Alvarez"
            ));

            FileNameReader reader = new FileNameReader(inputFile.toString(), new DefaultNameParser());
            List<Name> result = reader.read();

            // The invalid line was skipped; 2 valid names remain
            assertThat(result).hasSize(2);
            assertThat(result)
                .extracting(Name::toString)
                .containsExactly("Janet Parsons", "Marin Alvarez");
        }
    }

    // ─── Failure Cases ────────────────────────────────────────────────────────

    @Nested
    @DisplayName("when the file path is invalid")
    class InvalidFilePaths {

        @Test
        @DisplayName("should throw IOException when the file does not exist")
        void shouldThrowWhenFileDoesNotExist() {
            assertThatThrownBy(() ->
                new FileNameReader("/nonexistent/path/to/names.txt", new DefaultNameParser())
            )
                .isInstanceOf(IOException.class)
                .hasMessageContaining("not found");
        }

        @Test
        @DisplayName("should throw IOException when the path points to a directory, not a file")
        void shouldThrowWhenPathIsADirectory(@TempDir Path tempDir) {
            // tempDir itself is a directory, not a file
            assertThatThrownBy(() ->
                new FileNameReader(tempDir.toString(), new DefaultNameParser())
            )
                .isInstanceOf(IOException.class)
                .hasMessageContaining("not a regular file");
        }
    }
}
