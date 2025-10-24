package com.zanete.jobtitlenormaliser;

import java.util.List;

public class Utils {

  public Utils() {}

  /**
   * Filters out null or blank tokens from a list.
   *
   * @param tokens the list of tokens, may be null
   * @return a new list containing only non-blank, non-null tokens
   */
  public static List<String> filterValid(List<String> tokens) {
    if (tokens == null) {
      return List.of();
    }
    return tokens.stream()
        .filter(token -> token != null && !token.isBlank())
        .toList();
  }
}
