/*
ACR-3a1bb2d3ada84917883461b2cdf4a386
ACR-250023fdc1ba48dd88a7cebc20b56fcf
ACR-28c8bb7cedce422193847b539ab69dd6
ACR-65e3a18dfd7f4cfb90ee63e861858195
ACR-1881a159d9d146ed8b824ba8a159546d
ACR-fc8b80436e2d4b828d82af1261f5f7dd
ACR-a1302a5fde464516b9a3a67528d99481
ACR-35af09bda46f4c659991cd4735d5167f
ACR-b1704217e1ce4878b8de6cfb186c218e
ACR-e482a5b21c51465dac7a8aa07405b579
ACR-6bbf6e73300244f0af7a995a5269839b
ACR-6e47ef4e2e9b44ae94e3c0256b5fde71
ACR-5f5df2b6a94e4eed8412386a039c6439
ACR-f1607d6b9eca46c3b3399c663a0f1228
ACR-d30b68203a734450ae43b832ec495b37
ACR-afd3a8a52cae424383cca57158cb7d58
ACR-c57893aee30545dd8e1364d7f0af50b5
 */
package org.sonarsource.sonarlint.core.telemetry.payload;

import com.google.gson.annotations.SerializedName;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry.FixSuggestionStatus;

public record TelemetryFixSuggestionResolvedPayload(@SerializedName("status") @Nullable FixSuggestionStatus status,
                                                    @SerializedName("snippet_index") @Nullable Integer snippetIndex) {
}
