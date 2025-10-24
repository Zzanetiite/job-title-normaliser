package com.zanete.jobtitlenormaliser;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

class PreprocessorTest {

  private final Preprocessor preprocessor =
      new Preprocessor(List.of("senior", "junior", "lead", "principal"));

  private static List<String> csvToList(String csv) {
    if (csv == null || csv.isBlank()) {
      return List.of();
    }
    return Stream.of(csv.split(","))
        .map(String::trim)
        .collect(Collectors.toList());
  }

  @Test
  @DisplayName("Returns empty list for null input")
  void testNullInput() {
    assertEquals(List.of(), preprocessor.preprocess(null));
  }

  @Test
  @DisplayName("Returns empty list for blank input")
  void testBlankInput() {
    assertEquals(List.of(), preprocessor.preprocess("   "));
  }

  @ParameterizedTest(name = "Normalises and splits '{0}' into expected tokens")
  @CsvSource({
      "Software Engineer, 'software,engineer'",
      "Senior Developer, 'developer'",
      "Lead Accountant, 'accountant'",
      "Principal Software Engineer, 'software,engineer'",
      "junior java developer, 'java,developer'"
  })
  void testPrefixesRemovedAndTokenised(String input, String expected) {
    List<String> actual = preprocessor.preprocess(input);
    assertEquals(csvToList(expected), actual);
  }

  @ParameterizedTest(name = "Removes accents from '{0}'")
  @CsvSource({
      "résumé, resume",
      "café, cafe",
      "naïve, naive"
  })
  void testAccentsRemoved(String input, String expected) {
    List<String> actual = preprocessor.preprocess(input);
    assertEquals(List.of(expected), actual);
  }

  @ParameterizedTest(name = "Removes punctuation but keeps '+', '#', '.' in '{0}'")
  @CsvSource({
      "developer!, developer",
      "c++, c++",
      ".net, .net",
      "c#, c#",
      "full-stack_senior-engineer@place, 'full,stack,engineer,place'"
  })
  void testPunctuationRemoved(String input, String expected) {
    List<String> actual = preprocessor.preprocess(input);
    assertEquals(csvToList(expected), actual);
  }

  @Test
  @DisplayName("Removes duplicate tokens")
  void testDuplicateTokensRemoved() {
    List<String> actual = preprocessor.preprocess("java java developer developer");
    assertEquals(List.of("java", "developer"), actual);
  }

  @Test
  @DisplayName("Handles mixed separators and whitespace")
  void testMixedSeparators() {
    String input = "Software,Engineer/Developer;Accountant:Analyst";
    List<String> expected = List.of("software", "engineer", "developer", "accountant", "analyst");
    assertEquals(expected, preprocessor.preprocess(input));
  }

  @Test
  @DisplayName("Removes blank tokens that appear after splitting")
  void testBlankTokensRemoved() {
    List<String> actual = preprocessor.preprocess("developer  ,  , java   ");
    assertEquals(List.of("developer", "java"), actual);
  }

  @ParameterizedTest(name = "Mixed-case input '{0}' is normalised to lowercase")
  @CsvSource({
      "'Java Developer', 'java,developer'",
      "'C++ Engineer', 'c++,engineer'",
      "'PYTHON', 'python'"
  })
  void testLowercaseNormalization(String input, String expected) {
    List<String> actual = preprocessor.preprocess(input);
    assertEquals(csvToList(expected), actual);
  }

  static Stream<String> longTokensProvider() {
    return Stream.of(
        "a".repeat(1000),
        "developer".repeat(100)
    );
  }

  @ParameterizedTest(name = "Handles very long tokens '{0}' without exception")
  @MethodSource("longTokensProvider")
  void testLongTokensHandled(String longToken) {
    assertDoesNotThrow(() -> preprocessor.preprocess(longToken));
  }
}
