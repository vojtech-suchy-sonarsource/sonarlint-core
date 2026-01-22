/*
ACR-eca26d428341458684785b40ed8a7a3d
ACR-ab05716aefbb4d1eb8f4fc96a43a7100
ACR-c1145e58a30f42848a138e3f95a65189
ACR-6a0ff1b060a5415ba18d5eb2e3d0719e
ACR-fbd2edc22126443e952db8dc99f8671c
ACR-a1c157cfd4f2411fb86acc7fc52e13a0
ACR-e9e583f257c84cedb92ce21331494ae1
ACR-a53b62c32c12424a92e81a0ea118cfcf
ACR-d64401c61c924b069eef4c421caa1ab7
ACR-8e3ed34cfa75415bbcfd8c7d9794e8c4
ACR-5646eca42be240f680d089c6c36bd439
ACR-97fe572e56ed450ab0b9db649d7dc24b
ACR-0c72df1aaea34e7f843099d1079527b0
ACR-2615058ecae24b0491c626377e87a560
ACR-7c8b16ca286747df861db7c61b0cdcfb
ACR-571c6faaa9824fb2aaeb3d343851a1b1
ACR-66128382147944dda12ecd372ddf7ea5
 */
package org.sonarsource.sonarlint.core.embedded.server.filter;

import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.io.HttpFilterChain;
import org.apache.hc.core5.http.message.BasicHeader;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.mockito.Mockito.*;

class RateLimitFilterTests {

  private final ClassicHttpRequest request = mock(ClassicHttpRequest.class);
  private final HttpFilterChain.ResponseTrigger responseTrigger = mock(HttpFilterChain.ResponseTrigger.class);
  private final HttpContext context = mock(HttpContext.class);
  private final HttpFilterChain chain = mock(HttpFilterChain.class);
  private RateLimitFilter filter;

  @BeforeEach
  void init() {
    filter = new RateLimitFilter();
  }

  @Test
  void should_not_proceed_with_request_if_origin_is_null() throws HttpException, IOException {
    when(request.getHeader("Origin")).thenReturn(null);

    filter.handle(request, responseTrigger, context, chain);

    verify(responseTrigger).submitResponse(any());
    verify(chain, never()).proceed(any(), any(), any());
  }

  @Test
  void should_proceed_when_request_is_valid() throws HttpException, IOException {
    when(request.getHeader("Origin")).thenReturn(new BasicHeader("Origin", "https://example.com"));

    filter.handle(request, responseTrigger, context, chain);

    verify(responseTrigger, never()).submitResponse(any());
    verify(chain).proceed(any(), any(), any());
  }

}
