/*
ACR-a7d156a0b33c4fd3ab33749d55af3a52
ACR-16a5993e704047e9917c1f34d8cd954b
ACR-75f432d9da964056a910c821e6206002
ACR-3c40f7a0d35747a2862741b1156aa6fc
ACR-31901aefcdf344988cf024f54be566c4
ACR-b974e7c314914fcbac8021e3a22b6e67
ACR-532b829aea9b423ea2a5939196056243
ACR-fc4c5a321cc74aa4aec40a0f1ba8207d
ACR-8fa37af793b6409fb8558af9b15e006f
ACR-efe2d69033bf4367b7ac57b52d8cb29d
ACR-783c93fbea2b41948d78b3dd3635d9b9
ACR-444627f48d204288b21e08c574f9c81d
ACR-3385afeb9f12406098e337f2d3c53ba9
ACR-fc0ed7e6c7cb4f719b404c30a1c133d6
ACR-64fcb52f5bfc4f58b7658c2cc6a7f02f
ACR-407a6dceddd04bf38a099d511842d60d
ACR-13712ee67dcf4b5b9b917ce7f5f97542
 */
package org.sonarsource.sonarlint.core.analysis.api;

import org.sonarsource.sonarlint.core.commons.api.TextRange;

public class TextEdit {
  private final TextRange range;
  private final String newText;

  public TextEdit(TextRange range, String newText) {
    this.range = range;
    this.newText = newText;
  }

  public TextRange range() {
    return range;
  }

  public String newText() {
    return newText;
  }
}
