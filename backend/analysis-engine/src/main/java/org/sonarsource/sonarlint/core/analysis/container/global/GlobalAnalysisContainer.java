/*
ACR-8ec24bdeb0864e1c8e35b66f355f6237
ACR-c683efca7e1e4c5f9a1c936eab473047
ACR-160b71028ff6467aaabcf95bcc594845
ACR-35e683c1eef04ed2a8edac7b94939e98
ACR-02edf52eda7647d1b1b81ee3ba5de1fd
ACR-5197d92c7f9a4a96bca15af4f141f1aa
ACR-5eecb9e906464721b0346bc1f540540c
ACR-87997a0922d94302965d71072ddfff97
ACR-617728536fb44ec8a90836db452a1626
ACR-f5f7c01037f84d3981535f7cd24a062f
ACR-586cdec808c34388b55bc1e55819bee4
ACR-f0fb2ea9b80042f087942f7808359df0
ACR-d81f2d145a71487e95477e867b41f21a
ACR-9289899cd50741bb8e66f5ef34338117
ACR-e907e651aa3a465cadeedbf0f873808f
ACR-9e5d6621044a417b9d0fb4a59b581c26
ACR-8d787ffd513c4076afd3458583579cda
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

  //ACR-1d521d764a4c41be9972f87e2fb91004
  public ModuleRegistry getModuleRegistry() {
    return moduleRegistry;
  }

}
