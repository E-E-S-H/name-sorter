# Name Sorter

A command-line application that sorts a list of names from a file by **last name first**, then by **given names**. Built with Java 17, following SOLID design principles.

---

## Requirements

- Java 17 or higher
- Maven 3.8+

---

## How to Build

```bash
mvn clean package
```

This produces a fat executable JAR at `target/name-sorter-1.0.0.jar`.

---

## How to Run

```bash
java -jar target/name-sorter-1.0.0.jar ./unsorted-names-list.txt
```

**Output:**
- Sorted names are printed to the **console**
- Sorted names are written (or overwritten) to **`sorted-names-list.txt`** in the current directory

---

## How to Run Tests

```bash
mvn test
```

---

## Example

**Input** (`unsorted-names-list.txt`):
```
Janet Parsons
Vaughn Lewis
Adonis Julius Archer
Shelby Nathan Yoder
Marin Alvarez
```

**Output** (console and `sorted-names-list.txt`):
```
Marin Alvarez
Adonis Julius Archer
Vaughn Lewis
Janet Parsons
Shelby Nathan Yoder
```

---

## Name Rules

- A name must have **at least 1 given name** and **exactly 1 last name**
- A name may have **at most 3 given names**
- Invalid or blank lines in the input file are **skipped with a warning**

---

## Project Structure

```
src/main/java/com/dyedurham/namesorter/
├── model/          → Name value object (domain)
├── exception/      → InvalidNameException
├── parsing/        → NameParser interface + DefaultNameParser
├── sorting/        → NameSorter interface + LastNameFirstSorter
├── io/             → NameReader, NameWriter, FileNameReader, FileNameWriter, ConsoleNameWriter
├── app/            → NameSorterApp (orchestrator)
└── Main.java       → CLI entry point
```

---

## Design Decisions

- **SOLID principles** — each class has one job; all high-level classes depend on interfaces, not implementations
- **Java Records** — `Name` is an immutable value object using Java 17's `record` feature
- **Fail fast** — invalid names are rejected at construction time with descriptive error messages
- **Resilient I/O** — invalid lines in the input file are skipped with a warning rather than crashing the program
- **Pure sorting function** — `LastNameFirstSorter.sort()` has no side effects, making it trivially testable

---

## CI/CD

This project uses **GitHub Actions** for continuous integration. On every push or pull request to `main`, the automated workflow:
1. Sets up JDK 17 (Temurin)
2. Runs `mvn -B clean verify` (compilation + all 55 tests)
3. Executes an end-to-end smoke test against the packaged JAR
4. Fails the build if any test fails
