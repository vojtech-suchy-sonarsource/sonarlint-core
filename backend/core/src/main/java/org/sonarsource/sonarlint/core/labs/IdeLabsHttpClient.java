/*
ACR-66e34abfebdd44f5aecaa328f12b4771
ACR-10701d06ed5b41f49f6798b533983d2a
ACR-a382d7fa9b724b58a9e9de1997a63f67
ACR-34243bfc98a54c1f92bb02183c649b20
ACR-d93caae7e3ca4d8b8fab8961040965f9
ACR-556b690c5ea54c529dfac194a9936ce8
ACR-9ae6a95994d149b0a24a356c8c6698ec
ACR-5f8efe3651924f9cb0fe5782d52e3c37
ACR-f053ff8a8efc4e23b4618752aa47b0bc
ACR-8abf04ea5bba47ccbec066e4fd1ba7d9
ACR-08757ad713b74403ac0dee1d5f4030d5
ACR-f8d932595b284bccad7831fd4a260402
ACR-b9487ba7d4c143b09d0a0440462c922e
ACR-75156ae688c14004989b277a8cb2f043
ACR-9ec1ed61253344b69bd3e537224a6f79
ACR-6cef52b990c7417d94f9dcefcb27ebaf
ACR-771358a89468411fb972e7f84dc0f00c
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
