/*
ACR-8ac2fc5d9de94b8788d1862594a4ed3d
ACR-52fe57eeb8114755b82de5bcfd5ddb68
ACR-d13e57b427d84c75a3e9d648e8b23562
ACR-5f5c26e740db48cd8177cb18fe111532
ACR-b0f02504d37c4d79a910054b2ecbec26
ACR-08d57147d8064c89a5528babdf19e779
ACR-6a6bdb1d76b24e68b6cc59b3bff89be3
ACR-c0a6f55de551407193089899dfeb14e4
ACR-c1f425b90eac448c836e639d1669ba80
ACR-8efba71d95f64f7abea943b615c2c091
ACR-5902938822a5407f9a7971caedd2d76e
ACR-d573c3a4a4c14974963982fd0561e716
ACR-717e2ec074af44a5ae7918cc1db94a11
ACR-ed677f1d3258478a8924eb8cf205edf8
ACR-012bc15544d9481e888290c2df6c73a8
ACR-0bcb2d6b4cc2422b9aabd9631c190c8c
ACR-36d373b9deb542bc8c13b14cacae53c6
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

  /*ACR-4263bab525c0480289f7f90ee9f5e273
ACR-3b97d18de17a4448988523895d879310
ACR-9eb9e97ccf06407abf707ff1888d2a1d
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

  /*ACR-e0361b0bcf9a422f9292486447f9e5bc
ACR-bd3d990d5aa0446a94f9f6cd42e344a0
ACR-767b02335d4349bd9d34ee595a266164
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

  /*ACR-9887617f2f0b4f74a5e77fa4c4274730
ACR-620b7c2d46fd4f4789fa0e4655c833a8
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

  /*ACR-a5194f29272944a3afc086d527c9f35f
ACR-a590105c63d1418c89a690e3f06bd6d4
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
