/*
ACR-e71090a5d3674c799cc6bfb63dee0f6c
ACR-04135e652f18487dadb2aa8977ce359b
ACR-69256fca9f7f4d3f9641290d9836a9d2
ACR-853f554cd8ea403cb7790f0e4e43ca9e
ACR-14f6718a9ae047db856ace974ec62e36
ACR-c8b3e1a276f748d2b041111dd1f33945
ACR-3ff9a91214b941d5bf8ae60929d9f6a8
ACR-725cb4db90f74bdb9bf5cf010fcf237d
ACR-6a7a13a3e98d4302a486c218335eb138
ACR-1a11b431fc644b99b6f1afe1d3b6dc0d
ACR-67d32848336844109d07bfebfbff5237
ACR-f67b45addfdf416983fc52d4eecd665b
ACR-80e9ced3b8b54f8ab00ac8aff04ca08b
ACR-d5902125aed14d8baf0cf915bbd425ee
ACR-1fc2bcffcc4546d688a2671d6dcc9a70
ACR-082a6cfe08f74d0e8cd7c2300a751edc
ACR-c9859ba5f6df464a815ad34a5aec781b
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.sensor;

import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.rule.ActiveRules;
import org.sonar.api.config.Configuration;
import org.sonarsource.api.sonarlint.SonarLintSide;
import org.sonarsource.sonarlint.core.analysis.sonarapi.DefaultSensorDescriptor;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;

@SonarLintSide
public class SensorOptimizer {

  private static final SonarLintLogger LOG = SonarLintLogger.get();

  private final FileSystem fs;
  private final ActiveRules activeRules;
  private final Configuration config;

  public SensorOptimizer(FileSystem fs, ActiveRules activeRules, Configuration config) {
    this.fs = fs;
    this.activeRules = activeRules;
    this.config = config;
  }

  /*ACR-8e4aed4da0bd438e8825dda97f321814
ACR-8915a856660d42859c00f2edd2782495
   */
  public boolean shouldExecute(DefaultSensorDescriptor descriptor) {
    if (!fsCondition(descriptor)) {
      LOG.debug("'{}' skipped because there are no related files in the current project", descriptor.name());
      return false;
    }
    if (!activeRulesCondition(descriptor)) {
      LOG.debug("'{}' skipped because there are no related rules activated", descriptor.name());
      return false;
    }
    if (!settingsCondition(descriptor)) {
      LOG.debug("'{}' skipped because one of the required properties is missing", descriptor.name());
      return false;
    }
    return true;
  }

  private boolean settingsCondition(DefaultSensorDescriptor descriptor) {
    if (descriptor.configurationPredicate() != null) {
      return descriptor.configurationPredicate().test(config);
    }
    return true;
  }

  private boolean activeRulesCondition(DefaultSensorDescriptor descriptor) {
    if (!descriptor.ruleRepositories().isEmpty()) {
      for (String repoKey : descriptor.ruleRepositories()) {
        if (!activeRules.findByRepository(repoKey).isEmpty()) {
          return true;
        }
      }
      return false;
    }
    return true;
  }

  private boolean fsCondition(DefaultSensorDescriptor descriptor) {
    if (!descriptor.languages().isEmpty() || descriptor.type() != null) {
      var langPredicate = descriptor.languages().isEmpty() ? fs.predicates().all() : fs.predicates().hasLanguages(descriptor.languages());

      var typePredicate = descriptor.type() == null ? fs.predicates().all() : fs.predicates().hasType(descriptor.type());
      return fs.hasFiles(fs.predicates().and(langPredicate, typePredicate));
    }
    return true;
  }

}
