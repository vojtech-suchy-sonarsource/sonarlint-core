/*
ACR-bcf888713e5643bc85c54e01999cdda2
ACR-3240b033dd694553b432bee80117596b
ACR-0a2dbace248641a18909ef36ac4b9607
ACR-9ab764b1bc604bb692b8fbc69c9073f8
ACR-79360381dd3f44a28bc1b0b82a41f6a9
ACR-00c152a6b14244cb935fbfa778b0ac85
ACR-b35255d960ea429e8aa0e7e86f0b5835
ACR-266e18c329434bc78d277b77e7b03017
ACR-7102112e96e24555b80fe1346eb089dc
ACR-9b0597724cc14980aa145d259024f42c
ACR-4cbae846089744b99aabadf7ef883c79
ACR-f73acfe0d23c4874bee52833ca203f2d
ACR-ef22ddc486f244cbbd2593f3c0151ea0
ACR-732d2a562284471da414ec48d2deab32
ACR-16502685aa1f455dab525ea534424703
ACR-9e6a9577940d4f97aa377970d663304e
ACR-e0bd77aa9a7242dd908b00ec359db7eb
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
