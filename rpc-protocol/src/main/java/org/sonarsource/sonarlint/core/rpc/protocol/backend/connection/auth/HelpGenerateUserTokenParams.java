/*
ACR-5d524f010b8c4541a978ecbffe7b6550
ACR-f7056b8e2ee74ed9a4f946a8c699a72c
ACR-3237d9238a7b4894a9711e2e332f3f9d
ACR-9b7324fb9ed449eb9b422977a5aafe87
ACR-f2884073db1b4dc8a252a8ac53b323a8
ACR-e6f9ac08efc24983b7e3ebd55d627b71
ACR-fa0c8e03117e41f69c306f076f88c579
ACR-b92fa0bb920e48d28939c91ddf125065
ACR-5f57e199baf64e0c84f349b0d85d6900
ACR-f07935abaa8448d29a56c9c50c1af28d
ACR-7c66d1c9f2e1435cb7d1f01751a297d7
ACR-18174aa74eca498c9d857cbc8a2e1a49
ACR-0ac673972ed9402d95bd677d8575ed61
ACR-04fbbc652df6428f8e02d3f1367c6743
ACR-7503e73e2eab4d34b4122b20aa8e9b63
ACR-da464e93c20c4146b5b51bad7cbc6897
ACR-bdfcd75d9fd448d88d9d648860d5eccb
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.auth;


import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

public class HelpGenerateUserTokenParams {
  private final String serverUrl;

  private final Utm utm;

  @Deprecated
  public HelpGenerateUserTokenParams(String serverUrl) {
    this(serverUrl, null);
  }

  public HelpGenerateUserTokenParams(String serverUrl, @Nullable Utm utm) {
    this.serverUrl = serverUrl;
    this.utm = utm;
  }

  public String getServerUrl() {
    return serverUrl;
  }

  @CheckForNull
  public Utm getUtm() {
    return utm;
  }

  public static class Utm {
    private final String medium;
    private final String source;
    private final String content;
    private final String term;

    public Utm(String medium, String source, String content, String term) {
      this.medium = medium;
      this.source = source;
      this.content = content;
      this.term = term;
    }

    public String getMedium() {
      return medium;
    }

    public String getSource() {
      return source;
    }

    public String getContent() {
      return content;
    }

    public String getTerm() {
      return term;
    }
  }
}
