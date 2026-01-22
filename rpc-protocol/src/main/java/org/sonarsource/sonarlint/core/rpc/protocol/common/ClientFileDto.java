/*
ACR-2e270a046676410981ea505ee3bde50a
ACR-619aed519f754cd0aee903b07050c51a
ACR-c8719aa382b241fd9cdfb0a679c6c8a4
ACR-3a430f769a6d4bdc801c3c128d2f9984
ACR-2436fcb654f84c3b9a6938d227fc55c1
ACR-c9928660708c4156899702313dddf8db
ACR-6a5ea1d365c14ab89b3854d738822561
ACR-401207404c5a4407b25a6a4210b9c652
ACR-cdad60d692ce45d88007392570eb6a14
ACR-b7bd7cfe28ad451fa73d83e3345782ba
ACR-47f899113de54ce6899eaea8f934cab8
ACR-d4ca1b2cb09e4785954417589e36c35c
ACR-3bf58806190e409fbba7a3ca322bd24b
ACR-fcdb683cbb82415dba020f623936897e
ACR-db28d743dcdf425baba0d0dacd3c7af0
ACR-256cdf88a45845368891d48ea72f88d4
ACR-2532377061dd48ef9e86fc4692eaf343
 */
package org.sonarsource.sonarlint.core.rpc.protocol.common;

import java.net.URI;
import java.nio.file.Path;
import javax.annotation.Nullable;

public class ClientFileDto {

  private final URI uri;
  private final Path ideRelativePath;
  private final String configScopeId;
  @Nullable
  private final Boolean isTest;
  @Nullable
  private final String charset;
  @Nullable
  private final Path fsPath;
  @Nullable
  private final String content;
  @Nullable
  private final Language detectedLanguage;
  private final boolean isUserDefined;

  public ClientFileDto(URI uri, Path relativePath, String configScopeId, @Nullable Boolean isTest, @Nullable String charset, @Nullable Path fsPath, @Nullable String content,
    @Nullable Language detectedLanguage, boolean isUserDefined) {
    this.uri = uri;
    this.ideRelativePath = relativePath;
    this.configScopeId = configScopeId;
    this.isTest = isTest;
    this.charset = charset;
    this.fsPath = fsPath;
    this.content = content;
    this.detectedLanguage = detectedLanguage;
    this.isUserDefined = isUserDefined;
  }

  public URI getUri() {
    return uri;
  }

  public Path getIdeRelativePath() {
    return ideRelativePath;
  }

  public String getConfigScopeId() {
    return configScopeId;
  }

  @Nullable
  public Boolean isTest() {
    return isTest;
  }

  public String getCharset() {
    return charset;
  }

  public Path getFsPath() {
    return fsPath;
  }

  @Nullable
  public String getContent() {
    return content;
  }

  @Nullable
  public Language getDetectedLanguage() {
    return detectedLanguage;
  }

  public boolean isUserDefined() {
    return isUserDefined;
  }
}
