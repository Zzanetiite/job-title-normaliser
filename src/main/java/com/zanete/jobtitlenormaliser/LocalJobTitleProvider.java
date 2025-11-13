package com.zanete.jobtitlenormaliser;

import java.util.List;

/**
 * Provides a local, hard-coded set of job titles and prefixes for testing.
 */
public class LocalJobTitleProvider implements JobTitleProvider {
  @Override
  public List<String> getJobTitlePrefixesToIgnore() {
    return List.of("senior", "junior", "lead", "principal");
  }

  @Override
  public List<String> getNormalisedJobTitles() {
    return List.of("Software engineer", "Accountant");
  }
}
