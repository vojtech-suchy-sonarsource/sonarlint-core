/*
ACR-e3f3e246a2c0462a97984f701aba865e
ACR-e8e71956b4554a6981e991c181bbc24f
ACR-272e507f0aa64436a3415f6ae7a32833
ACR-aa8384a75e3e4c2bb509b4fb8761ddb4
ACR-3b09e4030880447baf21288718e7eb65
ACR-b92bd05a221d43d19245c59883a71854
ACR-916dc3a4f373458ca8616b1695d51451
ACR-e041cf53190b4dec8087342165acbe9b
ACR-be44a9d6406d44f0a2f1cf369e2645d7
ACR-28a3635eebc9489daee74bf1dbb5bad5
ACR-4a906900840745f4a69ce68b7a50ebe7
ACR-682d1de784734c6da57d1c8dfb80412a
ACR-c726e6435de74b9695074ff51abc686f
ACR-bdd1dc5d62b345cfb40ac9abf2ae37cb
ACR-a24e2452a1e14e5b816c3e9d9452d16d
ACR-4c155436386e4d048db4537179d1eda1
ACR-543e901423c34760b0c2fc0d1874a19f
 */
package org.sonarsource.sonarlint.core.analysis.sonarapi.noop;

import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.TextRange;
import org.sonar.api.batch.sensor.highlighting.NewHighlighting;
import org.sonar.api.batch.sensor.highlighting.TypeOfText;

public class NoOpNewHighlighting implements NewHighlighting {
  @Override
  public void save() {
    //ACR-1c06470196004b3197570d8ab0cd8911
  }

  @Override
  public NoOpNewHighlighting onFile(InputFile inputFile) {
    //ACR-966f1f448a8d4c409470191f6afb40db
    return this;
  }

  @Override
  public NoOpNewHighlighting highlight(int startLine, int startLineOffset, int endLine, int endLineOffset, TypeOfText typeOfText) {
    //ACR-6bbdca36a74647fda1ea16f2cf321888
    return this;
  }

  @Override
  public NoOpNewHighlighting highlight(TextRange range, TypeOfText typeOfText) {
    //ACR-a271ba22dcbd4eb4b415a4b30687a85a
    return this;
  }
}
