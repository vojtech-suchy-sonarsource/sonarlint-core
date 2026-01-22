/*
ACR-e10fb9a9ec08496aa906b5738608fb3d
ACR-5a77e82942844eb0bde825651d09b958
ACR-a8df47e03c7e4ed8a8bad6fd523dc084
ACR-347250e9753c46dbb186322aca2d6754
ACR-bc58df6c53b24105ba67c1831df93f06
ACR-8eb0d6a60bf04061a8a84ae21360b624
ACR-542a0d2d3c9f467a8fa6423b67bf34b7
ACR-9d05476a5cbd4929aa8266deaa00041d
ACR-0b52c4fcfb9e40a6b8675399d4d466f5
ACR-7e17435ad527439ba8cd72b83c755377
ACR-4193e9a612574f0eae044399b7b5bfc6
ACR-7e070d47d38e4e76866ea4710e291f3c
ACR-3f55f0ed6f284c44871305149c15d599
ACR-0b6f0f8e7e314a80a07e8009a9bce046
ACR-744ab0452975482cb36a2caf6af36d9f
ACR-814b03de66d5428a8d084eb0d4bd7ae8
ACR-76118b2504df46bc87b900f1a6694ca2
 */
package org.sonarsource.sonarlint.core.analysis.container.global;

import java.time.Clock;
import org.sonar.api.SonarQubeVersion;
import org.sonar.api.utils.System2;
import org.sonar.api.utils.UriReader;
import org.sonarsource.sonarlint.core.analysis.api.AnalysisSchedulerConfiguration;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.plugin.commons.ApiVersions;
import org.sonarsource.sonarlint.core.plugin.commons.LoadedPlugins;
import org.sonarsource.sonarlint.core.plugin.commons.container.SpringComponentContainer;
import org.sonarsource.sonarlint.core.plugin.commons.sonarapi.SonarLintRuntimeImpl;

public class GlobalAnalysisContainer extends SpringComponentContainer {
  protected static final SonarLintLogger LOG = SonarLintLogger.get();

  private GlobalExtensionContainer globalExtensionContainer;
  private ModuleRegistry moduleRegistry;
  private final AnalysisSchedulerConfiguration analysisGlobalConfig;
  private final LoadedPlugins loadedPlugins;

  public GlobalAnalysisContainer(AnalysisSchedulerConfiguration analysisGlobalConfig, LoadedPlugins loadedPlugins) {
    this.analysisGlobalConfig = analysisGlobalConfig;
    this.loadedPlugins = loadedPlugins;
  }

  @Override
  protected void doBeforeStart() {
    var sonarPluginApiVersion = ApiVersions.loadSonarPluginApiVersion();
    var sonarlintPluginApiVersion = ApiVersions.loadSonarLintPluginApiVersion();

    add(
      analysisGlobalConfig,
      loadedPlugins,
      GlobalSettings.class,
      new GlobalConfigurationProvider(),
      AnalysisExtensionInstaller.class,
      new SonarQubeVersion(sonarPluginApiVersion),
      new SonarLintRuntimeImpl(sonarPluginApiVersion, sonarlintPluginApiVersion, analysisGlobalConfig.getClientPid()),

      new GlobalTempFolderProvider(),
      UriReader.class,
      Clock.systemDefaultZone(),
      System2.INSTANCE);
  }

  @Override
  protected void doAfterStart() {
    declarePluginProperties();
    globalExtensionContainer = new GlobalExtensionContainer(this);
    globalExtensionContainer.startComponents();
    this.moduleRegistry = new ModuleRegistry(globalExtensionContainer, analysisGlobalConfig.getFileSystemProvider());
  }

  @Override
  public SpringComponentContainer stopComponents() {
    try {
      if (moduleRegistry != null) {
        moduleRegistry.stopAll();
      }
      if (globalExtensionContainer != null) {
        globalExtensionContainer.stopComponents();
      }
    } catch (Exception e) {
      LOG.error("Cannot close analysis engine", e);
    } finally {
      super.stopComponents();
    }
    return this;
  }

  private void declarePluginProperties() {
    loadedPlugins.getAnalysisPluginInstancesByKeys().values().forEach(this::declareProperties);
  }

  //ACR-96a56a078dc94044a40fa90124b7b992
  public ModuleRegistry getModuleRegistry() {
    return moduleRegistry;
  }

}
