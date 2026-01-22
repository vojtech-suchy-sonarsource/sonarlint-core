/*
ACR-16befe421b2544bab696183eb2d22b42
ACR-5f56741a2df44fab936020418e6b2bc7
ACR-248c3eb180024e8fa2e50924f80c6ee8
ACR-fe386f2ce80b411d9de325b2fb950015
ACR-3a9f8dd7fa8949b3808dec55ad000a50
ACR-c205ffba3e7445419eed5a2b8f6f48f0
ACR-4c82e1ba30fa43d0b271da3404177326
ACR-034e0a9a416b41f5b8b8d9a44058eb43
ACR-444136c0bc2949e6b9ae5fe30e64aeb1
ACR-56d45e7c32ee4de08494b5f9134e7c4c
ACR-2142655aff3d43deb573039f7f849326
ACR-1bf1de2e067b4ef08514dfa0f2528e0e
ACR-f2fcf75e9cb04d8b82cfc00fb5d1588f
ACR-f639a7e3f591478ab733e5c0032f503b
ACR-00a62f9cf0414810a0de7144939c9b51
ACR-4e79d7220f0d4d7f87b5502a0137c5b7
ACR-bfda8721d46d43bf9a4c382ce86f1e8c
 */
package org.sonarsource.sonarlint.core.hotspot;

public class HotspotStatusChangeException extends RuntimeException {
  public HotspotStatusChangeException(Throwable cause) {
    super("Cannot change status on the hotspot", cause);
  }
}
