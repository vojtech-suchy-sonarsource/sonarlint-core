/*
ACR-1112f9946c5a44acaa49a05d028148b3
ACR-2dfa66bfe6864fc6956c17f53e508230
ACR-e0919bf7337644b7888420b32dad9719
ACR-f890495ace474d1bbe8872ab3919be2a
ACR-f46e0e4d0e3545a9aa990b2775947a5b
ACR-a0ce9a2feb6b4cb88bb2de6fbe0c1190
ACR-20c874634cb94d17b13ad86f052e555d
ACR-c580b01551834f38a899045c4bbf1d55
ACR-6ffad0d5ba194b67b3520dce634e1451
ACR-e2e63ff0185b48aaba08a4af1b40370b
ACR-e6641f1b2b1e4eb9b854c1d30c12d9b5
ACR-387528c92e5849cfbd71f04ff3d72190
ACR-c8298b9ee7144e4bbf18b7081c25e69c
ACR-7a5ae7a022994f079005943ce08b494e
ACR-b5779a4ac4fd4f44a97d617bccd73159
ACR-f607153f33d544d3a60643e2b6255ea5
ACR-68ceddff11ce464c97f57db4ea2b49eb
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
