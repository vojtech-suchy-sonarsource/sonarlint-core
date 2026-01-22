/*
ACR-64097d806e18477eb35613ed366a9706
ACR-976a0179465c430dab7ff76c778025f6
ACR-dd1fb1abceb44f308bbf834a1b09e7c2
ACR-59235118c0a04307bf4eaf67b80d1e25
ACR-175c64ce87594667807beb8398bd0292
ACR-496c5d745c0f481389ea97d381843c74
ACR-254788ed961645bca77ff6a9b96fd29b
ACR-45257ca22fba40c094923605699a0f29
ACR-32cf142ab57d4e5fb7fa7b233d3a9e31
ACR-b9f85f6b0d1840aeadbc880045ec020b
ACR-7bb126eb2e8d4562b79c9749cff5ab17
ACR-c84b087da3c1408d9e0d898026860300
ACR-6a3c3dd74d9c4c569639476046d5e63a
ACR-1a8ae7e2e2c04540b2eb7e8e01cd2164
ACR-b75f952b69914500a7e2c90d67b621ed
ACR-4644dd82177c4ae0bd0d36f94c70591d
ACR-15dd7aa231104d24ba4685b60267ac33
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
