/*
ACR-86f974a0ba2e428c894b43e519d97f76
ACR-9b71ee796311447180a116bf731e5552
ACR-c6854c553bca49dd938a7bdee25d9788
ACR-a604c52a4d8b409489b821e5ae6526a6
ACR-55093db27bbc4654885fa6bcd7cf3f42
ACR-afa31c4a4ecb4cb6b4a305c47d1d1abe
ACR-5cf9a35bcd374fb19a3b83f1138f1324
ACR-520888cfccb147fd922626a4ec288a1c
ACR-28e27dbcb23e44a1bdc46696ac36884e
ACR-7ddbe02b823a4b63b57087e95c3b758a
ACR-eac5f3d483794c61aa23b9df3fc21cb5
ACR-3fbca10715b64712985dca16f9460d81
ACR-8cef487f824a47858aea9455515e67f6
ACR-c4ec6192845d40b7b9fbc9d43a65e8bb
ACR-e8e977f551cb40aa8ca016392b68454e
ACR-47f74f84507d4caf961564be5fb7eaa2
ACR-06805318a93541198ee78847b51d362a
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.org;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

public class GetOrganizationResponse {

  @Nullable
  private final OrganizationDto organization;

  public GetOrganizationResponse(@Nullable OrganizationDto organization) {
    this.organization = organization;
  }

  @CheckForNull
  public OrganizationDto getOrganization() {
    return organization;
  }
}
