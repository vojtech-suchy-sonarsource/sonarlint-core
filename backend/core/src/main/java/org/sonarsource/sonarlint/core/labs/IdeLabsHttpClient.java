/*
ACR-d5d7cd293b7f44a8a8d6e23b5f493e78
ACR-49a9fe2f00ea4fb28e110607e7706888
ACR-43c71f0607c54d90b1480f95c9a7fb44
ACR-a44965100764471cb3de1a98e7aac705
ACR-11eba8b7c1a04e9b864cab3e85462610
ACR-43d6599116544b98a0e907fabc56fb4d
ACR-02e70971dbf94b3dacf822f763000346
ACR-c83b8d7174cc47bc910dc14c501cfda9
ACR-25b69a4fb32845fe898bc89cabedb377
ACR-8fc010a0f1674c05b5a421a5c586fc8c
ACR-03bf3956f05643f78577a7f044bb3679
ACR-e63fc916bfbe4dc2be5b150a8d237946
ACR-a842288f149944db98ebc3ed4e536374
ACR-0e558e88b2364c0191b568da7a923d56
ACR-1cfe8fa73dfb480c8266153822f15d8d
ACR-3f38da18c3dc4ca28ad2a715096b0b48
ACR-ea5fe600bdf5410c942d7768baa30e43
 */
package org.sonarsource.sonarlint.core.labs;

import com.google.gson.Gson;
import org.sonarsource.sonarlint.core.http.HttpClient;
import org.sonarsource.sonarlint.core.http.HttpClientProvider;
import org.springframework.beans.factory.annotation.Qualifier;

public class IdeLabsHttpClient {
  private final HttpClient httpClient;
  private final String labsSubscriptionEndpoint;

  private final Gson gson = new Gson();

  public IdeLabsHttpClient(HttpClientProvider httpClientProvider, @Qualifier("labsSubscriptionEndpoint") String labsSubscriptionEndpoint) {
    this.httpClient = httpClientProvider.getHttpClient();
    this.labsSubscriptionEndpoint = labsSubscriptionEndpoint;
  }

  public HttpClient.Response join(String email, String ideName) {
    var requestBody = gson.toJson(new IdeLabsSubscriptionRequestPayload(email, ideName));

    return httpClient.post(labsSubscriptionEndpoint, "application/json", requestBody);
  }
}
