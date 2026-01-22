/*
ACR-079345980c814340a1d23ec77008a6a6
ACR-6377ded14eda40b6967cfc1e7bf572a6
ACR-c97ba6aa18a342138be4a38afc843490
ACR-12c505714dd848ef852862e49f1fafd2
ACR-720cc216ae904c119bd7003aa5d26496
ACR-7014d055bad94a81ac76c4d92eecafa1
ACR-ea076f8d73f740319dcc1cff798daeec
ACR-b542d7a04d794cf4820c256e83d25b5f
ACR-8475d13b938f4148bffc44f027bb7ccf
ACR-6af9c2686f714b59a8a29adb923a46ce
ACR-c212d638719c4aeeb5ca2372d282bc86
ACR-bc2feb9bf9c14aeb9d922a4b3dcf9c70
ACR-94d6b2514cd24499859e809630aec2c0
ACR-f4b81f4c50d146f8b767aa4cc653b532
ACR-66d04703ef5446e493aff6cb03ce472f
ACR-decf9a7b90b14826a70ba277d45bb614
ACR-469dd2ef7db5415abd23afac9af5e1ca
 */
package org.sonarsource.sonarlint.core.plugin.commons;

import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiPredicate;
import org.sonar.api.Plugin;
import org.sonar.api.config.Configuration;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.plugin.commons.container.ExtensionContainer;
import org.sonarsource.sonarlint.core.plugin.commons.sonarapi.PluginContextImpl;
import org.sonarsource.sonarlint.plugin.api.SonarLintRuntime;

public class ExtensionInstaller {
  private static final SonarLintLogger LOG = SonarLintLogger.get();

  private final SonarLintRuntime sonarRuntime;
  private final Configuration bootConfiguration;

  public ExtensionInstaller(SonarLintRuntime sonarRuntime, Configuration bootConfiguration) {
    this.sonarRuntime = sonarRuntime;
    this.bootConfiguration = bootConfiguration;
  }

  public void install(ExtensionContainer container, Map<String, Plugin> pluginInstancesByKey, BiPredicate<String, Object> extensionFilter) {
    for (Entry<String, Plugin> pluginInstanceEntry : pluginInstancesByKey.entrySet()) {
      var plugin = pluginInstanceEntry.getValue();
      var context = new PluginContextImpl.Builder()
        .setSonarRuntime(sonarRuntime)
        .setBootConfiguration(bootConfiguration)
        .build();
      var pluginKey = pluginInstanceEntry.getKey();
      try {
        plugin.define(context);
        loadExtensions(container, pluginKey, context, extensionFilter);
      } catch (Throwable t) {
        LOG.error("Error loading components for plugin '{}'", pluginKey, t);
      }
    }
  }

  private static void loadExtensions(ExtensionContainer container, String pluginKey, Plugin.Context context, BiPredicate<String, Object> extensionFilter) {
    for (Object extension : context.getExtensions()) {
      if (extensionFilter.test(pluginKey, extension)) {
        container.addExtension(pluginKey, extension);
      }
    }
  }

}
