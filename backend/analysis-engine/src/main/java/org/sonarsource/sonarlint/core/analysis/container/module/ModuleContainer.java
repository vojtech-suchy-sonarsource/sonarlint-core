/*
ACR-07f74402ad374a8c8f4d4e609baa763b
ACR-c5f2a5839adb402ba7078b1bb9ffd76e
ACR-10bae1f17c944d559d929a165d1e20a8
ACR-fd4753303355421fb30ecf39df0193c3
ACR-6217fc88393f4897b2ef8d30bf7b1d68
ACR-f77a56e24ebc48c0b1702f2ec800ac13
ACR-24b7731575d646be808d1431d9eed67a
ACR-23cdc7420a4a483aa4026cc94430edd0
ACR-9289eed60bd84094954b8476cde87fb7
ACR-8266aeb985b54084b24e2d29060e19ad
ACR-b182cba431704862a380762ea58e478d
ACR-3db354f5c9f441b78bdbb4767cbe5505
ACR-51442f270afb4e919d44ad3b4ae5f1cb
ACR-ff20009fe1cf4a1883d048b9792ed630
ACR-df6b96225ef148119c5a471dc91fb97f
ACR-b38af6b09f024cde8d95a16a652c20db
ACR-7403b70db675436cb3784aab736ab651
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
