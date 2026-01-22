/*
ACR-00a0341448cb4e4da086be6104456e31
ACR-1aa7e11f92bb4fd294c15472330c0ea0
ACR-971ae45f83a242ca86b8e456b6679c7e
ACR-304ee012907c4915925393fef7558a3d
ACR-e3552f797a9041cf8d4f6dd9a133407a
ACR-f090510c20fc408eae142d388d3148b7
ACR-5ca6ca9d30434ba5bce4c4a557020e29
ACR-4aeb4de7660d490e9a82eb1ed75b2c2a
ACR-cda15d5adace4d1ea9c1f58fedd2b5d7
ACR-4a8ebf8a68434a4aa5dc3f7d95e17587
ACR-3276accfffcd4e988fef295105cbb5f8
ACR-af295ec63433467cafb1b4967881d93e
ACR-ae4ad58996924335b6c99df01e944a80
ACR-8912b26b859947c78390f32308e4ee53
ACR-100ceb5fb4924b279601184e39852cad
ACR-b2eb873691d94ebfb9d565c1a446f31a
ACR-57753d35192348b288cdbbd383bdb721
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.log;

public class SetLogLevelParams {
  private final LogLevel newLevel;

  public SetLogLevelParams(LogLevel newLevel) {
    this.newLevel = newLevel;
  }

  public LogLevel getNewLevel() {
    return newLevel;
  }
}
