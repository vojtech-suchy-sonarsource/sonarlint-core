/*
ACR-36ce8dce8dc441529d4da22904aff221
ACR-96c131fc1e8441038b83ebec66c62fc3
ACR-6496fc4604be455d9b8b89eed9fbd38e
ACR-30561e2c7a5e4f11a571d71670c63607
ACR-9a9a245e881b4dd3b1bb68c7676928a3
ACR-05235b0add7a4888ba16718714d73280
ACR-3a9af3749bf5490bb3fa7b2a6c72febb
ACR-2e5b7a4b667a4b0b8fe6224f6c674edd
ACR-509e3964f2544304a20d1c282bdbddf2
ACR-e68230d7c24b43c58280aa46fd429331
ACR-6d775e8cafd142729bb0430d8babf362
ACR-91002358f1bb4197994ef0fd301deae9
ACR-6368551bcc5642e9bcc204656a6e8f15
ACR-cf075df654a045909ec0571edecc92d9
ACR-ddc691de16e74d199a3f439ff59fa1d0
ACR-b2d576752d7a4a9fa19ccb0066d303a1
ACR-50fbae0c01eb4016adb509a985604dfe
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
