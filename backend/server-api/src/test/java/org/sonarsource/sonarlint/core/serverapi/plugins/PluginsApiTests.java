/*
ACR-997f2b6df37647d68068d9f6a06fd08c
ACR-e8a98d26971c491aacf480e64c2abff1
ACR-5f51393ff0c443e1962482add693b1b7
ACR-e49adff889e1433bb8d7970e07299cf3
ACR-4bf1094f2cdd4b15b2becf7651d2fa92
ACR-a2407950813e42fd8540dfd0a83d9cff
ACR-c2dd91ba2dda4b62b66926d20b3a209b
ACR-091863d958584e6c904317079f4cc617
ACR-807fcf73df0f42e0a4bef8f5c22aaf87
ACR-b639daee503a43f9b6c9032d52188860
ACR-c7e83df7ecb7499b81dc80a3ba526e6d
ACR-3a6e2598b7714708baec743640c6e6c5
ACR-ab2a80c59bb44dc5af514e81633c75c6
ACR-26b5aece4c754b0bbe32dc25bac9f8c7
ACR-de7b40974e724993ac84d1b2cddb3990
ACR-8b9a1b59208c4fc78195beddfd4c5b0d
ACR-c110f8a0d3574c2b95da0531fe1616b3
 */
package org.sonarsource.sonarlint.core.serverapi.plugins;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.serverapi.MockWebServerExtensionWithProtobuf;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class PluginsApiTests {
  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();

  @RegisterExtension
  static MockWebServerExtensionWithProtobuf mockServer = new MockWebServerExtensionWithProtobuf();

  @Test
  void should_return_installed_plugins() {
    var underTest = new PluginsApi(mockServer.serverApiHelper());
    mockServer.addStringResponse("/api/plugins/installed", "{\"plugins\": [" +
      "{\"key\": \"pluginKey\", \"hash\": \"de5308f43260d357acc97712ce4c5475\", \"filename\": \"plugin-1.0.0.1234.jar\", \"sonarLintSupported\": true}" +
      "]}");

    var serverPlugins = underTest.getInstalled(new SonarLintCancelMonitor());

    assertThat(serverPlugins)
      .extracting("key", "hash", "filename", "sonarLintSupported")
      .containsOnly(tuple("pluginKey", "de5308f43260d357acc97712ce4c5475", "plugin-1.0.0.1234.jar", true));
  }

  @Test
  void should_return_plugin_content() {
    var underTest = new PluginsApi(mockServer.serverApiHelper());
    mockServer.addStringResponse("/api/plugins/download?plugin=pluginKey", "content");

    underTest.getPlugin("pluginKey", stream -> assertThat(stream).hasContent("content"), new SonarLintCancelMonitor());
  }

}
