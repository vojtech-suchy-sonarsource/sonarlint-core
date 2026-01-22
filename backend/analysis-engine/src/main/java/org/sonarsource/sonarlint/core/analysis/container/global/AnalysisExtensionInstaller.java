/*
ACR-5ede982af2b04086bee47ed1b2fb8243
ACR-819779919eb7446d8f4bb944f0262357
ACR-1d6f55ba656c42fb825dfb6559955da2
ACR-0608676c43794de3b1bbc19d6e11bb5a
ACR-0af71f190115404d958d82949fb0dc14
ACR-b710c1dc31ff4ec69e07d268287b4454
ACR-dd24adcdcf7b407e983b911fe30353bd
ACR-87f6faa0594a46879944a529c22b6ee0
ACR-4c087fda1dcf4ac09da654d44824b971
ACR-9907ce7ce160426f8b9508cc4bed4935
ACR-9ba56e37485643a9a848514f7a6ccb75
ACR-c6cedc9b9a2e4d98a246b86cb2b7edec
ACR-5713a614fb2f40088cae65e05eb94394
ACR-87dd867267f6478c828914f21cdf3eb8
ACR-2c69cf9d29884279af8d779104357b3b
ACR-94c98a13832e43ce87ddf760b0b2ba04
ACR-c2863fa5b48e45b097797789f38b21b3
 */
package org.sonarsource.sonarlint.core.analysis.container.global;

import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.config.Configuration;
import org.sonar.api.utils.AnnotationUtils;
import org.sonarsource.api.sonarlint.SonarLintSide;
import org.sonarsource.sonarlint.core.analysis.container.ContainerLifespan;
import org.sonarsource.sonarlint.core.commons.api.SonarLanguage;
import org.sonarsource.sonarlint.core.plugin.commons.ExtensionInstaller;
import org.sonarsource.sonarlint.core.plugin.commons.ExtensionUtils;
import org.sonarsource.sonarlint.core.plugin.commons.LoadedPlugins;
import org.sonarsource.sonarlint.core.plugin.commons.container.ExtensionContainer;
import org.sonarsource.sonarlint.plugin.api.SonarLintRuntime;

public class AnalysisExtensionInstaller extends ExtensionInstaller {

  private final LoadedPlugins loadedPlugins;

  public AnalysisExtensionInstaller(SonarLintRuntime sonarRuntime, LoadedPlugins loadedPlugins, Configuration bootConfiguration) {
    super(sonarRuntime, bootConfiguration);
    this.loadedPlugins = loadedPlugins;
  }

  public void install(ExtensionContainer container, ContainerLifespan lifespan) {
    super.install(container, loadedPlugins.getAnalysisPluginInstancesByKeys(),
      (pluginKey, extension) -> lifespan.equals(getSonarLintSideLifespan(extension)) && onlySonarSourceSensor(pluginKey, extension));
  }

  private static ContainerLifespan getSonarLintSideLifespan(Object extension) {
    var annotation = AnnotationUtils.getAnnotation(extension, SonarLintSide.class);
    if (annotation != null) {
      var lifespan = annotation.lifespan();
      if (SonarLintSide.MULTIPLE_ANALYSES.equals(lifespan) || "INSTANCE".equals(lifespan)) {
        return ContainerLifespan.INSTANCE;
      }
      if ("MODULE".equals(lifespan)) {
        return ContainerLifespan.MODULE;
      }
      if (SonarLintSide.SINGLE_ANALYSIS.equals(lifespan)) {
        return ContainerLifespan.ANALYSIS;
      }
    }
    return null;
  }

  private boolean onlySonarSourceSensor(String pluginKey, Object extension) {
    return SonarLanguage.containsPlugin(pluginKey) || loadedPlugins.getAdditionalAllowedPlugins().contains(pluginKey) || isNotSensor(extension);
  }

  private static boolean isNotSensor(Object extension) {
    return !ExtensionUtils.isType(extension, Sensor.class);
  }

}
