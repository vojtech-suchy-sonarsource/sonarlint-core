/*
ACR-85b70be0bbd74dc1a6b4b3a6a84020d5
ACR-e4f4fd0bb094437eb9a8e543a28a2bba
ACR-eb634602888a40e08266171558654d38
ACR-e6d8f79f999842a4a327d8face2de02a
ACR-4bade49fa977411a91c28f296153e53f
ACR-6096fe6f132a437883feffcc101f9335
ACR-f0c36b70f4d34e3990e16e29531449a8
ACR-5ab7b208dd55469d8c9d31720184bdf0
ACR-0b6c4da3297a4b3891485577da425111
ACR-e9472ce8dbcd416e84abbc253a5ee19b
ACR-9a4e35751f314f51807cb39ecb1987ab
ACR-24951d530ce348f2aac10cd7199e3130
ACR-7de1151f0b824703aeeab0e6be8cf901
ACR-56387d3e8ed14f928024e250add6600a
ACR-bc20d068a47b4fb5860e1bd49012665b
ACR-a1a5bdb945c84ce0bb1e03d047659883
ACR-af38c3a7d21e45dba2db94e1fb7bd201
 */
package org.sonarsource.sonarlint.core.http;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import javax.net.ssl.SSLException;
import org.apache.hc.client5.http.impl.DefaultHttpRequestRetryStrategy;
import org.apache.hc.core5.http.ConnectionClosedException;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.util.TimeValue;

public class RetryOnDemandStrategy extends DefaultHttpRequestRetryStrategy {

  private static final List<Class<? extends IOException>> SAME_AS_DEFAULT = Arrays.asList(
    InterruptedIOException.class,
    UnknownHostException.class,
    ConnectException.class,
    ConnectionClosedException.class,
    NoRouteToHostException.class,
    SSLException.class);

  public RetryOnDemandStrategy(int maxRetries, TimeValue defaultRetryInterval) {
    super(maxRetries,
      defaultRetryInterval,
      SAME_AS_DEFAULT,
      Arrays.asList(
        HttpStatus.SC_TOO_MANY_REQUESTS,
        HttpStatus.SC_INTERNAL_SERVER_ERROR,
        HttpStatus.SC_SERVICE_UNAVAILABLE));
  }

  @Override
  public boolean retryRequest(HttpResponse response, int execCount, HttpContext context) {
    return areRetriesEnabled(context)
      && super.retryRequest(response, execCount, context);
  }

  private static boolean areRetriesEnabled(HttpContext context) {
    var retriesEnabledFlag = context.getAttribute(ContextAttributes.RETRIES_ENABLED);
    return Boolean.TRUE.equals(retriesEnabledFlag);
  }
}
