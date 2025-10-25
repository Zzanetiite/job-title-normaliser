package com.zanete.jobtitlenormaliser;


import com.zanete.jobtitlenormaliser.matcher.CosineSimilarityMatcher;
import com.zanete.jobtitlenormaliser.matcher.FuzzyTokenMatcher;
import com.zanete.jobtitlenormaliser.matcher.InvalidWeightsException;
import com.zanete.jobtitlenormaliser.matcher.Matchers;
import com.zanete.jobtitlenormaliser.model.MatchedTitle;
import com.zanete.jobtitlenormaliser.model.MatcherWithWeight;
import com.zanete.jobtitlenormaliser.model.Title;
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
   * Minimum overall similarity score required for a title to be considered a valid match.
   */
  protected static final double TITLE_MATCH_SCORE_THRESHOLD = 0.75;

  private final Preprocessor preprocessor;
  private final Matchers matchers;
  private final List<Title> preprocessedJobTitles;

  /**
   * Constructs a normaliser with a preconfigured list of job title prefixes.
   */
  public Normaliser(JobTitleProvider jobTitleProvider, Matchers matchers) {
    preprocessor = new Preprocessor(jobTitleProvider.getJobTitlePrefixesToIgnore());
    this.matchers = matchers;
    this.preprocessedJobTitles = jobTitleProvider.getNormalisedJobTitles().stream()
        .map(title -> new Title(title, preprocessor.preprocess(title)))
        .toList();
  }

  /**
   * Attempts to normalise a given input title to one of the known standard titles.
   *
   * @param input raw job title text (e.g. "Senior Software Eng.")
   * @return the best-matching normalised title, or an empty string if none meet the threshold
   */
  public String normalise(String input) {
    return normaliseDetailed(input).map(MatchedTitle::title).orElse("");
  }

  public Optional<MatchedTitle> normaliseDetailed(String input) {
    List<String> inputTokens = preprocessor.preprocess(input);
    return preprocessedJobTitles.stream().map(title -> {
          double overallScore = matchers.getMatchers().stream()
              .mapToDouble(matcher -> calculateMatcherScore(matcher, inputTokens, title.tokens())).sum();
          return new MatchedTitle(title.value(), overallScore);
        }).filter(match -> match.overallScore() >= TITLE_MATCH_SCORE_THRESHOLD)
        .max(Comparator.comparingDouble(MatchedTitle::overallScore));
  }

  private double calculateMatcherScore(MatcherWithWeight matcherWithWeight,
                                       List<String> inputTokens, List<String> titleTokens) {
    var score = matcherWithWeight.matcher().calculateScore(inputTokens, titleTokens);
    var weight = matcherWithWeight.weight();
    return score * weight;
  }

  public static void main(String[] args) throws InvalidWeightsException {
    Normaliser normaliser = new Normaliser(new LocalJobTitleProvider(), Matchers.builder()
        .addMatcher(new FuzzyTokenMatcher(), 0.4)
        .addMatcher(new CosineSimilarityMatcher(), 0.6)
        .build());
    String inputTitle = "Java Engineer";
    String normalisedTitle = normaliser.normalise(inputTitle);
    System.out.printf("Normalised title %s to %s: %n", inputTitle, normalisedTitle);
  }
}