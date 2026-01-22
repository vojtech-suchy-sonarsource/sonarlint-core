/*
ACR-502bfc8f884f4bfe9d997b080b508005
ACR-ba3f9ce13e0543db853ede32478b6763
ACR-adc4d4999e264a49b3358a50b27f8c36
ACR-adda074b942143c790ce66785a6f874e
ACR-201517bad4ed42b58e1ea09551b4a9bf
ACR-fdf9148203114d91aaab4fc346cb6d87
ACR-7b43f973406143b891361830c19ce0db
ACR-9d844bb5ecbb4b7ba4182061bc98fe3b
ACR-2bb6e6837be44e4e802886abc19c5764
ACR-18ecc0f32d924ec4824943e48fa33c34
ACR-4e914aa2a16747258ee5169a1c26fdde
ACR-17ed5e76332748728ee5d6dcca964f79
ACR-69eab6e4807d4683ba3fcf86b464cd07
ACR-975ae1e8442043a191ba3b701c544a06
ACR-1f6ffe3c6902498ea8c8e2329407bfd6
ACR-ef02a3d9032e427895cecc4224e7ee99
ACR-2ac4856237dd4d2096bfcc855ef7f913
 */
package org.sonarsource.sonarlint.core.embedded.server.filter;

import java.io.IOException;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.io.HttpFilterChain;
import org.apache.hc.core5.http.message.BasicClassicHttpRequest;
import org.apache.hc.core5.http.message.BasicClassicHttpResponse;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;

class CspFilterTest {

  private CspFilter cspFilter;
  private HttpFilterChain.ResponseTrigger mockResponseTrigger;
  private HttpFilterChain mockFilterChain;
  private HttpContext mockContext;
  private ClassicHttpRequest mockRequest;

  @BeforeEach
  void setUp() {
    cspFilter = new CspFilter();
    mockResponseTrigger = Mockito.mock(HttpFilterChain.ResponseTrigger.class);
    mockFilterChain = Mockito.mock(HttpFilterChain.class);
    mockContext = Mockito.mock(HttpContext.class);
    mockRequest = new BasicClassicHttpRequest("GET", "http://localhost:64120/sonarlint/api/endpoint");
  }

  @Test
  void it_should_add_csp_header_to_the_response_when_response_is_successful() throws HttpException, IOException {
    doAnswer(invocation -> {
      HttpFilterChain.ResponseTrigger trigger = invocation.getArgument(1);
      var mockResponse = new BasicClassicHttpResponse(200);
      trigger.submitResponse(mockResponse);
      trigger.sendInformation(mockResponse);
      return null;
    }).when(mockFilterChain).proceed(eq(mockRequest), any(), eq(mockContext));

    cspFilter.handle(mockRequest, mockResponseTrigger, mockContext, mockFilterChain);

    var captor = ArgumentCaptor.forClass(ClassicHttpResponse.class);
    verify(mockResponseTrigger).submitResponse(captor.capture());
    var response = captor.getValue();
    var cspHeader = response.getHeader("Content-Security-Policy-Report-Only").getValue();

    assertThat(cspHeader).isEqualTo("connect-src 'self' http://localhost:64120;");
    verify(mockResponseTrigger).sendInformation(any());
  }

  @ParameterizedTest
  @ValueSource(strings = {"400", "401", "403", "404", "500"})
  void it_should_not_add_csp_header_to_the_response_when_response_is_unsuccessful(String responseCode) throws HttpException, IOException {
    doAnswer(invocation -> {
      HttpFilterChain.ResponseTrigger trigger = invocation.getArgument(1);
      var mockResponse = new BasicClassicHttpResponse(Integer.parseInt(responseCode));
      trigger.submitResponse(mockResponse);
      return null;
    }).when(mockFilterChain).proceed(eq(mockRequest), any(), eq(mockContext));

    cspFilter.handle(mockRequest, mockResponseTrigger, mockContext, mockFilterChain);

    var captor = ArgumentCaptor.forClass(ClassicHttpResponse.class);
    verify(mockResponseTrigger).submitResponse(captor.capture());
    var response = captor.getValue();
    assertThat(response.getHeader("Content-Security-Policy-Report-Only")).isNull();
  }
}

