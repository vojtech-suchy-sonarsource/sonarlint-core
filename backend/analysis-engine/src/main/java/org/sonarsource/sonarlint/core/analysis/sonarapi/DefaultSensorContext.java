/*
ACR-593be1f2bb9d4b2ebfbd5963ed76c93e
ACR-cbee148460fb49f9b635a84b52a78740
ACR-a7b03d25edc84265b1ee09127ab9226f
ACR-aec1478398944058932103b533dbbd33
ACR-4228be38aa1348a9855f3fb36c6b1ebb
ACR-e1bd556f621f4af1994b8ed16ba11e8c
ACR-02087c1d98394e5aa691aa506e0d8274
ACR-2b7535008c3f4e438896147ba5412602
ACR-c3a9b5e557a24eae82c997fda13515f1
ACR-2dcd8e62ab7a4587ab718b62d2ff4780
ACR-79f074ad267940e0a530ababada0a447
ACR-c514461a08f64399ad62b656ee5c81f5
ACR-5ba2d08fa903490bb136469eb8ee5504
ACR-9ac0776f96d545bfbdbb7c7a03ec5b9f
ACR-70fbff5bc5e54f5f93ad492e8d23e943
ACR-1ea0e58a72e1419dab34d64a6a14654d
ACR-f3b119c60d8342b09dcb6a3a8e39d56c
 */
package org.sonarsource.sonarlint.core.analysis.sonarapi;

import java.io.InputStream;
import java.io.Serializable;
import org.sonar.api.SonarRuntime;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.InputModule;
import org.sonar.api.batch.rule.ActiveRules;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.cache.ReadCache;
import org.sonar.api.batch.sensor.cache.WriteCache;
import org.sonar.api.batch.sensor.code.NewSignificantCode;
import org.sonar.api.batch.sensor.coverage.NewCoverage;
import org.sonar.api.batch.sensor.cpd.NewCpdTokens;
import org.sonar.api.batch.sensor.error.NewAnalysisError;
import org.sonar.api.batch.sensor.highlighting.NewHighlighting;
import org.sonar.api.batch.sensor.internal.SensorStorage;
import org.sonar.api.batch.sensor.issue.NewExternalIssue;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.measure.NewMeasure;
import org.sonar.api.batch.sensor.rule.NewAdHocRule;
import org.sonar.api.batch.sensor.symbol.NewSymbolTable;
import org.sonar.api.config.Configuration;
import org.sonar.api.config.Settings;
import org.sonar.api.scanner.fs.InputProject;
import org.sonar.api.utils.Version;
import org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem.SonarLintInputProject;
import org.sonarsource.sonarlint.core.analysis.sonarapi.noop.NoOpNewCoverage;
import org.sonarsource.sonarlint.core.analysis.sonarapi.noop.NoOpNewCpdTokens;
import org.sonarsource.sonarlint.core.analysis.sonarapi.noop.NoOpNewHighlighting;
import org.sonarsource.sonarlint.core.analysis.sonarapi.noop.NoOpNewMeasure;
import org.sonarsource.sonarlint.core.analysis.sonarapi.noop.NoOpNewSignificantCode;
import org.sonarsource.sonarlint.core.analysis.sonarapi.noop.NoOpNewSymbolTable;
import org.sonarsource.sonarlint.core.commons.progress.ProgressIndicator;

public class DefaultSensorContext implements SensorContext {

  private static final NoOpNewHighlighting NO_OP_NEW_HIGHLIGHTING = new NoOpNewHighlighting();
  private static final NoOpNewSymbolTable NO_OP_NEW_SYMBOL_TABLE = new NoOpNewSymbolTable();
  private static final NoOpNewCpdTokens NO_OP_NEW_CPD_TOKENS = new NoOpNewCpdTokens();
  private static final NoOpNewCoverage NO_OP_NEW_COVERAGE = new NoOpNewCoverage();
  private static final NoOpNewSignificantCode NO_OP_NEW_SIGNIFICANT_CODE = new NoOpNewSignificantCode();

