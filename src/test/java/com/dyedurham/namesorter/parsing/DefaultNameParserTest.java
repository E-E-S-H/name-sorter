package com.dyedurham.namesorter.parsing;

import com.dyedurham.namesorter.exception.InvalidNameException;
import com.dyedurham.namesorter.model.Name;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for {@link DefaultNameParser}.
 *
 * <p>These tests verify that the parser correctly splits raw name strings
 * into structured {@link Name} objects, handles whitespace gracefully,
 * and throws descriptive exceptions for invalid inputs.</p>
 */
@DisplayName("DefaultNameParser")
class DefaultNameParserTest {

    private DefaultNameParser parser;

    @BeforeEach
    void setUp() {
        // Fresh instance before each test — no shared state
        parser = new DefaultNameParser();
    }

    // ─── Happy Path ───────────────────────────────────────────────────────────

    @Nested
    @DisplayName("when parsing valid name strings")
    class ValidNames {

        @Test
        @DisplayName("should parse a name with 1 given name and 1 last name")
        void shouldParseOneGivenName() {
            Name result = parser.parse("Janet Parsons");

            assertThat(result.givenNames()).containsExactly("Janet");
            assertThat(result.lastName()).isEqualTo("Parsons");
        }

        @Test
        @DisplayName("should parse a name with 2 given names")
        void shouldParseTwoGivenNames() {
            Name result = parser.parse("Adonis Julius Archer");

            assertThat(result.givenNames()).containsExactly("Adonis", "Julius");
            assertThat(result.lastName()).isEqualTo("Archer");
        }

        @Test
        @DisplayName("should parse a name with 3 given names (maximum allowed)")
        void shouldParseThreeGivenNames() {
            Name result = parser.parse("Hunter Uriah Mathew Clarke");

            assertThat(result.givenNames()).containsExactly("Hunter", "Uriah", "Mathew");
            assertThat(result.lastName()).isEqualTo("Clarke");
        }

        @Test
        @DisplayName("should trim leading and trailing whitespace before parsing")
        void shouldTrimLeadingAndTrailingWhitespace() {
            Name result = parser.parse("  Janet Parsons  ");

            assertThat(result.toString()).isEqualTo("Janet Parsons");
        }

        @Test
        @DisplayName("should handle multiple spaces between words")
        void shouldHandleMultipleSpacesBetweenWords() {
            // 'split("\\s+")' collapses multiple spaces into a single split
            Name result = parser.parse("Janet   Parsons");

            assertThat(result.givenNames()).containsExactly("Janet");
            assertThat(result.lastName()).isEqualTo("Parsons");
        }

        @Test
        @DisplayName("should handle hyphenated names as a single token")
        void shouldTreatHyphenatedNameAsSingleToken() {
            // Hyphens are not whitespace — "Mary-Jane" is one token
            Name result = parser.parse("Mary-Jane Watson");

            assertThat(result.givenNames()).containsExactly("Mary-Jane");
            assertThat(result.lastName()).isEqualTo("Watson");
        }
    }

    // ─── Blank Line Detection ─────────────────────────────────────────────────

    @Nested
    @DisplayName("when checking for blank lines")
    class BlankLineDetection {

        @ParameterizedTest(name = "Line [{0}] should be identified as blank")
        @ValueSource(strings = {"", "   ", "\t", "\n", "  \t  "})
        @DisplayName("should return true for all whitespace-only inputs")
        void shouldReturnTrueForAllBlankInputs(String blank) {
            assertThat(parser.isBlankLine(blank)).isTrue();
        }

        @Test
        @DisplayName("should return true for null input")
        void shouldReturnTrueForNull() {
            assertThat(parser.isBlankLine(null)).isTrue();
        }

        @Test
        @DisplayName("should return false for a valid name string")
        void shouldReturnFalseForValidName() {
            assertThat(parser.isBlankLine("Janet Parsons")).isFalse();
        }
    }

    // ─── Failure Cases ────────────────────────────────────────────────────────

    @Nested
    @DisplayName("when parsing invalid name strings")
    class InvalidNames {

        @Test
        @DisplayName("should throw InvalidNameException for null input")
        void shouldThrowForNullInput() {
            assertThatThrownBy(() -> parser.parse(null))
                .isInstanceOf(InvalidNameException.class)
                .hasMessageContaining("null")
                .hasMessageContaining("must not be null");
        }

        @Test
        @DisplayName("should throw InvalidNameException for empty string")
        void shouldThrowForEmptyString() {
            assertThatThrownBy(() -> parser.parse(""))
                .isInstanceOf(InvalidNameException.class)
                .hasMessageContaining("must not be blank");
        }

        @ParameterizedTest(name = "Blank input [{0}] should throw InvalidNameException")
        @ValueSource(strings = {"   ", "\t", "\n"})
        @DisplayName("should throw InvalidNameException for all blank/whitespace-only inputs")
        void shouldThrowForBlankInputs(String blank) {
            assertThatThrownBy(() -> parser.parse(blank))
                .isInstanceOf(InvalidNameException.class);
        }

        @Test
        @DisplayName("should throw InvalidNameException when only one word is given (no last name)")
        void shouldThrowForSingleWord() {
            assertThatThrownBy(() -> parser.parse("Madonna"))
                .isInstanceOf(InvalidNameException.class)
                .hasMessageContaining("Madonna")
                .hasMessageContaining("1 word");
        }

        @Test
        @DisplayName("should throw InvalidNameException when more than 3 given names are provided")
        void shouldThrowForFourGivenNames() {
            // "A B C D LastName" → 4 given names → exceeds limit
            assertThatThrownBy(() -> parser.parse("A B C D LastName"))
                .isInstanceOf(InvalidNameException.class)
                .hasMessageContaining("at most 3 given names");
        }
    }
}
