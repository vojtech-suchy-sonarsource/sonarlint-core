/*
ACR-29edccfeec954a9aa6ed4fb10856823e
ACR-27cc0c54a45343cca4b47dcc5e0f3106
ACR-a186218b5ea143ddb0241796d5fda632
ACR-0d0549bcb2584765a23fbc68d185151f
ACR-819c2b27a446498b9febf057077c860c
ACR-7b1cdf1de67042f9b58cd063036242ef
ACR-b06bfad73d5447fc955ac34f16bc9b67
ACR-e8d5808615634f089dc086766be4f48f
ACR-a6dfe31dee9543ac97de5695b0ca1663
ACR-b4873b27cad74f3dac8426e790c24ec2
ACR-64d1c1cc734644d287750239c2437297
ACR-93d3afec81c94fbdbd79cdc34042b48e
ACR-19dd6c43c1f4410f82e466d8f5d3fd9b
ACR-f42965b5289a47cfb5b0e20831399430
ACR-8fa0f83b08d04c3eb1f095892649559e
ACR-cde27719074e4809b64a97a738f90cf5
ACR-4e00a44d92b64cc78b4ae3b24bf3e152
 */
package org.sonarsource.sonarlint.core.analysis.container.global;

import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonar.api.Plugin;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.config.Configuration;
import org.sonar.api.utils.Version;
import org.sonarsource.api.sonarlint.SonarLintSide;
import org.sonarsource.sonarlint.core.analysis.container.ContainerLifespan;
import org.sonarsource.sonarlint.core.plugin.commons.sonarapi.MapSettings;
import org.sonarsource.sonarlint.core.commons.log.LogOutput;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import org.sonarsource.sonarlint.core.plugin.commons.LoadedPlugins;
import org.sonarsource.sonarlint.core.plugin.commons.container.SpringComponentContainer;
import org.sonarsource.sonarlint.core.plugin.commons.sonarapi.SonarLintRuntimeImpl;
import org.sonarsource.sonarlint.plugin.api.SonarLintRuntime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class AnalysisExtensionInstallerTests {
  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();
  private static final String FAKE_PLUGIN_KEY = "foo";
  private static final String JAVA_PLUGIN_KEY = "java";
  private static final String DBD_PLUGIN_KEY = "dbd";

  private static final Configuration EMPTY_CONFIG = new MapSettings(Map.of()).asConfig();
  private static final Version PLUGIN_API_VERSION = Version.create(5, 4, 0);
  private static final long FAKE_PID = 123L;
  private static final SonarLintRuntime RUNTIME = new SonarLintRuntimeImpl(Version.create(8, 0), PLUGIN_API_VERSION, FAKE_PID);
  private AnalysisExtensionInstaller underTest;
  private LoadedPlugins loadedPlugins;
  private SpringComponentContainer container;

  @BeforeEach
  void prepare() {
    loadedPlugins = mock(LoadedPlugins.class);
    container = mock(SpringComponentContainer.class);
    underTest = new AnalysisExtensionInstaller(RUNTIME, loadedPlugins, EMPTY_CONFIG);
  }

  @Test
  void install_sonarlintside_extensions_with_default_lifespan_in_analysis_container_for_compatible_plugins() {
    when(loadedPlugins.getAnalysisPluginInstancesByKeys()).thenReturn(Map.of(FAKE_PLUGIN_KEY, new FakePlugin()));

    underTest.install(container, ContainerLifespan.ANALYSIS);

    verify(container).addExtension(FAKE_PLUGIN_KEY, FakeSonarLintDefaultLifespanComponent.class);
  }

  @Test
  void install_sonarlintside_extensions_with_single_analysis_lifespan_in_analysis_container_for_compatible_plugins() {
    when(loadedPlugins.getAnalysisPluginInstancesByKeys()).thenReturn(Map.of(FAKE_PLUGIN_KEY, new FakePlugin(FakeSonarLintSingleAnalysisLifespanComponent.class)));

    underTest.install(container, ContainerLifespan.ANALYSIS);

    verify(container).addExtension(FAKE_PLUGIN_KEY, FakeSonarLintSingleAnalysisLifespanComponent.class);
  }

  @Test
  void install_sonarlintside_extensions_with_multiple_analysis_lifespan_in_global_container_for_compatible_plugins() {
    when(loadedPlugins.getAnalysisPluginInstancesByKeys()).thenReturn(Map.of(FAKE_PLUGIN_KEY, new FakePlugin(FakeSonarLintMultipleAnalysisLifespanComponent.class)));

    underTest.install(container, ContainerLifespan.INSTANCE);

    verify(container).addExtension(FAKE_PLUGIN_KEY, FakeSonarLintMultipleAnalysisLifespanComponent.class);
  }

  @Test
  void install_sonarlintside_extensions_with_instance_lifespan_in_global_container_for_compatible_plugins() {
    when(loadedPlugins.getAnalysisPluginInstancesByKeys()).thenReturn(Map.of(FAKE_PLUGIN_KEY, new FakePlugin(FakeSonarLintInstanceLifespanComponent.class)));

    underTest.install(container, ContainerLifespan.INSTANCE);

    verify(container).addExtension(FAKE_PLUGIN_KEY, FakeSonarLintInstanceLifespanComponent.class);
  }

  @Test
  void dont_install_sonarlintside_extensions_with_multiple_analysis_lifespan_in_analysis_container_for_compatible_plugins() {
    when(loadedPlugins.getAllPluginInstancesByKeys()).thenReturn(Map.of(FAKE_PLUGIN_KEY, new FakePlugin(FakeSonarLintMultipleAnalysisLifespanComponent.class)));

    underTest.install(container, ContainerLifespan.ANALYSIS);

    verifyNoInteractions(container);
  }

  @Test
  void dont_install_sonarlintside_extensions_with_single_analysis_lifespan_in_global_container_for_compatible_plugins() {
    when(loadedPlugins.getAllPluginInstancesByKeys()).thenReturn(Map.of(FAKE_PLUGIN_KEY, new FakePlugin(FakeSonarLintSingleAnalysisLifespanComponent.class)));

    underTest.install(container, ContainerLifespan.INSTANCE);

    verifyNoInteractions(container);
  }

  @Test
  void install_sonarlintside_extensions_with_module_lifespan_in_module_container_for_compatible_plugins() {
    when(loadedPlugins.getAnalysisPluginInstancesByKeys()).thenReturn(Map.of(FAKE_PLUGIN_KEY, new FakePlugin(FakeSonarLintModuleLifespanComponent.class)));

    underTest.install(container, ContainerLifespan.MODULE);

    verify(container).addExtension(FAKE_PLUGIN_KEY, FakeSonarLintModuleLifespanComponent.class);
  }

  @Test
  void install_sensors_for_sonarsource_plugins_by_language() {
    when(loadedPlugins.getAnalysisPluginInstancesByKeys()).thenReturn(Map.of(JAVA_PLUGIN_KEY, new FakePlugin()));

    underTest.install(container, ContainerLifespan.ANALYSIS);

    verify(container).addExtension(JAVA_PLUGIN_KEY, FakeSensor.class);
  }

  @Test
  void install_sensors_for_sonarsource_plugins_by_allowlist() {
    when(loadedPlugins.getAnalysisPluginInstancesByKeys()).thenReturn(Map.of(DBD_PLUGIN_KEY, new FakePlugin()));
    when(loadedPlugins.getAdditionalAllowedPlugins()).thenReturn(Set.of(DBD_PLUGIN_KEY));

    underTest.install(container, ContainerLifespan.ANALYSIS);

    verify(container).addExtension(DBD_PLUGIN_KEY, FakeSensor.class);
  }

  @Test
  void dont_install_sensors_for_non_sonarsource_plugins() {
    when(loadedPlugins.getAnalysisPluginInstancesByKeys()).thenReturn(Map.of(FAKE_PLUGIN_KEY, new FakePlugin()));

    underTest.install(container, ContainerLifespan.ANALYSIS);

    verify(container, never()).addExtension(FAKE_PLUGIN_KEY, FakeSensor.class);
  }

  @Test
  void provide_sonarlint_context_for_plugin_definition() {
    var pluginInstance = new PluginStoringSonarLintPluginApiVersion();
    when(loadedPlugins.getAnalysisPluginInstancesByKeys()).thenReturn(Map.of(FAKE_PLUGIN_KEY, pluginInstance));

    underTest = new AnalysisExtensionInstaller(RUNTIME, loadedPlugins, EMPTY_CONFIG);

    underTest.install(container, ContainerLifespan.ANALYSIS);

    assertThat(pluginInstance.sonarLintPluginApiVersion).isEqualTo(PLUGIN_API_VERSION);
    assertThat(pluginInstance.clientPid).isEqualTo(FAKE_PID);
  }

  @Test
  void log_when_plugin_throws() {
    when(loadedPlugins.getAnalysisPluginInstancesByKeys()).thenReturn(Map.of(FAKE_PLUGIN_KEY, new ThrowingPlugin()));

    underTest = new AnalysisExtensionInstaller(RUNTIME, loadedPlugins, EMPTY_CONFIG);

    underTest.install(container, ContainerLifespan.ANALYSIS);

    assertThat(logTester.logs(LogOutput.Level.ERROR)).contains("Error loading components for plugin 'foo'");
  }

  private static class FakePlugin implements Plugin {
    private final Object component;

    private FakePlugin() {
      this(FakeSonarLintDefaultLifespanComponent.class);
    }

    public FakePlugin(Object component) {
      this.component = component;
    }

    @Override
    public void define(Context context) {
      context.addExtension(component);
      context.addExtension(FakeSensor.class);
    }

  }

  private static class ThrowingPlugin implements Plugin {
    @Override
    public void define(Context context) {
      throw new Error();
    }

  }

  private static class PluginStoringSonarLintPluginApiVersion implements Plugin {
    Version sonarLintPluginApiVersion;
    long clientPid;

    @Override
    public void define(Context context) {
      if (context.getRuntime() instanceof SonarLintRuntime) {
        sonarLintPluginApiVersion = ((SonarLintRuntime) context.getRuntime()).getSonarLintPluginApiVersion();
        clientPid = ((SonarLintRuntime) context.getRuntime()).getClientPid();
      }
    }

  }

  @SonarLintSide
  private static class FakeSonarLintDefaultLifespanComponent {
  }

  @SonarLintSide(lifespan = SonarLintSide.SINGLE_ANALYSIS)
  private static class FakeSonarLintSingleAnalysisLifespanComponent {
  }

  @SonarLintSide(lifespan = SonarLintSide.MULTIPLE_ANALYSES)
  private static class FakeSonarLintMultipleAnalysisLifespanComponent {
  }

  @SonarLintSide(lifespan = "MODULE")
  private static class FakeSonarLintModuleLifespanComponent {
  }

  @SonarLintSide(lifespan = "INSTANCE")
  private static class FakeSonarLintInstanceLifespanComponent {
  }

  private static class FakeSensor implements Sensor {

    @Override
    public void describe(SensorDescriptor descriptor) {

    }

    @Override
    public void execute(SensorContext context) {
    }
  }

}
