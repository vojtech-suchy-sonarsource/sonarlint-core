/*
ACR-079948d2e8e444dba351b13a57cf70b1
ACR-208de889857c41b68e570c97a2a20a94
ACR-f948da4cf5e945828af3f80003fd6410
ACR-945fbbe9af6f4e26b7757e0aa979197d
ACR-83e3e382e4114f56ab58f21a4e52ae9a
ACR-087677c137cf40b289f488db157cf396
ACR-d84633823ea8462cb63fac7fd5edb369
ACR-156667bbb4104f96b46e407f9ddc32f4
ACR-d09ee57b200942b196787a63d96c59c2
ACR-45703803cf4c42c3b0057e8c4a34d86f
ACR-49ca5bea5fa74a05a0100cdc3f53bf71
ACR-f0c4ffce508c40bdb67f38460a828d1f
ACR-295521b3b8e24e55b55b92daf86aa455
ACR-f53c07b0f28644a4995fdcf1d256da0f
ACR-ec54ac2f10664ecaa904c9a63c15a6a8
ACR-bb7b2796139a420cac447df840f7c494
ACR-c821b9e99bc04982b6d0ee1c83f7dbdd
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
