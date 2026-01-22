/*
ACR-42a771bbfd664261b44c2caa309456b1
ACR-d0443e50347c4730a4fa7ba75ebfd426
ACR-74e67eda49244746ae7194ad3cd9df01
ACR-2fc7f09145ba410a86b0423b4eda6ceb
ACR-9adee17e517c494b9d89370395d2479a
ACR-17c79362d70d48a386c125918d091f94
ACR-29a41588c6314cf29423c150a7771a7b
ACR-c5659a525e7f4c37b87860cac7c1ffa9
ACR-157b139d78594d47a5d269e799995575
ACR-c8830b368c65499388677cddeac61fb7
ACR-b70007db3ba54f4298c998dfb5233e7d
ACR-e91071bc71424614aa795701e3145f55
ACR-21deb63cf7ac4e1cb3cfe0cff1fe9596
ACR-95c70efde8054222a16ff932b35595e6
ACR-c273333416cd4551bee9d3aa83df327c
ACR-b46d3736b7ee40c9afe7b06517b9db81
ACR-695acc8f16bc4a569d67edd7fee168d2
 */
package org.sonarsource.sonarlint.core.sync;

public class SonarProjectBranchesChangedEvent {
  private final String connectionId;
  private final String sonarProjectKey;

  public SonarProjectBranchesChangedEvent(String connectionId, String sonarProjectKey) {
    this.connectionId = connectionId;
    this.sonarProjectKey = sonarProjectKey;
  }

  public String getConnectionId() {
    return connectionId;
  }

  public String getSonarProjectKey() {
    return sonarProjectKey;
  }
}
