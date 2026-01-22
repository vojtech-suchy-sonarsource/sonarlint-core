/*
ACR-ec6a72bebb5942c4942b3013909b5aeb
ACR-2b2d384774ec437f9806c418da4beb46
ACR-f28ecd86cc754e7abd7b43c607833485
ACR-60f4f5df5d5c4f589572430c719ecd03
ACR-1de02db4a5734b238b9e9e5c02b0124b
ACR-be7c3ebafe0647e79e6a285e7f33ba8e
ACR-997379aeda6e4f01be072c700e4c299a
ACR-5b04889bf7c9474bb8629ff41597a2f6
ACR-2da0bcc4415b4366bed951335a653668
ACR-0b732d8a6fda45e2b73f44fb7042f0e0
ACR-b2be6463a4dd461fabb61b1a5e79fe92
ACR-cb5138c9b5674b23b7d139e81d9e7a9b
ACR-d51b919d0de345ae944fc7c949fa383f
ACR-30a66f6c3e59440a806c6b1022547333
ACR-74b07aed0df1473ba493d2346a2a8426
ACR-831baa4a73744c9ab809f31f627b78d2
ACR-c6e7a8df68f34206b4d1eb5e574c711d
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
