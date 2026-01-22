/*
ACR-af1303b2ea5244d1900365e3316abe8c
ACR-c186bacf2c4947afa75bc56b16d0ae22
ACR-15da7e58502f40dfa80e6cda27862276
ACR-f9f8edd004944a9bacf4c6ed0138f29e
ACR-d0a78713070e4e869926614c2ccb877c
ACR-87d5ac50f36449fc85c606f867e542dc
ACR-cfdb2e50851d40ea98011f9924490477
ACR-89813fc3243e499485192a8b80504c84
ACR-9dc1be413a784c95944ebbf63e5abe56
ACR-7f0d7054b6424359be1e460bcc0a2605
ACR-03b218d49cff40f4a4a82eebfb1df097
ACR-d0e170c6da8044b7b4357f8573ea0e3c
ACR-60052079cdd747e9be18887c5a90bb88
ACR-b7431c49a6ea40aa8e5f3fde94897b4b
ACR-83cb7b41877d4304ab19531bd9657e02
ACR-7dda9bfe058f4bf784ee11debc57f946
ACR-d9c1147ed4514e29ad23be5e196b4acc
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis;

public class DidChangeAutomaticAnalysisSettingParams {
  private final boolean enabled;

  public DidChangeAutomaticAnalysisSettingParams(boolean enabled) {
    this.enabled = enabled;
  }

  public boolean isEnabled() {
    return enabled;
  }
}
