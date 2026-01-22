/*
ACR-ad5d74aaa37e44d7b0454878d54a6d4e
ACR-1589c2d0cf23419396b161a806645a71
ACR-823dfcb97c724dffb047f501b8814487
ACR-b08029e50e4043b1bd7e0255e7b94bec
ACR-0bb16beecfd24498b92b0ef95036f4d2
ACR-cb88725379cf48fcbf1159759b2d752d
ACR-f9879412de4b4c6da5a1c14739521dae
ACR-95c75942d4164fdc8f612edfcbb12bfc
ACR-0e2392673a6d4620b0821e9eb614ae9c
ACR-9f0786c637a94976bb136faf4ee1010c
ACR-6f05cd30fc4d4ae28fd5a4f770bb7fe3
ACR-3a47c834b58e4b0f902d4e932b8861bf
ACR-7b89e1b2beb64457869e3635e52d910a
ACR-38510522a5374b8a8ba957b3c33b7d5f
ACR-0237940f52d44f45932a192b33f8215d
ACR-3806758bed2546028d9ed33c2df99dc3
ACR-271895a25e9d4f98a2d1d411c11bb0bf
 */
package org.sonarsource.sonarlint.core.commons;

public enum ImpactSeverity {
  INFO,
  LOW,
  MEDIUM,
  HIGH,
  BLOCKER;

  public static ImpactSeverity mapSeverity(String severity) {
    if ("BLOCKER".equals(severity) || "ImpactSeverity_BLOCKER".equals(severity)) {
      return ImpactSeverity.BLOCKER;
    } else if ("INFO".equals(severity) || "ImpactSeverity_INFO".equals(severity)) {
      return ImpactSeverity.INFO;
    } else {
      return ImpactSeverity.valueOf(severity);
    }
  }
}
