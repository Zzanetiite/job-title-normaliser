package com.zanete.jobtitlenormaliser.matcher;

/**
 * Exception thrown when the sum of matcher weights in a {@link Matchers} object
 * does not equal 1.0.
 */
public class InvalidWeightsException extends Exception {

  public InvalidWeightsException(double currentWeight) {
    super("Matcher weights must add up to exactly 1.0. The current weight is " + currentWeight);
  }
}
