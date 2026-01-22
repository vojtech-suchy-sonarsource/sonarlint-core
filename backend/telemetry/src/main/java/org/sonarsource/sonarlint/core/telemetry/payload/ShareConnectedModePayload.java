/*
ACR-696c45e9874c4ec1b93417b273e3e756
ACR-d6313f7cd97e4a1194b32673041293a1
ACR-cb1fce5f48534069b29302bcc8378ada
ACR-9ad8e8a4d1a54066a69eedb0fe69ebcc
ACR-64387cc1c0d4400486ab5f01d550ae31
ACR-a924d1ea9fe34694b0f30fd8608b6339
ACR-60c98ad6c3e4483e907aefdf1a01fd63
ACR-fab19120744e41ab9c5cff52d7539c23
ACR-4e9d23e562334227a659423eb48a7bcf
ACR-9efac62be6c1451187d87f7226de90b1
ACR-d14c977cd1f443f08b3f232f909793f2
ACR-54fa014e5803486e88d50b60a41150fb
ACR-57616fb897d04badb084ce8843831086
ACR-e04db0558db34c9a938719ccfc4ab79a
ACR-4ae5558a8f35427593be63dd9216aa95
ACR-71209f1981fd472886958b5ae325f209
ACR-168dda79399a4162a8182e41f684b01e
 */
package org.sonarsource.sonarlint.core.telemetry.payload;

import com.google.gson.annotations.SerializedName;
import javax.annotation.Nullable;

public record ShareConnectedModePayload(@SerializedName("manual_bindings_count") @Nullable Integer manualAddedBindingsCount,
                                        @SerializedName("imported_bindings_count") @Nullable Integer importedAddedBindingsCount,
                                        @SerializedName("auto_bindings_count") @Nullable Integer autoAddedBindingsCount,
                                        @SerializedName("exported_connected_mode_count") @Nullable Integer exportedConnectedModeCount) {
}
