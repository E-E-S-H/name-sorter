package com.dyedurham.namesorter.io;

import com.dyedurham.namesorter.model.Name;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Writes a list of sorted {@link Name} objects to a plain text file.
 *
 * <p>
 * Each name is written on its own line in the format:
 * {@code "GivenName1 [GivenName2] [GivenName3] LastName"}.
 * </p>
 *
 * <p>
 * If the output file already exists, it is <strong>overwritten</strong>.
 * This satisfies the assessment requirement: "It should write/overwrite the
 * sorted list of names to a file called sorted-names-list.txt."
 * </p>
 *
 * <p>
 * The file is always written using UTF-8 encoding to support
 * international characters in names.
 * </p>
 */
public class FileNameWriter implements NameWriter {

    /** The path where the sorted names file will be written. */
    private final Path outputPath;

    /**
     * Constructs a {@code FileNameWriter} targeting the given output path.
     *
     * @param outputPath the path for the output file (e.g. "sorted-names-list.txt")
     */
    public FileNameWriter(String outputPath) {
        this.outputPath = Path.of(outputPath);
    }

    /**
     * Writes the sorted names to the output file, overwriting any existing content.
     *
     * <p>
     * Uses {@link StandardOpenOption#CREATE} and
     * {@link StandardOpenOption#TRUNCATE_EXISTING}
     * to ensure the file is created if it does not exist, or cleared and rewritten
     * if it already does.
     * </p>
     *
     * @param names the sorted list of names to write
     * @throws IOException if the file cannot be created or written to
     */
    @Override
    public void write(List<Name> names) throws IOException {
        // Convert each Name to its string representation, one per line
        List<String> lines = names.stream()
                .map(Name::toString)
                .collect(Collectors.toList());

        // Write to file — CREATE creates the file if absent, TRUNCATE_EXISTING
        // overwrites if present
        Files.write(
                outputPath,
                lines,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);
    }
}
