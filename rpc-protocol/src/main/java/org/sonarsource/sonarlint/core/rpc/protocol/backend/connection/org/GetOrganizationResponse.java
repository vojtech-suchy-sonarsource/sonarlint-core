/*
ACR-16d386932fcb4faf81d7747e5a85b7bd
ACR-f07a215b2c244e84a2103bd3f308d103
ACR-601c908d26e54d02af57b558a7bf8d11
ACR-5c92b8d13a1a4a97a9ebf57d9b946c4b
ACR-34c7c8da4ce440899310c7219de30547
ACR-d37ee41fe6734801ba557ebd03220f80
ACR-3c1fb0e94cfa48d5a6b11187a83df3d1
ACR-e891217c82b7421184d2da69d1ea3587
ACR-bc38babca1264e038dcb56d500a504a0
ACR-4d7d9df03ffb419ba9751a5efc2bcbe7
ACR-ceb17225488d4d918a17170b610d0ec6
ACR-bc875148f8c9400a86441b703931594b
ACR-18dc43e2d319485e90506ab49c07dcff
ACR-53ce10ef005149d282911c7ac59ea4d0
ACR-de81ba3a37be44eb9c0ae32a4f7ccbcb
ACR-3c1f739785b345979a1a74db9d0f02b7
ACR-a9505c730f9d462fbf83f661451f9c0f
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
