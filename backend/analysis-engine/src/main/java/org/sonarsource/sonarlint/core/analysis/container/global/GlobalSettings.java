/*
ACR-e986a7a4cb65493c93022996dc94d90a
ACR-c65c1d34647f419987c4207388efc520
ACR-566d375b8fc649daaa734cf47b8ce6e1
ACR-8cd4765aa4664925a47b83a300fc5dfd
ACR-6fe4de8511454fc99331c99b9114ad29
ACR-a392990dda7349d09c4cfa55e5039cdf
ACR-843a17813d33493aa8300306b543803f
ACR-29a06b9e312f47b289ae7c4b98df380f
ACR-f717edd1de7142239c6d6c0810fad64f
ACR-2998262a4ad94036817e56fcf8997604
ACR-7ec29996fdbb4667908104af36d54f81
ACR-55bf6690602e4b4899961d472ff29a11
ACR-01ca31eadb744d08a619c30bc1ee7b16
ACR-e42589487f3949959ba41f3d8b91d559
ACR-d420f8c775294df894164c2288f1723f
ACR-e1377f3235eb4ec49f4284ce30738dab
ACR-7a427013416f42ce9819b76a56316b13
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
