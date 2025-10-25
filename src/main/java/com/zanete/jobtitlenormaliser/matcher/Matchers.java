package com.zanete.jobtitlenormaliser.matcher;

import com.zanete.jobtitlenormaliser.model.MatcherWithWeight;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

/**
 * Encapsulates a collection of matchers, each with an associated weight, used to compute
 * a combined similarity score between job titles in a Normaliser.
 *
 * <p>This class supports building a weighted set of matchers through the {@link Builder} pattern.
 * The sum of all matcher weights must equal 1.0, otherwise {@link InvalidWeightsException} is thrown.
 */
@Getter
public class Matchers {
  private final List<MatcherWithWeight> matchers;

  private Matchers(List<MatcherWithWeight> matchersWithWeight) {
    this.matchers = matchersWithWeight;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private final List<MatcherWithWeight> matchers = new ArrayList<>();

    /**
     * Adds a matcher with a specified weight to the builder.
     *
     * @param matcher the matcher implementation to add
     * @param weight the weight of this matcher in the overall score calculation (0.0–1.0)
     * @return the builder instance for method chaining
     */
    public Builder addMatcher(Matcher matcher, double weight) {
      matchers.add(new MatcherWithWeight(matcher, weight));
      return this;
    }

    /**
     * Constructs the {@link Matchers} instance from the added matchers.
     *
     * @return a {@link Matchers} object containing the configured weighted matchers
     * @throws InvalidWeightsException if the sum of all matcher weights does not equal 1.0
     */
    public Matchers build() throws InvalidWeightsException {
      validateTotalWeight();
      return new Matchers(matchers);
    }

    private void validateTotalWeight() throws InvalidWeightsException {
      double totalWeight = matchers.stream().mapToDouble(MatcherWithWeight::weight).sum();
      if (totalWeight != 1.0) {
        throw new InvalidWeightsException(totalWeight);
      }
    }
  }
}
