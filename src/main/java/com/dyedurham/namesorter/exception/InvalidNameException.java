package com.dyedurham.namesorter.exception;

/**
 * Thrown when a raw string cannot be parsed into a valid
 * {@link com.dyedurham.namesorter.model.Name}.
 *
 * <p>
 * This is an unchecked (runtime) exception because an invalid name is a
 * data/programmer
 * error that the caller cannot reasonably recover from at runtime. Using a
 * dedicated exception
 * type (rather than a raw {@link IllegalArgumentException}) makes it easy to
 * catch specifically
 * in integration code and produce helpful error messages.
 * </p>
 *
 * <p>
 * Example scenarios that trigger this exception:
 * </p>
 * <ul>
 * <li>A name with only one word (no last name can be determined)</li>
 * <li>A name with more than 3 given names</li>
 * <li>A null or blank input string</li>
 * </ul>
 */
public class InvalidNameException extends RuntimeException {

    /**
     * Constructs a new exception with a message that includes the invalid input
     * and the specific reason it was rejected.
     *
     * @param rawName the raw string that failed to parse (included in the message
     *                for debugging)
     * @param reason  a human-readable explanation of why the input is invalid
     */
    public InvalidNameException(String rawName, String reason) {
        super(String.format("Cannot parse name '%s': %s", rawName, reason));
    }
}
