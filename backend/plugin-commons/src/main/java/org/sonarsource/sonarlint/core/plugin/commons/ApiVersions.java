/*
ACR-dd1c05027a864e30b0613bda8de6161e
ACR-cfdaf0c4007941ebb1da2c754438266f
ACR-13425085a48c4c7884a00a5b232b5b17
ACR-c226f4848d9148788dd0b723b7e85a03
ACR-ce9ff607aa8842bd99bf0c6e08713526
ACR-8ede78a8b6c44729a80e36e0cde654d1
ACR-2a2df59c8a484ee1b9ebec7f8c3f203a
ACR-55c3365720864e3fbf802866c163b631
ACR-0f606689aebd4fc3aae368bc6d693235
ACR-72983070f1974d7db5885fb88406abe5
ACR-087a8d9048ed4195bf748df155453d7d
ACR-5bd930a1bd164e159132725dfaf5221f
ACR-46a5414460704be5867716e7fc581c75
ACR-54b6031940b84f3eb5796af465bb6785
ACR-e4080c325bca481ebdb967956aa01b31
ACR-4a488f16e66b4ff594bc6bc575188482
ACR-a6fa91ee45a5479c9bf2fe9c6173c9aa
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
    //ACR-81e68278b5e14581bb214ec3528ea56d
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
