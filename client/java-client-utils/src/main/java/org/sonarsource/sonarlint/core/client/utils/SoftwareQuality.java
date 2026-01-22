/*
ACR-6b304a75787c459fbbe0b8e1aee29818
ACR-6d738ec408814d22b22d3965feb3d26d
ACR-a3d3b42951f7485995ef670eeb96b6fb
ACR-81cb3459f4364ada990155467040970d
ACR-15d902106d044285821689419f84abcb
ACR-c0cc1aa79fbb4a4f82a81083a2c7ff01
ACR-5ca04883e37f4d38afd038f3a6587ae6
ACR-900e4781bdc84a4f8e3d5a12b83df7f6
ACR-debeae55349a42c3b71921f2b0ef64ba
ACR-83f43eff21ad4e9da605fc95aa245b8e
ACR-02da49ff19924bce9df5c6bb867648df
ACR-b8c325c62d014e6087bf17d5dbdc4d6c
ACR-7e6ea8ad327342b290435c2dbfb79040
ACR-5185771df2ba4b60a74cfab1d1dae286
ACR-40197cb955e54613a34889e41405d46d
ACR-31081819684c43809a1a1694d4c660be
ACR-da38f6797e304b0cb864991cd90b5820
 */
package org.sonarsource.sonarlint.core.client.utils;

public enum SoftwareQuality {
  MAINTAINABILITY("Maintainability"),
  RELIABILITY("Reliability"),
  SECURITY("Security");

  private final String label;

  SoftwareQuality(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }

  public static SoftwareQuality fromDto(org.sonarsource.sonarlint.core.rpc.protocol.common.SoftwareQuality rpcEnum) {
    switch (rpcEnum) {
      case MAINTAINABILITY:
        return MAINTAINABILITY;
      case RELIABILITY:
        return RELIABILITY;
      case SECURITY:
        return SECURITY;
      default:
        throw new IllegalArgumentException("Unknown quality: " + rpcEnum);
    }
  }
}
