/*
ACR-2b603ac3116b45398f52e04cd8c82029
ACR-1461721cc70e471f8b963dd8d5037803
ACR-71b2b78246a94c22a236ec0d6e3dad3f
ACR-01bc8eca99bd475898a2ab69c4d33b7a
ACR-0ca0c20efe024c278a2bd8d9c6e3b694
ACR-7b275d5c4bdc4293aa6ecb1c2a2d3154
ACR-4ac58d6301434b6ab6f832203eafa416
ACR-dac65cce0fa344e7a3d76ae6c3d949e8
ACR-d0f6a7278aa541b8984960a898930f7a
ACR-4db3990ef6b247dc8d0c5d239fd7c35c
ACR-03d5c3fbd53b4a47916331ad2bebfcdc
ACR-f82594bf269840ce95abf9b95e9d9844
ACR-d6c615d693ac4339a573d6f86c5abaa1
ACR-4413d7ba3fac4cbf8daf711578e44eca
ACR-39b6b9550c874d8ca904188e2b950a67
ACR-7840e69484e84214a3ade9d2a1ab96fb
ACR-743ecbe14bf841b395dc1c70204ae0d1
 */
package org.sonarsource.sonarlint.core.telemetry.payload;

import com.google.gson.annotations.SerializedName;

public record TaintVulnerabilitiesPayload(@SerializedName("investigated_locally_count") int investigatedLocallyCount,
                                          @SerializedName("investigated_remotely_count") int investigatedRemotelyCount) {
}
