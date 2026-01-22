/*
ACR-4a863312277341a582498daf56c58e7e
ACR-18ba561c7cac49c2a441e1574971b97f
ACR-e816ada1edea4233be3977969172605f
ACR-10f1f6ee6b72418cbb0b3fd78d5d08ce
ACR-d4bc6957134745f384a0223a5d27641a
ACR-8f26144ba48443bc84c0f95df22d08a0
ACR-ac0b0c01a3b04be0b9412200fcb80560
ACR-8a6ae9178d464ccab20210cb08d32576
ACR-9f82e63d723d4887a35bd630d4940ad7
ACR-8892b8bd270f460e94e7ad3f4b81f7c8
ACR-4efb6b4729984779a078597d84b3f6d4
ACR-d8913df27f284d8988f5b6493ae4c7d3
ACR-e81563386756486f86c4b2f8602d641b
ACR-2a8b8b1e42674d4790835dc1e6f61efe
ACR-6ab83dee9cab49c4a9e8847f073c6414
ACR-961cc5d93c614e94bb6fac54bba5cc10
ACR-edd1f457c6ea47e8ae29273426b83772
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
