/*
ACR-567eebf046124d8591877d5d045dbe02
ACR-02d7a5844ad24374b56be55a01453a0a
ACR-5434cde1d091424181ab7f3cb6fce74d
ACR-6db74f8049b646898d9fe3b60ccf867a
ACR-e95c85c18e904877a9903417f59656ab
ACR-7576fa16691346c195fa7b5bca94945f
ACR-dbe21a9c48d14fa785206ced2a4f47ac
ACR-c34897a96b494e1181f3ef55c009125e
ACR-3322f9c7868d455a9bca8b2ea0e90c74
ACR-31472867f61c4a29829ee18f303fa7cc
ACR-5b50fb6c035c4b8484d99c7e82f84861
ACR-29602d7ff84e43ee92b4d229d525b9f7
ACR-2a506e901551487f99b26ffeae1b5ba5
ACR-da31fc0a25b14fcda3cde77c0ee890fe
ACR-53c2f12843234b5a8b4efdbad3ed1711
ACR-2736c031484943bbb8b639d34a08e11b
ACR-14e7db4bc2434a8698475b3766ddc7bd
 */
package org.sonarsource.sonarlint.core.analysis.api;

public class ClientModuleInfo {
  private final String key;
  private final ClientModuleFileSystem clientFileSystem;

  public ClientModuleInfo(String key, ClientModuleFileSystem clientFileSystem) {
    this.key = key;
    this.clientFileSystem = clientFileSystem;
  }

  public String key() {
    return key;
  }

  public ClientModuleFileSystem fileSystem() {
    return clientFileSystem;
  }
}
