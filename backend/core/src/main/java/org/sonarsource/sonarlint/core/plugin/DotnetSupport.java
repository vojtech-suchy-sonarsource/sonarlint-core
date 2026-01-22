/*
ACR-e1390deabef543959aa95b1b7bb3946d
ACR-f883058d9e9c40e8b44c2aac592f69e3
ACR-ab519b11295d44e889785cb3a216c50a
ACR-958b379bf7fc4eb4b79654e4c3c1d4c6
ACR-f7d05c56fa1d47fba5bec631a78351e8
ACR-e46d5167d7dc4d3783237c5f23930fe2
ACR-ee595a015892475799269c9fe9d97c5d
ACR-59062480a63d4279bbf836637236f1fd
ACR-fb1de8130e6d46c587fcea328c494709
ACR-1a5b49019e4b4ae8872c52f84b8b7329
ACR-67b02a9958f64a74a93b16258b6d5852
ACR-648d61cd53b445dfa651039b172db7fb
ACR-ecc844eef08247c6add3152d3e1b36e3
ACR-d8c1626e25f949939087eceee7a4f19d
ACR-2a05f514ae75462c8a7af956caec3b27
ACR-225232f8bd4846f3804634dab4712c9b
ACR-2262ff3b7ba541f59bed31b2a139da36
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
