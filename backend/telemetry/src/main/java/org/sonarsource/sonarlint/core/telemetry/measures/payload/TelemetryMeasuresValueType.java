/*
ACR-01f70c0e3c4c44769024d409f5bc37db
ACR-74bca6e2dda54239b27bd6432f672c69
ACR-adfff3c1fc294121a246b203dd82787d
ACR-eeb1ed1943ba4c278845fb19140ad611
ACR-9006f5ad9cd848e7b46a7511bf820471
ACR-7a6fd9bd02a647acb8ec2e6deac1a386
ACR-ee4016ac2466460c958f1be1866d3e97
ACR-9101ec650fc242378f8e81f0d23873ea
ACR-73a64f7f26024773a8245adb9eab86b4
ACR-8a04aba0dea442daba8dbdd21f53e6c1
ACR-cafe2bf3c7ae4b19a9aa34e0ccc40f3b
ACR-732ac7c95adc46a3803d826217579539
ACR-226a1fce00864169a4885626c6da4470
ACR-b2ceacd0ca524874acf398e43f207868
ACR-107baf74e658463980ead3084012aea1
ACR-2fc189e95762475aae6e51058f90d4a8
ACR-01e2fb920ea34bf096b80a7b80d681fa
 */
package org.sonarsource.sonarlint.core.telemetry.measures.payload;

import com.google.gson.annotations.SerializedName;

public enum TelemetryMeasuresValueType {
  @SerializedName("string")
  STRING,
  @SerializedName("integer")
  INTEGER,
  @SerializedName("boolean")
  BOOLEAN,
  @SerializedName("float")
  FLOAT
}
