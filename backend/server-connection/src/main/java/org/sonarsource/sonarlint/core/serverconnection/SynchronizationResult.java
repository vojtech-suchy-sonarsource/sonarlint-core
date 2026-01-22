/*
ACR-6e5da06ed3a444d5ad65c7e9d87f2254
ACR-0a2fbbf15b984c24b6874076c92cf812
ACR-703b5583d18043329d4e536d8834aa54
ACR-48c4e3c6b1ce423e93318d3a2befe10e
ACR-0721ad3f6ebc4bdf8d456cee85c9139e
ACR-57af1173aeed4e1988f942578f963614
ACR-d0697c30c6d9490a878cf2855aea4bd2
ACR-1d118669a6f54decb5cc5b3ac308c672
ACR-4fad6b6031be445e9a6ca0fd7e720647
ACR-d33df834d37f44419b723c891dbc9a23
ACR-1ab6931c9e554ebdb8e045a8be482c99
ACR-191bba4ddfff4c7ca5f1fc8f6df28e26
ACR-f3a1f4371c5b4b029166a66d37e3f453
ACR-61f6cdf053864d239a810dcb5d9bd228
ACR-43fedc07dd824e21af2d234d973ff5f2
ACR-4e7b2ea801b34bf7aa68826bd6eb5559
ACR-b5f66277e5414fa1906862af04a0d474
 */
package org.sonarsource.sonarlint.core.serverconnection;

public class SynchronizationResult {
  private final boolean analyzerUpdated;

  public SynchronizationResult(boolean analyzerUpdated) {
    this.analyzerUpdated = analyzerUpdated;
  }

  public boolean hasAnalyzerBeenUpdated() {
    return analyzerUpdated;
  }
}
