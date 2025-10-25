package com.zanete.jobtitlenormaliser.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.zanete.jobtitlenormaliser.matcher.Matcher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MatcherWithWeightTest {

  @Mock
  Matcher matcher;

  @Test
  @DisplayName("Successfully creates a MatcherWithWeight with valid weight")
  void testValidWeightStoresValues() {
    MatcherWithWeight mw = new MatcherWithWeight(matcher, 0.75);
    assertEquals(matcher, mw.matcher());
    assertEquals(0.75, mw.weight());
  }

  @Test
  @DisplayName("Throws IllegalArgumentException for negative weight")
  void testNegativeWeightThrows() {
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        new MatcherWithWeight(matcher, -0.1)
    );
    assertTrue(exception.getMessage().contains("Weight must be between 0.0 and 1.0"));
  }

  @Test
  @DisplayName("Throws IllegalArgumentException for weight greater than 1.0")
  void testWeightAboveOneThrows() {
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        new MatcherWithWeight(matcher, 1.1)
    );
    assertTrue(exception.getMessage().contains("Weight must be between 0.0 and 1.0"));
  }

  @Test
  @DisplayName("Throws NullPointerException if matcher is null")
  void testNullMatcherThrows() {
    NullPointerException exception = assertThrows(NullPointerException.class, () ->
        new MatcherWithWeight(null, 0.5)
    );
    assertTrue(exception.getMessage().contains("matcher"));
  }

  @Test
  @DisplayName("Edge case: weight = 0.0 is allowed")
  void testWeightZeroAllowed() {
    MatcherWithWeight mw = new MatcherWithWeight(matcher, 0.0);
    assertEquals(0.0, mw.weight());
  }

  @Test
  @DisplayName("Edge case: weight = 1.0 is allowed")
  void testWeightOneAllowed() {
    MatcherWithWeight mw = new MatcherWithWeight(matcher, 1.0);
    assertEquals(1.0, mw.weight());
  }
}
