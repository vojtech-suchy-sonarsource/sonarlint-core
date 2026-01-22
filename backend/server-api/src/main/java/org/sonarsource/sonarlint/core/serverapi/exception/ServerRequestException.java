/*
ACR-35d2172be2ed40549d7533d3a79fc5a2
ACR-614772d6e33449eda381b784e3755b2c
ACR-0c3db5ee94c34324b54255587d93ac1c
ACR-d21e8c13b70b47c7ba4f43865afe8478
ACR-021bed5e51f64ee38ba9eaae9ce7c35a
ACR-9116af40ab4542f091f93a7e5fdba250
ACR-34a81f1348bd4cc68e950f4ef83a27e7
ACR-4aca998c45664d63979b6d0454f5890b
ACR-37f420cd68e64056861856426f563670
ACR-0e21323fbad74449934ae7bf78fa1611
ACR-b708186f218d4376ab46820ecb2843f7
ACR-fcf70499f5e349339a1bb56c163e3a25
ACR-838da753e5bb4b62a5bca638739d3279
ACR-4535b56fd6084877a6f014dd5774cb54
ACR-4784e6598e77437cb4684d2eb6f431f0
ACR-4bfe1c3204fd44fba1df24e91a75646d
ACR-f544bbfc7af54593a782bab3f094a308
 */
package org.sonarsource.sonarlint.core.serverapi.exception;

import org.sonarsource.sonarlint.core.commons.SonarLintException;

public class ServerRequestException extends SonarLintException {
  public ServerRequestException(String message) {
    super(message);
  }

  public ServerRequestException(String message, Throwable cause) {
    super(message, cause);
  }
}
