/*
ACR-8ef04e5ffd0644acad18d908a9960c5c
ACR-be4e7dcd0840412cad4cf2d3f10920af
ACR-60d2b15399d54f49a37978d581e8013f
ACR-f4d1d56d49e74b238bb18229fe376c93
ACR-1854998c9eaa4685998479e22f34a051
ACR-0ce4380444ff4781b2576c9dbbd0e8bc
ACR-0d5f467608a040eeb88266d32ada7e86
ACR-916506b386dc4a6ba6a7e50555fc9bd4
ACR-54207688e84549a19307e6a2864a5df5
ACR-3eb1cdc4deaa4c388ee99d1aeaa2f95b
ACR-9668a979249d4ab693a66928f932ced8
ACR-a8a3e8998cff451482180c759932840c
ACR-5362ec2138834a38b63b713ad5dcfa47
ACR-e57aed729c6342fea66573dac98fc961
ACR-91d91ae6de9741cf8c252b45997bbe67
ACR-0bcde808d1f446c79d71a4aa2ec5f83e
ACR-ed24f32867864994acbaaf3ff2358d92
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.dogfooding;

public class IsDogfoodingEnvironmentResponse {
  private final boolean isDogfoodingEnvironment;

  public IsDogfoodingEnvironmentResponse(boolean isDogfoodingEnvironment) {
    this.isDogfoodingEnvironment = isDogfoodingEnvironment;
  }

  public boolean isDogfoodingEnvironment() {
    return isDogfoodingEnvironment;
  }
}
