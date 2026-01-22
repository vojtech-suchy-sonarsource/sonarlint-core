/*
ACR-2822845f9e6b43918c1be7e4ef14f3b9
ACR-58c75c7b97c340a5ac5bdc730f1a6586
ACR-10ebea77372548729641c18c49df5197
ACR-2b1a61be5c8746e9954d75b05fd15752
ACR-401d49e6353c4801a23f521d7afe3ea0
ACR-ebb1d6928a7b49f2bc0b202e3d8c05e0
ACR-4044263b44ef41cc9b5b79de2f80fd06
ACR-beb015e8ec3c4599b33d0b585c8ac64a
ACR-c07c20b6531643e1827a38a044eed2ca
ACR-5ef877637a1447bfaff6f69294afb2a8
ACR-c97938ada0044432ad05f3e3238f25ed
ACR-eef2a05cf76d480fa4b7e833c5e2b368
ACR-1dafddb8505a43b485fbecf968f47823
ACR-9aa7e249ff634f6898c53c3103397a54
ACR-6158497862ec415291ff28fca823979b
ACR-1ef63b9a9e0943d19cdac4856dfae7ba
ACR-1f95dc8d4d0243b0a1899f71b898ebc1
 */
package org.sonarsource.sonarlint.core.plugin;

import java.nio.file.Path;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.InitializeParams;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Language;

public class DotnetSupport {
  @Nullable
  private final Path actualCsharpAnalyzerPath;
  private final boolean supportsCsharp;
  private final boolean supportsVbNet;
  private final boolean shouldUseCsharpEnterprise;
  private final boolean shouldUseVbNetEnterprise;

  DotnetSupport(InitializeParams initializeParams, @Nullable Path actualCsharpAnalyzerPath, boolean shouldUseCsharpEnterprise, boolean shouldUseVbNetEnterprise) {
    supportsCsharp = initializeParams.getEnabledLanguagesInStandaloneMode().contains(Language.CS);
    supportsVbNet = initializeParams.getEnabledLanguagesInStandaloneMode().contains(Language.VBNET);
    this.actualCsharpAnalyzerPath = actualCsharpAnalyzerPath;
    this.shouldUseCsharpEnterprise = shouldUseCsharpEnterprise;
    this.shouldUseVbNetEnterprise = shouldUseVbNetEnterprise;
  }

  @Nullable
  public Path getActualCsharpAnalyzerPath() {
    return actualCsharpAnalyzerPath;
  }

  public boolean isSupportsCsharp() {
    return supportsCsharp;
  }

  public boolean isSupportsVbNet() {
    return supportsVbNet;
  }

  public boolean isShouldUseCsharpEnterprise() {
    return shouldUseCsharpEnterprise;
  }

  public boolean isShouldUseVbNetEnterprise() {
    return shouldUseVbNetEnterprise;
  }
}
