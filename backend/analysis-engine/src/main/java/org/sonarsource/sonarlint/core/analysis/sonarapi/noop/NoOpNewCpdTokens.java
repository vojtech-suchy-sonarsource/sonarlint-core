/*
ACR-e60020921bb64ebca34f8ca4c5c94235
ACR-7039ba74750f4e729c37f5409bac8a1c
ACR-d8e6be047fab484f9d104e0365dc870a
ACR-d99806d5e2004c68bdde39dd75616dec
ACR-e906119d79d34542806947f1d80755b1
ACR-69055d4a44094f15b2edd843f23b85d5
ACR-f8ca225ff73949d0b185d6c487945c8b
ACR-b7b165e571694a05b0a16ce18bfc37a1
ACR-f1accaeefb3646a285819952f2c188a1
ACR-75830a8c33bd436cbce90a8f7d18e18e
ACR-5cec98868c1e497992868c180de04428
ACR-da0577f030724a29b5d81989f9ff5b04
ACR-54809b50c5d04755bf36a983328646b0
ACR-e725c3a5eb4b4c8195e1bf3aa022c9f3
ACR-019abbf436884512a2819dfea76466ff
ACR-5846f92affa34d239424f8373abab4e0
ACR-29c3a23c70b64dd3b183408482c61ee2
 */
package org.sonarsource.sonarlint.core.analysis.sonarapi.noop;

import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.TextRange;
import org.sonar.api.batch.sensor.cpd.NewCpdTokens;

public class NoOpNewCpdTokens implements NewCpdTokens {
  @Override
  public void save() {
    //ACR-8d7682ae465b4bb2a05c2143bc89daa0
  }

  @Override
  public NoOpNewCpdTokens onFile(InputFile inputFile) {
    //ACR-510d7eb683ea452fb17f713a3c42ece0
    return this;
  }

  @Override
  public NoOpNewCpdTokens addToken(TextRange range, String image) {
    //ACR-0bb429a6838d488ba8298f9961dd275f
    return this;
  }

  @Override
  public NoOpNewCpdTokens addToken(int startLine, int startLineOffset, int endLine, int endLineOffset, String image) {
    //ACR-f2890b4ae7204b49871a573689a0f1d0
    return this;
  }
}
