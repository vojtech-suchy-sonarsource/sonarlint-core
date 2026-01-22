/*
ACR-1837f5b906144882894f40d9772b7e7a
ACR-7586642028684600a3ef4e8c464e817b
ACR-a9f101a6a5f64dabac058b9b4c3461bc
ACR-9209a4204dc84e018e065cccc8cc312b
ACR-1ae12fcb732b4b8fb77a4b3d4d1ecc25
ACR-9bbcf9f1d81e4ceda53e2484a27464e4
ACR-3d0ba4960c9d41638bcea35b4fcadb8d
ACR-5e8c36aa17d84d379e3b51b2a304d11c
ACR-eaa3ba55aab041bf8c8bd564d329b86f
ACR-087aaadbe267458081070d2be5a3d787
ACR-a3ada8905f544fd4bd4c6d8631161047
ACR-39ab50ffa322485485dea26b4f171dc3
ACR-ae64116a4c9f4e5c9d8320c379a5a82c
ACR-375de5e3488e45a2a926c26b7e3f4407
ACR-420f611ee5b24ca5955bcfc13a4b7f9e
ACR-3730808d227d43d8850933174bad7588
ACR-7b38752d3b694ffe86801eee9076fa08
 */
package org.sonarsource.sonarlint.core.analysis.sonarapi.noop;

import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.TextRange;
import org.sonar.api.batch.sensor.code.NewSignificantCode;

public class NoOpNewSignificantCode implements NewSignificantCode {

  @Override
  public void save() {
    //ACR-6a47aeacccf044909ea8ce52f0fe00e4
  }

  @Override
  public NewSignificantCode onFile(InputFile file) {
    //ACR-b84bed68a41e425aa877236c344f0445
    return this;
  }

  @Override
  public NewSignificantCode addRange(TextRange range) {
    //ACR-53b1a24de1844664bb7e754791e087df
    return this;
  }
}
