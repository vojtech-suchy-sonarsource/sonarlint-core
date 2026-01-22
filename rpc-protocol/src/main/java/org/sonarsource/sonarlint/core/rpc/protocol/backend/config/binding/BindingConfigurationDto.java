/*
ACR-b27352b037b549c5868329434e2846b7
ACR-9c06e19a2f1240fea3d66cb47acecb4d
ACR-97b8501700924da9ae03bbd3a981a3b2
ACR-5946cb158dc341bf82867bc464ee4f7d
ACR-9b6b2355daf3447b976460dc4b4a249a
ACR-a4a940278bda46efbcf1672a142aab28
ACR-a60f087bb898474cb534927e02d22487
ACR-99a1439993a8410bbb4f91454ebb64ee
ACR-ca2b65b03a424523aa6fb30a1e583b8e
ACR-db372856b2114d8d99167795f03e33c4
ACR-1f352bd9e16b4300bf572cf6db860edc
ACR-1c235990cecb448fb5428fad8cfb6234
ACR-d530fdf2c6c04a4d8994361b9f64f844
ACR-71ce8e49e7644c0880f9ce8ed636e5f6
ACR-6f27106d61144897a98fd6e94ea1d650
ACR-5232ab5499ae44919b8c07a8de5afe5c
ACR-43c6ccad53454760a481300663eb665b
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
