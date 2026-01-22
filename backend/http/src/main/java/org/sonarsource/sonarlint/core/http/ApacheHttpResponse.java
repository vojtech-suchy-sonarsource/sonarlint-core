/*
ACR-d2b950272f204aef9ce5e3500c609592
ACR-03a5d4bfb0b647eb84718b405782c655
ACR-d34ff627938741b8bcd59e5c768fe695
ACR-02474f248399432aa11e24ba5c4938ba
ACR-a2116a593f884ed2b9d547b7ea207d82
ACR-c0ea7b6baa3740a7b5f820d2a97c5705
ACR-644df67d7232420b96cf49f42b64efb2
ACR-6db74a4bbd84441a8215e7c139c57335
ACR-269372b6c5a447c29551f2d7520c6183
ACR-0e148c579e9241c39c7156431ddc31ac
ACR-e8fbac775094426fa441f2a4275fe405
ACR-cfb5c057e7f44e3b93cb9a6b037c3826
ACR-edabef075b1946ba92579f17333131a5
ACR-cb685848271948d2a0a541a80df275ed
ACR-89c727b069fe4465ac4875d266cc9ca9
ACR-958067c2ec29451799f91b00eca992af
ACR-0cd162d9366b4a089c7523ef4550d7c2
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
    //ACR-a9d29d859f2745099594ab687619eb5b
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
