/*
ACR-06f7eabb9ca84be391dc0d84c81ea30c
ACR-f3add00d403d417d90e1b06d5ed344e9
ACR-f595516e28b647a9af81fbc0a1c47c22
ACR-a5f294cd98d0401ab969e453e99cf1c9
ACR-73b9d9bb338b47dc93c68ce5ac34dbed
ACR-8c51cf32aff147a0b94d3b904af9e142
ACR-175b46772a8d4a92af930e642b880ba9
ACR-fff4eaedd60b432bac940ed564104467
ACR-b319efabd28e4bc7adf41b4ad3290a04
ACR-4105dd5ed78b45798703812847383d4f
ACR-4b921837d88b4920805a7a9bcd2ed910
ACR-b3072badf70746989300862f71bae379
ACR-09a569a4927e423b826d9a13a6dab2fd
ACR-89ee077a4cfd478abce392bba14c4ad1
ACR-ff687c29086642eb80615299012acfba
ACR-9cfd3f650f814204bd8469221e1d0523
ACR-d6ebd0c846f7469c84bff1474c3c8e3d
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
