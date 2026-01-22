/*
ACR-e7a666d6eb4c4f129bfbac34cd7f0a55
ACR-d9591c1232594e49b82f2d25e910fc8a
ACR-9ade1f73b8a3412b8e45336d518b31d4
ACR-7f98b4fe6aab41b48ba9034e4333a3ae
ACR-b4610736a778492abae07c0b8605fd73
ACR-dfaf9a2d093349b3b3598fff5e9dca0c
ACR-c3b6f297c79c4e26a4a506bf51593d78
ACR-563e29923dd8480dbb164f7aeae52195
ACR-6e6f43d7a94f44ec99df833de426b0a3
ACR-3d23077256224c7db5cf5dec5e555e35
ACR-ab8794fb32c245808f031ab361d259fc
ACR-2ba6bc44ede040f4bb8702ce00e1f7da
ACR-baf40e748f1d4806a4d8610efe8121ce
ACR-0577c3818cba4150871a420d696e3b75
ACR-2e8cfe0830f44f508cf2c4cb435f4a98
ACR-37a5b3abfb86460ea66ad9e1023c813c
ACR-1e20995f7d074fecbbde26aa1b3a24c1
 */
package org.sonarsource.sonarlint.core.serverapi.organization;

import org.sonarsource.sonarlint.core.serverapi.proto.sonarcloud.ws.Organizations.Organization;

public class ServerOrganization {
  private final String key;
  private final String name;
  private final String description;

  public ServerOrganization(Organization org) {
    this.key = org.getKey();
    this.name = org.getName();
    this.description = org.getDescription();
  }

  public String getKey() {
    return key;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }
}
