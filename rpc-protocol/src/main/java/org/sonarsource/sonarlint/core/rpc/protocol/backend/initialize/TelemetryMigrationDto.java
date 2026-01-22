/*
ACR-4795ae8d95144b87994ed6a81690c86c
ACR-972071c6f8f7484f9b4eeb2d81b61954
ACR-1fad47b057d042aa909bbeae805ad609
ACR-35ab7ccd8070425597255e510271dae3
ACR-61539b7fd9054a83b2f05d80fe814c46
ACR-b5826ece55e24ac5a5862d971753e033
ACR-707efa4dff3f483a8eed7b3344c412ac
ACR-214a73a2f13b47c6ae19b0ee78efb3e2
ACR-41c3be2802d845a186f44654e212d011
ACR-f9f0b7e274dd45878cd195f5f250efad
ACR-001dab55dd014ea581ccc88a148b31fb
ACR-10d1a0fc60f44104ab3e2ecbcd08380f
ACR-4533037cac3c417d9d7ac3b453c36faa
ACR-19ab35a3443048b9bbf004d78578a023
ACR-f145efdeebf04623b0c60d7003c578e4
ACR-d01cd042bc1649bda378b4e251cf85f7
ACR-0482c3a156ea4a4b88979993c632246e
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
