/*
ACR-7d633b752d6d40a282bc94bd79867a32
ACR-c74856a2acea4800a8f54f2dcf45ccac
ACR-87be645ca67742859f552f5cde4c74a5
ACR-a56d3edec73249d3b3d0718be7bd3538
ACR-888c7978dc484a48bd82dc6356cb65a4
ACR-e655ce8cdea04731877d4e71fc0c0111
ACR-ce5da036d9a94c58bd04e81bb6e62d6e
ACR-895291c1fe5042608b1516a463699853
ACR-be43a8135e1144fe95db0a612045b655
ACR-f79edc1393244fcf868a43b78c95907d
ACR-92b7665fbcb841b7ad04c5cd1f70a43b
ACR-a0424d50689d4054b96c195cb04d47c3
ACR-21084b8c57aa4f4f87ad1ba4fa057c5e
ACR-348efc79247f403ea284a0e718214b2a
ACR-d5b7d9420f264d94ab3887455bd17cc7
ACR-575067bdc4724665bb0eb351b1ba4e75
ACR-c45d5e9ec5654de2a817482dee285fb7
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
