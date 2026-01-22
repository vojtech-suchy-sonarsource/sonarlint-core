/*
ACR-857f39a35ceb4a9e806d8216207f8688
ACR-3e144074f59a4d328dc3d66f42e1641a
ACR-3f5a48ef6a4b450f855ad1cee59c400b
ACR-832c4b80170043f488300f99f0ac99e4
ACR-2d8b644ace154e92b2d28c5293549c1e
ACR-4f2719a905304e1cad5c8b1150897e9d
ACR-a9b4b06e310f4b3caeef3bb2d7d0f44f
ACR-192308fbec014a3584e3f5fe44435090
ACR-32c5c92c8d7144989ccad71fc003c985
ACR-144d5af6be854dbeb2285692615ac147
ACR-420c5f09a2d84fa79d10b62bfd91747f
ACR-f3be0b2215044d1ebde8e7635c339858
ACR-8a707af8d3ed4d329143a1b23a8b2e78
ACR-6f59157a178d4d919666916189a0c6ca
ACR-0f9d8cebe773432d9f323cafe2c784ab
ACR-91c2f7b99bad4666b12f34505ab9eb0c
ACR-1822cf83c30f44a68090166e93f0b416
 */
package org.sonarsource.sonarlint.core.test.utils.plugins;

import java.io.IOException;
import java.nio.file.Path;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import net.bytebuddy.ByteBuddy;
import org.jetbrains.annotations.NotNull;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonarsource.sonarlint.core.test.utils.plugins.src.DefaultPlugin;
import org.sonarsource.sonarlint.core.test.utils.plugins.src.DefaultRulesDefinition;
import org.sonarsource.sonarlint.core.test.utils.plugins.src.DefaultSensor;

public class SonarPluginBuilder {

  /*ACR-f0c1afb107844f6bbd5c4add4e4f6751
ACR-1e83b29f04c34577ba9be2878b545859
   */
  public static SonarPluginBuilder newSonarPlugin(String pluginKey) {
    return new SonarPluginBuilder(pluginKey);
  }

  private Class<? extends Sensor> sensorClass = DefaultSensor.class;

  private Class<? extends RulesDefinition> rulesDefinitionClass = DefaultRulesDefinition.class;
  private final String pluginKey;

  private SonarPluginBuilder(String pluginKey) {
    this.pluginKey = pluginKey;
  }

  public SonarPluginBuilder withSensor(Class<? extends Sensor> sensorClass) {
    this.sensorClass = sensorClass;
    return this;
  }

  public SonarPluginBuilder withRulesDefinition(Class<? extends RulesDefinition> rulesDefinitionClass) {
    this.rulesDefinitionClass = rulesDefinitionClass;
    return this;
  }

  public Path generate(Path folder) {
    var pluginPath = folder.resolve("my.jar");
    try (var pluginClass = new ByteBuddy()
      .redefine(DefaultPlugin.class)
      .make();
      var sensorType = new ByteBuddy()
        .redefine(sensorClass)
        .name(DefaultSensor.class.getName())
        .make();
      var rulesDefinitionType = new ByteBuddy()
        .redefine(rulesDefinitionClass)
        .name(DefaultRulesDefinition.class.getName())
        .make()) {
      pluginClass.toJar(pluginPath.toFile(), generateManifest());
      sensorType.inject(pluginPath.toFile());
      rulesDefinitionType.inject(pluginPath.toFile());
    } catch (IOException exception) {
      throw new IllegalStateException("Error when generating the plugin", exception);
    }

    return pluginPath;
  }

  @NotNull
  private Manifest generateManifest() {
    var manifest = new Manifest();
    manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
    manifest.getMainAttributes().put(new Attributes.Name("SonarLint-Supported"), "true");
    manifest.getMainAttributes().put(new Attributes.Name("Plugin-Class"), DefaultPlugin.class.getName());
    manifest.getMainAttributes().put(new Attributes.Name("Plugin-Key"), pluginKey);
    manifest.getMainAttributes().put(new Attributes.Name("Plugin-Version"), "10.0.0");
    return manifest;
  }
}
