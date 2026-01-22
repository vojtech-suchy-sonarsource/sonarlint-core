/*
ACR-c64bc92e15874fee835fe69eb8fbfa80
ACR-0daf0286841f418aa33cbca01b21b367
ACR-6f4d42b0a4ac45c1b82fd3f647412cd7
ACR-453a3aa8e27c49a59248e786f7bf17c9
ACR-9f138cc71b9f4749ad0a867e87e75290
ACR-6499ff4cea424389adf5b5bcf298278d
ACR-b5d1e02fab334aee96f34ef3df97e1b6
ACR-c652686caec74f1d8bd9c3b2e3b57048
ACR-e7f9f4a0d23845f7aafeb448c0b629ef
ACR-45fa3edf31364b579da6d05f64c89e89
ACR-40b564aa9b7043f5a1177eae416e6a4a
ACR-18fe2e6c986b40a68da4dc255da45e67
ACR-2e527099ab7140d4969d2e19ada7293c
ACR-da9d141941144ec59f8e8034902604f4
ACR-6ce5414017254893981d69586b043ec8
ACR-c565908bdfcf4e3890cdb9a4ad4d4371
ACR-ea34c293f1164f25affba2fe720fec1c
 */
package org.sonarsource.sonarlint.core.http;

import javax.annotation.Nullable;
import org.apache.hc.core5.util.Timeout;
import org.sonarsource.sonarlint.core.http.ssl.SslConfig;

public record HttpConfig(SslConfig sslConfig, @Nullable Timeout connectTimeout, @Nullable Timeout socketTimeout,
                         @Nullable Timeout connectionRequestTimeout, @Nullable Timeout responseTimeout) {

  private static final Timeout DEFAULT_CONNECT_TIMEOUT = Timeout.ofSeconds(60);
  private static final Timeout DEFAULT_RESPONSE_TIMEOUT = Timeout.ofMinutes(10);

  @Override
  public Timeout connectionRequestTimeout() {
    if (connectionRequestTimeout == null) {
      return DEFAULT_CONNECT_TIMEOUT;
    }
    return connectionRequestTimeout;
  }

  @Override
  public Timeout responseTimeout() {
    if (responseTimeout == null) {
      return DEFAULT_RESPONSE_TIMEOUT;
    }
    return responseTimeout;
  }

  @Override
  public Timeout connectTimeout() {
    if (connectTimeout == null) {
      return DEFAULT_CONNECT_TIMEOUT;
    }
    return connectTimeout;
  }

}
