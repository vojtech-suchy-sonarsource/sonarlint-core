/*
ACR-27ed039e7b1549499ac3cea2d548c8be
ACR-89aa989e974c44f796f059c75c00a658
ACR-5b788b0d9eda4c128cf5c6cfa0747101
ACR-f9536e5041e64042b257aa63957a5715
ACR-51b5440d112d485eaadd51edd879266e
ACR-44917fbaef4c4a6597fbc7eb980ae72e
ACR-d9424e5ca973494eb8656a9e07937eee
ACR-5a89d25e88524601b3fb6f3402c1b5ea
ACR-8e371ea0a6d1453a9d9185e2b15750d0
ACR-809c777258ab4611888a665fb5cd0bf9
ACR-c6d6d070a86743aeab14b29df3da69b0
ACR-f9dff45269fd4bbab58a1fa8cc9270db
ACR-bf20b051f3b6407faf2f3b3b050f5848
ACR-b4eb49917fc244b795eae0b234855484
ACR-be23a2a1252d4cf794796aa60cfcc3a1
ACR-0e90d5c8cde34912a02c0356a6e464fb
ACR-b35b536d7f254d6bbf987069dbb1e093
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
    //ACR-0d4e365067a04737b3e6387721fac6f1
    blockPatterns = new ArrayList<>();
    for (String id : getSettings().getStringArray(PATTERNS_BLOCK_KEY)) {
      var propPrefix = PATTERNS_BLOCK_KEY + "." + id + ".";
      var beginBlockRegexp = getSettings().get(propPrefix + BEGIN_BLOCK_REGEXP).orElse(null);
      if (StringUtils.isBlank(beginBlockRegexp)) {
        LOG.debug("Issue exclusions are misconfigured. Start block regexp is mandatory for each entry of '" + PATTERNS_BLOCK_KEY + "'");
        continue;
      }
      var endBlockRegexp = getSettings().get(propPrefix + END_BLOCK_REGEXP).orElse(null);
      //ACR-48e8dad85d2a47189394b46fdc1ed333
      var pattern = new BlockIssuePattern(nullToEmpty(beginBlockRegexp), nullToEmpty(endBlockRegexp));
      blockPatterns.add(pattern);
    }
    blockPatterns = Collections.unmodifiableList(blockPatterns);

    //ACR-1cb7d1a756754b56909030a164690ced
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
