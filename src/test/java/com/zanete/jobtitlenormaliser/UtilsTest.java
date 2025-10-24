package com.zanete.jobtitlenormaliser;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UtilsTest {

  @Test
  @DisplayName("Returns empty list for null input")
  void testNullInput() {
    List<String> result = Utils.filterValid(null);
    assertNotNull(result, "Result should not be null");
    assertTrue(result.isEmpty(), "Result should be empty for null input");
  }

  @Test
  @DisplayName("Returns empty list for empty input")
  void testEmptyList() {
    List<String> result = Utils.filterValid(List.of());
    assertNotNull(result);
    assertTrue(result.isEmpty(), "Result should be empty for empty input list");
  }

  @Test
  @DisplayName("Keeps valid tokens only")
  void testAllValidTokens() {
    List<String> input = List.of("java", "python", "c++");
    List<String> result = Utils.filterValid(input);
    assertEquals(input, result, "All valid tokens should be retained");
  }

  @Test
  @DisplayName("Filters out null and blank strings")
  void testFilterBlanksAndNulls() {
    ArrayList<String> input = new ArrayList<>();
    input.add(null);
    input.add("java");
    input.add(" ");
    List<String> expected = List.of("java");

    List<String> result = Utils.filterValid(input);
    assertEquals(expected, result, "Should remove nulls and blank strings");
  }
}
