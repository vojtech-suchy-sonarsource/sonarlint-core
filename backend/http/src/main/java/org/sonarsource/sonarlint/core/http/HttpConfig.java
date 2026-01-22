/*
ACR-f6e20fbe873b4564b85761623da81451
ACR-e0bde55aab2248f1844a5ffbf8498c4b
ACR-d1923bdd6102417c94e6cda445dee6da
ACR-e9fb45d53c08492592eb3fcf92ca4e41
ACR-3a8efe9168d744b39d9bba3390206bb5
ACR-7b325e193e9a41cd8fd2ac45fc08bc83
ACR-c59f21f2ea804c72bf0aae7cf8c2e1d4
ACR-178a4eafdb5140d9bb103a343a5fcc3c
ACR-56fe591737c244c4aa3c01aaee4de0ad
ACR-f551e47e47c544bdb582de026722032b
ACR-da9a43bfeeb744ec8767f0e820b537cd
ACR-831bc55379b94da8b085b261c3e58ec6
ACR-f56150347aa7424090f1a579f08e1b5d
ACR-9609949bab1e4b3482f383346bcaef85
ACR-20b4bfd5c3874078b0826a6902e05c81
ACR-6aaadf6021ac4ea881860b733f0e4ae6
ACR-26a53641176041c296f1c09a432867ec
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
