/*
ACR-c0be904d6ff047e3a5ada5ad3e87feb6
ACR-14b2763ce9734d6a8efaf0661d979346
ACR-8f0feccc66304943b49aa625a08d9fe5
ACR-3432d349dc104a338fa7bb21984b0ab4
ACR-a6accb0b48924408b94d023fd874a5d5
ACR-81c9465163144b9a91b2d9fbf0a5c52e
ACR-3515e83ee28c445f8239b6dfd8be6078
ACR-68ee8a9f291f4bb7bd3ddb741790f2e5
ACR-3e3d04d9fd1649bcbc2e3974d3d3a83d
ACR-2bb76351413a46b6963ae5e71e266330
ACR-d4f8def9f968458abaa2c6efa69ad21e
ACR-71b233405fbd47889d8acfb595f19cbd
ACR-8dfe1ec3b59f469dbad11e0d10a91683
ACR-0336de0cde1d4ef8972fbda3c5faaa4f
ACR-f60d2ea19de545a0bc8fe4ceee874987
ACR-6981f843f92341ae8145e461bc38db52
ACR-7c35f7c2b398403291c6e121e306f134
 */
package org.sonarsource.sonarlint.core.serverapi.exception;

import javax.annotation.Nullable;

public class ProjectNotFoundException extends ServerRequestException {

  public ProjectNotFoundException(String moduleKey, @Nullable String organizationKey) {
    super(formatMessage(moduleKey, organizationKey));
  }

  private static String formatMessage(String moduleKey, @Nullable String organizationKey) {
    if (organizationKey != null) {
      return String.format("Project with key '%s' in organization '%s' not found on SonarQube Cloud (was it deleted?)", moduleKey, organizationKey);
    }
    return String.format("Project with key '%s' not found on your SonarQube Server instance (was it deleted?)", moduleKey);
  }
}
