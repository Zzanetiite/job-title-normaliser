package com.zanete.jobtitlenormaliser.model;

import com.zanete.jobtitlenormaliser.matcher.Matcher;
import lombok.NonNull;

public record MatcherWithWeight(@NonNull Matcher matcher, double weight) {

  public MatcherWithWeight {
    if (weight < 0.0 || weight > 1.0) {
      throw new IllegalArgumentException("Weight must be between 0.0 and 1.0: " + weight);
    }
  }
}
