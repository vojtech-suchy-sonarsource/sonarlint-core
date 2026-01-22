/*
ACR-c6ff6f85ab614b37a0d8e6c54b90b142
ACR-2e240475933846eab0f4e936025f8057
ACR-2325357c666049f68180e12783cc83c8
ACR-85f27ec0e87a4f499bb5146feccc8fca
ACR-348ca9466bc94ed789c5998f8ee7d29b
ACR-a522342cdeb241ca89f882bf246f4ea7
ACR-37504f91f3434791a701500ca8f574ca
ACR-abf1a4e0759a40b39529c3ca8cc06a16
ACR-4b8b42efde8a4ba5a7b1d35d716e63bd
ACR-1c95b482c1294dfba249dd9c3fd8fc42
ACR-a35cdfbe303749a39cc5859117f08daf
ACR-24b3125dddb64428a38e499a3de1f1eb
ACR-cdd70c68234a4ce29726f61026ff0cb6
ACR-3d804a387c69443c997bca851129c2da
ACR-6fe5af7899304569b35a8dc678e34d26
ACR-c9d8ecec11a04448bb740de7f7c89373
ACR-f6020f674b3d4cab979ce1352656bd1d
 */
package org.sonarsource.sonarlint.core.telemetry.payload;

import com.google.gson.annotations.SerializedName;
import javax.annotation.Nullable;

public record ShareConnectedModePayload(@SerializedName("manual_bindings_count") @Nullable Integer manualAddedBindingsCount,
                                        @SerializedName("imported_bindings_count") @Nullable Integer importedAddedBindingsCount,
                                        @SerializedName("auto_bindings_count") @Nullable Integer autoAddedBindingsCount,
                                        @SerializedName("exported_connected_mode_count") @Nullable Integer exportedConnectedModeCount) {
}
