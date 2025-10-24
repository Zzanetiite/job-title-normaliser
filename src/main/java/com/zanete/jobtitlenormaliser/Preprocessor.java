package com.zanete.jobtitlenormaliser;


import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * String preprocessor responsible for cleaning and tokenising job titles.
 *
 * <p>Pipeline (single stream with a boolean prefix check):
 * <ol>
 *   <li>Unicode normalise, lowercase and trim</li>
 *   <li>Split using common separators</li>
 *   <li>Remove accents and punctuation</li>
 *   <li>Filter out blank tokens and common prefixes via {@link #isCommonPrefix(String)}</li>
 * </ol>
 */
public class Preprocessor {

  // Splits text on whitespace and common punctuation while preserving
  // characters used in technology names such as C#, C++, and .NET.
  // The separators include: spaces, commas, slashes, semicolons, colons,
  // quotes, parentheses, brackets, braces, exclamation marks, question marks,
  // at signs, underscores, and dashes — but NOT '+', '#', or '.'.
  private static final String COMMON_TEXT_SEPARATORS_REGEX = "[\\s,\\/;:'\"()\\[\\]{}!?@_\\-]+";

  private final List<String> jobTitlePrefixes;

  public Preprocessor(List<String> jobTitlePrefixes) {
    this.jobTitlePrefixes = jobTitlePrefixes;
  }

  /**
   * Cleans and tokenises a given input string.
   *
   * @param input the raw input string
   * @return a list of cleaned, normalised tokens (words)
   */
  public List<String> preprocess(String input) {
    if (input == null || input.isBlank()) {
      return List.of();
    }

    String normalised = Normalizer.normalize(input, Normalizer.Form.NFD)
        .toLowerCase()
        .trim();

    return Arrays.stream(normalised.split(COMMON_TEXT_SEPARATORS_REGEX))
        .map(this::removeAccents)
        .map(this::removePunctuation)
        .filter(token -> !isCommonPrefix(token))
        .distinct()
        .collect(Collectors.toList());
  }

  /**
   * Removes accents (diacritics) from characters, e.g. "résumé" → "resume".
   */
  private String removeAccents(String input) {
    if (input == null) {
      return "";
    }
    return input.replaceAll("\\p{M}", "");
  }

  /**
   * Removes residual punctuation and non-alphanumeric characters from tokens,
   * except for special symbols commonly used in technology names.
   *
   * <p>This cleanup step runs after tokenisation and mainly handles edge cases
   * such as stray punctuation that remains at the start or end of tokens.
   * It intentionally preserves '+', '#', and '.' to keep tokens like
   * "C++", "C#", and ".NET" intact.</p>
   *
   * <p>Example:</p>
   * <pre>
   * "developer!" → "developer"
   * "c++"        → "c++"
   * ".net"       → ".net"
   * "c#"         → "c#"
   * </pre>
   *
   * @param input the token to clean
   * @return the cleaned token with valid characters preserved
   */
  private String removePunctuation(String input) {
    if (input == null) {
      return "";
    }
    // Keep letters, numbers, and symbols '+', '#', and '.'
    return input.replaceAll("[^a-z0-9+#.]", "");
  }

  /**
   * Returns true if the token is considered a prefix to filter out.
   * This also treats null/blank tokens as "common prefixes" so they are removed
   * by the single stream pipeline.
   *
   * @param token candidate token (already lowercased)
   * @return true if token is blank or present in the prefix list
   */
  private boolean isCommonPrefix(String token) {
    return token == null || token.isBlank() || jobTitlePrefixes.contains(token);
  }
}