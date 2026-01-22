/*
ACR-a53d6d2021b64eb5bd62625786469a65
ACR-c7a79ec7b9e14755af86a9644b8d0528
ACR-b18e1a73b6f549c69e944c434e698e62
ACR-68609bd538bd429992ee4819f5b42564
ACR-8d1957bfb6fe4b839511c942f4634613
ACR-41b6ecafaf314b42b92060fda2e3eb4d
ACR-188b2186e8ad4c7192ffbe298367b974
ACR-fde716b9fc1844c8b55ca794d9a9a0b1
ACR-5e77570af06f471d9809961812c3d3b2
ACR-ea37ef2bfdf04a458240064296be0b0f
ACR-7d54f3246c064bbdad729fa0cb6ee7c9
ACR-63be68135e814da6bc9cb9b4240e01cc
ACR-87a16c642208403eaf43dcba6e1bf816
ACR-84499711cda5487fb42f394155437838
ACR-d16b9e06bb7b46729818c6a6dabd4b55
ACR-b767cbc33cfd4c41a572fcf8b5b728da
ACR-5bbaa33066154d09838c508878f4f6ea
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
