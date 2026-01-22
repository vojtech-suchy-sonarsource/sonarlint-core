/*
ACR-8572c6cb50f1421192391583b1b6a9f2
ACR-9154b07401554499ac73ba18e9d04f4f
ACR-fb9b864fc96c4ff29ac7f943cf4970e8
ACR-f9f836e8d0264b3182514cc60d4d434c
ACR-9c961d5b41464f30a841665929dcf15c
ACR-e005de32d22f4b17a0d374ae25b01555
ACR-c1e92e1bce42474d988621bc6cb8a33e
ACR-c99cbd30e56949ddb3c91c42f5f10409
ACR-2e823f9e715f4ddc9f6bbdbeac837529
ACR-2faa7636efeb4d3391d3d6f2758b9fa3
ACR-bb87303aa7054f0cb9b171c8b416e579
ACR-9ce55282c2e64a7b90869d56d56f8461
ACR-6b5cfc69da634f6f8681b18dcfdc065b
ACR-64820349dde24430ba2be8d88170cc60
ACR-2d0b65342cd645a8b7340619e09c4da3
ACR-6fe1c66643f34c90bacdeb2955e742e6
ACR-45af22f1e86c4560baace14961319aed
 */
package org.sonarsource.sonarlint.core.serverconnection;

import java.nio.file.Path;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.io.TempDir;
import org.sonarsource.sonarlint.core.commons.Version;
import org.sonarsource.sonarlint.core.commons.api.SonarLanguage;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.commons.storage.SonarLintDatabase;
import org.sonarsource.sonarlint.core.serverapi.ServerApi;
import testutils.MockWebServerExtensionWithProtobuf;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class PluginsSynchronizerTests {

  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();

  @RegisterExtension
  static MockWebServerExtensionWithProtobuf mockServer = new MockWebServerExtensionWithProtobuf();
  private PluginsSynchronizer underTest;

  /*ACR-699d38d665434bd08dab4632d3199b19
ACR-e2a7d89bdf2f4514aba18b36848903ad
ACR-6f1e1ca0f43c4d41b956285b67244e52
   */
  @Test
  void should_not_synchronize_sonar_text_pre_104(@TempDir Path dest) {
    mockServer.addStringResponse("/api/plugins/installed", "{\"plugins\": [" +
      "{\"key\": \"text\", \"hash\": \"de5308f43260d357acc97712ce4c5475\", \"filename\": \"sonar-text-plugin-1.2.3.4.jar\", \"sonarLintSupported\": true}," +
      "{\"key\": \"textenterprise\", \"hash\": \"de5308f43260d357acc97712ce4c5475\", \"filename\": \"sonar-text-enterprise-plugin-5.6.7.8.jar\", \"sonarLintSupported\": false}" +
      "]}");

    var databaseService = mock(SonarLintDatabase.class);
    underTest = new PluginsSynchronizer(Set.of(SonarLanguage.SECRETS), new ConnectionStorage(dest, "connectionId", databaseService), Set.of("text"));
    underTest.synchronize(new ServerApi(mockServer.serverApiHelper()), Version.create("10.3"), new SonarLintCancelMonitor());

    assertThat(dest.resolve("636f6e6e656374696f6e4964/plugins/plugin_references.pb")).exists();
    assertThat(dest.resolve("636f6e6e656374696f6e4964/plugins/sonar-text-plugin-1.2.3.4.jar")).doesNotExist();
  }

  /*ACR-60f0fbf1ade846009b62e1d4c02ff2c8
ACR-9284753f91474979bfcdc16a423f276d
ACR-768b61b3fa1e4440be172ce7150fe7e6
   */
  @Test
  void should_synchronize_sonar_text_post_103(@TempDir Path dest) {
    mockServer.addStringResponse("/api/plugins/installed", "{\"plugins\": [" +
      "{\"key\": \"text\", \"hash\": \"de5308f43260d357acc97712ce4c5475\", \"filename\": \"sonar-text-plugin-2.3.4.5.jar\", \"sonarLintSupported\": true}," +
      "{\"key\": \"textenterprise\", \"hash\": \"de5308f43260d357acc97712ce4c5475\", \"filename\": \"sonar-text-enterprise-plugin-5.6.7.8.jar\", \"sonarLintSupported\": true}" +
      "]}");
    mockServer.addStringResponse("/api/plugins/download?plugin=text", "content-text");
    mockServer.addStringResponse("/api/plugins/download?plugin=textenterprise", "content-textenterprise");
    var databaseService = mock(SonarLintDatabase.class);

    underTest = new PluginsSynchronizer(Set.of(SonarLanguage.SECRETS), new ConnectionStorage(dest, "connectionId", databaseService), Set.of("text"));
    underTest.synchronize(new ServerApi(mockServer.serverApiHelper()), Version.create("10.4"), new SonarLintCancelMonitor());

    assertThat(dest.resolve("636f6e6e656374696f6e4964/plugins/plugin_references.pb")).exists();
    assertThat(dest.resolve("636f6e6e656374696f6e4964/plugins/sonar-text-plugin-2.3.4.5.jar")).exists();
    assertThat(dest.resolve("636f6e6e656374696f6e4964/plugins/sonar-text-enterprise-plugin-5.6.7.8.jar")).exists();
  }

  /*ACR-2d0812201f6e48b6a047a587afef7bf4
ACR-09a74856d71f44de81f0baddaab40bc7
   */
  @Test
  void should_synchronize_sonar_go_enterprise_in_2025_2(@TempDir Path dest) {
    mockServer.addStringResponse("/api/plugins/installed", "{\"plugins\": [" +
      "{\"key\": \"text\", \"hash\": \"de5308f43260d357acc97712ce4c5475\", \"filename\": \"sonar-text-plugin-2.3.4.5.jar\", \"sonarLintSupported\": true}," +
      "{\"key\": \"textenterprise\", \"hash\": \"de5308f43260d357acc97712ce4c5475\", \"filename\": \"sonar-text-enterprise-plugin-5.6.7.8.jar\", \"sonarLintSupported\": true}," +
      "{\"key\": \"goenterprise\", \"hash\": \"de5308f43260d357acc97712ce4c5475\", \"filename\": \"sonar-go-enterprise-plugin-1.2.3.4.jar\", \"sonarLintSupported\": false}" +
      "]}");
    mockServer.addStringResponse("/api/plugins/download?plugin=text", "content-text");
    mockServer.addStringResponse("/api/plugins/download?plugin=textenterprise", "content-textenterprise");
    mockServer.addStringResponse("/api/plugins/download?plugin=goenterprise", "content-goenterprise");
    var databaseService = mock(SonarLintDatabase.class);

    underTest = new PluginsSynchronizer(Set.of(SonarLanguage.SECRETS, SonarLanguage.GO), new ConnectionStorage(dest, "connectionId", databaseService), Set.of("text", "go"));
    underTest.synchronize(new ServerApi(mockServer.serverApiHelper()), Version.create("2025.2"), new SonarLintCancelMonitor());

    assertThat(dest.resolve("636f6e6e656374696f6e4964/plugins/plugin_references.pb")).exists();
    assertThat(dest.resolve("636f6e6e656374696f6e4964/plugins/sonar-text-plugin-2.3.4.5.jar")).exists();
    assertThat(dest.resolve("636f6e6e656374696f6e4964/plugins/sonar-text-enterprise-plugin-5.6.7.8.jar")).exists();
    assertThat(dest.resolve("636f6e6e656374696f6e4964/plugins/sonar-go-enterprise-plugin-1.2.3.4.jar")).exists();
  }

  @Test
  void should_not_synchronize_sonar_go_enterprise_in_2025_2_if_language_not_enabled(@TempDir Path dest) {
    mockServer.addStringResponse("/api/plugins/installed", "{\"plugins\": [" +
      "{\"key\": \"goenterprise\", \"hash\": \"de5308f43260d357acc97712ce4c5475\", \"filename\": \"sonar-go-enterprise-plugin-1.2.3.4.jar\", \"sonarLintSupported\": false}" +
      "]}");
    mockServer.addStringResponse("/api/plugins/download?plugin=goenterprise", "content-goenterprise");
    var databaseService = mock(SonarLintDatabase.class);

    underTest = new PluginsSynchronizer(Set.of(SonarLanguage.SECRETS), new ConnectionStorage(dest, "connectionId", databaseService), Set.of("text", "go"));
    underTest.synchronize(new ServerApi(mockServer.serverApiHelper()), Version.create("2025.2"), new SonarLintCancelMonitor());

    assertThat(dest.resolve("636f6e6e656374696f6e4964/plugins/plugin_references.pb")).exists();
    assertThat(dest.resolve("636f6e6e656374696f6e4964/plugins/sonar-go-enterprise-plugin-1.2.3.4.jar")).doesNotExist();
  }

  /*ACR-fe8a12776d95406593de618ecde38198
ACR-ea83f5b3d79a45f2af9528c27f5639e5
   */
  @Test
  void should_synchronize_sonar_go_in_2025_3(@TempDir Path dest) {
    mockServer.addStringResponse("/api/plugins/installed", "{\"plugins\": [" +
      "{\"key\": \"text\", \"hash\": \"de5308f43260d357acc97712ce4c5475\", \"filename\": \"sonar-text-plugin-2.3.4.5.jar\", \"sonarLintSupported\": true}," +
      "{\"key\": \"textenterprise\", \"hash\": \"de5308f43260d357acc97712ce4c5475\", \"filename\": \"sonar-text-enterprise-plugin-5.6.7.8.jar\", \"sonarLintSupported\": true}," +
      "{\"key\": \"go\", \"hash\": \"de5308f43260d357acc97712ce4c5475\", \"filename\": \"sonar-go-plugin-1.2.3.4.jar\", \"sonarLintSupported\": true}" +
      "]}");
    mockServer.addStringResponse("/api/plugins/download?plugin=text", "content-text");
    mockServer.addStringResponse("/api/plugins/download?plugin=textenterprise", "content-textenterprise");
    mockServer.addStringResponse("/api/plugins/download?plugin=go", "content-go");
    var databaseService = mock(SonarLintDatabase.class);

    underTest = new PluginsSynchronizer(Set.of(SonarLanguage.SECRETS, SonarLanguage.GO), new ConnectionStorage(dest, "connectionId", databaseService), Set.of("text", "go"));
    underTest.synchronize(new ServerApi(mockServer.serverApiHelper()), Version.create("2025.2"), new SonarLintCancelMonitor());

    assertThat(dest.resolve("636f6e6e656374696f6e4964/plugins/plugin_references.pb")).exists();
    assertThat(dest.resolve("636f6e6e656374696f6e4964/plugins/sonar-text-plugin-2.3.4.5.jar")).exists();
    assertThat(dest.resolve("636f6e6e656374696f6e4964/plugins/sonar-text-enterprise-plugin-5.6.7.8.jar")).exists();
    assertThat(dest.resolve("636f6e6e656374696f6e4964/plugins/sonar-go-plugin-1.2.3.4.jar")).exists();
  }

  @Test
  void should_not_synchronize_sonar_go_in_2025_3_if_language_not_enabled(@TempDir Path dest) {
    mockServer.addStringResponse("/api/plugins/installed", "{\"plugins\": [" +
      "{\"key\": \"go\", \"hash\": \"de5308f43260d357acc97712ce4c5475\", \"filename\": \"sonar-go-plugin-1.2.3.4.jar\", \"sonarLintSupported\": true}" +
      "]}");
    mockServer.addStringResponse("/api/plugins/download?plugin=go", "content-go");
    var databaseService = mock(SonarLintDatabase.class);

    underTest = new PluginsSynchronizer(Set.of(SonarLanguage.SECRETS), new ConnectionStorage(dest, "connectionId", databaseService), Set.of("text", "go"));
    underTest.synchronize(new ServerApi(mockServer.serverApiHelper()), Version.create("2025.2"), new SonarLintCancelMonitor());

    assertThat(dest.resolve("636f6e6e656374696f6e4964/plugins/plugin_references.pb")).exists();
    assertThat(dest.resolve("636f6e6e656374696f6e4964/plugins/sonar-go-plugin-1.2.3.4.jar")).doesNotExist();
  }

  @Test
  void should_synchronize_csharp_enterprise_if_language_enabled(@TempDir Path dest) {
    mockServer.addStringResponse("/api/plugins/installed", "{\"plugins\": [" +
      "{\"key\": \"csharpenterprise\", \"hash\": \"de5308f43260d357acc97712ce4c5475\", \"filename\": \"sonar-csharpenterprise-plugin-1.2.3.4.jar\", \"sonarLintSupported\": false}"
      +
      "]}");
    mockServer.addStringResponse("/api/plugins/download?plugin=csharpenterprise", "content-go");
    var databaseService = mock(SonarLintDatabase.class);

    underTest = new PluginsSynchronizer(Set.of(SonarLanguage.CS), new ConnectionStorage(dest, "connectionId", databaseService), Set.of());
    underTest.synchronize(new ServerApi(mockServer.serverApiHelper()), Version.create("2025.2"), new SonarLintCancelMonitor());

    assertThat(dest.resolve("636f6e6e656374696f6e4964/plugins/plugin_references.pb")).exists();
    assertThat(dest.resolve("636f6e6e656374696f6e4964/plugins/sonar-csharpenterprise-plugin-1.2.3.4.jar")).exists();
  }

  @Test
  void should_not_synchronize_csharp_enterprise_if_language_disabled(@TempDir Path dest) {
    mockServer.addStringResponse("/api/plugins/installed", "{\"plugins\": [" +
      "{\"key\": \"csharpenterprise\", \"hash\": \"de5308f43260d357acc97712ce4c5475\", \"filename\": \"sonar-csharpenterprise-plugin-1.2.3.4.jar\", \"sonarLintSupported\": false}"
      +
      "]}");
    mockServer.addStringResponse("/api/plugins/download?plugin=csharpenterprise", "content-csharp");
    var databaseService = mock(SonarLintDatabase.class);

    underTest = new PluginsSynchronizer(Set.of(SonarLanguage.GO), new ConnectionStorage(dest, "connectionId", databaseService), Set.of());
    underTest.synchronize(new ServerApi(mockServer.serverApiHelper()), Version.create("2025.2"), new SonarLintCancelMonitor());

    assertThat(dest.resolve("636f6e6e656374696f6e4964/plugins/plugin_references.pb")).exists();
    assertThat(dest.resolve("636f6e6e656374696f6e4964/plugins/sonar-csharpenterprise-plugin-1.2.3.4.jar")).doesNotExist();
  }

  @Test
  void should_synchronize_vbnet_enterprise_if_language_enabled(@TempDir Path dest) {
    mockServer.addStringResponse("/api/plugins/installed", "{\"plugins\": [" +
      "{\"key\": \"vbnetenterprise\", \"hash\": \"de5308f43260d357acc97712ce4c5475\", \"filename\": \"sonar-vbnetenterprise-plugin-1.2.3.4.jar\", \"sonarLintSupported\": false}" +
      "]}");
    mockServer.addStringResponse("/api/plugins/download?plugin=vbnetenterprise", "content-vb");
    var databaseService = mock(SonarLintDatabase.class);
    underTest = new PluginsSynchronizer(Set.of(SonarLanguage.VBNET), new ConnectionStorage(dest, "connectionId", databaseService), Set.of());
    underTest.synchronize(new ServerApi(mockServer.serverApiHelper()), Version.create("2025.2"), new SonarLintCancelMonitor());

    assertThat(dest.resolve("636f6e6e656374696f6e4964/plugins/plugin_references.pb")).exists();
    assertThat(dest.resolve("636f6e6e656374696f6e4964/plugins/sonar-vbnetenterprise-plugin-1.2.3.4.jar")).exists();
  }

  @Test
  void should_not_synchronize_vbnet_enterprise_if_language_disabled(@TempDir Path dest) {
    mockServer.addStringResponse("/api/plugins/installed", "{\"plugins\": [" +
      "{\"key\": \"vbnetenterprise\", \"hash\": \"de5308f43260d357acc97712ce4c5475\", \"filename\": \"sonar-vbnetenterprise-plugin-1.2.3.4.jar\", \"sonarLintSupported\": false}" +
      "]}");
    mockServer.addStringResponse("/api/plugins/download?plugin=vbnetenterprise", "content-go");
    var databaseService = mock(SonarLintDatabase.class);

    underTest = new PluginsSynchronizer(Set.of(SonarLanguage.GO), new ConnectionStorage(dest, "connectionId", databaseService), Set.of());
    underTest.synchronize(new ServerApi(mockServer.serverApiHelper()), Version.create("2025.2"), new SonarLintCancelMonitor());

    assertThat(dest.resolve("636f6e6e656374696f6e4964/plugins/plugin_references.pb")).exists();
    assertThat(dest.resolve("636f6e6e656374696f6e4964/plugins/sonar-vbnetenterprise-plugin-1.2.3.4.jar")).doesNotExist();
  }
}
