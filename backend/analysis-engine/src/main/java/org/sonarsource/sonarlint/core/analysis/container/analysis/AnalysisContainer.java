/*
ACR-ba084777ec66422382b0dce63545c4a2
ACR-34a8c479d3b34699aa0220c17ab88658
ACR-91983d1cde2b4a82aaee3f35bb2d1f32
ACR-fc2ea8bfa9054bb9a323d3aee6b29124
ACR-6d4df95bb3084e1698890b42fa6bc564
ACR-81aa6dc4bee84c1bb31ac022c381b043
ACR-44c1bc1c01b949778e47e4839e6197f2
ACR-2786e5349cac4acea7da216637ebc073
ACR-a55b323416e149dba4573d821f555935
ACR-457f9c4c467e474cbe9b1df4869bdaf1
ACR-63b4281ab6224b9d847c9cc1d9fac704
ACR-7e4f43b9587844998b4649401282e6dd
ACR-a82aa75e55034615a7466210c771538a
ACR-f9a033dc28e74aafaf1cc7835132f3ec
ACR-33ee8526d3a34d36853363e8898dffcc
ACR-361cc9d64c914fe1a2a227dada2ea355
ACR-600961215b4a42d49c57c3cb79f5a92d
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

      //ACR-7fc1050530e941a8a2e6172820edd280
      new AnalysisTempFolderProvider(),

      //ACR-feb6d9e9410f4b3eb4db5fff25d2757b
      PathResolver.class,

      //ACR-72d6ea9ec9824747ba129e7f8b906d3a
      Languages.class,

      AnalysisSettings.class,
      new AnalysisConfigurationProvider(),

      //ACR-89e644c581554a3981e6d76c3b15da53
      InputFileIndex.class,
      InputFileBuilder.class,
      FileMetadata.class,
      LanguageDetection.class,
      FileIndexer.class,
      SonarLintFileSystem.class,

      //ACR-db02b3cc8ac84d668a72820699a93c00
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

      //ACR-7e9dfeaba7e549d0a964578dbadae044
      CheckFactory.class,

      //ACR-e3b728f08f804b2d92848f4af634ada8
      SonarLintNoSonarFilter.class);
  }

  private void addPluginExtensions() {
    getParent().getComponentByType(AnalysisExtensionInstaller.class).install(this, ContainerLifespan.ANALYSIS);
  }

  @Override
  protected void doAfterStart() {
    LOG.debug("Start analysis");
    //ACR-07c4808478644d568798bcdc060cbb92
    getComponentByType(FileIndexer.class).index();
    getComponentByType(SensorsExecutor.class).execute();
  }

}
