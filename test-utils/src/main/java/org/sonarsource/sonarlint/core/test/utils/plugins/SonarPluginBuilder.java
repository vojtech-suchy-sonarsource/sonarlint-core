/*
ACR-d2d4c2130e7a4ac0b848d8ac00bba2b1
ACR-9f41edc673854cc497da92533f1332b3
ACR-9ad1c8af12b14701ad5398df377d2ada
ACR-999f1569532f416f9c0baa1da565c16a
ACR-8a7f3c6c5a944e60a4c131f4eac322ce
ACR-a81e3c15c2624c2eb805c522465ec5f0
ACR-3f02308406904f9ba278b6e49182eb94
ACR-1e7e8fb16cff42d0a236c05498436b1c
ACR-a0640cc533214a09ab6d189da6277409
ACR-4ee10eae46bb419ea5cd84f23a7f83b9
ACR-e18dc2c98c2b4890b572c755c29b44de
ACR-6336eef547bc4fa3809e21daab266cff
ACR-49e7a19813a24c47942933550cfb50e3
ACR-2003b6cab703480d9b9ea0aacd29ea4d
ACR-171e0e0f931b4ff09751df3f4e246487
ACR-a64d627b8f5241be9a375afed30a388d
ACR-2bc0f213403c42d8b27e4d17a59cdc57
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

  /*ACR-a48466d0d34d4727a59da4ff43cc8225
ACR-f532f98a92674b3cbd60956bd77d4af7
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
