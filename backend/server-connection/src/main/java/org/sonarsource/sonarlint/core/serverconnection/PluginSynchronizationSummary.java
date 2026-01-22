/*
ACR-a62e485730d94f7cb24ad72614d3adf3
ACR-f6c7c39c1a4347559d31eb37afb9c6e9
ACR-3dc02d44d9cf489a9f24b8ad4b674615
ACR-8764a23faea044188f1e5b1df75749bf
ACR-378fc8577fd44c39b8daa131deb657b3
ACR-6e88309d07e64d91809ffb6e69cb3cf2
ACR-cc1182f0f31e40d4b035092ca8874d7a
ACR-cd0782f217bc44e9acfc49cd67c67d13
ACR-9602cc478bb045ffb60caba0b58c9971
ACR-a93bc1e04ac2443db1fce69c74d0aa8c
ACR-51ee6e76c4354fe49dd8e8010ced3d0e
ACR-db8bce8e558c4e97934d494317daaf84
ACR-46ec3098919944b3a74f10e1e83c578b
ACR-abfc12fc35f44f4faa2254cac154d9ab
ACR-49119c11586341ae90d65b111bfb147e
ACR-950cb217c10149f7b7b3a35e6970bd8c
ACR-fe2a895687db4dc282d34a27baf8afe8
 */
package org.sonarsource.sonarlint.core.serverconnection;

public class PluginSynchronizationSummary {
  private final boolean anyPluginSynchronized;

  public PluginSynchronizationSummary(boolean anyPluginSynchronized) {
    this.anyPluginSynchronized = anyPluginSynchronized;
  }

  public boolean anyPluginSynchronized() {
    return anyPluginSynchronized;
  }
}
