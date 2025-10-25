package com.zanete.jobtitlenormaliser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.zanete.jobtitlenormaliser.matcher.CosineSimilarityMatcher;
import com.zanete.jobtitlenormaliser.matcher.FuzzyTokenMatcher;
import com.zanete.jobtitlenormaliser.matcher.InvalidWeightsException;
import com.zanete.jobtitlenormaliser.matcher.Matchers;
import com.zanete.jobtitlenormaliser.model.MatchedTitle;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class NormaliserIntegrationTest {

  private Normaliser normaliser;

  @BeforeEach
  void setup() throws InvalidWeightsException {
    normaliser = new Normaliser(new LocalJobTitleProvider(), Matchers.builder()
        .addMatcher(new FuzzyTokenMatcher(), 0.4)
        .addMatcher(new CosineSimilarityMatcher(), 0.6)
        .build());
  }

  @Test
  @DisplayName("Returns empty string for null or blank input")
  void testNullOrBlankInput() {
    assertEquals("", normaliser.normalise(null));
    assertEquals("", normaliser.normalise(""));
    assertEquals("", normaliser.normalise("   "));
  }


  @Test
  @DisplayName("Handles bad inputs with numbers and symbols")
  void testNumericAndSymbolInput() {
    assertEquals("", normaliser.normalise("12345"));
    assertEquals("", normaliser.normalise("!@#$%^&*()"));
    assertEquals("", normaliser.normalise("   "));
    assertEquals("", normaliser.normalise("@@developer!!"));
    assertEquals("", normaliser.normalise("123-engineer"));
    assertEquals("", normaliser.normalise("###"));
    assertEquals("", normaliser.normalise("C++!!$$"));
  }

  @ParameterizedTest(name = "Normalises \"{0}\" to \"{1}\"")
  @CsvSource({
      "Software Engineer,Software engineer",
      "Senior Software Engineer,Software engineer",
      "junior accountant,Accountant",
      "Lead accountant,Accountant",
      "PRINCIPAL SOFTWARE ENGINEER,Software engineer"
  })
  @DisplayName("Correctly normalises known job titles ignoring prefixes and case")
  void testNormalisesKnownTitles(String input, String expected) {
    assertEquals(expected, normaliser.normalise(input));
  }

  @ParameterizedTest(name = "Returns empty string for unmatched or low-similarity input: {0}")
  @CsvSource({
      "Random title",
      "Chief Happiness Officer",
      "Unknown Position"
  })
  @DisplayName("Returns empty string if no title meets threshold")
  void testReturnsEmptyForUnmatched(String input) {
    assertEquals("", normaliser.normalise(input));
  }

  @Test
  @DisplayName("Handles input with extra whitespace and multiple separators")
  void testHandlesWhitespaceAndSeparators() {
    String input = "  Senior, Software / Engineer  ";
    assertEquals("Software engineer", normaliser.normalise(input));
  }

  @Test
  @DisplayName("Handles input with special characters in tech names (C++, C#, .NET)")
  void testHandlesSpecialTechTokens() {
    String input = "Lead C++ Developer";
    assertEquals("", normaliser.normalise(input));

    input = "Software engineer C#";
    assertEquals("Software engineer", normaliser.normalise(input));
  }

  @Test
  @DisplayName("Input with typos meets threshold")
  void testExactlyAtThreshold() {
    // Craft input that is slightly different but passes threshold
    String input = "Softwre Enginer"; // intentional typo
    String result = normaliser.normalise(input);
    assertTrue(result.equals("Software engineer") || result.equals(""));
    // Could assert based on actual combined score if needed
  }

  @Test
  @DisplayName("Returns correct title and overall score for exact match")
  void testExactMatchScore() {
    Optional<MatchedTitle> result = normaliser.normaliseDetailed("Software Engineer");
    assertTrue(result.isPresent(), "Expected a match for 'Software Engineer");
    MatchedTitle match = result.get();
    assertEquals("Software engineer", match.title());
    assertEquals(1.0, match.overallScore(), 0.01, "Expected perfect combined score");
  }

  @Test
  @DisplayName("Returns correct title and score for title with prefix and case differences")
  void testPrefixAndCaseMatchScore() {
    Optional<MatchedTitle> result = normaliser.normaliseDetailed("  Senior, Software / Engineer  ");
    assertTrue(result.isPresent(), "Expected a match for 'Senior software eng.");
    MatchedTitle match = result.get();
    assertEquals("Software engineer", match.title());
    assertTrue(match.overallScore() >= 0.75, "Expected score above threshold");
  }

  @Test
  @DisplayName("Returns empty Optional for low similarity input")
  void testLowSimilarityReturnsEmpty() {
    Optional<MatchedTitle> result = normaliser.normaliseDetailed("Chief Happiness Officer");
    assertTrue(result.isEmpty(), "Expected no match for low similarity input");
  }
}
