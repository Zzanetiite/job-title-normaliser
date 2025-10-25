package com.zanete.jobtitlenormaliser;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class PreprocessorTest {

  private final Preprocessor preprocessor =
      new Preprocessor(List.of("senior", "junior", "lead", "principal"));

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

  @ParameterizedTest(name = "Normalises and splits \"{0}\" into expected tokens {1}")
  @MethodSource("prefixRemovalProvider")
  void testPrefixesRemovedAndTokenised(String input, List<String> expected) {
    assertEquals(expected, preprocessor.preprocess(input));
  }

  private static Stream<Arguments> prefixRemovalProvider() {
    return Stream.of(
        Arguments.of("Software Engineer", List.of("software", "engineer")),
        Arguments.of("Senior Developer", List.of("developer")),
        Arguments.of("Lead Accountant", List.of("accountant")),
        Arguments.of("Principal Software Engineer", List.of("software", "engineer")),
        Arguments.of("junior java developer", List.of("java", "developer"))
    );
  }

  @ParameterizedTest(name = "Removes accents from \"{0}\" → {1}")
  @MethodSource("accentRemovalProvider")
  void testAccentsRemoved(String input, List<String> expected) {
    assertEquals(expected, preprocessor.preprocess(input));
  }

  private static Stream<Arguments> accentRemovalProvider() {
    return Stream.of(
        Arguments.of("résumé", List.of("resume")),
        Arguments.of("café", List.of("cafe")),
        Arguments.of("naïve", List.of("naive"))
    );
  }

  @ParameterizedTest(name = "Removes punctuation but keeps '+', '#', '.' in \"{0}\" → {1}")
  @MethodSource("punctuationProvider")
  void testPunctuationRemoved(String input, List<String> expected) {
    assertEquals(expected, preprocessor.preprocess(input));
  }

  private static Stream<Arguments> punctuationProvider() {
    return Stream.of(
        Arguments.of("developer!", List.of("developer")),
        Arguments.of("c++", List.of("c++")),
        Arguments.of(".net", List.of(".net")),
        Arguments.of("c#", List.of("c#")),
        Arguments.of("full-stack_senior-engineer@place",
            List.of("full", "stack", "engineer", "place"))
    );
  }

  @Test
  @DisplayName("Removes duplicate tokens")
  void testDuplicateTokensRemoved() {
    assertEquals(List.of("java", "developer"),
        preprocessor.preprocess("java java developer developer"));
  }

  @Test
  @DisplayName("Handles mixed separators and whitespace")
  void testMixedSeparators() {
    String input = "Software,Engineer/Developer;Accountant:Analyst";
    List<String> expected = List.of("software", "engineer", "developer", "accountant", "analyst");
    assertEquals(expected, preprocessor.preprocess(input));
  }

  @Test
  @DisplayName("Removes blank tokens after splitting")
  void testBlankTokensRemoved() {
    List<String> actual = preprocessor.preprocess("developer  ,  , java   ");
    assertEquals(List.of("developer", "java"), actual);
  }

  @ParameterizedTest(name = "Normalises mixed-case {0} → {1}")
  @MethodSource("lowercaseProvider")
  void testLowercaseNormalization(String input, List<String> expected) {
    assertEquals(expected, preprocessor.preprocess(input));
  }

  private static Stream<Arguments> lowercaseProvider() {
    return Stream.of(
        Arguments.of("Java Developer", List.of("java", "developer")),
        Arguments.of("C++ Engineer", List.of("c++", "engineer")),
        Arguments.of("PYTHON", List.of("python"))
    );
  }

  @ParameterizedTest(name = "Handles very long token input {0} safely")
  @MethodSource("longTokensProvider")
  void testLongTokensHandled(String longToken) {
    assertDoesNotThrow(() -> preprocessor.preprocess(longToken));
  }

  private static Stream<String> longTokensProvider() {
    return Stream.of("a".repeat(1000), "developer".repeat(100));
  }
}
