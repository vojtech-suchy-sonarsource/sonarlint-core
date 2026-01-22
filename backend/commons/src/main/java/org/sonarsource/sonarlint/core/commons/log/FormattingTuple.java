/*
ACR-7d72493431ee4b368d9523a54190a802
ACR-1b3b891aaefc42199516a7cae0c1c6b0
ACR-13c8ba6b85ad4961abf40864dabcecc5
ACR-7651543540f54418875b3e65b50968fe
ACR-09c5d72b862447a59a2d976a3f0e0c79
ACR-54397e6d8aed46b19acc78a1d4b57e35
ACR-8628341c542644d0beca20a361ce293a
ACR-c57eb337352648a2b753b8e8953370e2
ACR-c77201fff8504a46a9e111a1c546a128
ACR-f0f9ff22527c45058fe8c551a1f02fe9
ACR-2f5dce8cc9ac4695883f53f65f7b1a0f
ACR-688acd3a57df47fa88fb5ba8cf4ac818
ACR-2b762f585dd644488de38410119baa79
ACR-f25fbb0c8dee45d180247098604da813
ACR-aff263835f24403989c7d93657c95fc7
ACR-fe6b1351c7664cf299512a7073e4be34
ACR-1828349927a3400fb11e4518042beeda
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
