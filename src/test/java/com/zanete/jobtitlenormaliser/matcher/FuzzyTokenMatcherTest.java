package com.zanete.jobtitlenormaliser.matcher;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.zanete.jobtitlenormaliser.Preprocessor;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;


class FuzzyTokenMatcherTest {
  private final Preprocessor preprocessor =
      new Preprocessor(List.of("senior", "junior", "lead", "principal"));

  private static List<String> tokens(String... items) {
    return List.of(items);
  }

  @Test
  @DisplayName("Returns perfect score (1.0) for identical token lists")
  void testIdenticalTokensReturnPerfectScore() {
    double score = FuzzyTokenMatcher.fuzzyScore(tokens("developer"), tokens("developer"));
    assertEquals(1.0, score, 0.0001);
  }

  @Test
  @DisplayName("Returns 0.0 when one or both lists are empty")
  void testEmptyListsReturnZero() {
    assertEquals(0.0, FuzzyTokenMatcher.fuzzyScore(tokens(), tokens()));
    assertEquals(0.0, FuzzyTokenMatcher.fuzzyScore(tokens("java"), tokens()));
    assertEquals(0.0, FuzzyTokenMatcher.fuzzyScore(tokens(), tokens("python")));
  }

  @Test
  @DisplayName("Handles null input lists defensively (treat as empty)")
  void testNullListsHandledSafely() {
    assertDoesNotThrow(() -> FuzzyTokenMatcher.fuzzyScore(null, null));
  }

  @ParameterizedTest(name = "Matching is case sensitive, comparing {0} vs {1}")
  @CsvSource({
      "Java, java",
      "PYTHON, python",
      "C++, c++"
  })
  void testMatchingIsCaseSensitive(String token1, String token2) {
    double score = FuzzyTokenMatcher.fuzzyScore(tokens(token1), tokens(token2));
    assertTrue(score < 1.0,
        "Expected score < 1.0 because cases differ. Tokens must be pre-processed to achieve accurate results");
  }

  @ParameterizedTest(name = "Verify partial match between {0} and {1} returns expected score {2}")
  @CsvSource({
      "Java engineer, Software engineer, 0.65",
      "C# engineer, Software engineer, 0.63",
      "Chief Accountant, Accountant, 0.88",
      "engineer, engineering, 0.94",
      "developer, development, 0.92",
      "account, accountant, 0.94",
      "C, C#, 0.85",
      "JavaScript, Java, 0.88",
      "12345, 123, 0.91",
      "dev_ops, devops, 0.96",
      "Supercalifragilisticexpialidocious, Supercalifragilisticexpialidoc, 0.97"
  })
  void testPartialMatchesProduceExpectedScores(String token1, String token2, double expected) {
    double score = FuzzyTokenMatcher.fuzzyScore(tokens(token1), tokens(token2));
    assertEquals(expected, score, 0.01,
        String.format("Expected score %.2f for tokens: %s vs %s", expected, token1, token2));
  }

  @Test
  @DisplayName("Verifies that fuzzyScore averages token similarities when the first list has more tokens than the second")
  void testFindsMatchWhenDifferentLengths() {
    double score = FuzzyTokenMatcher.fuzzyScore(tokens("software", "engineer"), tokens("engineer"));
    assertEquals(0.67, score, 0.01);
  }

  @Test
  @DisplayName("Verifies that fuzzyScore selects the best matching token for each input token and averages the results")
  void testAveragesBestMatches() {
    double score =
        FuzzyTokenMatcher.fuzzyScore(tokens("java", "developer"), tokens("java", "engineer"));
    assertEquals(0.82, score, 0.01);
  }

  @Test
  @DisplayName("FuzzyScore is unaffected by duplicates because preprocessor handles them")
  void testPreprocessedDuplicatesDoNotChangeScore() {
    List<String> preprocessedTokens = preprocessor.preprocess("java java developer");
    List<String> targetTokens = preprocessor.preprocess("java, developer");
    double score1 = FuzzyTokenMatcher.fuzzyScore(preprocessedTokens, targetTokens);
    assertEquals(1.0, score1);
  }

  @ParameterizedTest(name = "Verify can process special characters: {0}")
  @CsvSource({
      "c#",
      "c++",
      ".net"
  })
  void testSpecialCharacterTokensAreHandled(String token) {
    double score = FuzzyTokenMatcher.fuzzyScore(tokens(token), tokens(token));
    assertEquals(1.0, score);
  }

  @Test
  @DisplayName("Ignores blank tokens in input")
  void testBlankTokensAreHandled() {
    double score = FuzzyTokenMatcher.fuzzyScore(tokens(" ", "developer"), tokens("developer"));
    assertEquals(1.0, score);
  }

  static Stream<List<String>> longTokensProvider() {
    return Stream.of(
        tokens("a".repeat(1000)),
        tokens("developer".repeat(100))
    );
  }

  @ParameterizedTest
  @MethodSource("longTokensProvider")
  @DisplayName("Handles very long tokens without overflow or error")
  void testLongTokensDoNotBreakFunction(List<String> longTokenList) {
    assertDoesNotThrow(() -> FuzzyTokenMatcher.fuzzyScore(longTokenList, tokens("developer")));
  }
}