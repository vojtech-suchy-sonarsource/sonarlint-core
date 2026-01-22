/*
ACR-75d2086b58a84060b898b02f95cca686
ACR-5c69afbe0ef64336adaa848ba098a9cc
ACR-1fea97c56d1f41d18e72d32672ceed16
ACR-5df3f6c367a549edbd56b5fe80436539
ACR-8a4090d6ff6f46908217fe55518378cd
ACR-b80aa7dcf99c4e98bc4f9b7f64352d46
ACR-95ebfebc961a4d0c9b7348b0b95dafbb
ACR-3ae0ed39a88347ec9e241cda74860eec
ACR-3162656677e648ba950b9d87f529f34d
ACR-a829400758504ad2a330384a55de250a
ACR-5f6fb3efe9ea4384ae45f71edaef1223
ACR-abf487cb2b044b1f827a91d60b6fedcc
ACR-9420271ff6ca4f899bb704df38c97ea4
ACR-1c0d0a6721864c4d9d763a0660f77adb
ACR-d3570650068a4bf59eb1c696c46fe331
ACR-439ab259a0dd41419233593656ec01d6
ACR-a779c91432fd4ca292edb695c4f12d38
 */
package org.sonarsource.sonarlint.core.plugin.skipped;

import org.sonarsource.sonarlint.core.plugin.commons.api.SkipReason;

public class SkippedPlugin {
  private final String key;
  private final SkipReason reason;

  public SkippedPlugin(String key, SkipReason skipReason) {
    this.key = key;
    this.reason = skipReason;
  }

  public String getKey() {
    return key;
  }

  public SkipReason getReason() {
    return reason;
  }
}
