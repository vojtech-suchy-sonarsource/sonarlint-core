/*
ACR-f2f2e288379949458bbd45c0cca3b805
ACR-022415b1cd0f4a669513f9ab6b1f5bdb
ACR-41e33bc6054f4d77b924677c0b3207ac
ACR-7ec359dd025540e99b3162bd18d7b625
ACR-9d91db324d3b495791be7085e9ed7347
ACR-7b117ae780594c08b6d83b983e314984
ACR-ab817887a3104c2f83224819bc87c931
ACR-f50d831e0f074c23ad651f386506bf3e
ACR-4facedefb0a84587b83911634c0c780e
ACR-62fdc07e2e2445db93d95bfd983625e7
ACR-713b32b930c44a48866c91cfc458daf7
ACR-cb52e5cba3f44f1cb09120e9743efa62
ACR-dde142ad349c460b90029ff10e216ba3
ACR-3a3336aca6d54042baecca64768eb7bf
ACR-e9722ce549b944d48666be6dee4e3b24
ACR-59d13c6863d841d7b0e2f86ff4494ce8
ACR-f85b1d5ed5d443458c75f5241af2e23c
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.issue;

public class ReopenIssueResponse {

  private final boolean success;

  public ReopenIssueResponse(boolean success) {
    this.success = success;
  }

  public boolean isSuccess() {
    return success;
  }
}
