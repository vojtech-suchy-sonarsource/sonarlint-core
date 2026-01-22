/*
ACR-5de3eb6579ae4b49a75b97bde185cbf3
ACR-70210d5b631d4f6186921e49bb1112e3
ACR-3acdc4e09bea4d79a2d90dcd5ac34760
ACR-e2effe7537be43fe88b77484bb95af84
ACR-9f78c2f27b7b4e4a9226989ffb0f9aa6
ACR-a79bdefbdf48457ca8e418d009d6fb42
ACR-16145d25582a4943926a60c22ba9d96e
ACR-a625382fdc944b4ab298a8b2bc2fcbb0
ACR-053f9fc871984ee2864b92f6fecc7822
ACR-9771d71e6482462193ae69efdf06426b
ACR-58a0fc02fae74c5187b434b3ec12e2c3
ACR-7351dc419ed3411096eac462174e56a4
ACR-8203ff3e73fa4b5b8a7212e9fee17e28
ACR-631bec40790a48718a806db6f33d4506
ACR-92d983bd28904b18978a9f3cd796c7c3
ACR-f63b71de6ec14d8299f51b872faa7b1a
ACR-57c10b3210cd437a86abd004a02ab8e3
 */
package org.sonarsource.sonarlint.core.serverapi.system;

public record ServerStatusInfo(String id, String status, String version) {
  public boolean isUp() {
    return "UP".equals(status);
  }
}
