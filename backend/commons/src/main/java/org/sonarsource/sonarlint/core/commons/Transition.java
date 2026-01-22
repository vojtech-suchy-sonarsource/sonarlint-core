/*
ACR-49c6362169c440f6891e841e2c73b47c
ACR-8201dfa2a8f6407dab666f836966dff4
ACR-4485f3ae493a4428a8691235d8f7b175
ACR-383c7e3860d5408abcf9529992a24c1e
ACR-524c758a2f264fcd9a5cb305dd8b5ecc
ACR-e8a15ce592e242db9ffc3d38b338c292
ACR-e6f39d2881cc49be90f48475c72f6651
ACR-be6b41218eda467bb9bc5df3e5ef7194
ACR-9356dbd791b0415d957bab685b12f04c
ACR-357327e5b3864755af6667ac7fe4984f
ACR-b7f890a09a4244dda6511b43d602596a
ACR-f07c363f0fa84e5b8370f652906372df
ACR-60d694f0c66d4a27b4408a0275da543d
ACR-f047c1a4c161428c8e0f7af263d9d02e
ACR-aec03693c9774294af7655630a8bc3b7
ACR-b2e6c8c243314b94af4bc5b344442be6
ACR-72a89bc7d9434708af4c2160ddc64e26
 */
package org.sonarsource.sonarlint.core.commons;

public enum Transition {

  ACCEPT("accept"),
  WONT_FIX("wontfix"),
  FALSE_POSITIVE("falsepositive"),
  REOPEN("reopen");

  private final String status;

  Transition(String status) {
    this.status = status;
  }

  public String getStatus() {
    return status;
  }

}
