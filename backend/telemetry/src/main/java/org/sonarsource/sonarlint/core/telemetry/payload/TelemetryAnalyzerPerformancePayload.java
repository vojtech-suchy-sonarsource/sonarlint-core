/*
ACR-718fa329a06a47a7a875076aaaef9af1
ACR-5158f01e4024444fbb0b4373c8b34efc
ACR-4790a9be6ca44ca7a0dbcd799d3bc6f1
ACR-13dcb8da7a0c4d22bcbde4f3586b0981
ACR-a3c0c7db1d8c4954ba391c373cf40e45
ACR-980b98983e8348edbc4ba92fce4f04c9
ACR-e81a373dec424374a83dc11e3c77b2ce
ACR-6c659ed1018a48e58590604431b36693
ACR-055f8be5cc2540deab6169f0cdd726f3
ACR-53ef48a5fa2143c98613155bb0e7d19c
ACR-a5d515141ef54af2a40dd440cd5cde74
ACR-a051df2eb8144cf09a545d32d0169a55
ACR-73f42affcf5d47c994cb3a9bf7eba319
ACR-39f32444b64a4e3787c53692fdf34770
ACR-be84731285924caeb7700bd381bd0cd9
ACR-2d922077657e4e3d900f1cdc9d06601b
ACR-63035257c49f4051924a6eefc539690e
 */
package org.sonarsource.sonarlint.core.telemetry.payload;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;
import java.util.Map;

public record TelemetryAnalyzerPerformancePayload(String language,
                                                  @SerializedName("rate_per_duration") Map<String, BigDecimal> distribution) {
}
