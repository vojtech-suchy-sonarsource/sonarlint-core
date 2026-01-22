/*
ACR-097bd1c7a19447d5afaae0a8a45ac9ad
ACR-aad15db8033d4d1e9ded798f117c50d6
ACR-90b29ae2360446bb94cb3e450985fd30
ACR-4a78d94ccd7943c69800a26852a39288
ACR-7689edc23d0946759dbf24d1f50d8046
ACR-6b582bc46e6b432eab4e8380cc488ff6
ACR-adacc76054f341ab92c9843831edc7cf
ACR-8c6cd478a25240ed9ffd5df3782f3ae7
ACR-b8937c8773fe4a6c9cea36b2215980f1
ACR-c30329f1d6c84cfea13e2746afe05308
ACR-a09ae0ea2ade45bd829400a77640c01a
ACR-65a6cdd1125140e5968ace6b2f61d114
ACR-97f911c87ce14773a02d7e28cedefaa9
ACR-2eb10cca8c9d4f3688758da6942a3665
ACR-bb841a6461364b32a47aecd310a22594
ACR-bc334f73cf87467ea8a6de21008c08ed
ACR-85114e55753844bc922ab68982d9e84a
 */
package org.sonarsource.sonarlint.core.commons.util.git.exceptions;

public class GitException extends RuntimeException {
  private final String path;

  public GitException(String path) {
    this.path = path;
  }

  public String getPath() {
    return path;
  }
}
