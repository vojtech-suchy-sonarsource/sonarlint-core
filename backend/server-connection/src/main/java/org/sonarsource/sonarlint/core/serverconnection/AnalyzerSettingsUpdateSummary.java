/*
ACR-d60553272ff24ee286d04ab2eed1121b
ACR-c37694d4b3ac4d2c876f6f126680211c
ACR-04a9244d21fc4d029e567cf5f3b484c6
ACR-cff267e3a0ce423c96411fb4b54b0e64
ACR-5eb7fa6f787040048678161ac327428b
ACR-668a5f76a85446df83cd751b43210660
ACR-4d4f960f2f83434e925a7b295e8a9663
ACR-1a23346a37aa44a6bbf18c29b15682f4
ACR-a8927e47e52b41a3a7f7c861519e976a
ACR-02ee6a811744412eb42e87d2110345aa
ACR-74d4c6fc7e1d40dc8e5c75099a935fb7
ACR-5ac1c835ae574d349f7db5678d715d15
ACR-eeffdc6b6317463c8b7217e5e7679969
ACR-b74b24052dcb411dafc43f2007bffaac
ACR-b539a094659c49b0b569460fc945b190
ACR-4976289e4bd14aeab6ed338c0e2b689e
ACR-d1938f8a6301439485a5f6822d0238c5
 */
package org.sonarsource.sonarlint.core.serverconnection;

import java.util.Map;

public class AnalyzerSettingsUpdateSummary {

  private final Map<String, String> updatedSettingsValueByKey;

  public AnalyzerSettingsUpdateSummary(Map<String, String> updatedSettingsValueByKey) {
    this.updatedSettingsValueByKey = updatedSettingsValueByKey;
  }

  public Map<String, String> getUpdatedSettingsValueByKey() {
    return updatedSettingsValueByKey;
  }
}
