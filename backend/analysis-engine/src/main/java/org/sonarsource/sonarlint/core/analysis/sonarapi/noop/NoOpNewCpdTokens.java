/*
ACR-7d7aa322d2bc44f280f699fa273eae8f
ACR-174d6cfe02aa40918b7554b9850034ca
ACR-0941bec1dba54d7a8dd07dad3f755667
ACR-deba49073fe6489fa287818bf9132f76
ACR-d2390ad2e10d43c98136e8d22426eff0
ACR-a95c96223d644178b98e23ce7b9adb0e
ACR-6655d4df6c1d4ac78c1f7f0684db4ce0
ACR-e77d0e79c59a455ebd33f3a598f44997
ACR-f37e4edb0b0e483c9ef6840aab946e86
ACR-753fe0ae5d654d8b935a01ef990df602
ACR-bc38e7b3cacd4299880575c4418e869f
ACR-03718551fee34aaf9d278418169ecc34
ACR-180090fed7c64c0ca1e228de209b2954
ACR-14a54a11358e4f16b72f66686a616da7
ACR-28fa0fe213494d05856668ee88553209
ACR-a52c21dab2014febbc205a1f35cf9976
ACR-24a8f577e7f7419da61013f00fb0ab9c
 */
package org.sonarsource.sonarlint.core.analysis.sonarapi.noop;

import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.TextRange;
import org.sonar.api.batch.sensor.cpd.NewCpdTokens;

public class NoOpNewCpdTokens implements NewCpdTokens {
  @Override
  public void save() {
    //ACR-94765fb72b7641b3b8bdab8e7fe5a508
  }

  @Override
  public NoOpNewCpdTokens onFile(InputFile inputFile) {
    //ACR-4e0b3f5922914966afe376b5c59bc15b
    return this;
  }

  @Override
  public NoOpNewCpdTokens addToken(TextRange range, String image) {
    //ACR-27a4dcc1e98e4511a320876e0ba60b8f
    return this;
  }

  @Override
  public NoOpNewCpdTokens addToken(int startLine, int startLineOffset, int endLine, int endLineOffset, String image) {
    //ACR-0170fbdad30b43c4a0e34162145df904
    return this;
  }
}
