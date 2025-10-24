package com.zanete.jobtitlenormaliser.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MatchedTitle {
  private String title;
  private double overallScore;
}