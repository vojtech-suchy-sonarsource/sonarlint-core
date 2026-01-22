/*
ACR-a49218042db84fbbbbfb78ba8e222bf2
ACR-53987302feee408ab3e61ecc4c0c05e8
ACR-e5390b0fd7cf41c2b2e17c7670f756fc
ACR-4cb8654f126541e0875b997570fb485b
ACR-097fcd9c4d864e5a96e92a756518646f
ACR-4d468d1190244f3cab3eb0359bb6f7fe
ACR-6158a8b995554d4a8c4d61053f4bb983
ACR-46c73bd8e1084e999553466332fba065
ACR-1a83cd1d6178412c83baa7377b0c2a0a
ACR-075d33d7cc8e4e378ca26453efe669a2
ACR-0102e35f59b04e15bea8227f9b3fe2f6
ACR-682848993a834063af13ea49532e6392
ACR-9e386c91ac2b40dcaf38b299ee361c45
ACR-ae32adc444284822ada65387f609b18e
ACR-8845dae0e5194d028e221c0986139453
ACR-38bc3f2f1e7f4d6a8ab0dd611f7bf730
ACR-ddffe39bdaa84fe9a5e0fd40d5aa5e33
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize;

import java.time.OffsetDateTime;

public class TelemetryMigrationDto {
  private final OffsetDateTime installTime;
  private final long numUseDays;
  private final boolean isEnabled;

  public TelemetryMigrationDto(OffsetDateTime installTime, long numUseDays, boolean isEnabled) {
    this.installTime = installTime;
    this.numUseDays = numUseDays;
    this.isEnabled = isEnabled;
  }

  public OffsetDateTime getInstallTime() {
    return installTime;
  }

  public long getNumUseDays() {
    return numUseDays;
  }

  public boolean isEnabled() {
    return isEnabled;
  }
}
