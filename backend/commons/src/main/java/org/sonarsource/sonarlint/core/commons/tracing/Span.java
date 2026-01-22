/*
ACR-adfe57a9f21149aba52fa2c1799947d7
ACR-7d3712eed8ee4dadb59371665cdc94a8
ACR-a43054ece67e4873bfdfd56b7b91e531
ACR-b8c9792bb346422abb8967534869aee3
ACR-42e3bed992664c949d0c9e810793a5d2
ACR-9ded98e632d14a05af44056df9acd153
ACR-1e4fc7389ca041cda425aca1513f6d11
ACR-0430c75555d6496384f87ddcc34edeec
ACR-2ff2c20e91e644d9921120e9dd8c039f
ACR-5c5866b6112b4341bccf8dafedc50289
ACR-7943c91256484c7ca9b20f25e08870fa
ACR-d6cc4d87423f418c9f1f35e3f1904f5b
ACR-dddaed147b7d482d8a2b35c1e032d531
ACR-ab7b0107e9674fc4abaa0b62696a4f80
ACR-82064f6ad20c4a06a98dec430cfd897b
ACR-1939f76893f74774a6c90476f9c9fbcf
ACR-3062f82fc051440b92d71f6e56e1641f
 */
package org.sonarsource.sonarlint.core.commons.tracing;

import io.sentry.ISpan;
import io.sentry.SpanStatus;

public class Span {

  private final ISpan sentrySpan;

  Span(ISpan sentrySpan) {
    this.sentrySpan = sentrySpan;
  }

  public void finishExceptionally(Throwable throwable) {
    this.sentrySpan.setThrowable(throwable);
    this.sentrySpan.finish(SpanStatus.INTERNAL_ERROR);
  }

  public void finishSuccessfully() {
    this.sentrySpan.finish(SpanStatus.OK);
  }
}
