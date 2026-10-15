package com.dyedurham.namesorter.io;

import com.dyedurham.namesorter.exception.InvalidNameException;
import com.dyedurham.namesorter.model.Name;
import com.dyedurham.namesorter.parsing.NameParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads a list of {@link Name} objects from a plain text file.
 *
 * <p>Each line in the file is expected to contain exactly one full name
 * in the format: {@code "GivenName1 [GivenName2] [GivenName3] LastName"}.</p>
 *
 * <p><strong>Resilient parsing strategy:</strong> Rather than crashing the
 * entire program when one line is invalid, this reader skips problematic lines
 * and prints a warning. This is appropriate for the assessment scenario where
 * a 1000-name file might contain occasional blank lines or formatting issues.</p>
 *
 * <p>This class has a single responsibility: reading names from a file.
 * It delegates all parsing logic to the injected {@link NameParser}.</p>
 */
public class FileNameReader implements NameReader {

    /** The path to the file containing unsorted names. */
    private final Path filePath;

    /**
     * The parser used to convert each raw line into a {@link Name}.
     * Injected via constructor to follow the Dependency Inversion Principle.
     */
    private final NameParser parser;

    /**
     * Constructs a {@code FileNameReader} for the given file path.
     *
     * @param filePath the path to the input names file (e.g. "./unsorted-names-list.txt")
     * @param parser   the parser used to convert raw lines into {@link Name} objects
     * @throws IOException if the specified file does not exist or cannot be accessed
     */
    public FileNameReader(String filePath, NameParser parser) throws IOException {
        this.filePath = Path.of(filePath);
        this.parser = parser;

        // Validate the file exists at construction time so failures are reported
        // immediately, rather than later when read() is called.
        if (!Files.exists(this.filePath)) {
            throw new IOException(
                "Input file not found: '" + filePath + "'. " +
                "Please provide a valid path to the names file."
            );
        }

        if (!Files.isRegularFile(this.filePath)) {
            throw new IOException(
                "'" + filePath + "' is not a regular file. Please provide a file path, not a directory."
            );
        }
    }

    /**
     * Reads all names from the file, skipping blank lines and logging warnings
     * for any lines that cannot be parsed.
     *
     * <p>This resilient strategy ensures that a single bad line in a 1000-name
     * file does not abort the entire operation.</p>
     *
     * @return a list of successfully parsed {@link Name} objects; may be empty
     * @throws IOException if the file cannot be read
     */
    @Override
    public List<Name> read() throws IOException {
        List<String> lines = Files.readAllLines(filePath);
        List<Name> names = new ArrayList<>();

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);

            // Skip blank lines silently — they are noise, not errors
            if (parser.isBlankLine(line)) {
                continue;
            }

            try {
                names.add(parser.parse(line));
            } catch (InvalidNameException e) {
                // Log a warning and continue — one bad line should not abort the program
                System.err.printf(
                    "WARNING: Skipping line %d — %s%n", i + 1, e.getMessage()
                );
            }
        }

        return names;
    }
}
