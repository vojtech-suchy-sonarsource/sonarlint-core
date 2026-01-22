/*
ACR-614e7365f60548e395e37ca07607758c
ACR-c96b111633e84a368f028bcfc22dc4ad
ACR-1e68f583959947dcb07ea6ccf308ebfc
ACR-869de0fc50424f35a74a89f620f48e0e
ACR-a77eed874a7448e09c2a400687605a39
ACR-36626e3de19c42409104d3e294b349c8
ACR-d40e309edf194686be9215ab5ca558b3
ACR-086e5d9e97244bdcaefb41aeeb166fd2
ACR-3ab391a65bc94510804b77791650ccdb
ACR-ba7827c9662544219bfd7b2034a809c7
ACR-a9832e11ff15407db2c6acfa166dbe22
ACR-4b9702f3139f4021b6d4cce9ae671155
ACR-4972f7d0e95147f1a55c8617421d8b99
ACR-eef6c9f2fc58423b838d39ad380d7b55
ACR-0e203f76db884fc48ffa9a076ad3329f
ACR-4bb7c9ebaca9465c847cfaad10f8050a
ACR-104713acfa5647e68fa1254777db2653
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry;

public class FindingsFilteredParams {
  private final String filterType;

  public FindingsFilteredParams(String filterType) {
    this.filterType = filterType;
  }

  public String getFilterType() {
    return filterType;
  }
}
