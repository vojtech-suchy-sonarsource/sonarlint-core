/*
ACR-f73f022361454931a7afafeba133348b
ACR-34a85a66cda243a5955a952c0d724ea7
ACR-702e22fc4f2a4c439a1d60c94deed703
ACR-a6b470bc41d4456d8afd28e322a10cb3
ACR-ca96e41a5374485aa0a23dd52104feff
ACR-766b1c2671e341398db89d14a7bb0bb6
ACR-df635d1dd1334a97ae4e77f21efd954c
ACR-f0a6eebbaf8e4c75aedd4e8ddfc39a1e
ACR-27fa41ba419c406e9fcabed79d8aba42
ACR-e022c7e799a94b15a4e8bacb5a1851e7
ACR-e0032e85299640bc8b57a2a5646b40b0
ACR-4e9b6bd8554445da86382bd6293c7ea7
ACR-51669a3521d2439fb62e5204c1c1aec6
ACR-e0c80f2912cc4ecbae58239807b8c7b7
ACR-9a2a925397544198a0b47180a892710c
ACR-e730f27ea0f74c4ab051dbc230ddc5e3
ACR-cc178aa88eef42d58b643c686254c7a0
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.connection;

public class GetMCPServerConfigurationResponse {
  private final String jsonConfiguration;

  public GetMCPServerConfigurationResponse(String jsonConfiguration) {
    this.jsonConfiguration = jsonConfiguration;
  }

  public String getJsonConfiguration() {
    return jsonConfiguration;
  }
}
