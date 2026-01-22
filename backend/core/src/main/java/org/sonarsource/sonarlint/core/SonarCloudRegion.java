/*
ACR-bca0ed471c42419dbc44f74087f04a7c
ACR-57182a428d784f4786acecf87c920a13
ACR-21855bb2bb45457d950bb8df638da033
ACR-8c7d241dcf4f49bc9381c42f5739f3a3
ACR-2d49a6b601d7494ca33b744bffa2c9b4
ACR-8429f3e8bffa41b5adedc66cdfe1bae9
ACR-37923d6bdcf449bdb8e39f4099c190ed
ACR-36030bb28b584f6b91e7d7eca513e12a
ACR-4d5a2a14656c4ee59b629169c0d393d0
ACR-57c114b130354b1c9696794b91f6604d
ACR-e8dbcdb3309f4ed889f55f944ec7adc8
ACR-a3b0eec0fd554e2d9a594c55224f3ffc
ACR-25206d0256804e96b9e81468a9db236e
ACR-c8a5429259bf4e8d94fbad20bd3edfd4
ACR-08d210a1dfc44bf7ab5215bfd79c99d0
ACR-1f928a0d0b324076983412d0bd02b8d6
ACR-a7d303de35884a6da32a56b927455f54
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
