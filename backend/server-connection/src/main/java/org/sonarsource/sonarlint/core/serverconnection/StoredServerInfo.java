/*
ACR-05b76a6b1791472680974b23f03d5a3c
ACR-75d60d355e964854886d805b0ae41600
ACR-5fc98375e00a4452afc47872ad755cb9
ACR-7d679a2cc124408b863676fd0f891c16
ACR-d06e7fa230a04f9fa1d69740e6103751
ACR-4dad3a72e42c4cd0ac6a36dc311b84ab
ACR-f4d3d62d884f44f3af2dd3ab004e7708
ACR-41830fee376b4010905b17f7f7762915
ACR-0ae2667175c04ab7b1c98e2fe54db33f
ACR-70bf6d18865b4b4498399081ccf231ce
ACR-789e53c9b0f34917bd5d2ac37a29ea5b
ACR-ab8a10b280e447029d58c0f112d5394d
ACR-0c03d2ee5a4e4fb2adc23965f2ef0665
ACR-3ac65dac094443ba814c4010694a7185
ACR-909cd243effa4350b1cf8801bcf0e4f0
ACR-59b2e40ba80a47f892f27fd4a3f5c46b
ACR-82552206a7014ed08e3ff1c1af44eed1
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
      //ACR-395ef48eb3c54fbeae3e1dc33967e193
      return globalSettings.getAsBoolean(MQR_MODE_SETTING).orElse(false);
    }
    //ACR-63887b322ea648338a0bf85ddbb4e517
    return version.satisfiesMinRequirement(Version.create(MIN_MQR_MODE_SUPPORT_VERSION));
  }

  public boolean hasFeature(Feature feature) {
    return features.contains(feature);
  }
}
