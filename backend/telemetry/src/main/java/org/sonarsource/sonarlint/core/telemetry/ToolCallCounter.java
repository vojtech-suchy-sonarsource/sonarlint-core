/*
ACR-5e0110bd76fe4a769964455f0e774fb1
ACR-25dc1996ce874a668830d9cd57705d2e
ACR-5f591a4482294779a76ee18508299f3a
ACR-31b3e0df01074a539c731c9492b6058b
ACR-80f8b31b1e1d476792e50c2e509657f8
ACR-510baa247b974624a740113b9f986fb4
ACR-67e6ec67da824f38b73dfae056554c9c
ACR-db3d8804d7fd45c1ad79f8c403ca31e8
ACR-869a82eb70954847907e9f98e5ea6cf5
ACR-81dae9ecd3964e598f1b6db2e3e09480
ACR-957168c581af47b99338b94e188bdac0
ACR-01e02d15da5f4a178018af03fc78aa46
ACR-7958afd465bc4e469167452e117bddd0
ACR-7e9e169079174169a2d62b47a80e7b7c
ACR-3119ed6a876840458171c02fcc2d7daa
ACR-a3c085c2bcf84340809494c1b016856f
ACR-97f73145ae5d4c7aae2177ebf29cc8d2
 */
package org.sonarsource.sonarlint.core.telemetry;

public class ToolCallCounter {
  private int success;
  private int error;

  public ToolCallCounter() {
  }

  public ToolCallCounter(int success, int error) {
    this.success = success;
    this.error = error;
  }

  public void incrementCount(boolean succeeded) {
    if (succeeded) {
      success++;
    } else {
      error++;
    }
  }

  public int getSuccess() {
    return success;
  }

  public int getError() {
    return error;
  }
}
