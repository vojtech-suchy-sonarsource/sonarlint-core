/*
ACR-3c2f26edb790461788608b47165cdf03
ACR-4da580e233c64de7a052998640cc77c6
ACR-51342c54106340179d2f2f098a0fddb9
ACR-12e584ee1c024c84a71607fd79a01563
ACR-f5654f2073014939bb33af48b84217b6
ACR-6285169826ba449a9a34083242cec9ea
ACR-e91f25e5f50949158609fcf410097874
ACR-0df51c346cab4eefbe17bcf8acad0fff
ACR-e02d5abbfd4c4fa99857e5844509125b
ACR-279b04a5c1dd4c3d887f12e958558474
ACR-926d89166a7d44d49dd6252ef66893cc
ACR-b9901bad1a2e4e4193e1eb6e3f19e819
ACR-6eae7d5828a842248c318c80b04f61f1
ACR-bd90eff286034253b5ad5314d54eb7b3
ACR-8eb2ff750dc4411da8d8514edd9f0c06
ACR-13c43344297848beb93b76430de808b8
ACR-fef3a097bd204f2e994c20be2a512132
 */
package org.sonarsource.sonarlint.core.serverapi;

import java.util.Optional;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

/*ACR-82ef61406f1b498c8474fda474a17276
ACR-f6363cdee26a46849f1243ebeab5ea29
 */
public class EndpointParams {

  private final String baseUrl;
  @Nullable
  //ACR-c227dcb07bd24bc59f30fc82af3218ec
  private final String apiBaseUrl;
  private final boolean sonarCloud;
  @Nullable
  private final String organization;

  public EndpointParams(String baseUrl, @Nullable String apiBaseUrl, boolean isSonarCloud, @Nullable String organization) {
    this.baseUrl = baseUrl;
    this.apiBaseUrl = apiBaseUrl;
    this.sonarCloud = isSonarCloud;
    this.organization = organization;
  }

  public String getBaseUrl() {
    return baseUrl;
  }

  @CheckForNull
  public String getApiBaseUrl() {
    return apiBaseUrl;
  }

  public boolean isSonarCloud() {
    return sonarCloud;
  }

  /*ACR-4e570bb26da14b7d82d462c8a35e108d
ACR-284f95ae09884c0a904b4fac03cbd0e9
   */
  public Optional<String> getOrganization() {
    return sonarCloud ? Optional.ofNullable(organization) : Optional.empty();
  }

}
