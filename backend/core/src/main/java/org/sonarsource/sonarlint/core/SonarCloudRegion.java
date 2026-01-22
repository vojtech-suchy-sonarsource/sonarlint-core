/*
ACR-2bfcd59f80b143d084c3e72daa977b04
ACR-8a29b3184d5d402dbc38421a1c7f3876
ACR-c2d9e99f84bf4d248f42c55d3b885a4d
ACR-e96f73210efa4b329898400a034934b1
ACR-9705dfe91a80408f85164049ab4b9623
ACR-8b54d7041d664c9b951c192e0b02a82e
ACR-b51b05828b144e2198b5395df108e34d
ACR-49b169bff88747f8a1f0f7ad89e43063
ACR-2c7765d5c3484039a31826beba0b7c5b
ACR-4e6a797bd8704279bd42ed8ebb5ad92b
ACR-1e5bbeb4f8314a569e7e5667c31985ad
ACR-3c5b89bfc479407c82723cfc256c335f
ACR-918641b1a5334663a830de7a2b98540a
ACR-83f9ff901cb44d81a6b239b1d3a95298
ACR-d200b6f53c5643cdb54d52d7132957af
ACR-6d2b506fbeba483d8315d233a2f66d30
ACR-206edd8e63104cadbba30f5582021059
 */
package org.sonarsource.sonarlint.core;

import java.net.URI;
import java.util.Arrays;

public enum SonarCloudRegion {
  EU("https://sonarcloud.io", "https://api.sonarcloud.io", "wss://events-api.sonarcloud.io/"),
  US("https://sonarqube.us", "https://api.sonarqube.us", "wss://events-api.sonarqube.us/");

  public static final String[] CLOUD_URLS = Arrays.stream(values())
    .map(SonarCloudRegion::getProductionUri)
    .map(Object::toString)
    .toArray(String[]::new);

  private final URI productionUri;
  private final URI apiProductionUri;
  private final URI webSocketUri;

  SonarCloudRegion(String productionUri, String apiProductionUri, String webSocketUri) {
    this.productionUri = URI.create(productionUri);
    this.apiProductionUri = URI.create(apiProductionUri);
    this.webSocketUri = URI.create(webSocketUri);
  }

  public URI getProductionUri() {
    return productionUri;
  }

  public URI getApiProductionUri() {
    return apiProductionUri;
  }

  public URI getWebSocketUri() {
    return webSocketUri;
  }
}
