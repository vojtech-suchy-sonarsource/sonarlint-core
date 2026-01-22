/*
ACR-13976da372d8436783325413ace63894
ACR-62abb8b6064840ca871c677e42fb99aa
ACR-90f72f9a2841485288d6db6ccfdbb3bc
ACR-a378322860524962b3f52b77ebc04f28
ACR-2017693991f948a1804e545997f8b08d
ACR-9eba776a320e4f6aa094c902635748da
ACR-ef45fef58df142b88cf9f857bb25b3d5
ACR-200e46af3ccc4a3b881f0b2f88a941e5
ACR-c7366fbe65874603bfabec9865853b7c
ACR-262d3dbc9f5146daacbfcbd2d4233683
ACR-7b44503eb4c84cb8b670e23cb6d23df9
ACR-999051b329cf4bb894c48e6dee4269cd
ACR-4136b183d7ff43869b35c7684d9af7a7
ACR-8a844bf684df4ca2abfc40351748e3b8
ACR-7cd6916e4612431d8d1414fad5d85962
ACR-b74268ade630469ba43665a246442fe5
ACR-a444bcf926644bdb994462baace91033
 */
package org.sonarsource.sonarlint.core.telemetry.payload;

import com.google.gson.annotations.SerializedName;
import java.util.HashMap;
import java.util.Map;
import org.sonarsource.sonarlint.core.telemetry.TelemetryHelpAndFeedbackCounter;

public class TelemetryHelpAndFeedbackPayload {

  @SerializedName("count_by_link")
  private final Map<String, Integer> counters;

  public TelemetryHelpAndFeedbackPayload(Map<String, TelemetryHelpAndFeedbackCounter> counters) {
    this.counters = new HashMap<>();
    counters.forEach((link, count) -> this.counters.put(link, count.getHelpAndFeedbackLinkClickedCount()));
  }

  public Map<String, Integer> getCounters() {
    return counters;
  }
}
