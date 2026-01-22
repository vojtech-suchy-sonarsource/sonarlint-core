/*
ACR-970e98a6f4e042bebc46fc1e28786298
ACR-c5db039c41704673abd8c17911358432
ACR-f4c80a55fbff48edbeef26b3330586f7
ACR-ab695b14d51146b48c711a50ffe587c0
ACR-d7b211fc62244ea79240bf4e21dd18fb
ACR-83a4921c411f461ab2ccdd88d6caefdd
ACR-127e0ed9228249d6ac373332b6b53d7a
ACR-5c30bfb4168946e1954bc0c8bce3f04b
ACR-f101ae41303e4aadbf43e530efd77e58
ACR-1837734d36914e7383be0c7637c75e59
ACR-411af0df12614d2e8a2acb5a6ad8f557
ACR-49ac0ff2135f4d7eb1fa52e0b6227af7
ACR-d1f5add78bc1470092165be904859033
ACR-8ef827cb663949d0a48c8b0d67ba6cce
ACR-4458ed40f63f4036b2be413e801cc96f
ACR-b7f552c051f54d70bc68d37e4bb1603b
ACR-deb4db478c54436ca0190956699f619c
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis;

import org.sonar.api.batch.rule.CheckFactory;
import org.sonar.api.resources.Languages;
import org.sonar.api.scan.filesystem.PathResolver;
import org.sonarsource.sonarlint.core.analysis.container.ContainerLifespan;
import org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem.FileIndexer;
import org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem.FileMetadata;
import org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem.InputFileBuilder;
import org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem.InputFileIndex;
import org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem.LanguageDetection;
import org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem.SonarLintFileSystem;
import org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem.SonarLintInputProject;
import org.sonarsource.sonarlint.core.analysis.container.analysis.issue.IssueFilters;
import org.sonarsource.sonarlint.core.analysis.container.analysis.issue.ignore.EnforceIssuesFilter;
import org.sonarsource.sonarlint.core.analysis.container.analysis.issue.ignore.IgnoreIssuesFilter;
import org.sonarsource.sonarlint.core.analysis.container.analysis.issue.ignore.SonarLintNoSonarFilter;
import org.sonarsource.sonarlint.core.analysis.container.analysis.issue.ignore.pattern.IssueExclusionPatternInitializer;
import org.sonarsource.sonarlint.core.analysis.container.analysis.issue.ignore.pattern.IssueInclusionPatternInitializer;
import org.sonarsource.sonarlint.core.analysis.container.analysis.issue.ignore.scanner.IssueExclusionsLoader;
import org.sonarsource.sonarlint.core.analysis.container.analysis.sensor.SensorOptimizer;
import org.sonarsource.sonarlint.core.analysis.container.analysis.sensor.SensorsExecutor;
import org.sonarsource.sonarlint.core.analysis.container.analysis.sensor.SonarLintSensorStorage;
import org.sonarsource.sonarlint.core.analysis.container.global.AnalysisExtensionInstaller;
import org.sonarsource.sonarlint.core.analysis.sonarapi.DefaultSensorContext;
import org.sonarsource.sonarlint.core.analysis.sonarapi.noop.NoOpFileLinesContextFactory;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.commons.progress.ProgressIndicator;
import org.sonarsource.sonarlint.core.plugin.commons.container.SpringComponentContainer;

public class AnalysisContainer extends SpringComponentContainer {

  private static final SonarLintLogger LOG = SonarLintLogger.get();
  private final ProgressIndicator cancelMonitor;

  public AnalysisContainer(SpringComponentContainer globalContainer, ProgressIndicator progressIndicator) {
    super(globalContainer);
    this.cancelMonitor = progressIndicator;
  }

  @Override
  protected void doBeforeStart() {
    addCoreComponents();
    addPluginExtensions();
  }

  private void addCoreComponents() {
    add(
      cancelMonitor,
      SonarLintInputProject.class,
      NoOpFileLinesContextFactory.class,

      //ACR-3d5c626bd78e4d0d9be2ac385e1b7fae
      new AnalysisTempFolderProvider(),

      //ACR-9d89eb04a5a643bfa3bf14765b3633ac
      PathResolver.class,

      //ACR-8649f8316e6a487ca025c67d46cb21f4
      Languages.class,

      AnalysisSettings.class,
      new AnalysisConfigurationProvider(),

      //ACR-d8630baab62f4026997bf9f081d2d53d
      InputFileIndex.class,
      InputFileBuilder.class,
      FileMetadata.class,
      LanguageDetection.class,
      FileIndexer.class,
      SonarLintFileSystem.class,

      //ACR-28c6caea9acf4b6bba904f32287b152a
      EnforceIssuesFilter.class,
      IgnoreIssuesFilter.class,
      IssueExclusionPatternInitializer.class,
      IssueInclusionPatternInitializer.class,
      IssueExclusionsLoader.class,

      SensorOptimizer.class,
      SensorsExecutor.class,

      DefaultSensorContext.class,
      SonarLintSensorStorage.class,
      IssueFilters.class,

      //ACR-8c91b65ff4f04b20afd4b5505551c527
      CheckFactory.class,

      //ACR-e3c5a829a21b49cdbf15ad59875584a2
      SonarLintNoSonarFilter.class);
  }

  private void addPluginExtensions() {
    getParent().getComponentByType(AnalysisExtensionInstaller.class).install(this, ContainerLifespan.ANALYSIS);
  }

  @Override
  protected void doAfterStart() {
    LOG.debug("Start analysis");
    //ACR-0dc6aad78e12450cab3e7e79b6980c63
    getComponentByType(FileIndexer.class).index();
    getComponentByType(SensorsExecutor.class).execute();
  }

}
