package com.dyedurham.namesorter.io;

import com.dyedurham.namesorter.model.Name;

import java.util.List;

/**
 * Writes a list of sorted {@link Name} objects to standard output (the
 * console).
 *
 * <p>
 * This is one of two implementations of {@link NameWriter} used simultaneously
 * by the application. The other is {@link FileNameWriter}. The application
 * writes
 * to both destinations by passing a list of writers to
 * {@link com.dyedurham.namesorter.app.NameSorterApp}.
 * </p>
 *
 * <p>
 * This class intentionally has no configuration or state — it simply
 * delegates to {@link System#out}. Its existence as a separate class (rather
 * than an inline lambda) makes the dependency explicit and mockable in tests.
 * </p>
 */
public class ConsoleNameWriter implements NameWriter {

    /**
     * Prints each sorted name to the console, one per line.
     *
     * <p>
     * This method never throws — writing to {@link System#out} is always
     * available in normal JVM execution. The {@link java.io.IOException} in the
     * signature is inherited from the {@link NameWriter} interface contract.
     * </p>
     *
     * @param names the sorted list of names to print; must not be {@code null}
     */
    @Override
    public void write(List<Name> names) {
        names.forEach(System.out::println);
    }
}
