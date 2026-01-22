/*
ACR-6f1da31f375044239c5bcf51e49a12f2
ACR-967a7b35cd84422483cd868e0e0ed492
ACR-f8824932578246bfadeeeaa59aa8182a
ACR-b16c5aea1f344edbb08c0598e231fab1
ACR-f4d324c8281647579a69b85cc403100d
ACR-d7d538b36e594d27b16447c24042cb86
ACR-e483c70b01e64b1f9db2050f84d7b494
ACR-ccdeaf2f62fa4b38b6350784959b4d19
ACR-a26c7b25ca014b98a60bdf680bfc73b1
ACR-68c47c0087b943b8824edcb7e9967a52
ACR-2a0e8c957a074a87a5dde0a77ed38e81
ACR-734dad07361749ad8284c7b22fc5e7cd
ACR-b9187bb2cff945a6a6101fa26a8d991b
ACR-c23bc4a1a50747ce9ba2f24dae4021a6
ACR-ca4f571cfbc14b0d93489963a0a9afb2
ACR-33a7d587a42d4d7cbfdfcf5a2f24fbfc
ACR-ce7dc319a9dd4e76b3d574535ee554d9
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize;

import java.net.URI;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

/*ACR-71a298f805a541178a585850d5fa1862*/
public class SonarQubeCloudRegionDto {
  @Nullable
  private final URI uri;
  @Nullable
  private final URI apiUri;
  @Nullable
  private final URI webSocketsEndpointUri;

  /*ACR-088639a0bc724210927baadb274720fe
ACR-d9d54c7f24464767af09b337fd6e6e90
ACR-3a360ee327e242f8861682b477998e46
ACR-b6e0ebc5b26a46cd8499eddf991a795b
ACR-c60e392771314d549a8f5118722ad768
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
