package com.dyedurham.namesorter.model;

import com.dyedurham.namesorter.exception.InvalidNameException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for the {@link Name} value object.
 *
 * <p>Tests are grouped using {@link Nested} inner classes to separate
 * construction (happy path), edge cases, and failure cases visually
 * in the test report.</p>
 */
@DisplayName("Name")
class NameTest {

    // ─── Happy Path ───────────────────────────────────────────────────────────

    @Nested
    @DisplayName("when constructed with valid input")
    class ValidConstruction {

        @Test
        @DisplayName("should accept exactly 1 given name (minimum)")
        void shouldAcceptOneGivenName() {
            Name name = new Name(List.of("Janet"), "Parsons");

            assertThat(name.givenNames()).containsExactly("Janet");
            assertThat(name.lastName()).isEqualTo("Parsons");
        }

        @Test
        @DisplayName("should accept exactly 2 given names")
        void shouldAcceptTwoGivenNames() {
            Name name = new Name(List.of("Adonis", "Julius"), "Archer");

            assertThat(name.givenNames()).containsExactly("Adonis", "Julius");
            assertThat(name.lastName()).isEqualTo("Archer");
        }

        @Test
        @DisplayName("should accept exactly 3 given names (maximum)")
        void shouldAcceptThreeGivenNames() {
            Name name = new Name(List.of("Hunter", "Uriah", "Mathew"), "Clarke");

            assertThat(name.givenNames()).containsExactly("Hunter", "Uriah", "Mathew");
            assertThat(name.lastName()).isEqualTo("Clarke");
        }

        @Test
        @DisplayName("toString() should format as 'Given1 LastName' for single given name")
        void shouldFormatSingleGivenName() {
            Name name = new Name(List.of("Janet"), "Parsons");

            assertThat(name.toString()).isEqualTo("Janet Parsons");
        }

        @Test
        @DisplayName("toString() should format as 'Given1 Given2 Given3 LastName' for three given names")
        void shouldFormatThreeGivenNames() {
            Name name = new Name(List.of("Hunter", "Uriah", "Mathew"), "Clarke");

            assertThat(name.toString()).isEqualTo("Hunter Uriah Mathew Clarke");
        }

        @Test
        @DisplayName("two Names with identical data should be equal (record equality)")
        void shouldBeEqualWhenDataIsIdentical() {
            Name first  = new Name(List.of("Janet"), "Parsons");
            Name second = new Name(List.of("Janet"), "Parsons");

            assertThat(first).isEqualTo(second);
            assertThat(first.hashCode()).isEqualTo(second.hashCode());
        }

        @Test
        @DisplayName("given names list should be immutable after construction")
        void givenNamesListShouldBeImmutable() {
            Name name = new Name(List.of("Janet"), "Parsons");

            // Attempting to mutate the returned list should throw
            assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> name.givenNames().add("Extra"));
        }
    }

    // ─── Failure Cases ────────────────────────────────────────────────────────

    @Nested
    @DisplayName("when constructed with invalid input")
    class InvalidConstruction {

        @Test
        @DisplayName("should throw InvalidNameException when given names list is null")
        void shouldThrowWhenGivenNamesIsNull() {
            assertThatThrownBy(() -> new Name(null, "Parsons"))
                .isInstanceOf(InvalidNameException.class);
        }

        @Test
        @DisplayName("should throw InvalidNameException when given names list is empty")
        void shouldThrowWhenGivenNamesIsEmpty() {
            assertThatThrownBy(() -> new Name(List.of(), "Parsons"))
                .isInstanceOf(InvalidNameException.class)
                .hasMessageContaining("at least");
        }

        @Test
        @DisplayName("should throw InvalidNameException when more than 3 given names are provided")
        void shouldThrowWhenFourGivenNamesProvided() {
            assertThatThrownBy(() -> new Name(List.of("A", "B", "C", "D"), "Smith"))
                .isInstanceOf(InvalidNameException.class)
                .hasMessageContaining("at most 3 given names");
        }

        @Test
        @DisplayName("should throw InvalidNameException when last name is null")
        void shouldThrowWhenLastNameIsNull() {
            assertThatThrownBy(() -> new Name(List.of("Janet"), null))
                .isInstanceOf(InvalidNameException.class);
        }

        @Test
        @DisplayName("should throw InvalidNameException when last name is blank")
        void shouldThrowWhenLastNameIsBlank() {
            assertThatThrownBy(() -> new Name(List.of("Janet"), "   "))
                .isInstanceOf(InvalidNameException.class);
        }
    }
}
