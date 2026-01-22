/*
ACR-1910740146e048ae8c95a81635b7ba58
ACR-4b1f05e2f6864b4fa4317bc523c15f70
ACR-94c78d87b5f548d396cdfeb79e9d4c65
ACR-dda890b58464436fa04464a9f47a2559
ACR-d5a2b8ec7894465c90d25bef3d0c155c
ACR-84636cf0c88b4225b1357d451812fa76
ACR-00b95a7fa2f344bd8b5dedff9eaef331
ACR-39ddca20bbe64d6093e069c5cacf6523
ACR-3a10507faab94a64bd834ce3b2ef4de6
ACR-d78ec600fe8e45b39c84e1d923dc102f
ACR-21eca69287db48dcb1593890f8663abc
ACR-25c2d79639a04599b7cc5862e3f55161
ACR-f6f90bc5ce6e4cd893fc02c4b6cbeed0
ACR-029a9790df82461e9ad3d8f32d696fd6
ACR-c8d125f402b6490ea6e7bfec3054b614
ACR-fd0a97b320bb422f9f3bb85c5b92e8d5
ACR-95f53d6b18b840e799d22782fc314828
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry;

public class ToggleIdeLabsEnablementParams {

  private final boolean newValue;

  public ToggleIdeLabsEnablementParams(boolean newValue) {
    this.newValue = newValue;
  }

  public boolean getNewValue() {
    return newValue;
  }
}
