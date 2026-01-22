/*
ACR-5d39af06b4f24a638975c0bd22429f71
ACR-69d39a2382464c91b8320cef7353a870
ACR-3846ab2d68c14386b48412ee7080c95e
ACR-b634fee601e3491084050f3f3138c675
ACR-8ee1cd12d21045ef921e78f672723ce5
ACR-4e46609d747c45f385aa44dc61ff73ec
ACR-eb8d7d244c7f4126a795eb4bc008da7b
ACR-19ed37d10b6a4e44801f59281ea9ce06
ACR-7dcd91b4c4264d5cbb3d96fff8f3b760
ACR-5f38c7676f8e4fab930a769ab72da252
ACR-f0d3f2dfa24f4c678e60d1ea79bb007f
ACR-95e83411c5504309b1f552b8c8ad2b43
ACR-e52162b88f4c4f49b8b288b7a32df3cd
ACR-fe841c9d7fe04442b78edbe4ebbeab26
ACR-6513fee1568d428a947b22809e40b667
ACR-b24626cd81224a499e8b0fea73fdc84d
ACR-f6372e019d8c4c30ac2e50ed95743c8a
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
