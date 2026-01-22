/*
ACR-9500c83fceee46598e47c11546cc22ab
ACR-a7903b0d23fc4ac89641eec5fa8206af
ACR-be3de1e541d64e478148991f0324df83
ACR-3bdac123f20e42ca85342e4c78ddf144
ACR-bd9787610b124221a6823ea1c9f3dbe9
ACR-6a59ee9be8aa4bec97756267c42d42a0
ACR-e28e8d48538e4edfb61846001b0b6fcc
ACR-e4e4330e62ed4c9f80f8d345100a691e
ACR-461ac9b9aecc4d458ee90dc02aa02b2f
ACR-5418f9f884d74a489436064781b2456a
ACR-a59c7607a2d94277a93e2d92e24e30c0
ACR-b18ab6a7f1db4182ab8869ecd6912de0
ACR-dc2faaedac99470eb7222d0834ba65b8
ACR-a2f92be3f17f47b5a1574c7fb26d793a
ACR-9c7ccc9f72b04173b52008a76a136510
ACR-f33f847761cd4ec3b19bd560a5cf8a57
ACR-f0ecea04187a4aecbb0f053da4ffc875
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
