package com.zanete.jobtitlenormaliser.matcher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MatchersTest {

  @Mock
  Matcher matcher1;
  @Mock
  Matcher matcher2;

  @Test
  @DisplayName("Successfully builds a Matchers instance with correct weights")
  void testBuildWithValidWeights() throws InvalidWeightsException {
    Matchers matchers = Matchers.builder()
        .addMatcher(matcher1, 0.4)
        .addMatcher(matcher2, 0.6)
        .build();

    assertEquals(2, matchers.getMatchers().size());
    assertEquals(matcher1, matchers.getMatchers().get(0).matcher());
    assertEquals(0.4, matchers.getMatchers().get(0).weight());
    assertEquals(matcher2, matchers.getMatchers().get(1).matcher());
    assertEquals(0.6, matchers.getMatchers().get(1).weight());
  }

  @Test
  @DisplayName("Throws InvalidWeightsException if weights do not sum to 1.0")
  void testBuildWithInvalidWeightsThrows() {
    InvalidWeightsException exception = assertThrows(InvalidWeightsException.class, () -> {
      Matchers.builder()
          .addMatcher(matcher1, 0.3)
          .addMatcher(matcher2, 0.6)
          .build();
    });

    assertTrue(exception.getMessage().contains("0.9"));
  }

  @Test
  @DisplayName("Throws InvalidWeightsException when no matchers are added")
  void testBuildWithNoMatchersThrows() {
    InvalidWeightsException exception = assertThrows(InvalidWeightsException.class, () -> {
      Matchers.builder().build();
    });
    assertTrue(exception.getMessage().contains("0.0"));
  }

  @Test
  @DisplayName("Allows a single matcher with weight 1.0")
  void testSingleMatcherWithFullWeight() throws InvalidWeightsException {
    Matchers matchers = Matchers.builder()
        .addMatcher(matcher1, 1.0)
        .build();

    assertEquals(1, matchers.getMatchers().size());
    assertEquals(matcher1, matchers.getMatchers().get(0).matcher());
    assertEquals(1.0, matchers.getMatchers().get(0).weight());
  }
}
