package com.zanete.jobtitlenormaliser.matcher;

import static com.zanete.jobtitlenormaliser.Utils.filterValid;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.NoArgsConstructor;

/**
 * Utility class for computing the cosine similarity between two lists of tokens.
 * <p>
 * Cosine similarity is a measure of similarity between two non-zero vectors
 * of an inner product space that measures the cosine of the angle between them.
 * It is commonly used in text analysis and information retrieval to compare
 * documents or tokenised strings.
 * <p>
 * For more information, see:
 * <a href="https://en.wikipedia.org/wiki/Cosine_similarity">Cosine similarity - Wikipedia</a>
 */
@NoArgsConstructor
public class CosineSimilarityMatcher implements Matcher {

  /**
   * Computes the cosine similarity between two lists of tokens.
   *
   * <p>This method converts the token lists into term-frequency vectors,
   * calculates the dot product, and normalises by the vector magnitudes
   * to produce a similarity score between 0.0 and 1.0.</p>
   *
   * @param tokens1 the first list of tokens
   * @param tokens2 the second list of tokens
   * @return a double value between 0.0 and 1.0 representing the cosine similarity;
   * returns 0.0 if either list is empty
   */
  public double calculateScore(List<String> tokens1, List<String> tokens2) {
    tokens1 = filterValid(tokens1);
    tokens2 = filterValid(tokens2);

    if (tokens1.isEmpty() || tokens2.isEmpty()) {
      return 0.0;
    }

    Map<String, Integer> frequency1 = getTermFrequency(tokens1);
    Map<String, Integer> frequency2 = getTermFrequency(tokens2);

    Set<String> allTokens = new HashSet<>(frequency1.keySet());
    allTokens.addAll(frequency2.keySet());

    double dotProduct = 0.0;
    double magnitudeSquared1 = 0.0;
    double magnitudeSquared2 = 0.0;

    for (String token : allTokens) {
      int count1 = frequency1.getOrDefault(token, 0);
      int count2 = frequency2.getOrDefault(token, 0);

      dotProduct += count1 * count2;
      magnitudeSquared1 += count1 * count1;
      magnitudeSquared2 += count2 * count2;
    }

    if (magnitudeSquared1 == 0 || magnitudeSquared2 == 0) {
      return 0;
    }

    double magnitude = Math.sqrt(magnitudeSquared1) * Math.sqrt(magnitudeSquared2);
    return dotProduct / magnitude;
  }

  /**
   * Computes the term frequency map for a list of tokens.
   *
   * <p>The returned map contains each unique token as a key and the number
   * of times it appears in the list as the value.</p>
   *
   * @param tokens the list of tokens
   * @return a map from token to frequency count
   */
  private static Map<String, Integer> getTermFrequency(List<String> tokens) {
    Map<String, Integer> frequencyMap = new HashMap<>();
    for (String token : tokens) {
      frequencyMap.put(token, frequencyMap.getOrDefault(token, 0) + 1);
    }
    return frequencyMap;
  }


}
