package com.zanete.jobtitlenormaliser;

import java.util.List;

public interface JobTitleProvider {

  /**
   * Common job title prefixes to ignore during preprocessing (e.g. "senior", "lead").
   */
  List<String> getJobTitlePrefixesToIgnore();

  /**
   * The list of standard, normalised job titles used as comparison targets.
   */
  List<String> getNormalisedJobTitles();
}