  private final Settings settings;
  private final FileSystem fs;
  private final ActiveRules activeRules;
  private final SensorStorage sensorStorage;
  private final SonarLintInputProject project;
  private final SonarRuntime sqRuntime;
  private final ProgressIndicator progressIndicator;
  private final Configuration config;

  public DefaultSensorContext(SonarLintInputProject project, Settings settings, Configuration config, FileSystem fs, ActiveRules activeRules, SensorStorage sensorStorage,
    SonarRuntime sqRuntime, ProgressIndicator progressIndicator) {
    this.project = project;
    this.settings = settings;
    this.config = config;
    this.fs = fs;
    this.activeRules = activeRules;
    this.sensorStorage = sensorStorage;
    this.sqRuntime = sqRuntime;
    this.progressIndicator = progressIndicator;
  }

  @Override
  public Settings settings() {
    return settings;
  }

  @Override
  public Configuration config() {
    return config;
  }

  @Override
  public FileSystem fileSystem() {
    return fs;
  }

  @Override
  public ActiveRules activeRules() {
    return activeRules;
  }

  @Override
  public <G extends Serializable> NewMeasure<G> newMeasure() {
    return new NoOpNewMeasure<>();
  }

  @Override
  public NewIssue newIssue() {
    return new DefaultSonarLintIssue(project, fs.baseDir().toPath(), sensorStorage);
  }

  @Override
  public NewHighlighting newHighlighting() {
    return NO_OP_NEW_HIGHLIGHTING;
  }

  @Override
  public NewCoverage newCoverage() {
    return NO_OP_NEW_COVERAGE;
  }

  @Override
  public InputModule module() {
    return project;
  }

  @Override
  public InputProject project() {
    return project;
  }

  @Override
  public Version getSonarQubeVersion() {
    return sqRuntime.getApiVersion();
  }

  @Override
  public SonarRuntime runtime() {
    return sqRuntime;
  }

  @Override
  public NewSymbolTable newSymbolTable() {
    return NO_OP_NEW_SYMBOL_TABLE;
  }

  @Override
  public NewCpdTokens newCpdTokens() {
    return NO_OP_NEW_CPD_TOKENS;
  }

  @Override
  public NewAnalysisError newAnalysisError() {
    return new DefaultAnalysisError(sensorStorage);
  }

  @Override
  public boolean isCancelled() {
    return progressIndicator.isCanceled();
  }

  @Override
  public void addContextProperty(String key, String value) {
    //ACR-828cbda0c4f34b22a7da325f912881bc
  }

  @Override
  public void markForPublishing(InputFile inputFile) {
    //ACR-8b0da7a8ea1a4500ae57c81057981246
  }

  @Override
  public void markAsUnchanged(InputFile inputFile) {
    //ACR-34d1ffe39d054163b9d9917112bc00e2
  }

  @Override
  public NewExternalIssue newExternalIssue() {
    throw unsupported();
  }

  @Override
  public NewSignificantCode newSignificantCode() {
    return NO_OP_NEW_SIGNIFICANT_CODE;
  }

  @Override
  public NewAdHocRule newAdHocRule() {
    throw unsupported();
  }

  private static UnsupportedOperationException unsupported() {
    return new UnsupportedOperationException("Not supported in SonarLint");
  }

  @Override
  public boolean canSkipUnchangedFiles() {
    return false;
  }

  @Override
  public boolean isCacheEnabled() {
    return false;
  }

  @Override
  public ReadCache previousCache() {
    throw unsupported();
  }

  @Override
  public WriteCache nextCache() {
    throw unsupported();
  }

  @Override
  public void addTelemetryProperty(String key, String value) {
    //ACR-5df8f9b571aa4cb0874a3eb7842544d8
  }

  @Override
  public void addAnalysisData(String s, String s1, InputStream inputStream) {
    //ACR-f08c1957f1144b16a9918a5308d22274
    //ACR-94971baf352e4503a4d5604a4ab6e4a4
  }

  @Override
  public boolean isFeatureAvailable(String s) {
    return false;
  }
}
