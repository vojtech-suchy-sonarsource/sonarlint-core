/*
ACR-1ffe3f62fb224ca6a1b95af45fcb04bc
ACR-c99d87c6950e473cb8524da96f861f1b
ACR-4de0e0bfa59e4126be00f0a824b70ff1
ACR-679fd748858a416a81085fa4a5a9ed66
ACR-91f447cfa9c54650be522af891ae73c8
ACR-aa9d18b2851b42818613f4bae3ac3b9c
ACR-3ee30765001244458abcc9d69f4e2660
ACR-9e30c01f0c774b0eaf0651eb0b2b24f4
ACR-c59429da0fdd4ebea3078a10c9e8ceea
ACR-498443ec55964dc38247d8346260243b
ACR-1932b93dab5d4ea29a297f51b01a270a
ACR-f3ca2eee0c154b3d8d91c5a3b0f02664
ACR-becfe4c62baf4c7a8cfbf942a8d2bbdc
ACR-f8c48f09e0ab445b99659419b714b4ac
ACR-4e7f8338d4a54576b48bedd910ef4e7f
ACR-328c70e6666b4ed5945ec657c8112f0b
ACR-1c1cb71ac0404fd8a5d82f4af45f45b8
 */
package org.sonarsource.sonarlint.core.commons.log;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

class FormattingTuple {

  private final String message;
  private final Throwable throwable;

  public FormattingTuple(@Nullable String message) {
    this(message, null);
  }

  public FormattingTuple(@Nullable String message, @Nullable Throwable throwable) {
    this.message = message;
    this.throwable = throwable;
  }

  @CheckForNull
  public String getMessage() {
    return message;
  }

  @CheckForNull
  public Throwable getThrowable() {
    return throwable;
  }

}
