package com.dyedurham.namesorter.model;

import com.dyedurham.namesorter.exception.InvalidNameException;

import java.util.List;

/**
 * Represents a person's full name as an immutable value object.
 *
 * <p>A valid name consists of:</p>
 * <ul>
 *   <li>One {@code lastName} — always the final word in the original string</li>
 *   <li>Between 1 and 3 {@code givenNames} — all words preceding the last name</li>
 * </ul>
 *
 * <p>This class is implemented as a Java {@code record}, which provides:</p>
 * <ul>
 *   <li>Immutability by default (fields are final)</li>
 *   <li>Auto-generated {@code equals()}, {@code hashCode()}, and component accessors</li>
 *   <li>A clean, minimal declaration that communicates intent clearly</li>
 * </ul>
 *
 * <p>Validation is enforced in the compact constructor so that an invalid
 * {@code Name} can never be constructed — this is the "fail fast" principle.</p>
 *
 * <p>Example valid names:</p>
 * <pre>
 *   new Name(List.of("Janet"), "Parsons")
 *   new Name(List.of("Adonis", "Julius"), "Archer")
 *   new Name(List.of("Hunter", "Uriah", "Mathew"), "Clarke")
 * </pre>
 */
public record Name(List<String> givenNames, String lastName) {

    /** The minimum number of given names a person must have. */
    private static final int MIN_GIVEN_NAMES = 1;

    /** The maximum number of given names a person may have. */
    private static final int MAX_GIVEN_NAMES = 3;

    /**
     * Compact constructor — runs automatically before the record is created.
     *
     * <p>Validates all business rules and makes a defensive copy of the given names list
     * so that external mutation of the original list cannot affect this record.</p>
     *
     * @throws InvalidNameException if any validation rule is violated
     */
    public Name {
        // Validate last name
        if (lastName == null || lastName.isBlank()) {
            throw new InvalidNameException(lastName, "last name must not be null or blank.");
        }

        // Validate given names list exists and is not empty
        if (givenNames == null || givenNames.isEmpty()) {
            throw new InvalidNameException(
                String.join(" ", givenNames == null ? List.of() : givenNames) + " " + lastName,
                "a name must have at least " + MIN_GIVEN_NAMES + " given name."
            );
        }

        // Validate given names count does not exceed the maximum
        if (givenNames.size() > MAX_GIVEN_NAMES) {
            throw new InvalidNameException(
                String.join(" ", givenNames) + " " + lastName,
                "a name may have at most " + MAX_GIVEN_NAMES + " given names, but found " + givenNames.size() + "."
            );
        }

        // Defensive copy: prevent external mutation of the list after construction
        givenNames = List.copyOf(givenNames);
    }

    /**
     * Returns the full name formatted as: "GivenName1 [GivenName2] [GivenName3] LastName"
     *
     * <p>This is the canonical display format used for both console output
     * and file output.</p>
     *
     * @return the full name as a single space-separated string
     */
    @Override
    public String toString() {
        return String.join(" ", givenNames) + " " + lastName;
    }
}
