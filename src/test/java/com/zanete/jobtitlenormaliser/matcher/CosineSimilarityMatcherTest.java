package com.zanete.jobtitlenormaliser.matcher;


import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.zanete.jobtitlenormaliser.Preprocessor;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

class CosineSimilarityMatcherTest {
  final CosineSimilarityMatcher matcher = new CosineSimilarityMatcher();
  private final Preprocessor preprocessor =
      new Preprocessor(List.of("senior", "junior", "lead", "principal"));

  private static List<String> tokens(String... items) {
    return List.of(items);
  }

  @Test
  @DisplayName("Returns perfect score (1.0) for identical token lists")
  void testIdenticalTokensReturnPerfectScore() {
    List<String> input = preprocessor.preprocess("developer java");
    double score = matcher.calculateScore(input, input);
    assertEquals(1.0, score, 0.0001);
  }

  @Test
  @DisplayName("Returns 0.0 when one or both lists are empty")
  void testEmptyListsReturnZero() {
    assertEquals(0.0, matcher.calculateScore(tokens(), tokens()));
    assertEquals(0.0, matcher.calculateScore(tokens("java"), tokens()));
    assertEquals(0.0, matcher.calculateScore(tokens(), tokens("python")));
  }

  @Test
  @DisplayName("Handles null input lists defensively")
  void testNullListsHandledSafely() {
    assertDoesNotThrow(() -> matcher.calculateScore(null, null));
  }

  @ParameterizedTest(name = "Case-sensitive comparison: {0} vs {1}. Non-equal produce 0.0")
  @CsvSource({
      "Java, java",
      "PYTHON, python",
      "C++, c++",
      "engineer, engineering",
      "developer, development",
      "account, accountant"
  })
  void testCaseSensitivity(String t1, String t2) {
    double score = matcher.calculateScore(tokens(t1), tokens(t2));
    assertEquals(0.0, score, "Expected 0.0 score for tokens that are not the same");
  }

  @ParameterizedTest(name = "Partial overlap between {0} and {1}. Non-equal produce 0.0")
  @CsvSource({
      "Java engineer, Software engineer, 0.50",
      "C# engineer, Software engineer, 0.50",
      "Chief Accountant, Accountant, 0.71",
      "java developer, java engineer, 0.50"
  })
  void testPartialMatchesProduceExpectedScores(String t1, String t2, double expected) {
    List<String> tokens1 = preprocessor.preprocess(t1);
    List<String> tokens2 = preprocessor.preprocess(t2);
    double score = matcher.calculateScore(tokens1, tokens2);
    assertEquals(expected, score, 0.01);
  }

  @Test
  @DisplayName("Handles lists of different lengths successfully")
  void testDifferentLengthsHandled() {
    List<String> tokens1 = preprocessor.preprocess("software engineer lead");
    List<String> tokens2 = preprocessor.preprocess("engineer");
    double score = matcher.calculateScore(tokens1, tokens2);
    assertEquals(0.71, score, 0.01);
  }

  @Test
  @DisplayName("Duplicate tokens do not affect score after preprocessing")
  void testDuplicatesHandled() {
    List<String> tokensWithDuplicates = preprocessor.preprocess("java java developer developer");
    List<String> targetTokens = preprocessor.preprocess("java developer");
    double score = matcher.calculateScore(tokensWithDuplicates, targetTokens);
    assertEquals(1.0, score, 0.0001, "Duplicates removed by preprocessing should not change score");
  }

  @ParameterizedTest(name = "Special character token {0} is processed successfully")
  @CsvSource({"C#", "C++", ".net"})
  void testSpecialCharacterTokensAreHandled(String token) {
    List<String> tokensList = preprocessor.preprocess(token);
    double score = matcher.calculateScore(tokensList, tokensList);
    assertEquals(1.0, score, 0.0001);
  }

  @Test
  @DisplayName("Ignores blank tokens in input")
  void testBlankTokensAreHandled() {
    double score =
        matcher.calculateScore(tokens(" ", "developer"), tokens("developer"));
    assertEquals(1.0, score);
  }

  static Stream<List<String>> longTokensProvider() {
    return Stream.of(
        List.of("a".repeat(1000)),
        List.of("developer".repeat(100))
    );
  }

  @ParameterizedTest
  @MethodSource("longTokensProvider")
  @DisplayName("Handles very long tokens without overflow or error")
  void testLongTokensDoNotBreakFunction(List<String> longTokenList) {
    assertDoesNotThrow(
        () -> matcher.calculateScore(longTokenList, tokens("developer")));
  }
}