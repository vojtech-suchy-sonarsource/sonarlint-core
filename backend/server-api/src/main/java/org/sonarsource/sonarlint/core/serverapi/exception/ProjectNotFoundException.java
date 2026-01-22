/*
ACR-2500d9ddde1541b5a530305e4f0b1fa3
ACR-0762286ee06b43b4b6d3980b70fb7e45
ACR-38e92ae64a0f410e82efd23ef3db4a92
ACR-79e66b2c2fae47dfa875e8174e006d22
ACR-08f789e282b74505934c4d805b82b3e0
ACR-68694b15f63e408c9b957e234f55ade4
ACR-25b381d08429445fb9286b6462608fe1
ACR-b6facf54950942d68319845c55ab89ce
ACR-ca9df73763b542a890114ee1a796078c
ACR-c102871ba185491596f31c5c2f7ef90d
ACR-f7ad5684dc824c3a9665d057eef18fdc
ACR-04a8ad6ede9a4e9b9b5356d9e5fab4e1
ACR-7a327eb058514e0db245ed0cdbea97f1
ACR-6fb4eba20a744c2a93e8c6ef7a805047
ACR-bf192fcb84354eccb23ac1a3d02145ec
ACR-c04a0d0d6c7f441cb61d67eae453b81f
ACR-311cb9bc41dc49f9aec7cb89221f287d
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
