/*
ACR-66429ea32e9b4fdba6b31c0945251c59
ACR-4e7fe880f8b0435495e85ddefc5bb3e4
ACR-62cb24dd65eb4dda90800fa3183c34aa
ACR-46b182e7cdac48baa5d2a27f3bd0b3b4
ACR-20c6a865741d46e48004c489aa96e903
ACR-ae19ae666369422987c4c6364bdbb058
ACR-56460a36376b4fd296b80e082f778c47
ACR-d22ca35bbef544ae9de117401c4d7460
ACR-87a48e961de54d72bd4e38eb08a71f55
ACR-d31af3a954f44ac79d3f1d2c55bf8037
ACR-1819e328d76b406590ed18e300678b7d
ACR-179d12b5566f48a897294550ca94c8c5
ACR-8876bdd8557e447da1777400b361aa17
ACR-a2b2258c9a554ccfbf4697d78a994a7c
ACR-38ddf0a99b4d4cfe90866a21fb11e948
ACR-66d598f4faf44d42a9d4f27849f146e7
ACR-67aaddb0cb534747b50d100ad9ec28b8
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
