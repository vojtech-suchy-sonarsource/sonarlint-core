/*
ACR-49b1741646ea4707bfebc09a0f1ef069
ACR-81b5a102fbf34519bd0144776a9bfed6
ACR-fa7c5b2e36f143cd8121a81172f1d9b0
ACR-cb3c94ed22554f86bf39e17606d8c52a
ACR-65db9ef6ca564d45ab1283bc62c9b309
ACR-e294e85d6d8c448e94d0cbebd3e75bd9
ACR-199822115fbc4997b32db89a2009317f
ACR-f09fae997a0a415d859e4279a882ba88
ACR-f3263ef6cbd941caa2a39ddf398c572a
ACR-35a6fd21ceb44dbaae127c3cd38e29e1
ACR-a2e6aacdee0b412b94583c03d78cc477
ACR-c97775951b4c4aa8aa6bdbb0a80690b0
ACR-30567f9bd8884e9ca741a7588b6f4318
ACR-bd06334684714a82b7a299331633d964
ACR-8200f701af9842a58c4d36dd74b2aaeb
ACR-90e88328a1f54ef7b533aac63838715a
ACR-35edf77152e64983a954779794309b54
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
