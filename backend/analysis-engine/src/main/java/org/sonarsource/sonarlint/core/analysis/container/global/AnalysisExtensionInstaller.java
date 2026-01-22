/*
ACR-054b79498bd74c818dd75b5b1aa423fa
ACR-db7f30fbde3f4ae8a6c0f97aa465420d
ACR-5ad2af51775f471da1f20a153bb615ed
ACR-abe33ec2a7524ff58de5b3123341e366
ACR-db14b2a411054d85b34f945d3e2bd9af
ACR-702ef224bf1442b2925c46b38854dc5b
ACR-bb62816fa9284303b60f1ca0b1b35578
ACR-90b7c5cf1f90432a887cf09491b403f7
ACR-181c628ca25144e39fa474871fc01399
ACR-e60198f83ccd4d33be23f81766096065
ACR-c0c0bbc52adb48118c38a44bec97b45d
ACR-0483c7700a0a47a98d578a1452fd57de
ACR-b2412d8c025141fdab16a2837800b67a
ACR-d13133762cab4b79ac3152c3273bbd44
ACR-9de9523674984be9b2dc222d44d78dec
ACR-1b5532f7e603470b88fb3a61a1cdffab
ACR-745fae4c6a804298b20d464481330723
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
