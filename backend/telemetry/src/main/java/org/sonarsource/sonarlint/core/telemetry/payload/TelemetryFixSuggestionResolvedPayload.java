/*
ACR-7f2ff3d28374423aad6923050cc2bc58
ACR-f94549c07d5e4d06a4bffa2fff6c1f98
ACR-b2352906abfb49a082cf8617dff6a23b
ACR-63f74d037d0247d987abd9de78cea87b
ACR-beeb9bcf32ca4e74be0810851ee23cdc
ACR-ff231ea7ac4d4a359e22773693b0d470
ACR-fc3a95fa32f048f7be962f7d2c95af40
ACR-aba78e0aefc2423081849daa4a3a1fc5
ACR-66677d35e7344d59ac6b2078f40c4e26
ACR-8d9bab373cf24767be9683a29c169db0
ACR-11a7202c43db4289bda7a2417ae6404b
ACR-1098a033d6874486be15e255e32c0820
ACR-2eee3381c5e84b4d976a8e9a4d046794
ACR-efa5e4fb5e46433aa628f3c59402cdeb
ACR-b4849d16784c4368896e3c4ab85c82bb
ACR-596a44d8c6af43ba86965594cbafe36c
ACR-3555ed242b0e4f5bb60ec23e9a35b635
 */
package org.sonarsource.sonarlint.core.telemetry.payload;

import com.google.gson.annotations.SerializedName;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry.FixSuggestionStatus;

public record TelemetryFixSuggestionResolvedPayload(@SerializedName("status") @Nullable FixSuggestionStatus status,
                                                    @SerializedName("snippet_index") @Nullable Integer snippetIndex) {
}
