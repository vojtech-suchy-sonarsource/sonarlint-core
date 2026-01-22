/*
ACR-e8266f63d4884f18bc6fac994994c1c1
ACR-13c0791cbaec4fb99c157437e36a869b
ACR-d0113536d5e5430d95742813a5408ce3
ACR-75391999c9dc466f9362e95775eaf9d4
ACR-5e389da8abb54577840dd718e7247af5
ACR-99a6114d003a4074bf46a7bbc3e5bc5c
ACR-47f18d1f495a4998bdbc638631f556e7
ACR-b8eb8eb2605b460385c5ba7ee66d9c67
ACR-a7d412b1ee1e4842b37d04553301026b
ACR-01b9cd1e5de040f1a68c6900bfc94f64
ACR-ba467eea15874591aa2de2cc5ad70b1b
ACR-62b90571f7684352bdd7114d888dc61e
ACR-98dff6da8b6842e48a120d542567c06d
ACR-86928ee5dcde45f097df197e0200ba7f
ACR-5470331f54f2477d815ecdf404080c52
ACR-fdbe9b718f22469faa7ae9899dc3e169
ACR-eecc993e457f425397397bd8f9cb9ca0
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

  /*ACR-d3c8f46f71794dcdaf352b4b2acab2eb
ACR-05155493ebf246b7bda88a52ecf29b90
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
