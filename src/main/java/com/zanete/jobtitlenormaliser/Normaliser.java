package com.zanete.jobtitlenormaliser;


import com.zanete.jobtitlenormaliser.matcher.CosineSimilarityMatcher;
import com.zanete.jobtitlenormaliser.matcher.FuzzyTokenMatcher;
import com.zanete.jobtitlenormaliser.model.MatchedTitle;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Responsible for normalising job titles by comparing input text
 * against a set of known standard titles using multiple similarity measures.
 *
 * <p>Each title is preprocessed and compared using:
 * <ul>
 *   <li><b>Cosine Similarity</b> – measures overlap in token frequency (to find word overlap).</li>
 *   <li><b>Fuzzy Matching (Jaro–Winkler)</b> – measures string closeness between individual tokens (to account for typos).</li>
 * </ul>
 * <p>
 * The final similarity score combines both approaches with configurable weighting.
 */
public class Normaliser {
  /**
   * Common job title prefixes to ignore during preprocessing (e.g. "senior", "lead").
   */
  protected static final List<String> JOB_TITLE_PREFIXES =
      List.of("senior", "junior", "lead", "principal");

  /**
   * The list of standard, normalised job titles used as comparison targets.
   */
  protected static final List<String> JOB_TITLES_NORMALISED =
      List.of("Software engineer", "Accountant");

  /**
   * Minimum overall similarity score required for a title to be considered a valid match.
   */
  protected static final double TITLE_MATCH_SCORE_THRESHOLD = 0.75;

  /**
   * Weight assigned to cosine similarity (token overlap measure).
   */
  protected static final double COSINE_WEIGHT = 0.6;

  /**
   * Weight assigned to fuzzy matching (string similarity measure).
   */
  protected static final double FUZZY_WEIGHT = 0.4;

  private final Preprocessor preprocessor;

  /**
   * Constructs a normaliser with a preconfigured list of job title prefixes.
   */
  public Normaliser() {
    preprocessor = new Preprocessor(JOB_TITLE_PREFIXES);
  }

  /**
   * Attempts to normalise a given input title to one of the known standard titles.
   *
   * @param input raw job title text (e.g. "Senior Software Eng.")
   * @return the best-matching normalised title, or an empty string if none meet the threshold
   */
  public String normalise(String input) {
    return normaliseDetailed(input).map(MatchedTitle::getTitle).orElse("");
  }

  public Optional<MatchedTitle> normaliseDetailed(String input) {
    List<String> inputTokens = preprocessor.preprocess(input);
    return JOB_TITLES_NORMALISED.stream().map(title -> {
          var titleTokens = preprocessor.preprocess(title);
          double cosineScore = CosineSimilarityMatcher.cosineScore(inputTokens, titleTokens);
          double fuzzyScore = FuzzyTokenMatcher.fuzzyScore(inputTokens, titleTokens);
          double overallScore = computeOverallScore(cosineScore, fuzzyScore);
          return new MatchedTitle(title, overallScore);
        }).filter(match -> match.getOverallScore() >= TITLE_MATCH_SCORE_THRESHOLD)
        .max(Comparator.comparingDouble(MatchedTitle::getOverallScore));
  }

  /**
   * Combines cosine similarity and fuzzy matching scores into a single weighted score.
   *
   * <p>Formula: <code>(cosineScore * COSINE_WEIGHT) + (fuzzyScore * FUZZY_WEIGHT)</code></p>
   * <p>The weights reflect the relative importance of token overlap versus character-level
   * similarity and sum to 1.0 for normalised scoring.</p>
   *
   * @param cosineScore the cosine similarity score (token overlap)
   * @param fuzzyScore  the fuzzy matching score (string closeness)
   * @return a combined similarity score between 0.0 and 1.0
   */
  private double computeOverallScore(double cosineScore, double fuzzyScore) {
    return (cosineScore * COSINE_WEIGHT) + (fuzzyScore * FUZZY_WEIGHT);
  }
}