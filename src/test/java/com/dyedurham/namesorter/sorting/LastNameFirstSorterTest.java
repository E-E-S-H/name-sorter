package com.dyedurham.namesorter.sorting;

import com.dyedurham.namesorter.model.Name;
import com.dyedurham.namesorter.parsing.DefaultNameParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for {@link LastNameFirstSorter}.
 *
 * <p>Because {@link LastNameFirstSorter#sort(List)} is a pure function
 * (no I/O, no side effects), these tests require no mocking and run extremely fast.
 * This is the most critical and deeply tested layer of the application.</p>
 */
@DisplayName("LastNameFirstSorter")
class LastNameFirstSorterTest {

    private LastNameFirstSorter sorter;
    private DefaultNameParser parser;

    @BeforeEach
    void setUp() {
        sorter = new LastNameFirstSorter();
        parser = new DefaultNameParser();
    }

    // ─── Edge Cases ───────────────────────────────────────────────────────────

    @Nested
    @DisplayName("with edge case inputs")
    class EdgeCases {

        @Test
        @DisplayName("should return an empty list when given an empty list")
        void shouldReturnEmptyListForEmptyInput() {
            List<Name> result = sorter.sort(List.of());

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("should return the same single name when list has only one entry")
        void shouldReturnSingleNameUnchanged() {
            List<Name> input = List.of(parser.parse("Janet Parsons"));

            List<Name> result = sorter.sort(input);

            assertThat(result)
                .extracting(Name::toString)
                .containsExactly("Janet Parsons");
        }

        @Test
        @DisplayName("should return a correctly ordered list when input is already sorted")
        void shouldHandleAlreadySortedList() {
            List<Name> input = List.of(
                parser.parse("Marin Alvarez"),
                parser.parse("Janet Parsons")
            );

            List<Name> result = sorter.sort(input);

            assertThat(result)
                .extracting(Name::toString)
                .containsExactly("Marin Alvarez", "Janet Parsons");
        }

        @Test
        @DisplayName("should not mutate the original input list")
        void shouldNotMutateInputList() {
            // Ensures sort() returns a NEW list and doesn't reorder the caller's list in-place
            List<Name> original = new ArrayList<>();
            original.add(parser.parse("Zoe Smith"));
            original.add(parser.parse("Anna Smith"));
            List<Name> snapshot = new ArrayList<>(original);

            sorter.sort(original);

            // Original list must remain unchanged after calling sort()
            assertThat(original)
                .extracting(Name::toString)
                .containsExactlyElementsOf(snapshot.stream().map(Name::toString).toList());
        }
    }

    // ─── Sorting Logic ────────────────────────────────────────────────────────

    @Nested
    @DisplayName("when sorting by last name")
    class SortByLastName {

        @Test
        @DisplayName("should sort names alphabetically by last name")
        void shouldSortByLastNameAlphabetically() {
            List<Name> input = List.of(
                parser.parse("Janet Parsons"),      // P
                parser.parse("Marin Alvarez"),      // A
                parser.parse("Mikayla Lopez")       // L
            );

            assertThat(sorter.sort(input))
                .extracting(Name::toString)
                .containsExactly(
                    "Marin Alvarez",
                    "Mikayla Lopez",
                    "Janet Parsons"
                );
        }

        @Test
        @DisplayName("should sort by first given name when last names are equal")
        void shouldSortByFirstGivenNameWhenLastNamesMatch() {
            List<Name> input = List.of(
                parser.parse("Zoe Smith"),
                parser.parse("Anna Smith")
            );

            assertThat(sorter.sort(input))
                .extracting(Name::toString)
                .containsExactly("Anna Smith", "Zoe Smith");
        }

        @Test
        @DisplayName("should sort by second given name when last name and first given name are equal")
        void shouldSortBySecondGivenNameWhenFirstNamesMatch() {
            List<Name> input = List.of(
                parser.parse("John Zoe Smith"),
                parser.parse("John Anna Smith")
            );

            assertThat(sorter.sort(input))
                .extracting(Name::toString)
                .containsExactly("John Anna Smith", "John Zoe Smith");
        }

        @Test
        @DisplayName("should sort name with fewer given names before one with more, when all shared names are equal")
        void shouldSortFewerGivenNamesBeforeMoreWhenPrefixIsEqual() {
            // "John Smith" and "John Paul Smith" — same first name, same last name
            // "John Smith" has no second given name → empty string sorts before "Paul"
            List<Name> input = List.of(
                parser.parse("John Paul Smith"),
                parser.parse("John Smith")
            );

            assertThat(sorter.sort(input))
                .extracting(Name::toString)
                .containsExactly("John Smith", "John Paul Smith");
        }
    }

    // ─── Specification Test ───────────────────────────────────────────────────

    @Nested
    @DisplayName("assessment specification")
    class SpecificationCompliance {

        @Test
        @DisplayName("should produce the exact sorted output defined in the assessment requirements")
        void shouldMatchAssessmentSpecificationExactly() {
            /*
             * This test pins the sorter's output to the exact requirement from the spec.
             * If this test fails, a core requirement has been broken.
             *
             * Input (from assessment):
             *   Janet Parsons, Vaughn Lewis, Adonis Julius Archer, Shelby Nathan Yoder,
             *   Marin Alvarez, London Lindsey, Beau Tristan Bentley, Leo Gardner,
             *   Hunter Uriah Mathew Clarke, Mikayla Lopez, Frankie Conner Ritter
             */
            List<Name> input = List.of(
                parser.parse("Janet Parsons"),
                parser.parse("Vaughn Lewis"),
                parser.parse("Adonis Julius Archer"),
                parser.parse("Shelby Nathan Yoder"),
                parser.parse("Marin Alvarez"),
                parser.parse("London Lindsey"),
                parser.parse("Beau Tristan Bentley"),
                parser.parse("Leo Gardner"),
                parser.parse("Hunter Uriah Mathew Clarke"),
                parser.parse("Mikayla Lopez"),
                parser.parse("Frankie Conner Ritter")
            );

            assertThat(sorter.sort(input))
                .extracting(Name::toString)
                .containsExactly(
                    "Marin Alvarez",
                    "Adonis Julius Archer",
                    "Beau Tristan Bentley",
                    "Hunter Uriah Mathew Clarke",
                    "Leo Gardner",
                    "Vaughn Lewis",
                    "London Lindsey",
                    "Mikayla Lopez",
                    "Janet Parsons",
                    "Frankie Conner Ritter",
                    "Shelby Nathan Yoder"
                );
        }
    }
}
