/*
ACR-84bf75dfab514b43b38038e955b3339d
ACR-28ec3ef2645341afbb7ed68aaf48802d
ACR-3dfd25b99c874317820d878d88d96290
ACR-bd200573f99d4d739ddd4db5f83c503b
ACR-6823e5297c0d4571b449291f39845289
ACR-ba3d17265cba456492d6d75c5357a15b
ACR-86c638afc9fe4aac8bdb83c8a12d3cdb
ACR-99ea2ed8729849f79f79d58f974872eb
ACR-f2571a4839bd4a34bf79b30621d4d261
ACR-01686fb1a33a4e189bf414e7debb18f0
ACR-5defcde315484613a00805e4dff82c76
ACR-b469abf426c6433bb7cd3616a38b84a2
ACR-60a92dcf742a4b949a9dfc4868adbe8a
ACR-07a37bde43a94e15bb3d5bfd1043fa27
ACR-adc09324c19046dbbc6850af3c50ebb3
ACR-64c5e24b806c4e51bdded19b38961b98
ACR-739e81d6b52947039704afd811a7d0ad
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
    //ACR-9ef08e5b4c924784add2a9fc37ddf5e7
  }

  @Override
  public void markForPublishing(InputFile inputFile) {
    //ACR-eb0a07cc0f274bd1b23c2116812fd7a2
  }

  @Override
  public void markAsUnchanged(InputFile inputFile) {
    //ACR-fefd048242574beba2905b7ec766ec68
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
    //ACR-94db1e671c1949bc9486e2e177a5491f
  }

  @Override
  public void addAnalysisData(String s, String s1, InputStream inputStream) {
    //ACR-680107a62d8f4072bf48d4fa6eb22dde
    //ACR-0f768a97f8c84ccfa059378d3237de4b
  }

  @Override
  public boolean isFeatureAvailable(String s) {
    return false;
  }
}
