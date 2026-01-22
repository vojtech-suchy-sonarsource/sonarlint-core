/*
ACR-739b897a2f88489f9f6571949e435995
ACR-78cc724ff1044adfb1359e28e0951a09
ACR-c6d1f29e52794576ac7c8585ae18aebc
ACR-9d6554db9f3f45c5989f6e2dfe0d62a2
ACR-ea13be79a53e4ae0ade65628ee8ff17c
ACR-227b1d17e8d04d4ebe9c36681be817c3
ACR-48dbf0cb1c0d4d69a21f4e99bf2a2b9b
ACR-1d2cde05e227485d8502ea78f2f993ff
ACR-86b6405b7b104ded9c63545dad445036
ACR-315ecfb836b746a98ec356b108c5be5a
ACR-6582f66ca4c446c7bccc4a332b6e167c
ACR-39a6ae3d3d884a739ac44982ab05f887
ACR-752105306dd643b6ae250cb5770a754f
ACR-bb34dfdca4e646188efc1069fd7a5189
ACR-cb3835af2716414fbed5cac650e69950
ACR-76d8016dd0374cbe86e4804fcca5c565
ACR-43289841b3514af198df22f1cb54aeb4
 */
package org.sonarsource.sonarlint.core.serverapi.system;

public record ServerStatusInfo(String id, String status, String version) {
  public boolean isUp() {
    return "UP".equals(status);
  }
}
