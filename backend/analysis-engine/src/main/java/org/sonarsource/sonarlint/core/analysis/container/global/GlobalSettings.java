/*
ACR-4d24d9bfbb264bff94b6b22df9685721
ACR-5ae2fba8900f4279b42e47a00b26656e
ACR-511ad6b03279447f8b8f7c2490bbb251
ACR-c56cd202473644728b91837dee5eb339
ACR-53deee4be6ec4c2d8974a81482eb293e
ACR-17e1fda5002b40d9abb46ff0c4dc6eff
ACR-98d34550496b4d71b021702229dca7ad
ACR-05a5d5b5e80c4aa48778c6efe0ddb95b
ACR-37e95980b1e348ea92b84735aa38e51e
ACR-9639af4655984b2a8f11d57244f4a696
ACR-1cdad2ab13ee43088db77cb312e5accc
ACR-5dd5775dfeb5406385147c8c0df69d74
ACR-b863826e0371453cac51d52d83f381c0
ACR-431bfbc7631a40779a16dce6a91dd572
ACR-731fb23cd60e477bab690d028b7d705c
ACR-938800db091743509c4a443fc26bac00
ACR-32af1257a15a4a4682c4e239d6016fc3
 */
package org.sonarsource.sonarlint.core.analysis.container.global;

import org.sonar.api.config.PropertyDefinitions;
import org.sonarsource.sonarlint.core.analysis.api.AnalysisSchedulerConfiguration;
import org.sonarsource.sonarlint.core.plugin.commons.sonarapi.MapSettings;

public class GlobalSettings extends MapSettings {

  public GlobalSettings(AnalysisSchedulerConfiguration config, PropertyDefinitions propertyDefinitions) {
    super(propertyDefinitions, config.getEffectiveSettings());
  }

}
