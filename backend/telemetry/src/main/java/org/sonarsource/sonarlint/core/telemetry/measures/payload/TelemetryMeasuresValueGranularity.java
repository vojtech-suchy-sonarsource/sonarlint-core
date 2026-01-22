/*
ACR-2a677702846e4114a44271f2bd70d4e4
ACR-7ac28af0bfd244b4b178c20ed55b9772
ACR-42f571339fd24815b6af2d82d5d02ab9
ACR-dc9f6597d5d9447799d0b9c77b67fd75
ACR-e02d5d03f6994d2483b1fb16e98d54e1
ACR-326150a734e0471ab1054d838316f9bb
ACR-3c0f050896c74e60a8b7b4a17140a403
ACR-53fc5cefcdee4074ae4a6a369e93be3b
ACR-46c6749c2dad46a68a03d8258ebb4d48
ACR-a41723f01c634a3c937efa9944a1fc93
ACR-275a3953dee24b709d865ab2780b76e2
ACR-e1ceee1510dd415b8e4ba3963bbae8ad
ACR-939ba9516530404396be9e684be4d296
ACR-35cb1f9426ef435fb7d56e063db552de
ACR-36fbc21ab0da4b9d840fbf5bc578b59b
ACR-f0395a60d68a4635a77bf315bbe7ff40
ACR-348dfe1b2c6346e19a7cc94d7865cfad
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
