/*
ACR-a108339fc1294100ad32a7cf9f81abd4
ACR-003e29861e9a4c869baac98b15013521
ACR-6e791933d253424eb524fdf46b46fece
ACR-43814bf6df9a40f6891157345f95a0de
ACR-edc5637f5d8347bfaf26137361d8e58d
ACR-c3a29938348d4f28aeedc69b1549e933
ACR-3ee1bd497f1842dcb62a4ea4338a2393
ACR-dafaa630dd89412cb1585d04cac79f53
ACR-52ead3cff6ce4a279f23b4343c9de5d4
ACR-43bcc41ab15a4b51b56533bd823bfa63
ACR-4d626efe00e742d882fb6cdb532c44b5
ACR-f939d8864f1b48ae8670f70192145331
ACR-b16521f419954b199d890c183c1af0c5
ACR-3d3d1cfd42d347759897a7add192db9d
ACR-f8693bef509344f0bcdf8930e264a2f3
ACR-4071d29edae24ad49fcfed6c7f0e8d69
ACR-10c3e0e6ccb14df288888a95f8506d85
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
