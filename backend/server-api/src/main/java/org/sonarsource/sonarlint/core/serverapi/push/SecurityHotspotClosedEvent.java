/*
ACR-4dfa48bc9100402fb1b5a0e192df771b
ACR-fea372218cdf4110839fae7b646607aa
ACR-221b4c506c9f4e39838fecbd92c7a48c
ACR-2093b175b9c540f7b41bfab4a5253ccc
ACR-ad3795f3d4e04b1eaa5d140bb7abceb8
ACR-7d9b1b4b44714de3a9ee6716ee76e965
ACR-b9a795b4725449f2a0133e4650c4d183
ACR-92c843b0a945482aad907c5123c229da
ACR-a62f38399f9c4b03bbb7b72c21c248c9
ACR-a1d56ab398c847a09ad111ddcc60f2d3
ACR-361b812c17574b15b10a2f26bdacb6c9
ACR-548f0881b26448399501f0b8ba9b33ec
ACR-5f4efbdba2b949738e55d1b58051de61
ACR-a6ce8051ce654d4da31321e87d51bd83
ACR-25094c620880496e937bbe81149f4b6c
ACR-ea79ff3b2bed4362a6cd0a8c1d0177a0
ACR-4b2d01b0f74a49b0ac39c42be8de12a4
 */
package org.sonarsource.sonarlint.core.serverapi.push;

import java.nio.file.Path;

public class SecurityHotspotClosedEvent implements ServerHotspotEvent {
  private final String projectKey;
  private final String hotspotKey;
  private final Path filePath;

  public SecurityHotspotClosedEvent(String projectKey, String hotspotKey, Path filePath) {
    this.projectKey = projectKey;
    this.hotspotKey = hotspotKey;
    this.filePath = filePath;
  }
  @Override
  public String getProjectKey() {
    return projectKey;
  }
  public String getHotspotKey() {
    return hotspotKey;
  }
  @Override
  public Path getFilePath() {
    return filePath;
  }
}
