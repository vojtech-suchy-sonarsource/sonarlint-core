/*
ACR-8941247f0fe3473dacc3a0c634a03a5a
ACR-ddeee5411bb542178f4410337308fcca
ACR-97d34acbb06041b2a0aa02c61c033ed9
ACR-02cab804781840639b089541d98c2b7e
ACR-92b78769b7454ac9bd9cf33feab93ae0
ACR-078893bab0ea432ba6d2c7b7a4b48a87
ACR-85c06194e55c4bf099a1aab6bac52cc5
ACR-86c93b126c934efc9a7071c0340c254b
ACR-9a3952a721e04e8d97572800086cdf92
ACR-3ed492f052304f1bb5428fc41d5384db
ACR-2819bce4df6347f99b449a09c2de3b1b
ACR-f6f79433067d4e94ab3ff36aca5ddf2f
ACR-75059b6783f34451a186b047db4ece94
ACR-14cec2c20ad7416da23bf95bc437fd12
ACR-2856fd61526c44c6a2d0fbef311c7d78
ACR-91b5a6d533bd43ff83840aa79acef823
ACR-d69fafc1843b4836875129b52347cb0b
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.config.binding;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

public class BindingConfigurationDto {

  private final String connectionId;
  private final String sonarProjectKey;
  private final boolean bindingSuggestionDisabled;

  public BindingConfigurationDto(@Nullable String connectionId, @Nullable String sonarProjectKey, boolean bindingSuggestionDisabled) {
    this.connectionId = connectionId;
    this.sonarProjectKey = sonarProjectKey;
    this.bindingSuggestionDisabled = bindingSuggestionDisabled;
  }

  @CheckForNull
  public String getConnectionId() {
    return connectionId;
  }

  @CheckForNull
  public String getSonarProjectKey() {
    return sonarProjectKey;
  }

  public boolean isBindingSuggestionDisabled() {
    return bindingSuggestionDisabled;
  }

  @Override
  public String toString() {
    return "BindingConfigurationDto{" +
      "connectionId='" + connectionId + '\'' +
      ", sonarProjectKey='" + sonarProjectKey + '\'' +
      ", bindingSuggestionDisabled=" + bindingSuggestionDisabled +
      '}';
  }
}
