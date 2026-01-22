/*
ACR-157be8906b5c4b80b7ed54bd6c52973d
ACR-23273b628da44d77b84c3d9908138b09
ACR-6dcb4410f28247618d7d56460544cbc6
ACR-4ca4e57d9176481b9e6bbd1d77987bbf
ACR-9d8b6390b7bb46d5831e834649eb5ef4
ACR-b6a54ebd7644428488e10683bd6f4134
ACR-38acff94c478459f930a6e93491a05bb
ACR-c759b21311a04b1da8bf23ab0ea6637c
ACR-99c6af1a54f44285a0570557f9e01748
ACR-5c7f53dd82e74496a36658667f527953
ACR-b54a6389acc747ec8ad41d17283ff92d
ACR-c270e6f320f741fcb4ef1aa680dc58e9
ACR-c9012ff4dd1d426aae5d354aa46b2e16
ACR-fc710bf573f742df847d74af0528122e
ACR-f7d271f2ffbe4432b73bfb7d4fbeb362
ACR-2ae9cc2573144bba8a7b46290ecce96d
ACR-0c7cf24fd19042d68f06cba8794b1fbc
 */
package org.sonarsource.sonarlint.core.telemetry;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public class TelemetryAnalyzerPerformance {
  private static final TreeMap<Integer, String> INTERVALS;
  private int analysisCount;

  static {
    INTERVALS = new TreeMap<>();
    INTERVALS.put(300, "0-300");
    INTERVALS.put(500, "300-500");
    INTERVALS.put(1000, "500-1000");
    INTERVALS.put(2000, "1000-2000");
    INTERVALS.put(4000, "2000-4000");
    INTERVALS.put(Integer.MAX_VALUE, "4000+");
  }

  private final Map<String, Integer> frequencies;

  public TelemetryAnalyzerPerformance() {
    frequencies = new LinkedHashMap<>();
    INTERVALS.forEach((k, v) -> frequencies.put(v, 0));
  }

  public void registerAnalysis(int analysisTimeMs) {
    var entry = INTERVALS.higherEntry(analysisTimeMs);
    if (entry != null) {
      frequencies.compute(entry.getValue(), (k, v) -> v != null ? (v + 1) : 1);
      analysisCount++;
    }
  }

  public Map<String, Integer> frequencies() {
    return frequencies;
  }

  public int analysisCount() {
    return analysisCount;
  }
}
