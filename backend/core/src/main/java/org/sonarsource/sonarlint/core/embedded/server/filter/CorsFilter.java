/*
ACR-47b749d5871f48df9bbe2f9674232d42
ACR-6982afeab654409593be424dd5121516
ACR-f3b19d15930649c3acdaed837e43764e
ACR-a2643205ec8a4a22927d67a22daa345f
ACR-7a41a763334b4a86baf8d10deba785df
ACR-98a992bcdb374e7e8e390141a768cc67
ACR-5513c20f14cd4bdb95f34bb782288b65
ACR-95c2bd201fb64661ae47a61d5fe1e7cd
ACR-96d9c5aa7d504e73aa9216d9d9c8605a
ACR-43464437e2fd4276837d0231d7618c7c
ACR-631f5c71fd0c4cdface18c7a07e7092f
ACR-baa6a1f30a1a4e26889317662074eebd
ACR-b6f7a4e1058340daaeb7bed2857843fd
ACR-7ff8d6dea15c4d03a590468e276c8e91
ACR-6fb9a1d2c14a40c38405b18a8068c595
ACR-85af5869cc724d3198907067019fb515
ACR-6a7d42b674824229afabcb1ad0f4bb86
 */
package org.sonarsource.sonarlint.core.embedded.server.filter;

import java.io.IOException;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.Method;
import org.apache.hc.core5.http.io.HttpFilterChain;
import org.apache.hc.core5.http.io.HttpFilterHandler;
import org.apache.hc.core5.http.message.BasicClassicHttpResponse;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.sonarsource.sonarlint.core.embedded.server.AttributeUtils;

public class CorsFilter implements HttpFilterHandler {

  @Override
  public void handle(ClassicHttpRequest request, HttpFilterChain.ResponseTrigger responseTrigger, HttpContext context, HttpFilterChain chain)
    throws HttpException, IOException {
    var origin = AttributeUtils.getOrigin(context);

    if (Method.OPTIONS.name().equalsIgnoreCase(request.getMethod())) {
      var response = new BasicClassicHttpResponse(HttpStatus.SC_OK);
      response.addHeader("Access-Control-Allow-Origin", origin);

      response.addHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
      response.addHeader("Access-Control-Allow-Private-Network", true);
      responseTrigger.submitResponse(response);
    } else {
      chain.proceed(request, new HttpFilterChain.ResponseTrigger() {
        @Override
        public void sendInformation(ClassicHttpResponse classicHttpResponse) throws HttpException, IOException {
          responseTrigger.sendInformation(classicHttpResponse);
        }

        @Override
        public void submitResponse(ClassicHttpResponse classicHttpResponse) throws HttpException, IOException {
          classicHttpResponse.addHeader("Access-Control-Allow-Origin", origin);
          responseTrigger.submitResponse(classicHttpResponse);
        }
      }, context);
    }
  }
}
