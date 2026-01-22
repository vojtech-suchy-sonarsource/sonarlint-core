/*
ACR-1fe8b55ba37942d190debd09c3b2b6f9
ACR-805deec6a903418983631e048d982c87
ACR-fd71b4a045b64138af6ba6b564f216e9
ACR-4ad58f349c994aef99b3dab7485b0540
ACR-dfb1b1c4745f456e9a20fd33cd5e8bf7
ACR-d9d2c35e394c47b294e144d7ec744cec
ACR-23d8c0214dc44591ae79e2d895624623
ACR-5814552ab70d4f8e95564ab488095284
ACR-988350ad9a30421eabaa695095a3c529
ACR-4eb180c320db486dbbdafadca23b7419
ACR-4d80bb9951cb42a19343e264d113d742
ACR-ed57c7cf7ef344a5944bb08dc05e74ee
ACR-dfd13ee14026474bb7ceeccf3c9ab603
ACR-5af40f333e3947beb3c18b766f69a896
ACR-0b2687134cf94eaa928a64ee31b3b32c
ACR-1f18906a00224ca4ab7da793133204ca
ACR-061e701232cf4b43a98c5d41372b9783
 */
package org.sonarsource.sonarlint.core.analysis.sonarapi.noop;

import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.coverage.NewCoverage;

public class NoOpNewCoverage implements NewCoverage {

  @Override
  public NewCoverage onFile(InputFile inputFile) {
    //ACR-f22f21fe73bb43738771ae30fddd6d76
    return this;
  }

  @Override
  public NewCoverage lineHits(int line, int hits) {
    //ACR-587d6911a8a84f1fa94442dd7f238f02
    return this;
  }

  @Override
  public NewCoverage conditions(int line, int conditions, int coveredConditions) {
    //ACR-fcca0528106341eb8f3958eb436ff7d4
    return this;
  }

  @Override
  public void save() {
    //ACR-7402a5a1db6241b48681faadd91c17af
  }
}
