/*
ACR-9320b35002df4a758c1da33648eea50c
ACR-902ed83e18af4e9f916ff9244ae1b981
ACR-4566180e42da49b7956d99fba27f3c4d
ACR-0da7487485f046949b0321eff0f91155
ACR-3592ce0a54e544548c4ccd6e7ac8da24
ACR-6be30fbb791644a5954ecef13b3d96a3
ACR-68fb8be73c3c44c29e830c2b41f54416
ACR-21a1b541af104028a311fb090f3622c8
ACR-43ba442cc4d243aabdfa219510c23ce8
ACR-5f25ba1ef07b4745aaf2227a9832276c
ACR-5ef19fef3a534516b3d38b3d6fc415a1
ACR-eb279073a42d408fa038438a732dd32c
ACR-01a25780e89246cd9d102eebc9426f36
ACR-daf062fad1224e699d38db8caa8b5179
ACR-000abb00636c4410b2ab938e4b4d6bf4
ACR-5fc7e4da7d7b40079a5807b42d9a4907
ACR-e74afb4e713947b69dc482d552d54323
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.binding;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

public class AssistBindingResponse {
  private final String configurationScopeId;

  public AssistBindingResponse(@Nullable String configurationScopeId) {
    this.configurationScopeId = configurationScopeId;
  }

  @CheckForNull
  public String getConfigurationScopeId() {
    return configurationScopeId;
  }
}
