/*
ACR-75af732bf5c54a46b339f9bca2500ad8
ACR-00b0c190bf944fffa7fd39cfe9d25189
ACR-2e5b49baeca14f119f102fc330e9fd21
ACR-d669e68e0db6437493eb5f642aab86fb
ACR-22260cb7fdca4fdb850fdd5df8ab0986
ACR-bcdd700bc37b4620b8758561bbef776d
ACR-1c799b9ef622442fafa570daada5502f
ACR-c2240dbefdea4bdf85a5dfeda72105bb
ACR-075f308a3ca543949a66965115085d80
ACR-045c8bb523f449889733231bf037a968
ACR-a9d91e63c32048128eafc82c55bd77b0
ACR-d63efaa9dee643af94fb78ac4dd371c2
ACR-0f18a0bb5c8b48a7a420055438131e44
ACR-3f1009e7e5b6497eb62bf0251cd12f32
ACR-d6b3d99d1f904fcd97dc37e643d2508a
ACR-a0baaa42a00348e980824428b115fe8b
ACR-be6bef76519f41aa93bb381945a49afe
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.validate;

public class ValidateConnectionResponse {

  private final boolean success;

  private final String message;

  public ValidateConnectionResponse(boolean success, String message) {
    this.success = success;
    this.message = message;
  }

  public boolean isSuccess() {
    return success;
  }

  public String getMessage() {
    return message;
  }
}
