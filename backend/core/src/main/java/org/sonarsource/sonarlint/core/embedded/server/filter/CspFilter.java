/*
ACR-f247213f319444e09fe162b3dd6a6959
ACR-d966b9213ec5413395afc38658a5ee54
ACR-8da10630fcdd46eea09821d12a1a744f
ACR-d09d710c3a6c44a2a1d51a810ba76d90
ACR-1934552a470941c78095566e64407e2d
ACR-aece525073b2442c89d12f8176b2b8db
ACR-29c227149fa842f4896078fe6e393b53
ACR-2769ba15c5884d818631e9e26ed2176b
ACR-3bd898c401e64911ba5215120a744a59
ACR-ddecd3250aa84b4981f56df75fa8838e
ACR-9e6f6d85bc864add9459a920b44122d1
ACR-015a9c463e59484bb554b4c605abdc83
ACR-4844f34ab4dd403cb5a865b7eef44a66
ACR-f6b1c3adcb8a44b88e6b0a64d114d62b
ACR-f144e6ebc4684f4d955b652d156b36cc
ACR-8e404f7887fe4e46b03d379b9bf4f71f
ACR-ec66130cfe2c49b7a07730dc5c42014b
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
