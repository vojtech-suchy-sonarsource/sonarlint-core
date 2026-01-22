/*
ACR-4c172049d7f647308a4a3941644972d7
ACR-0fa15b9002c34bd5a63b010900c5938d
ACR-85f1905552034203b981b89018284fbd
ACR-56fe26277732406c83d9ec46ac0249e2
ACR-49f3269047114c0c84c8a16522a66272
ACR-dcca5ff9dbc4417e993bef82795fd19e
ACR-2bddb3a51474428bbadac8f7b38659d5
ACR-936a47b783434aebab93780a1927c9b0
ACR-5a3f0cf5ae48452ea9c120c132c81165
ACR-ce532504a050454fb4d66ec715df4356
ACR-6ad3348c578540fcb6ff4778aa09d15f
ACR-43915c280e12425c807ed9a847018a2e
ACR-c298072f460949e7b7a75fc687e88a1d
ACR-025664946412418991103dc40153c04c
ACR-08e9840d26904be48ac08b96922994ee
ACR-a94d58f038e24de38612497282219239
ACR-1dfec5cc13f143c29bb5bd5e466f8934
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.sca;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

public class CheckDependencyRiskSupportedResponse {

  private final boolean supported;
  private final String reason;

  public CheckDependencyRiskSupportedResponse(boolean supported, @Nullable String reason) {
    this.supported = supported;
    this.reason = reason;
  }

  public boolean isSupported() {
    return supported;
  }

  @CheckForNull
  public String getReason() {
    return reason;
  }

}
