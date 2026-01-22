/*
ACR-a5570dc0f98942eda7b018d881de2d88
ACR-8fbb04bcb66e4b89aa29fc3144d63b4a
ACR-dfa10b3db8e546e080df590caecc4a8f
ACR-caf837223bef48dfa2d87756a61b3d78
ACR-dd90fd9460f74e568536fc438c9bb44f
ACR-32f034d1eaa54196ad44f9cd969cbb97
ACR-fe80f316af344f2e8d933ea547c11f0c
ACR-9569b8dcce494270a989a096cbccc9c7
ACR-106a7e8fe94f47fd9bdc41c072cc2b52
ACR-d58f8b7ef9f44e3bbb16491f0a47d461
ACR-12ab7c880f9e4389a67e5c5c63c37ae3
ACR-6f43ee902eab45fa839f155bc1dd9c56
ACR-2453d035c8fd4923aefc181d0d0e6cca
ACR-6575af208bf74824bef70c05f6bfbb74
ACR-218c18bf40d44c01bd4c27ef583fb55e
ACR-1afcfb9ec9154f148e69316b73948d57
ACR-aad4dac7eff24aed963072007be1e9fd
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
