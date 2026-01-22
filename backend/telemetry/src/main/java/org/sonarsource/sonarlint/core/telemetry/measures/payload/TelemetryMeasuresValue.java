/*
ACR-0b9d4f21c89b4e98ab5a69e02a3884f4
ACR-0438eca7266547758698b20e4d70abc5
ACR-9bdcae32aef44fdc80cb8980f69d98df
ACR-785d3600170c455b99ae0d931a1abd49
ACR-8d6e074b2af649b3b124d91640621fbc
ACR-8885399609dc435da2075e18675cc014
ACR-3858a32a1a954580aa0f419eb0b2563f
ACR-9aec107079974f39a52cf1c8fa4e99a1
ACR-dd68bbb5db6a486d857086e4e7901a39
ACR-ff7a47b2481b485087c22d41eec6e6ce
ACR-0c4cfa20495b4be0a73e2506fcb93aa6
ACR-27d714546f134f858f195dfaa4e3b246
ACR-9c948e66415f4a8782fdfeb04dec55e2
ACR-1bd3da8427414fc1840d405a6d439b58
ACR-b9afe223e769458eb76a0161e60bfb19
ACR-fa5ae63409f64d1eb0f12ef6409f066e
ACR-0c640d3d5ece48c5beb4e2494f7e4bb9
 */
package org.sonarsource.sonarlint.core.telemetry.measures.payload;

import com.google.gson.annotations.SerializedName;

public class TelemetryMeasuresValue {

  private static final String KEY_PATTERN = "^([a-z_][a-z0-9_]{1,126}\\.)[a-z_][a-z0-9_]{1,126}$";

  @SerializedName("key")
  private final String key;

  @SerializedName("value")
  private final String value;

  @SerializedName("type")
  private final TelemetryMeasuresValueType type;

  @SerializedName("granularity")
  private final TelemetryMeasuresValueGranularity granularity;

  public TelemetryMeasuresValue(String key, String value, TelemetryMeasuresValueType type, TelemetryMeasuresValueGranularity granularity) {
    this.key = validateKey(key);
    this.value = value;
    this.type = type;
    this.granularity = granularity;
  }

  /*
ACR-faca3edb691246fc997d4fcae67474c3
ACR-0db91e6b1436499b958e16e5c028aa2f
ACR-ae64125acedb4c2fa80ee9b55b7ce2cd
ACR-1e68ca33b68b40d49b3150f246ac8532
   */
  private static String validateKey(String maybeKey) throws IllegalArgumentException {
    if (maybeKey.matches(KEY_PATTERN)) {
      return maybeKey;
    }
    throw new IllegalArgumentException("Invalid measure key: " + maybeKey);
  }
}
