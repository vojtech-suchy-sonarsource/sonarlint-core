/*
ACR-476dfb8e1e5449e2b5cbb822f51073b9
ACR-46cb6cfc35d9430e94bf015671c75481
ACR-df40ab537282411bbc7e10f50d9a2203
ACR-d9d7712563794ce1ae7d9eeca9e187b4
ACR-80db667e1a7c478c8f04a0d4f205bb73
ACR-c7c8f04ea1da4f5fba29b2ee926ecbbf
ACR-4afec745a3074c5bab82e56b91098c31
ACR-d23027c0322c475188edcb7f86846e09
ACR-16db16ee1f8f4594bebb61c6d8749ad9
ACR-9c9864a1a92543b094fd7b01698c94f9
ACR-d4103837b1d844d7b5db8216b6069965
ACR-cf6c0c476ad4413384967b85e1ed89f5
ACR-200548a18b9c4e6f97fac3c9dc013da2
ACR-aaf280d59501435fa16349665810af05
ACR-390b8da7359542e7b1ba68705cbd5910
ACR-1c0071eaa06d4105a877497f2919031d
ACR-678e2fa6027841f9b9bc29291f02b415
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize;

import java.net.URI;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

/*ACR-cc219e8a618d4f68b3c1f9de85e76597*/
public class SonarQubeCloudRegionDto {
  @Nullable
  private final URI uri;
  @Nullable
  private final URI apiUri;
  @Nullable
  private final URI webSocketsEndpointUri;

  /*ACR-256ebc7a647445bdb4c68a1e39a8840a
ACR-10da238640dd4078bef6f16bd6ec822b
ACR-986818bd7da64dbab9a4c20017a3b67d
ACR-cf26ed62cfb8471a97c4ffff646fe8bd
ACR-104c296d1b2841649b240c8d44a8d89a
   */
  public SonarQubeCloudRegionDto(@Nullable URI uri, @Nullable URI apiUri, @Nullable URI webSocketsEndpointUri) {
    this.uri = uri;
    this.apiUri = apiUri;
    this.webSocketsEndpointUri = webSocketsEndpointUri;
  }

  @CheckForNull
  public URI getUri() {
    return uri;
  }

  @CheckForNull
  public URI getApiUri() {
    return apiUri;
  }

  @CheckForNull
  public URI getWebSocketsEndpointUri() {
    return webSocketsEndpointUri;
  }
}
