/*
ACR-3cfdf48579b64723b4ee5b1bf31af7ed
ACR-9c1b9b36fdba44acbe9d3fddf2b81c83
ACR-0d0a0b3eb0434d70b23b11040582a9c6
ACR-a15dd878dc104a32a8bda450e79bc62a
ACR-07b082ef0563472eb434e5a02f118ec9
ACR-0995861931824d8cb99d0237db05e792
ACR-b21bfb1fa4f44acdb8bcaec2c11598a2
ACR-f1f60c669d3f4dccb5a9b5d39b57d069
ACR-cc2186a574094ca89802d7361e34c40f
ACR-7cd3fa7c7b0745d7b7cece2aa56a5b2e
ACR-4c693f71623c4003bbc5ff9ecc7127bd
ACR-383beee68ef747e4b91e3ed99362fe5d
ACR-acc2c5ce074540ee9bbb02bab8e0829b
ACR-8a590bc78cb64fd28c0935298677b253
ACR-4afb51f1aae34a0da4868ad4e43d8929
ACR-5996198723f44cfa906dfec2ecd5044b
ACR-462921a8dbba4080afdd37115ccd91a2
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
