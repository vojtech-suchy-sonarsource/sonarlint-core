/*
ACR-08dcd1ffdd1d42b286242e947ac30989
ACR-5aa0b8e181f443b6bb44c94de83ccdf0
ACR-4a34278d23a34ab2b8f30418aa3184d5
ACR-92ab79b0a8a046d885ddf0beeea20baa
ACR-6c71d81b6a0f49d298e83dd77a6a9dc3
ACR-dda7c28b3d6a4c32834ed4f4da7f8363
ACR-7329e5c3e72346e3ac90adfd16d12f46
ACR-f44d470b468f407aa0cb642132d2f267
ACR-95a2cbd6d878479f8a8d7ac5c3034628
ACR-5445766a75e14136a96978906303eab5
ACR-4623028deddc4e2d94f3f398b1238388
ACR-50a81f9b1aa84f94a1b720ceed56acd2
ACR-656b73239748481288e7414f99e177ab
ACR-34dca0f54bb445f7b6e16560ea80d9f9
ACR-06d5f207c8f94942843a028d50dd2988
ACR-c7134b130d824046afeb9efa64d7c547
ACR-17559744e387433a8b8bf556ca18a8c0
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
