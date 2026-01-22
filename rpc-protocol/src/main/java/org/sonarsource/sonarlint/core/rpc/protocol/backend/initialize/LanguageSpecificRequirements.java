/*
ACR-f294a276aa054b3b901094d07dd7dcaa
ACR-a0b1cff314154fccb0177479639cc0b0
ACR-f0b0b90640f747e6b1456879923b7a01
ACR-e0dd05f16745414292a097a4d6665af6
ACR-fba21021986a42fab62e56e9a81c0b1f
ACR-0eac35fb4e5a4b29a6675652e06d060c
ACR-281bd4b10bf34064a10848469773b69a
ACR-e5a46af3c0e045e7bef317944197c7fc
ACR-d823a8fa15454650ac2b0fc6b28b5750
ACR-bcbdf4d159204e5296b8a24499b31e01
ACR-0e5f8e5259a349e282f28580326dc638
ACR-f2dab35045624b26ac67d80e41185b9a
ACR-cf91c49a72154d038aca456a6df3e5c7
ACR-a4f22293681f4aa9b7405a0ea0bd68b4
ACR-5c5d2655e5d34a02bebd70f06790a642
ACR-9577ad62d9594c2fb08fed305ab0aeed
ACR-119bd6e82fc249d599a0d401062d62b1
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

public class LanguageSpecificRequirements {
  private final JsTsRequirementsDto jsTsRequirements;
  private final OmnisharpRequirementsDto omnisharpRequirements;

  public LanguageSpecificRequirements(@Nullable JsTsRequirementsDto jsTsRequirements, @Nullable OmnisharpRequirementsDto omnisharpRequirements) {
    this.jsTsRequirements = jsTsRequirements;
    this.omnisharpRequirements = omnisharpRequirements;
  }

  @CheckForNull
  public JsTsRequirementsDto getJsTsRequirements() {
    return jsTsRequirements;
  }

  @CheckForNull
  public OmnisharpRequirementsDto getOmnisharpRequirements() {
    return omnisharpRequirements;
  }
}
