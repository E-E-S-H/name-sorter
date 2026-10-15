package com.dyedurham.namesorter.parsing;

import com.dyedurham.namesorter.exception.InvalidNameException;
import com.dyedurham.namesorter.model.Name;

import java.util.Arrays;
import java.util.List;

/**
 * Default implementation of {@link NameParser} that parses space-separated name strings.
 *
 * <p><strong>Parsing rules:</strong></p>
 * <ol>
 *   <li>The input string is stripped of leading/trailing whitespace.</li>
 *   <li>It is split on one or more whitespace characters (handles multiple spaces).</li>
 *   <li>The <em>last</em> token becomes the {@code lastName}.</li>
 *   <li>All <em>preceding</em> tokens become the {@code givenNames}.</li>
 *   <li>Validation of the token count is then delegated to the {@link Name} constructor.</li>
 * </ol>
 *
 * <p>This class has a single responsibility: transform a raw string into
 * a structured {@link Name}. It does not sort, read files, or write output.</p>
 */
public class DefaultNameParser implements NameParser {

    /**
     * Parses a raw space-separated name string into a {@link Name}.
     *
     * <p>Examples:</p>
     * <pre>
     *   parse("Janet Parsons")           → Name(["Janet"], "Parsons")
     *   parse("Adonis Julius Archer")    → Name(["Adonis", "Julius"], "Archer")
     *   parse("  Hunter Uriah  Clarke ") → Name(["Hunter", "Uriah"], "Clarke")
     * </pre>
     *
     * @param rawName the raw name string to parse
     * @return a structured {@link Name}
     * @throws InvalidNameException if {@code rawName} is null, blank, or a single word
     */
    @Override
    public Name parse(String rawName) {
        // Reject null inputs immediately with a clear message
        if (rawName == null) {
            throw new InvalidNameException("null", "input must not be null.");
        }

        // Trim and check for blank input (empty string or only whitespace)
        String trimmed = rawName.strip();
        if (trimmed.isEmpty()) {
            throw new InvalidNameException(rawName, "input must not be blank.");
        }

        // Split on any whitespace sequence (handles single/multiple spaces, tabs)
        String[] tokens = trimmed.split("\\s+");

        // A name needs at least 2 tokens: one given name + one last name
        if (tokens.length < 2) {
            throw new InvalidNameException(
                rawName,
                "must have at least 1 given name and 1 last name, but only 1 word was found."
            );
        }

        // Last token is the last name; everything before it is given names
        String lastName = tokens[tokens.length - 1];
        List<String> givenNames = Arrays.asList(Arrays.copyOf(tokens, tokens.length - 1));

        // Name constructor handles the max-3-given-names validation
        return new Name(givenNames, lastName);
    }

    /**
     * Returns {@code true} if the line is null, empty, or contains only whitespace.
     *
     * <p>Used by file readers to skip blank lines gracefully without throwing.</p>
     *
     * @param line the line to evaluate
     * @return {@code true} if the line should be skipped
     */
    @Override
    public boolean isBlankLine(String line) {
        return line == null || line.isBlank();
    }
}
