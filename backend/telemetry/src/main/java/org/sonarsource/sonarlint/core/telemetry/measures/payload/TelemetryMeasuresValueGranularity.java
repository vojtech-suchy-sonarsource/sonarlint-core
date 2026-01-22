/*
ACR-6a695c4c0dcf4acb8b7814d2226ec0c8
ACR-9f749ad9dc3048768aec5885ad385c03
ACR-df773b6f9733472eb47ea5ffb1b7a4d2
ACR-704df0ae4fd84c19995edc3af7cec7d5
ACR-d7809fcbdd214eeead7cb6d88e046999
ACR-9f328300dc0b42cca52f309d51162dea
ACR-6a44c6c3828c47009218461ada8c8731
ACR-dd932d9773104ecd916f5ce9f233827c
ACR-c72f53f87f8a4524a8b80984c5d7d291
ACR-66dbe2479b3145408d19e6f5e0c11819
ACR-8e0207e8f3d14f14be14ee0198251cf8
ACR-902de5c66dac4311ba2e452caf3a302e
ACR-13d9e72e7dfe4bfa89fcb8e66cee02aa
ACR-fbed7298e1fd41eeb353a1979b1b73c9
ACR-0b502952622b4b0f9aafdeb2d791afe9
ACR-c66fc054783e45bcb49b466a42462f19
ACR-74d183f8beaa48be8b40539e982d4aac
 */
package org.sonarsource.sonarlint.core.telemetry.measures.payload;

import com.google.gson.annotations.SerializedName;

public enum TelemetryMeasuresValueGranularity {
  @SerializedName("daily")
  DAILY,
  @SerializedName("weekly")
  WEEKLY,
  @SerializedName("monthly")
  MONTHLY
}
