/*
ACR-5e0cefdcefcc43ce8df67834e5da6f10
ACR-8d05403a6b124b3881312c175252d567
ACR-11ec40ee40f14d41a517ec95582f55c0
ACR-dfebda39b3574490a1f5e7ebab1112ed
ACR-c55a83942bc249559df886a38d76b99b
ACR-f4c6b37abb74411e8ebdd663c265e3bf
ACR-4fc33b9bfcbc41f2952f6845bec1b30d
ACR-173e9cfc803240d098744368759fbb09
ACR-7a521344b1e24d4e94364edc07927e2a
ACR-4884dce5a966428eadc4bb04bba81e30
ACR-1151cab82f2a4e0098fb70a3e32dbb35
ACR-e17571cc41134749b0f903a865a978eb
ACR-08ec73ddeb1b40bda38dd0ac3f24baf5
ACR-da4bab5e544541db954823b2656bf4ee
ACR-6c6b1f312b8c46419772480e4e530d47
ACR-324a7dd2bc014b0fb5a2127d8d7e36f4
ACR-951cd9cc436b413a97ea9f822dc24d92
 */
package org.sonarsource.sonarlint.core.analysis.api;

import java.util.List;

public class QuickFix {

  private final List<ClientInputFileEdit> inputFileEdits;
  private final String message;

  public QuickFix(List<ClientInputFileEdit> inputFileEdits, String message) {
    this.inputFileEdits = inputFileEdits;
    this.message = message;
  }

  public List<ClientInputFileEdit> inputFileEdits() {
    return inputFileEdits;
  }

  public String message() {
    return message;
  }
}
