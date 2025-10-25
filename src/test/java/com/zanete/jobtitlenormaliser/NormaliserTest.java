package com.zanete.jobtitlenormaliser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.zanete.jobtitlenormaliser.matcher.InvalidWeightsException;
import com.zanete.jobtitlenormaliser.matcher.Matcher;
import com.zanete.jobtitlenormaliser.matcher.Matchers;
import com.zanete.jobtitlenormaliser.model.MatchedTitle;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NormaliserTest {

  private static final int TEST_JOB_TITLE_COUNT = 2;
  private static final String TEST_TITLE = "Software Engineer";
  private static final List<String> TEST_TOKENS_SE = List.of("software", "engineer");
  private static final List<String> TEST_TOKENS_ACCOUNTANT = List.of("accountant");
  private Normaliser normaliser;

  @Mock
  JobTitleProvider jobTitleProvider;
  @Mock
  Matcher matcher1;
  @Mock
  Matcher matcher2;

  @BeforeEach
  void setup() throws InvalidWeightsException {
    when(jobTitleProvider.getJobTitlePrefixesToIgnore()).thenReturn(
        List.of("senior", "junior", "lead", "principal")
    );
    when(jobTitleProvider.getNormalisedJobTitles()).thenReturn(
        List.of("Software Engineer", "Accountant")
    );

    normaliser = new Normaliser(jobTitleProvider, Matchers.builder()
        .addMatcher(matcher1, 0.25)
        .addMatcher(matcher2, 0.75)
        .build()
    );
  }

  @Test
  @DisplayName("normaliseDetailed calls calculateScore on each matcher")
  void testCalculateScoreCalled() {
    when(matcher1.calculateScore(anyList(), anyList())).thenReturn(0.8);
    when(matcher2.calculateScore(anyList(), anyList())).thenReturn(0.6);

    normaliser.normaliseDetailed(TEST_TITLE);

    verify(matcher1, times(TEST_JOB_TITLE_COUNT)).calculateScore(eq(TEST_TOKENS_SE), anyList());
    verify(matcher2, times(TEST_JOB_TITLE_COUNT)).calculateScore(eq(TEST_TOKENS_SE), anyList());
  }

  @Test
  @DisplayName("normalise returns empty string if all matches below threshold")
  void testNormaliseReturnsEmptyIfBelowThreshold() {
    when(matcher1.calculateScore(anyList(), anyList())).thenReturn(0.1);
    when(matcher2.calculateScore(anyList(), anyList())).thenReturn(0.2);

    String normalised = normaliser.normalise("Test title");

    assertEquals("", normalised);
  }

  @Test
  @DisplayName("normaliseDetailed returns highest-scoring title")
  void testNormaliseDetailedOptional() {
    var title = List.of("accountant");
    // Return low scores for first title
    when(matcher1.calculateScore(title, TEST_TOKENS_SE)).thenReturn(0.1);
    when(matcher2.calculateScore(title, TEST_TOKENS_SE)).thenReturn(0.1);
    // Return high scores for second title
    when(matcher1.calculateScore(title, TEST_TOKENS_ACCOUNTANT)).thenReturn(0.5);
    when(matcher2.calculateScore(title, TEST_TOKENS_ACCOUNTANT)).thenReturn(1.0);

    MatchedTitle normalised = normaliser.normaliseDetailed("accountant").get();

    // Expecting second title to be returned as best match
    assertEquals(0.875, normalised.overallScore());
    assertEquals("Accountant", normalised.title());
  }
}
