/*
ACR-5c537b17139a48278756589910f5d9c6
ACR-c7c6e5173c4b4575a4ea349b51d4ea40
ACR-60f85006387047e5bbc8c90165a6f47b
ACR-1a187769655d46cbb59d5409be7320fa
ACR-5a8dc85fc7e7491d8601a38fefd69c7e
ACR-cb3062de18b54b62bc855346328959ae
ACR-52b83fd8fa384a39ae9cc4a7f8f26255
ACR-53d191a62a834a30a158b364c35dd889
ACR-f53110adba344e189af2633f6aa73a2c
ACR-a71f44906ddc4c37b064d441735c4f1a
ACR-275383a67a6443d4b5d819e19fbd245e
ACR-5325791c45f14fbfbbb5f4e7325317aa
ACR-852ab775bfdd482f9d40debffe41673e
ACR-964b0bea08174af59e9c44c0ba03a391
ACR-b0086f5a4bac4250b303b6a9532b8837
ACR-655ebd79d9bd4da282592bfba24d4162
ACR-09f19e8879d0408ab61437c26612d043
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
