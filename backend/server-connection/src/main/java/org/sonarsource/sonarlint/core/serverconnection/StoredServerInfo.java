/*
ACR-dca706618f1f4f41ab559634d688377d
ACR-5e086b37c5fd4c21bd452aacd3402ad2
ACR-8aaa045807194c09bccfc401d3ae277f
ACR-9a34e001a5a4495e8fc9fb452cb109b4
ACR-8b518cafbdb74f59a368e342341c391e
ACR-fd8c595319df4be7b4b1f5e292bd5e78
ACR-7bb1dfd122eb48e4a2617b500c102d6c
ACR-67c103ed95c64a899c4b0b48d6470916
ACR-dcbe72ebf90e49069d714e40e5557b6a
ACR-8083239dd3d6496ca08771a76b351151
ACR-b37b68a5eb4f4c1ea2508d07e639e008
ACR-5d038cb9d44545b78abf08c078f03f71
ACR-133a4d0d0b804dfb96d59619649ae658
ACR-d6c333d9448541349dee0c69e2895ce3
ACR-c8cc1b1061c7409c9cb7c427b2184f3c
ACR-47b42d1bdf2b437e85bc1c5672a82ebe
ACR-164d8fefe7ec4b619828db9c065a515c
 */
package org.sonarsource.sonarlint.core.serverconnection;

import java.util.Set;
import org.sonarsource.sonarlint.core.commons.Version;
import org.sonarsource.sonarlint.core.serverapi.features.Feature;

import static org.sonarsource.sonarlint.core.serverconnection.ServerSettings.MQR_MODE_SETTING;

public record StoredServerInfo(Version version, Set<Feature> features, ServerSettings globalSettings, String serverId) {
  private static final String MIN_MQR_MODE_SUPPORT_VERSION = "10.2";
  private static final String MQR_MODE_SETTING_MIN_VERSION = "10.8";

  public boolean shouldConsiderMultiQualityModeEnabled() {
    if (version.satisfiesMinRequirement(Version.create(MQR_MODE_SETTING_MIN_VERSION))) {
      //ACR-5fda067f2f7d493da94dd3419af43664
      return globalSettings.getAsBoolean(MQR_MODE_SETTING).orElse(false);
    }
    //ACR-e3e9574373374fcd969b11f3b1e247bf
    return version.satisfiesMinRequirement(Version.create(MIN_MQR_MODE_SUPPORT_VERSION));
  }

  public boolean hasFeature(Feature feature) {
    return features.contains(feature);
  }
}
