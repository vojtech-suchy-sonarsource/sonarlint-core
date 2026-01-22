/*
ACR-8cbc48bbb4784573934aeef9f03db5e3
ACR-8f90037bddef418999e058a9dd40d4d3
ACR-eeb0ee8dd43b4ca89e806158af937481
ACR-fdecc7dec20142fa9cbac4ba3cb1767e
ACR-2d8ff579575440a6b543917f7659f752
ACR-0d27df755a01438cb14ce4b082887fe8
ACR-b9ea1b62e1a849649f6c99647b1ac1cd
ACR-6fc9e0478dbe4121bbc577077315a1b8
ACR-5bc6725b5e0646f8a71a0f96cf5cf1ab
ACR-2074eca184d74058ba23534c40d55e96
ACR-18246ec7ccae4c2a901791cf228e8410
ACR-2f19d2e23c5d4980b80dbf73338f518f
ACR-ad3a1b50f4334dec948cf9087ff97005
ACR-953cec99401e42bf885c04f5ea017b4e
ACR-bda2a718e22d4ea8a19ef96107eb9581
ACR-a52b6cf7fd584bb18048b5d4e0d36232
ACR-c31812eefc8640e4a522bf67d9e0edd5
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.issue.ignore.pattern;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.sonar.api.config.Configuration;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;

public class IssueExclusionPatternInitializer extends AbstractPatternInitializer {

  private static final SonarLintLogger LOG = SonarLintLogger.get();

  public static final String EXCLUSION_KEY_PREFIX = "sonar.issue.ignore";
  public static final String BLOCK_SUFFIX = ".block";
  public static final String PATTERNS_BLOCK_KEY = EXCLUSION_KEY_PREFIX + BLOCK_SUFFIX;
  public static final String BEGIN_BLOCK_REGEXP = "beginBlockRegexp";
  public static final String END_BLOCK_REGEXP = "endBlockRegexp";
  public static final String ALLFILE_SUFFIX = ".allfile";
  public static final String PATTERNS_ALLFILE_KEY = EXCLUSION_KEY_PREFIX + ALLFILE_SUFFIX;
  public static final String FILE_REGEXP = "fileRegexp";
  private List<BlockIssuePattern> blockPatterns;
  private List<String> allFilePatterns;

  public IssueExclusionPatternInitializer(Configuration config) {
    super(config);
    loadFileContentPatterns();
  }

  @Override
  protected String getMulticriteriaConfigurationKey() {
    return EXCLUSION_KEY_PREFIX + ".multicriteria";
  }

  @Override
  public boolean hasConfiguredPatterns() {
    return hasFileContentPattern() || hasMulticriteriaPatterns();
  }

  private void loadFileContentPatterns() {
    //ACR-3470cf19659b43b5a838b715345df71b
    blockPatterns = new ArrayList<>();
    for (String id : getSettings().getStringArray(PATTERNS_BLOCK_KEY)) {
      var propPrefix = PATTERNS_BLOCK_KEY + "." + id + ".";
      var beginBlockRegexp = getSettings().get(propPrefix + BEGIN_BLOCK_REGEXP).orElse(null);
      if (StringUtils.isBlank(beginBlockRegexp)) {
        LOG.debug("Issue exclusions are misconfigured. Start block regexp is mandatory for each entry of '" + PATTERNS_BLOCK_KEY + "'");
        continue;
      }
      var endBlockRegexp = getSettings().get(propPrefix + END_BLOCK_REGEXP).orElse(null);
      //ACR-2a08b15254db4bf99f4f74cf614530e4
      var pattern = new BlockIssuePattern(nullToEmpty(beginBlockRegexp), nullToEmpty(endBlockRegexp));
      blockPatterns.add(pattern);
    }
    blockPatterns = Collections.unmodifiableList(blockPatterns);

    //ACR-5484c8df94d74092803add8f75d40588
    allFilePatterns = new ArrayList<>();
    for (String id : getSettings().getStringArray(PATTERNS_ALLFILE_KEY)) {
      var propPrefix = PATTERNS_ALLFILE_KEY + "." + id + ".";
      var allFileRegexp = getSettings().get(propPrefix + FILE_REGEXP).orElse(null);
      if (StringUtils.isBlank(allFileRegexp)) {
        LOG.debug("Issue exclusions are misconfigured. Remove blank entries from '" + PATTERNS_ALLFILE_KEY + "'");
        continue;
      }
      allFilePatterns.add(nullToEmpty(allFileRegexp));
    }
    allFilePatterns = Collections.unmodifiableList(allFilePatterns);
  }

  private static String nullToEmpty(@Nullable String str) {
    if (str == null) {
      return "";
    }
    return str;
  }

  public List<BlockIssuePattern> getBlockPatterns() {
    return blockPatterns;
  }

  public List<String> getAllFilePatterns() {
    return allFilePatterns;
  }

  public boolean hasFileContentPattern() {
    return !(blockPatterns.isEmpty() && allFilePatterns.isEmpty());
  }

}
