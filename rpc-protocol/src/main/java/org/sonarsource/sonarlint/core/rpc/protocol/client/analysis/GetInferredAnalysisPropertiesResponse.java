/*
ACR-41bac512595e48dab07df874c849f485
ACR-16e265e7606447d3b8e7bdd679ab8274
ACR-ecca5e5b6f114d7f861890ab51ab1185
ACR-3825595e6b99487aa88b1767189ebe37
ACR-2908395141b04362a8b6578c918b5012
ACR-531eeb93513c4ba99da2b89c46eb3c4d
ACR-6fdbf13ee991452abf33ef653d1a0db4
ACR-2f8370a69638404c816df0e45cb85140
ACR-560d7f864e694a2b864c3351133492ab
ACR-8a172f03645e405bbd12d96538a92b69
ACR-c0692ff2699e49b6853e390d877a6152
ACR-4af28300c4734346b29210dc5ddbe7d7
ACR-5293bbdbff594a6ea16d113eae2053e9
ACR-cc45de06d89047cba2015d815e89af05
ACR-b256fd6a8c2c4f719cfd0f174d2da858
ACR-6dbf2204e18f4d78b8eca8a9a4764939
ACR-3a7d663958ad4577acff613da34cfc91
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.analysis;

import java.util.Map;

public class GetInferredAnalysisPropertiesResponse {
  private final Map<String, String> properties;

  public GetInferredAnalysisPropertiesResponse(Map<String, String> properties) {
    this.properties = properties;
  }

  public Map<String, String> getProperties() {
    return properties;
  }

}
