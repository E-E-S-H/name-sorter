package com.dyedurham.namesorter.io;

import com.dyedurham.namesorter.model.Name;

import java.io.IOException;
import java.util.List;

/**
 * Defines the contract for reading a list of {@link Name} objects from a source.
 *
 * <p>Abstracting the source behind this interface follows the
 * <strong>Dependency Inversion Principle</strong>: the application core depends
 * on this interface, not on a concrete file implementation. This makes it trivial
 * to swap the source (e.g. from a file to a database or REST API) without
 * touching any business logic.</p>
 *
 * <p>Implementations are responsible for:</p>
 * <ul>
 *   <li>Skipping blank lines gracefully</li>
 *   <li>Logging warnings for lines that cannot be parsed</li>
 *   <li>Throwing {@link IOException} if the source cannot be accessed</li>
 * </ul>
 */
public interface NameReader {

    /**
     * Reads and returns all valid names from the source.
     *
     * <p>Invalid lines (blank lines or unparseable names) are skipped with a
     * warning rather than causing the entire operation to fail.</p>
     *
     * @return a list of parsed {@link Name} objects; may be empty but never {@code null}
     * @throws IOException if the underlying source cannot be read
     */
    List<Name> read() throws IOException;
}
