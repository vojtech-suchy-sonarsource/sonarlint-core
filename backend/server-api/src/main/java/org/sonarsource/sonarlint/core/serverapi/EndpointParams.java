/*
ACR-6fd6132dd9dc4d2e96b1023e085b6075
ACR-2d1eaa9f67ab4a0eb7f009384580f510
ACR-885753d3352e4a0dbc458c68159a373e
ACR-49cc8791927944a396d15e1d65fc0678
ACR-46c09765f182416db9c4bd16fba2204d
ACR-fdb7bdceada94d53a03ad67e2f98005f
ACR-ba2545a8b4a640b8bd9484d491551b9c
ACR-dbb1f98e21ef4beea6f60b4cfefbdb1b
ACR-5d6b11392888404685590a2be1ea6dcb
ACR-8b6f1df0227e49f58a8c82127ee697bd
ACR-a996d853f29e4be983f9bba6fd2ebdde
ACR-4005c4e25bee46aaa62863e604068ddc
ACR-d7b7f3f4b02b4442ae182d5c3d4a4203
ACR-b495642010194423bc13311855633df8
ACR-332eb9da1a544419bdadfba8d67f013e
ACR-c738b6f01b5546d4a68bddd89b10e7b0
ACR-33ae46d42a894353a380dc56ff94db3d
 */
package org.sonarsource.sonarlint.core.serverapi;

import java.util.Optional;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

/*ACR-7d7009dd867948d4ab6fef893ebc972b
ACR-3f85b799e7e0486289e92e7e002c8d81
 */
public class EndpointParams {

  private final String baseUrl;
  @Nullable
  //ACR-3e53ef9927a544668d832d295b54299b
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

  /*ACR-25fb27f6b8c449e685ad600f01afb498
ACR-b75687feb3834a6483828afee5cdea57
   */
  public Optional<String> getOrganization() {
    return sonarCloud ? Optional.ofNullable(organization) : Optional.empty();
  }

}
