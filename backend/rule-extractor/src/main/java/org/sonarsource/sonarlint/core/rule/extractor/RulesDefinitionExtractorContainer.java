/*
ACR-d94d1d8bdcbd475dacba2564c148ced8
ACR-7187f475c7bf4d64a04a8ab5b5b549e4
ACR-86cabfca40d248b783b6acdfc6c45735
ACR-aec4e1cd9011404caeb3523a1a389fd3
ACR-a70fb5d68dc0486183973fe97ea0d133
ACR-26d74894604848e3b221b3833193b3f5
ACR-e1d6d5f65c214a94a6f2875944e47679
ACR-80db3fb4d99945e988d2cd68ad5d6b90
ACR-7b1f42b27db64a81a558c9b23d449445
ACR-dd89d0a565c840b8a4dd1581d061fe79
ACR-be209373f3054ad0afd172d66ff16891
ACR-1006992036864c4389c50e0246ae2c05
ACR-9da94ea12b7c41e7b633b6fe305a5c85
ACR-58fc58e894de4927a36bae7cb56e3c0b
ACR-8d85bf9a2414403db526bdc198e34746
ACR-9ea93c0be3b740958b8af061db6c0a52
ACR-2280b15a7c7c413eac1358a3a64212dc
 */
package org.sonarsource.sonarlint.core.rule.extractor;

import java.util.Map;
import org.sonar.api.Plugin;
import org.sonar.api.SonarQubeVersion;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.server.rule.RulesDefinition.Context;
import org.sonar.api.server.rule.RulesDefinitionXmlLoader;
import org.sonar.api.utils.AnnotationUtils;
import org.sonarsource.api.sonarlint.SonarLintSide;
import org.sonarsource.sonarlint.core.plugin.commons.ApiVersions;
import org.sonarsource.sonarlint.core.plugin.commons.ExtensionInstaller;
import org.sonarsource.sonarlint.core.plugin.commons.ExtensionUtils;
import org.sonarsource.sonarlint.core.plugin.commons.container.SpringComponentContainer;
import org.sonarsource.sonarlint.core.plugin.commons.sonarapi.ConfigurationBridge;
import org.sonarsource.sonarlint.core.plugin.commons.sonarapi.SonarLintRuntimeImpl;

public class RulesDefinitionExtractorContainer extends SpringComponentContainer {
  private Context rulesDefinitionContext;
  private final Map<String, Plugin> pluginInstancesByKeys;
  private final RuleSettings settings;

  public RulesDefinitionExtractorContainer(Map<String, Plugin> pluginInstancesByKeys, RuleSettings settings) {
    this.pluginInstancesByKeys = pluginInstancesByKeys;
    this.settings = settings;
  }

  @Override
  protected void doBeforeStart() {
    var sonarPluginApiVersion = ApiVersions.loadSonarPluginApiVersion();
    var sonarlintPluginApiVersion = ApiVersions.loadSonarLintPluginApiVersion();

    var sonarLintRuntime = new SonarLintRuntimeImpl(sonarPluginApiVersion, sonarlintPluginApiVersion, -1);

    var extensionInstaller = new ExtensionInstaller(sonarLintRuntime, new EmptyConfiguration());
    extensionInstaller.install(this, pluginInstancesByKeys, (key, ext) -> {
      if (ExtensionUtils.isType(ext, Sensor.class)) {
        //ACR-d36194ae6092432eac7c9eab78bf4322
        return false;
      }
      var annotation = AnnotationUtils.getAnnotation(ext, SonarLintSide.class);
      if (annotation != null) {
        var lifespan = annotation.lifespan();
        return SonarLintSide.SINGLE_ANALYSIS.equals(lifespan);
      }
      return false;
    });
    add(
      settings,
      ConfigurationBridge.class,
      RuleExtractionSettings.class,
      sonarLintRuntime,
      new SonarQubeVersion(sonarPluginApiVersion),
      RulesDefinitionXmlLoader.class,
      RuleDefinitionsLoader.class,
      NoopTempFolder.class);
  }

  @Override
  protected void doAfterStart() {
    this.rulesDefinitionContext = getComponentByType(RuleDefinitionsLoader.class).getContext();
  }

  public Context getRulesDefinitionContext() {
    return rulesDefinitionContext;
  }

}
