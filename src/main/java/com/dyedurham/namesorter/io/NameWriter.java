package com.dyedurham.namesorter.io;

import com.dyedurham.namesorter.model.Name;

import java.io.IOException;
import java.util.List;

/**
 * Defines the contract for writing a list of sorted {@link Name} objects to a destination.
 *
 * <p>Separating reading and writing into distinct interfaces follows the
 * <strong>Interface Segregation Principle</strong>: a class that only needs to
 * write names is not forced to implement a {@code read()} method it does not use.</p>
 *
 * <p>This design also follows the <strong>Open/Closed Principle</strong>: new
 * destinations (e.g. a database writer, a REST API writer) can be added as new
 * implementations without changing any existing code. The {@link com.dyedurham.namesorter.app.NameSorterApp}
 * accepts a list of writers, so multiple destinations can be active simultaneously.</p>
 */
public interface NameWriter {

    /**
     * Writes the given list of sorted names to this writer's destination.
     *
     * <p>If the destination already exists (e.g. a file), it must be overwritten.</p>
     *
     * @param names the sorted list of names to write; must not be {@code null}
     * @throws IOException if the destination cannot be written to
     */
    void write(List<Name> names) throws IOException;
}
