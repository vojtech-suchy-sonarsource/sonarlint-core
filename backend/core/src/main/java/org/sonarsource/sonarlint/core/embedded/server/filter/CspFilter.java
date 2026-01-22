/*
ACR-1c45d1e4ff214d66b1f77f8d16f767d8
ACR-41c054bd0b324e42b51470742d7830e2
ACR-574fa0e702904e6dbb3e2e3470e0e01e
ACR-088a1971c37f4b57a4e22a14718e1200
ACR-1fe5b590bf8246d7994211e99502ffab
ACR-a43f2012fe7b45a991e74646faff511e
ACR-11548f0e5d5547bdbdf7aa88629c8c27
ACR-516af322ed3a4c9bbc8200a4b775b4f4
ACR-0d5753695d2e44339383b0e78b99f5d0
ACR-535eacd50c0e41719015a282309bfa32
ACR-01d45bc226084a3f8a91d893093b7504
ACR-191992ed4b9d43f581226db7108f3bac
ACR-b6c7ff0043d44271b54ffa1d40ba47ca
ACR-83295b11e34648e4be46d5fc8353b121
ACR-9f3897ca61614deab6c352b73ee1feeb
ACR-7a65e6a10aae4f5981fb6aedd86da9b1
ACR-243bbb0b53a64f2ca3ab5a68b176302d
 */
package org.sonarsource.sonarlint.core.embedded.server.filter;

import java.io.IOException;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.HttpFilterChain;
import org.apache.hc.core5.http.io.HttpFilterHandler;
import org.apache.hc.core5.http.protocol.HttpContext;

public class CspFilter implements HttpFilterHandler {

  @Override
  public void handle(ClassicHttpRequest request, HttpFilterChain.ResponseTrigger responseTrigger, HttpContext context, HttpFilterChain chain)
    throws HttpException, IOException {
    chain.proceed(request, new HttpFilterChain.ResponseTrigger() {
      @Override
      public void sendInformation(ClassicHttpResponse classicHttpResponse) throws HttpException, IOException {
        responseTrigger.sendInformation(classicHttpResponse);
      }

      @Override
      public void submitResponse(ClassicHttpResponse response) throws HttpException, IOException {
        if (response.getCode() >= HttpStatus.SC_BAD_REQUEST) {
          responseTrigger.submitResponse(response);
          return;
        }
        var port = request.getAuthority().getPort();
        response.setHeader("Content-Security-Policy-Report-Only", "connect-src 'self' http://localhost:" + port + ";");
        responseTrigger.submitResponse(response);
      }
    }, context);
  }
}
