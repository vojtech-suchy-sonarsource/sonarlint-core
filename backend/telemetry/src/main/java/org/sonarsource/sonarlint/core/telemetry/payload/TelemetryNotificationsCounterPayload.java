/*
ACR-65ed1a0ea875480a9c7541315b1285ba
ACR-75022c749f7c4bddad598d0505b83e52
ACR-531b1bda1b224151aa31aa234b67c476
ACR-37601e7404dc4f2cab3d4ba45bda0ed1
ACR-ceb2eca521fd4ec5a982733bb34704c8
ACR-0595cdad035540dd9098b6d3a13f7fce
ACR-b0ba390aeb8245b5bc8318838fc41a4c
ACR-97f6d270202c48e98c5e2a734e2c2489
ACR-4670043eff284a69ab7f31eea2cc2935
ACR-ecb68de6b7dc49829fc54a8109d80a8c
ACR-691f29071f10423089c6dfcb9181172b
ACR-a4430316db02416785ec37f046c39793
ACR-08b749e9f5684c6e8a21edcc08ef8826
ACR-c70679a6911e4ae591842f661917ee2f
ACR-a7eab521fbd94b8fa5e1c3d99cb6488a
ACR-9ba40b56249f4ea4aef276e628e72026
ACR-5698917208c440cc84bd99a1c8009c05
 */
package org.sonarsource.sonarlint.core.telemetry.payload;

import com.google.gson.annotations.SerializedName;

public record TelemetryNotificationsCounterPayload(@SerializedName("received") int devNotificationsCount,
                                                   @SerializedName("clicked") int devNotificationsClicked) {
}
