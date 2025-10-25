package com.zanete.jobtitlenormaliser;

import java.util.List;

/**
 * Provides a local, hard-coded set of job titles and prefixes for the {@link Normaliser}.
 *
 * <p>This implementation of {@link JobTitleProvider} is primarily useful for testing
 * or local development. It defines:
 * <ul>
 *   <li>Job title prefixes to ignore during normalisation (e.g., "senior", "junior").</li>
 *   <li>A small set of standard, normalised job titles (e.g., "Software engineer", "Accountant").</li>
 * </ul>
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
