/*
ACR-557794a83d77400cb8891bd27552ea6f
ACR-6026dc4f780e443b95b6da4fe934af38
ACR-af1bf66f003f4388b301582973e0d069
ACR-a6d086c7578b46bfb15b1c69671c98c5
ACR-72dd3eb1a31943c5a1971a8f7a71313e
ACR-21c9a354245143c89a1c5c5bc46e90b7
ACR-5416808d5bad40fa8394b02973433679
ACR-a006b781e0c543aca0ad0a4d98795015
ACR-e91bd020f26941199c76c98746083aff
ACR-a94217b0ea354ee89a5aec4d38480c80
ACR-1c9860d85b484380a69345c93a78c905
ACR-0165012efa0d4f9b912e52ebe1b766aa
ACR-117c0a6b5ce240a392c01cccc98c5353
ACR-a800be3a12244df58d74e3de60e86543
ACR-8dbb2a7376de4454919e641ded0a4848
ACR-4038178178b941e7a5540bc1dda5e0f3
ACR-1027250b10564a678c8302df127e39fe
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.projects;

import java.util.List;

public class GetAllProjectsResponse {

  private final List<SonarProjectDto> sonarProjects;

  public GetAllProjectsResponse(List<SonarProjectDto> sonarProjects) {
    this.sonarProjects = sonarProjects;
  }

  public List<SonarProjectDto> getSonarProjects() {
    return sonarProjects;
  }
}
