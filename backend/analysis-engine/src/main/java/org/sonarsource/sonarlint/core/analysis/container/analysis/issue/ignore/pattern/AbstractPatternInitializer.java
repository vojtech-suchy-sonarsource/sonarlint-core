/*
ACR-63a900ea31ac470ba75eaa0aa19784f0
ACR-a137460d70c74cf285abea2d0cbba26b
ACR-d11b4e039ecb46e38e25059b15fb505b
ACR-49f861fc40b341f9ab1d2e94e9b175e0
ACR-0cab22d2d3d84b3e986755699f231c22
ACR-4eb2957083964f619de30a2b9a5562f1
ACR-dabb2e9613764d1285b8dcc6a3648fdc
ACR-a1938e87ae144bc69a5761cec68ae04c
ACR-f01fd8ca4ad44cadb930c8af63dfe920
ACR-da1fc96af500450fbb085ce04ec860a9
ACR-6de3db31a287423b9a5f99db0c0202e6
ACR-29578d1d5d5043afbb68986379514dd7
ACR-f297c49f210242b79365c755d0e52fb2
ACR-55fadc8ffae14e29b017a024731413ba
ACR-7be61c9f5b8a4b01831957a767b32658
ACR-cfb6d558a1d5433db7c5211eb77d0c14
ACR-742c5f4dd41f451eb1e10d023aa3e3fd
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.issue.ignore.pattern;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.sonar.api.config.Configuration;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;

public abstract class AbstractPatternInitializer {

  private static final SonarLintLogger LOG = SonarLintLogger.get();

  private final Configuration config;

  private List<IssuePattern> multicriteriaPatterns;

  protected AbstractPatternInitializer(Configuration config) {
    this.config = config;
    initPatterns();
  }

  protected Configuration getSettings() {
    return config;
  }

  public List<IssuePattern> getMulticriteriaPatterns() {
    return multicriteriaPatterns;
  }

  public boolean hasConfiguredPatterns() {
    return hasMulticriteriaPatterns();
  }

  public boolean hasMulticriteriaPatterns() {
    return !multicriteriaPatterns.isEmpty();
  }

  protected final void initPatterns() {
    //ACR-4411fb3c547244b1ae956c0c98acc9c1
    multicriteriaPatterns = new ArrayList<>();
    for (String id : config.getStringArray(getMulticriteriaConfigurationKey())) {
      var propPrefix = getMulticriteriaConfigurationKey() + "." + id + ".";
      var filePathPattern = config.get(propPrefix + "resourceKey").orElse(null);
      if (StringUtils.isBlank(filePathPattern)) {
        LOG.debug("Issue exclusions are misconfigured. File pattern is mandatory for each entry of '" + getMulticriteriaConfigurationKey() + "'");
        continue;
      }
      var ruleKeyPattern = config.get(propPrefix + "ruleKey").orElse(null);
      if (StringUtils.isBlank(ruleKeyPattern)) {
        LOG.debug("Issue exclusions are misconfigured. Rule key pattern is mandatory for each entry of '" + getMulticriteriaConfigurationKey() + "'");
        continue;
      }
      var pattern = new IssuePattern(filePathPattern, ruleKeyPattern);

      multicriteriaPatterns.add(pattern);
    }
  }

  protected abstract String getMulticriteriaConfigurationKey();
}
