/*
ACR-3e07ed4ca7214b2f83268e233c141293
ACR-4c2dade6b22941f0b3811a5271417d12
ACR-bcb8485ea5d5486486ae1bd0cc1e913b
ACR-dde6b6a7f08e43b7a25ad49fc0b99e5f
ACR-462e86eeff5d41aab19651d56ff1fffd
ACR-36d04e3f874d44f9bdfc810fc66d1ec1
ACR-b31a82418c814667b2231fb3782d1622
ACR-820a3bd7ba1e40b6be9894bfd2a6ba05
ACR-74bffea088b6450284eb6db8a558ccf2
ACR-a3bc78c0e19a42c0b91565032accbc5b
ACR-f14e630033bf47f4b1d35a1fdf62a54b
ACR-809e3a1151484fb0916a3681c8559e6d
ACR-516716cb1a24401aa5226110101dcabd
ACR-b93d93b9ee014c0baa32c4d54950c51d
ACR-e275880d07af4a73a0fe70ee52abd45d
ACR-5f5903947f6343ac974bfa1094ab72a1
ACR-89c8d1913ffb472c96ff987c0f5864c3
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.rules;

public class GetEffectiveRuleDetailsResponse {
  private final EffectiveRuleDetailsDto details;

  public GetEffectiveRuleDetailsResponse(EffectiveRuleDetailsDto details) {
    this.details = details;
  }

  public EffectiveRuleDetailsDto details() {
    return details;
  }
}
