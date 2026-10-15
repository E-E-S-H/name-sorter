package com.dyedurham.namesorter.parsing;

import com.dyedurham.namesorter.model.Name;

/**
 * Defines the contract for parsing a raw string into a {@link Name} object.
 *
 * <p>Separating this interface from its implementation follows the
 * <strong>Dependency Inversion Principle</strong>: callers depend on this
 * abstraction, not on any specific parsing strategy. This makes it easy to
 * swap in an alternative parser (e.g. CSV, JSON) without changing any
 * consuming code.</p>
 *
 * <p>Implementations are responsible for:</p>
 * <ul>
 *   <li>Splitting the raw string into given names and a last name</li>
 *   <li>Trimming whitespace</li>
 *   <li>Throwing {@link com.dyedurham.namesorter.exception.InvalidNameException}
 *       for inputs that cannot produce a valid {@link Name}</li>
 * </ul>
 */
public interface NameParser {

    /**
     * Parses a raw name string into a structured {@link Name} object.
     *
     * @param rawName the raw string to parse (e.g. "Janet Parsons")
     * @return a valid {@link Name} object
     * @throws com.dyedurham.namesorter.exception.InvalidNameException
     *         if the string is null, blank, has only one word, or has more than 4 words
     */
    Name parse(String rawName);

    /**
     * Returns {@code true} if the given line is blank or contains only whitespace.
     *
     * <p>Used by readers to skip empty lines in input files without throwing
     * an exception, since blank lines are not an error — just noise.</p>
     *
     * @param line the line to check
     * @return {@code true} if the line should be skipped
     */
    boolean isBlankLine(String line);
}
