/*
ACR-0ede968ede2749be8a366ca92937e7e7
ACR-30eb0383281342cf86304faba183bd50
ACR-be9394aa8d724eab92892f238f0bc883
ACR-c50f992dd9da42528c3e37ed3daefbae
ACR-e861bd8d51ce48738c1bb3a1fc89f27e
ACR-db4d5ce27f1a46728c217b1c57a1aedb
ACR-aad8857cb80848e0b5f32dfa9c0cfa35
ACR-ae9faedb5c194b498e399e5c4a89e10c
ACR-ffed23de804e4d078a92962a465da0c0
ACR-de21f411dc3f49558df7c9cda65d07d1
ACR-40c267b2d4694654a711ff7433e84bb5
ACR-13b4b794a7d842e998d117ff64e08312
ACR-fba7e44763804a9c83a18a6828dc5727
ACR-fc6f76ce57a744399d7961a441186c21
ACR-bff0c783616d4e148f153e149782cf11
ACR-2e8c48a807224f9c80e21275cbe85d62
ACR-1825752f07384f589c6ca0e45ab5a6c5
 */
package org.sonarsource.sonarlint.core.embedded.server.filter;

import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.HttpFilterChain;
import org.apache.hc.core5.http.io.HttpFilterHandler;
import org.apache.hc.core5.http.message.BasicClassicHttpResponse;
import org.apache.hc.core5.http.protocol.HttpContext;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import org.sonarsource.sonarlint.core.embedded.server.AttributeUtils;

public class RateLimitFilter implements HttpFilterHandler {

  private static final int MAX_REQUESTS_PER_ORIGIN = 10;
  private static final long TIME_FRAME_MS = TimeUnit.SECONDS.toMillis(10);
  private final ConcurrentHashMap<String, RequestCounter> requestCounters = new ConcurrentHashMap<>();

  @Override
  public void handle(ClassicHttpRequest request, HttpFilterChain.ResponseTrigger responseTrigger, HttpContext context, HttpFilterChain chain)
    throws HttpException, IOException {
    var originHeader = request.getHeader("Origin");
    var origin = originHeader != null ? originHeader.getValue() : null;
    if (origin == null) {
      var response = new BasicClassicHttpResponse(HttpStatus.SC_BAD_REQUEST);
      responseTrigger.submitResponse(response);
    } else {
      if (!isRequestAllowed(origin)) {
        var response = new BasicClassicHttpResponse(HttpStatus.SC_TOO_MANY_REQUESTS);
        responseTrigger.submitResponse(response);
      } else {
        context.setAttribute(AttributeUtils.ORIGIN_ATTRIBUTE, origin);
        chain.proceed(request, responseTrigger, context);
      }
    }
  }

  private boolean isRequestAllowed(String origin) {
    long currentTime = System.currentTimeMillis();
    var counter = requestCounters.computeIfAbsent(origin, k -> new RequestCounter(currentTime));
    requestCounters.compute(origin, (k, v) -> {
      if (currentTime - counter.timestamp > TIME_FRAME_MS) {
        counter.timestamp = currentTime;
        counter.count = 1;
      } else {
        counter.count++;
      }
      return counter;
    });
    return counter.count <= MAX_REQUESTS_PER_ORIGIN;
  }

  private static class RequestCounter {
    long timestamp;
    int count;

    RequestCounter(long timestamp) {
      this.timestamp = timestamp;
      this.count = 0;
    }
  }

}
