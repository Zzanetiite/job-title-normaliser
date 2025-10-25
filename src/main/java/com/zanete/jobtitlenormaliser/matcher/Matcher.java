package com.zanete.jobtitlenormaliser.matcher;

import java.util.List;

/**
 * Defines a generic interface for calculating similarity scores between two lists of tokens.
 *
 * <p>Implementations may use different algorithms (e.g., cosine similarity, fuzzy matching) to
 * compare two tokenised job titles and return a score between 0.0 and 1.0.
 */
public interface Matcher {

  /**
   * Computes the cosine similarity between two lists of tokens.
   *
   * @param tokens1 the first list of tokens
   * @param tokens2 the second list of tokens
   * @return a double value between 0.0 and 1.0 representing the cosine similarity;
   * returns 0.0 if either list is empty
   */
  double calculateScore(List<String> tokens1, List<String> tokens2);
}
