package com.zanete.jobtitlenormaliser.matcher;

import static com.zanete.jobtitlenormaliser.Utils.filterValid;

import java.util.List;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;

/**
 * Utility class for computing a fuzzy similarity score between two lists of tokens
 * using the Jaro-Winkler distance.
 * For more information, see:
 * <a href="https://en.wikipedia.org/wiki/Jaro%E2%80%93Winkler_distance">Jaro-Winkler similarity - Wikipedia</a>
 */
public class FuzzyTokenMatcher implements Matcher {

  private static final JaroWinklerSimilarity jaroWinkler = new JaroWinklerSimilarity();

  /**
   * Computes a fuzzy similarity score between two lists of tokens.
   *
   * <p>For each token in the first list, the method finds the best-matching token
   * in the second list using Jaro-Winkler similarity. The overall score is the
   * average of these best matches, producing a value between 0.0 and 1.0.</p>
   *
   * @param tokens1 the first list of tokens
   * @param tokens2 the second list of tokens
   * @return a double value between 0.0 and 1.0 representing the fuzzy similarity;
   * returns 0.0 if either list is empty
   */
  public double calculateScore(List<String> tokens1, List<String> tokens2) {
    tokens1 = filterValid(tokens1);
    tokens2 = filterValid(tokens2);

    if (tokens1.isEmpty() || tokens2.isEmpty()) {
      return 0.0;
    }

    double score = 0.0;
    for (String token1 : tokens1) {
      double best = 0.0;
      for (String token2 : tokens2) {
        best = Math.max(best, jaroWinkler.apply(token1, token2));
      }
      score += best;
    }
    return score / tokens1.size();
  }
}