/*
ACR-bf3d05591e034fc1b686c61e9d49c44b
ACR-28a622a2b0e74b638ce3d27d21aef91f
ACR-64437312a8be4cb3bf1d4af73fbd40bd
ACR-ad1f311b58d0401a92592aaa52cfcddc
ACR-b9a3e745caa84ecab222827153c59d14
ACR-bf44936380f545e1980b5f8a7cb52d5c
ACR-eff94c3a1bf142a4bf41758ba15bfa07
ACR-dfd2ee4ae22a4c1aa9fc2cde71845b69
ACR-01b77d431a9f48039b602fb07f63a4e9
ACR-28e64834e67c4b029e7c7bc4414db328
ACR-558749c4310347b7857fefdb3e163f9c
ACR-560c79838bdb4bd3874579974b34470f
ACR-a7e75e55e7624717814846c4ce5ee3c5
ACR-1c3370a59e8e482dbd3d43b979870474
ACR-fd9fadc23a034b0a9796fb8fd93a5c77
ACR-985dbe72f6154cc6b8987bf7820e4fc1
ACR-27fa67f0729a431498a44a64e60afb0d
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
        //ACR-7570d652db184b8ebba633fb5dac98c4
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
