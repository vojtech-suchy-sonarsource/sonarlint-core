/*
ACR-c16ff7a902ea4db7a2526c336a3d7d0e
ACR-6d217fb50cda4b909994438edb677479
ACR-482ae94023d54c14a5c6225d0565adef
ACR-320e030124eb46ac89068ba1e1df9056
ACR-3741612bfa6742608b1e014d8452f74d
ACR-f9f88624cfbb41d3bb4fdc930b843292
ACR-179aa120e7bd4047b4d74e18cf8d5491
ACR-67a394af01b04495a7bafbd3009aebad
ACR-48d11609598f4d698f2a2bcc4fe2f730
ACR-348531bdc7eb4f40a06721bc989cda2f
ACR-6215c42aa5f34f319baa06c952a6bd1e
ACR-d2e9a20230fd42948004fb1a76d2d883
ACR-1a66548caa9b4ab59426efbbea02eee0
ACR-1df629d1d26a4b27b4096b500f1214ca
ACR-66559723c008490db1ed88279a0dc6db
ACR-e38b2fa659574145a13609fb039ed8d9
ACR-f942c640e7ff476b9a03537a3bda6ceb
 */
package org.sonarsource.sonarlint.core.repository.config;

import java.util.Optional;
import java.util.function.BiFunction;
import javax.annotation.Nullable;

public record BindingConfiguration(@Nullable String connectionId, @Nullable String sonarProjectKey, boolean bindingSuggestionDisabled) {

  public static BindingConfiguration noBinding() {
    return noBinding(false);
  }

  public static BindingConfiguration noBinding(boolean bindingSuggestionDisabled) {
    return new BindingConfiguration(null, null, bindingSuggestionDisabled);
  }

  public boolean isBound() {
    return connectionId != null && sonarProjectKey != null;
  }

  public <G> Optional<G> ifBound(BiFunction<String, String, G> calledIfBound) {
    if (isBound()) {
      return Optional.of(calledIfBound.apply(connectionId, sonarProjectKey));
    }
    return Optional.empty();
  }

}
