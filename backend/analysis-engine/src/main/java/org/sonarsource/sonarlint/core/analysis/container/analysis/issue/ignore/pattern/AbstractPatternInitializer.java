/*
ACR-4bf9e839359e4b35998b1b8c66c51184
ACR-5fa8ad49ac0846498265338f9fb6d0b4
ACR-636de7ab26a54c389f03e47c75cc03ef
ACR-cb2d2ff42411414e8d34f62658d40c02
ACR-018c8a1cddc6448c81b41aacdcdaf30b
ACR-5b68ffb5dcaf4f658307a9e6ee4615d2
ACR-ff33805091e84741bfcb2410833116cb
ACR-ae16bd10c8ff47ae92e2fe130c35a39f
ACR-5b800ca5d9a54477b92395e7941bd43c
ACR-288f70723623459995396661baf30be0
ACR-1540ebb51801462d989803e4da815213
ACR-64a51db0c63047548ad24029e29495cc
ACR-51c31879030a4162a8eeba418ebf65d5
ACR-27457f491e164e7e8700066b63a7b3be
ACR-b1ce036a47c343efad8db9d811182032
ACR-ccf461a45d334e22a139af9774b25d57
ACR-58c920ed140e405386e3e75d824ebceb
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
    //ACR-097deccf056044218f43a94e573b9ff2
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
