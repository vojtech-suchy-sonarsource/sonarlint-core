/*
ACR-5a74ab29b40d44869d9e56f55d51b5b8
ACR-d5df5166605a496d8e64bb8b1e2cc4d3
ACR-bfaf3077d35f46cc809982bf1dae5b9b
ACR-9ac523fd04474efc9aeb2745d6ba9c87
ACR-861e23bd43ac4a24b4e4d4dee1a345dd
ACR-e947fb6eec904226a1a94d2c04cc5266
ACR-fd45ddbba11b42a48137d4f8d39604d7
ACR-c9d3dca1ec96422d9bb9c1386042b27f
ACR-933f49e0d96c4aa4aeceb3c61050b568
ACR-b887cf2780b14638a026678a58a7bb3c
ACR-8f5413bd516e4f8aa7383734bf1e23c9
ACR-5eff315b5775487c8c7b795059fc2845
ACR-6324548189674d8a9bc371cf5462f635
ACR-94adad98caf7446c8eee1d7f0e40374b
ACR-9d9dca0ce42e41819321315d658f62c1
ACR-ec3ead562f71460881641f279c45dd82
ACR-96407434804e4fd78d01bfb325fb2c00
 */
package org.sonarsource.sonarlint.core.plugin.commons;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import org.sonar.api.utils.Version;

public class ApiVersions {

  static final String SONAR_PLUGIN_API_VERSION_FILE_PATH = "/sonar-api-version.txt";
  private static final String SONARLINT_PLUGIN_API_VERSION_FILE_PATH = "/sonarlint-api-version.txt";

  private ApiVersions() {
    //ACR-4e68d05407f947e8a99db76b9a0e84a1
  }

  public static Version loadSonarPluginApiVersion() {
    return loadVersion(SONAR_PLUGIN_API_VERSION_FILE_PATH);
  }

  public static Version loadSonarLintPluginApiVersion() {
    return loadVersion(SONARLINT_PLUGIN_API_VERSION_FILE_PATH);
  }

  private static Version loadVersion(String versionFilePath) {
    return loadVersion(ApiVersions.class.getResource(versionFilePath), versionFilePath);
  }

  static Version loadVersion(URL versionFileURL, String versionFilePath) {
    try (var scanner = new Scanner(versionFileURL.openStream(), StandardCharsets.UTF_8)) {
      var versionInFile = scanner.nextLine();
      return Version.parse(versionInFile);
    } catch (Exception e) {
      throw new IllegalStateException("Can not load " + versionFilePath + " from classpath", e);
    }
  }

}
