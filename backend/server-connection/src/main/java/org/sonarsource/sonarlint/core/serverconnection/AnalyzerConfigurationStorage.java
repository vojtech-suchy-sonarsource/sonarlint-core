/*
ACR-01a9d9dd78b34a58adc71d16c9524cd2
ACR-0cff6930cae648db822ce39cce60c074
ACR-909d76d9b5be43b198ec6412c2f4c4ba
ACR-612b02569263484abd36e16a8cb5389f
ACR-3c84830b2ea14209833f60e622d1fd20
ACR-861a323bc1a943e795130af6237606c9
ACR-a0089787aaae4550a6cab471ad1d4c59
ACR-ad59bc6549ea43e78b86ac63142c1622
ACR-8dc13b2345114434a43465fb69f6316a
ACR-537a7aa6c5604fb29adefaabed4d2202
ACR-449d6ac4122545b0be2cd9d183de71e6
ACR-13173a0ae4fe4071ab1d77af008f5559
ACR-0d2123a04529456fba2949987c784286
ACR-3a26e677a29c4b5eba8ae79c7e1ed39d
ACR-b87a3a385435499d8f9a4a653d4dfceb
ACR-5a2bf040011e4fb09d736e1bd7ed2639
ACR-d73f5ed4b42243de8c591f3776e8afca
 */
package org.sonarsource.sonarlint.core.serverconnection;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import org.sonarsource.sonarlint.core.commons.IssueSeverity;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.serverapi.push.parsing.common.ImpactPayload;
import org.sonarsource.sonarlint.core.serverapi.rules.ServerActiveRule;
import org.sonarsource.sonarlint.core.serverconnection.proto.Sonarlint;
import org.sonarsource.sonarlint.core.serverconnection.storage.ProtobufFileUtil;
import org.sonarsource.sonarlint.core.serverconnection.storage.RWLock;
import org.sonarsource.sonarlint.core.serverconnection.storage.StorageException;

import static org.sonarsource.sonarlint.core.serverconnection.storage.ProtobufFileUtil.writeToFile;

public class AnalyzerConfigurationStorage {
  private static final SonarLintLogger LOG = SonarLintLogger.get();
  private final RWLock rwLock = new RWLock();
  private final Path storageFilePath;

  public AnalyzerConfigurationStorage(Path projectStorageRoot) {
    this.storageFilePath = projectStorageRoot.resolve("analyzer_config.pb");
  }

  public boolean isValid() {
    if (!Files.exists(storageFilePath)) {
      LOG.debug("Analyzer configuration storage doesn't exist: {}", storageFilePath);
      return false;
    }
    return tryRead().isPresent();
  }

  public void store(AnalyzerConfiguration analyzerConfiguration) {
    FileUtils.mkdirs(storageFilePath.getParent());
    var data = adapt(analyzerConfiguration);
    LOG.debug("Storing project analyzer configuration in {}", storageFilePath);
    rwLock.write(() -> writeToFile(data, storageFilePath));
    LOG.debug("Stored project analyzer configuration");
  }

  private Optional<AnalyzerConfiguration> tryRead() {
    try {
      return Optional.of(read());
    } catch (Exception e) {
      LOG.debug("Could not load analyzer configuration storage", e);
      return Optional.empty();
    }
  }

  public AnalyzerConfiguration read() {
    return adapt(rwLock.read(() -> readConfiguration(storageFilePath)));
  }

  public void update(UnaryOperator<AnalyzerConfiguration> updater) {
    FileUtils.mkdirs(storageFilePath.getParent());
    rwLock.write(() -> {
      Sonarlint.AnalyzerConfiguration config;
      try {
        config = readConfiguration(storageFilePath);
      } catch (StorageException e) {
        LOG.warn("Unable to read storage. Creating a new one.", e);
        config = Sonarlint.AnalyzerConfiguration.newBuilder().build();
      }
      writeToFile(adapt(updater.apply(adapt(config))), storageFilePath);
      LOG.debug("Storing project data in {}", storageFilePath);
    });
  }

  private static Sonarlint.AnalyzerConfiguration readConfiguration(Path projectFilePath) {
    return ProtobufFileUtil.readFile(projectFilePath, Sonarlint.AnalyzerConfiguration.parser());
  }

  private static AnalyzerConfiguration adapt(Sonarlint.AnalyzerConfiguration analyzerConfiguration) {
    return new AnalyzerConfiguration(
      new Settings(analyzerConfiguration.getSettingsMap()),
      analyzerConfiguration.getRuleSetsByLanguageKeyMap().entrySet().stream().collect(Collectors.toMap(
        Map.Entry::getKey,
        e -> adapt(e.getValue()))),
      analyzerConfiguration.getSchemaVersion());
  }

  private static Sonarlint.AnalyzerConfiguration adapt(AnalyzerConfiguration analyzerConfiguration) {
    return Sonarlint.AnalyzerConfiguration.newBuilder()
      .setSchemaVersion(analyzerConfiguration.getSchemaVersion())
      .putAllSettings(analyzerConfiguration.getSettings().getAll())
      .putAllRuleSetsByLanguageKey(analyzerConfiguration.getRuleSetByLanguageKey().entrySet().stream()
        .collect(Collectors.toMap(Map.Entry::getKey, e -> adapt(e.getValue()))))
      .build();
  }

  private static RuleSet adapt(Sonarlint.RuleSet ruleSet) {
    return new RuleSet(
      ruleSet.getRuleList().stream().map(AnalyzerConfigurationStorage::adapt).toList(),
      ruleSet.getLastModified());
  }

  private static ServerActiveRule adapt(Sonarlint.RuleSet.ActiveRule rule) {
    return new ServerActiveRule(
      rule.getRuleKey(),
      IssueSeverity.valueOf(rule.getSeverity()),
      rule.getParamsMap(),
      rule.getTemplateKey(),
      rule.getOverriddenImpactsList().stream()
        .map(impact -> new ImpactPayload(impact.getSoftwareQuality(), impact.getSeverity()))
        .toList());
  }

  private static Sonarlint.RuleSet adapt(RuleSet ruleSet) {
    return Sonarlint.RuleSet.newBuilder()
      .setLastModified(ruleSet.getLastModified())
      .addAllRule(ruleSet.getRules().stream().map(AnalyzerConfigurationStorage::adapt).toList()).build();
  }

  private static Sonarlint.RuleSet.ActiveRule adapt(ServerActiveRule rule) {
    return Sonarlint.RuleSet.ActiveRule.newBuilder()
      .setRuleKey(rule.getRuleKey())
      .setSeverity(rule.getSeverity().name())
      .setTemplateKey(rule.getTemplateKey())
      .putAllParams(rule.getParams())
      .addAllOverriddenImpacts(rule.getOverriddenImpacts().stream()
        .map(impact -> Sonarlint.RuleSet.ActiveRule.newBuilder().addOverriddenImpactsBuilder()
          .setSoftwareQuality(impact.getSoftwareQuality())
          .setSeverity(impact.getSeverity())
          .build())
        .toList())
      .build();
  }
}
