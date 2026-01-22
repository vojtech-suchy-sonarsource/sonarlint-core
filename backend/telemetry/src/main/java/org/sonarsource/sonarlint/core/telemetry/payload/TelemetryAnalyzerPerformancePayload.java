/*
ACR-cbb683d5fc5f43ecaf51fb45dd02f68e
ACR-f6b7382436f144bfa8bc403f3c12367a
ACR-bece580815ee4355966c432c1f361eb1
ACR-f8d72b25a7e94ebf88112020e59c39d2
ACR-f1ae1857dc364e9fb657f00dbda45903
ACR-8139ab2c0ff84a4fb962bc19926d8338
ACR-4dd4b48926514a73bf5f7e609394d716
ACR-8651e37d4b5a43b08b9bc08fe7d66ccb
ACR-0ea58e9ec7134a2c891f9413bbcada81
ACR-1b636ca55b7d48528db035a9b0236e6d
ACR-84ad95fd45544efe9337b240db25db58
ACR-b3e2bc352b58476bb6f616d4b82ab74c
ACR-8e968815970c46df9cb1f57ae214481b
ACR-552f5223948c414da9b441a630d5bd31
ACR-5e98dd7dfc1e4619a39c904a3d0011b4
ACR-adecf565730e4ccd8c81b51f81fc30cd
ACR-9bb6c50752044897bdf0913d53920ad7
 */
package org.sonarsource.sonarlint.core.telemetry.payload;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;
import java.util.Map;

public record TelemetryAnalyzerPerformancePayload(String language,
                                                  @SerializedName("rate_per_duration") Map<String, BigDecimal> distribution) {
}
