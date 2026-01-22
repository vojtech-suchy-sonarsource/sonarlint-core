/*
ACR-375a18cf0c63453e8d959f780ca70ab2
ACR-a5b38bd1b0e5453b9f080a125e205acb
ACR-e5d1710061ff4287a14b1d508172319a
ACR-d5b6356009f8429195d25ddda7372ac1
ACR-98264a110298417d9391286d42765f5f
ACR-18ad0a635d0b45d99036b8304ec139d2
ACR-60e42d29fe954934a54932939a12cf66
ACR-6cdae592aeee40ccb392f9939cfadf65
ACR-8294685332574cdf8f2b0cccaa0af346
ACR-9ff726ac69b949318256ec2b9f0ffc35
ACR-a3aff71fe6214397a26fde2fdbe4ad89
ACR-81a36b7804b6468196967c70c5a1f663
ACR-6adbea321bb046079a62857c70c24386
ACR-6fd80baa168c4e5d92628b2463a81f7d
ACR-a5d0cea55a874bb7a1a37c8c9b1ea432
ACR-2a989893a61d46bf87822d65f2f63c85
ACR-03d64e1d38d845b3bb54fdccb2b2c96f
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
