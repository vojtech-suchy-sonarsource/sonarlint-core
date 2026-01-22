/*
ACR-c0c85e285f764f679b7d37b40e78a829
ACR-5e98360a9a384970b27c170a03f2c81f
ACR-49c32f68efb94af58bb946b1e90567bb
ACR-941596cbe0e6457a94bd0b8e65434e38
ACR-65a841fbd83047198a9a649d09d602bc
ACR-896f108be56145b18a236299a3ab0c2c
ACR-038dd8b124d14930a4ef1946fcf95aa5
ACR-f1e7a550bf534038b1ad78171ada51be
ACR-863e3b04ccb74f76a418cc11ced7c65a
ACR-3d14dbe515494105ae268d5b81665663
ACR-7be8a8f8ca754cce84890485ce2eebbc
ACR-ffce844fe0874911a4c4e30a5785de11
ACR-3770f5dea1ef4176b5af14378ff7a330
ACR-3607b7d35d2a45e09cb711e3dcf203c7
ACR-29a24439b45b464f87bafa7cb22b41ea
ACR-fd5dfe815cb944d692d2ac647577d63b
ACR-37ff8264bf88464d90033536614d395a
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem;

import org.sonar.api.batch.fs.InputModule;
import org.sonar.api.scanner.fs.InputProject;

public class SonarLintInputProject implements InputModule, InputProject {

  public static final String SONARLINT_FAKE_PROJECT_KEY = "sonarlint";

  @Override
  public String key() {
    return SONARLINT_FAKE_PROJECT_KEY;
  }

  @Override
  public boolean isFile() {
    return false;
  }

}
