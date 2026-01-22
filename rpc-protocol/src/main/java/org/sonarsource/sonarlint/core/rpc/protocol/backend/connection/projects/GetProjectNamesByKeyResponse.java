/*
ACR-b429309586f746648137d9e75c710649
ACR-6ffb20738fbc4991817b0bff6a3a7f40
ACR-4c182a28a33f4e16b63dcd406bedfdee
ACR-8ee986b2bfcb45f880b74ddc9f5f5cdc
ACR-dcce6e653a034dd0ae63431febfa1c68
ACR-dd172e3070234efeb7500837afb0b5aa
ACR-356f020e82b94d57946a67b78b28226c
ACR-8c01da2fc0d84b12ba5a761f8a384d48
ACR-4d05f039acc24cc38bf3b4ca5ecadde4
ACR-5146d14a4289442392b8e22f10d87a30
ACR-ab43088f5f5c498687bb7a3300780d30
ACR-7a4c28cb983543b29604684f0e5e9a5e
ACR-3077d776ba0847039daad2d4251fd98a
ACR-435a108b861d47b18771014d41b27333
ACR-46b1ebbda18a41729696d09d22d85318
ACR-6d08314b932249c19b1e90c384fdadf5
ACR-83c2216cfcf0423a9ccc4aed97b0953e
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.projects;

import java.util.Map;

public class GetProjectNamesByKeyResponse {

  private final Map<String, String> projectNamesByKey;

  public GetProjectNamesByKeyResponse(Map<String, String> projectNamesByKey) {
    this.projectNamesByKey = projectNamesByKey;
  }

  public Map<String, String> getProjectNamesByKey() {
    return projectNamesByKey;
  }
}
