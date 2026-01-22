/*
ACR-c835c46b2c6c4e90a517c7f28137fb6b
ACR-c3e37ee4309d4abd927589cd2c4b5b8a
ACR-12533eeeeb6240afa39042d1517d321b
ACR-e3cfb2169c28479d96345d7915428509
ACR-ddffb16c73a049cc884b94d5c6fb16a2
ACR-51daa0550a8b496f8793cacfbacf9d9c
ACR-bdfe7fa613424b099b3a3521b5e4141c
ACR-8af954042217440994c6331896c6b2d0
ACR-292c26ea96484da98093dd05f1656cb2
ACR-2522918420db4d3ab84640d218212fef
ACR-650293c908e24127b937fea2206eeb56
ACR-dd92aa28abb7488eb8701451bda2468c
ACR-2f6a61eabd61415f9d12c32133113a30
ACR-c6f61a5da499453fa39d0f467fbaa7f5
ACR-6573d5d64a8f44b69dc3a22a43cc6306
ACR-bee2eb8a9fb84dcaa8cf69c1419666c8
ACR-1d0844ded7d540e597d3e633ac27148b
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
