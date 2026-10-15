package com.dyedurham.namesorter.sorting;

import com.dyedurham.namesorter.model.Name;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Sorts a list of {@link Name} objects alphabetically by last name first,
 * then by each given name in sequence (first, second, third).
 *
 * <p><strong>Sort order example:</strong></p>
 * <pre>
 *   Input:  Beau Tristan Bentley, Marin Alvarez, Adonis Julius Archer
 *   Output: Marin Alvarez, Adonis Julius Archer, Beau Tristan Bentley
 * </pre>
 *
 * <p><strong>Design notes:</strong></p>
 * <ul>
 *   <li>This method is a <em>pure function</em>: given the same input, it always
 *       returns the same output, with no side effects (no I/O, no state mutation).</li>
 *   <li>Pure functions are the easiest to unit test — no mocking required.</li>
 *   <li>The input list is never mutated; a new sorted list is always returned.</li>
 *   <li>Sorting is case-sensitive (Java's natural String ordering). This means
 *       uppercase letters sort before lowercase. This behaviour is documented
 *       and tested rather than silently normalised.</li>
 * </ul>
 */
public class LastNameFirstSorter implements NameSorter {

    /**
     * Sorts the given list of names by last name, then by given names (first, second, third).
     *
     * <p>If two names share the same last name, they are further sorted by their
     * first given name. If those also match, the second given name is used as a
     * tiebreaker, and so on. Names with fewer given names sort before names with
     * more given names when all preceding given names are equal (empty string
     * is used as a placeholder for missing given names in the comparison).</p>
     *
     * @param names the list of names to sort (must not be {@code null})
     * @return a new list of names in sorted order; the original list is unchanged
     */
    @Override
    public List<Name> sort(List<Name> names) {
        return names.stream()
                .sorted(buildComparator())
                .collect(Collectors.toList());
    }

    /**
     * Builds the multi-level {@link Comparator} used to sort names.
     *
     * <p>Extracted into its own method to keep {@link #sort(List)} clean
     * and to allow the comparator to be tested or reused independently.</p>
     *
     * <p>Sort priority:</p>
     * <ol>
     *   <li>Last name (primary sort key)</li>
     *   <li>First given name (secondary sort key)</li>
     *   <li>Second given name, if present (tertiary sort key)</li>
     *   <li>Third given name, if present (quaternary sort key)</li>
     * </ol>
     *
     * @return a {@link Comparator} that implements the required sort order
     */
    private Comparator<Name> buildComparator() {
        return Comparator
                // 1. Sort by last name
                .comparing(Name::lastName)

                // 2. Sort by first given name
                .thenComparing(name -> name.givenNames().get(0))

                // 3. Sort by second given name (use empty string if not present)
                .thenComparing(name -> name.givenNames().size() > 1
                        ? name.givenNames().get(1)
                        : "")

                // 4. Sort by third given name (use empty string if not present)
                .thenComparing(name -> name.givenNames().size() > 2
                        ? name.givenNames().get(2)
                        : "");
    }
}
