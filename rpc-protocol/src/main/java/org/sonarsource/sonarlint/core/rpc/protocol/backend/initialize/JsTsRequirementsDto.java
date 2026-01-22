/*
ACR-c6cac300be5e4382b0f51ba1c4375c88
ACR-ad7ffea4be85442abb9dfd8cc69eb33c
ACR-4884dfcae01a4b75a66a2fa431696181
ACR-029addec8aac4e6dbcd92ddb4e102336
ACR-6de4b8aec6164721a01efeb78ae3a149
ACR-13296582a4d54cd0887818436c30023d
ACR-112c5ab4638649bdbdcb4c09b8d15d7f
ACR-0ffef3b1494445069ff027fd3025fd08
ACR-d636c39ca6cc4011b4bc70bf351183e5
ACR-b6e71c60835741ff8fe08b3a9519aeb9
ACR-36f71bcce9e5473cbbe7ffbede9862fa
ACR-d74e3f8b519f473e810e4b4a9c729a7d
ACR-825268aedaa548c18a37c6024262298e
ACR-9771edda4f8d47c385310b15aa1b60c2
ACR-2324c8ed17064d0f91210dc161d74689
ACR-d5906ae5ea4e4163bcffa3bebbb8995f
ACR-11947af9961b423fb0aef9cc27db7a1f
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize;

import java.nio.file.Path;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

public class JsTsRequirementsDto {
  @Nullable
  private final Path clientNodeJsPath;
  @Nullable
  private final Path bundlePath;

  public JsTsRequirementsDto(@Nullable Path clientNodeJsPath, @Nullable Path bundlePath) {
    this.clientNodeJsPath = clientNodeJsPath;
    this.bundlePath = bundlePath;
  }

  @CheckForNull
  public Path getClientNodeJsPath() {
    return clientNodeJsPath;
  }

  @CheckForNull
  public Path getBundlePath() {
    return bundlePath;
  }
}
