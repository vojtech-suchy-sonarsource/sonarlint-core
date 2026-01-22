/*
ACR-66bd4e74e2524405b2efccc561e72e8f
ACR-1be49ead041d4d8292121ae9791b6581
ACR-92bb63751a6f4b1cb9129adfc8fc6cb1
ACR-1a98b7b60c444a2ab295da68a4a2d123
ACR-46a61079f2844f2182ed8c748b56d3d7
ACR-f3063e00bc44425ab894adc40f5641b0
ACR-bc1a93aae7384cbc8644769ed13fdfb1
ACR-006845f8a8184862b85359ec69216424
ACR-d3d5b268b8664fd6b4f9b3b4619fa538
ACR-07f4d2c0a0fd47caa5d3ef77740dc1dd
ACR-c49cd7dc540d4673b7ddf039f984d308
ACR-f9ceef8fb98943429009278eb1e6101f
ACR-8aed71aa04164c3c89e49ece237ac41c
ACR-d3a405778a1b47749749ce18fae12e0a
ACR-dd82a9dda605430bbe1afa6398dcdeff
ACR-c8a223983c424019b19fb95ec1a4c90c
ACR-ba45a548e38e4c2394dea0451d2a6a6a
 */
package org.sonarsource.sonarlint.core.analysis.container.module;

import java.util.function.Consumer;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.analysis.api.AnalysisConfiguration;
import org.sonarsource.sonarlint.core.analysis.api.AnalysisResults;
import org.sonarsource.sonarlint.core.analysis.api.Issue;
import org.sonarsource.sonarlint.core.analysis.container.ContainerLifespan;
import org.sonarsource.sonarlint.core.analysis.container.analysis.AnalysisContainer;
import org.sonarsource.sonarlint.core.analysis.container.analysis.IssueListenerHolder;
import org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem.FileMetadata;
import org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem.LanguageDetection;
import org.sonarsource.sonarlint.core.analysis.container.global.AnalysisExtensionInstaller;
import org.sonarsource.sonarlint.core.analysis.sonarapi.ActiveRulesAdapter;
import org.sonarsource.sonarlint.core.analysis.sonarapi.SonarLintModuleFileSystem;
import org.sonarsource.sonarlint.core.commons.tracing.Trace;
import org.sonarsource.sonarlint.core.commons.progress.ProgressIndicator;
import org.sonarsource.sonarlint.core.plugin.commons.container.SpringComponentContainer;

import static org.sonarsource.sonarlint.core.commons.tracing.Trace.startChild;

public class ModuleContainer extends SpringComponentContainer {

  private final boolean isTransient;

  public ModuleContainer(SpringComponentContainer parent, boolean isTransient) {
    super(parent);
    this.isTransient = isTransient;
  }

  @Override
  protected void doBeforeStart() {
    add(
      SonarLintModuleFileSystem.class,
      ModuleInputFileBuilder.class,
      FileMetadata.class,
      LanguageDetection.class,

      ModuleFileEventNotifier.class);
    getParent().getComponentByType(AnalysisExtensionInstaller.class).install(this, ContainerLifespan.MODULE);
  }

  public boolean isTransient() {
    return isTransient;
  }

  public AnalysisResults analyze(AnalysisConfiguration configuration, Consumer<Issue> issueListener, ProgressIndicator progressIndicator, @Nullable Trace trace) {
    var analysisContainer = startChild(trace, "newAnalysisContainer", "analyze", () -> new AnalysisContainer(this, progressIndicator));
    analysisContainer.add(configuration);
    analysisContainer.add(new IssueListenerHolder(issueListener));
    analysisContainer.add(startChild(trace, "newActiveRulesAdapter", "analyze", () ->
      new ActiveRulesAdapter(configuration.activeRules())));
    var defaultAnalysisResult = new AnalysisResults();
    analysisContainer.add(defaultAnalysisResult);
    if (trace != null) {
      analysisContainer.add(trace);
    }
    analysisContainer.execute(trace);
    return defaultAnalysisResult;
  }
}
