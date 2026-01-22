/*
ACR-bda76c0709e041d582753c8d1c402c79
ACR-4efcdc3fae88418c8d6dfdc4570ffd7b
ACR-718bd1308126419da260c819a5bdb3d2
ACR-c3349cb1fa694b8dbfa4e103dc3c18f8
ACR-37cc7f712e4c42da9f06489f7550c93c
ACR-68adfe8754034c5b9367f792f736c70b
ACR-30106afe137d470e948880378e44f642
ACR-e8a6b940c5c744a983c9bb7d0d901160
ACR-0787549435184292a0dae37e198acc42
ACR-b9e19b12f1ce4582bb8288dae1273124
ACR-529de57bbcaa45498fd0022cb0fcc357
ACR-62564eb14b264a268222d3e66ee47b96
ACR-b1ce3ca89fde40a584c80ae7b4b99eff
ACR-c5528a148c5a441787d528e48b57e9d5
ACR-d3f61f93c42a471ba4f01455d8f22c2e
ACR-850715aca6884c0784b1a72043e20dd2
ACR-a7eba3c1e3204639b5c49cb75ae38e4d
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
ACR-aef449605f644dedbe1d07dc913efb36
ACR-f2a739bf7e6841c4bb85176eea057601
ACR-58855e2d414141378ff907856c04059f
ACR-91207ed738b74b42be2e851b440ac72b
   */
  private static String validateKey(String maybeKey) throws IllegalArgumentException {
    if (maybeKey.matches(KEY_PATTERN)) {
      return maybeKey;
    }
    throw new IllegalArgumentException("Invalid measure key: " + maybeKey);
  }
}
