package com.dyedurham.namesorter.sorting;

import com.dyedurham.namesorter.model.Name;

import java.util.List;

/**
 * Defines the contract for sorting a list of {@link Name} objects.
 *
 * <p>By coding against this interface rather than a concrete class, the application
 * follows the <strong>Open/Closed Principle</strong>: new sorting strategies
 * (e.g. sort by first name, sort by name length) can be added by creating a new
 * implementation — without modifying any existing code.</p>
 *
 * <p>Implementations must:</p>
 * <ul>
 *   <li>Return a new sorted list — they must NOT mutate the input list.</li>
 *   <li>Handle empty and single-element lists gracefully.</li>
 * </ul>
 */
public interface NameSorter {

    /**
     * Sorts the given list of names according to the implementation's ordering strategy.
     *
     * @param names the unsorted list of names (must not be {@code null})
     * @return a new sorted list of names; the original list is not modified
     */
    List<Name> sort(List<Name> names);
}
