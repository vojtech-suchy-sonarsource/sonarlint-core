/*
ACR-40b3db6bfcda441db03d8ce56c730d80
ACR-805457f60eee42a399ac2aeda86f4f3e
ACR-463885895dc24592bb482bf65075e7a7
ACR-21716b9a61024aebb497829c78eb66a3
ACR-37742119fc7c4b75a31a05ed4e40c3cd
ACR-a9c3d28f3115424781c8d07bef1323bf
ACR-e7c99fa9eb974f1d941d6ecea900b66b
ACR-09b5ebab4424408cb6ae45b0733657a5
ACR-522670fd13aa419a93960e379fabab18
ACR-bdbc7f928bf84e1ba4a94a3a9091abd9
ACR-9d8fbe38dcdd407c98623598e348ca8e
ACR-c95b7d8c321044c0906ec4fea10fde50
ACR-438008a6c0ec41e2aa583fbe77c7bcba
ACR-325917690a1844988084390484b0d2ac
ACR-e597965b6cbf4782a380ca29a6524dc8
ACR-c77057f9f551466e80458225acb83d0e
ACR-262bdb305bc14b1d92fd47a1e1832e09
 */
package org.sonarsource.sonarlint.core.http;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;

class ApacheHttpResponse implements HttpClient.Response {

  private final String requestUrl;
  private final SimpleHttpResponse response;

  public ApacheHttpResponse(String requestUrl, SimpleHttpResponse response) {
    this.requestUrl = requestUrl;
    this.response = response;
  }

  @Override
  public int code() {
    return response.getCode();
  }

  @Override
  public String bodyAsString() {
    return response.getBodyText();
  }

  @Override
  public InputStream bodyAsStream() {
    if (response.getBodyBytes() == null) {
      return new ByteArrayInputStream(new byte[0]);
    }
    return new ByteArrayInputStream(response.getBodyBytes());
  }

  @Override
  public void close() {
    //ACR-3464b42eb64e4df8942cdebf32d31f64
  }

  @Override
  public String url() {
    return requestUrl;
  }

  @Override
  public String toString() {
    return response.toString();
  }
}
