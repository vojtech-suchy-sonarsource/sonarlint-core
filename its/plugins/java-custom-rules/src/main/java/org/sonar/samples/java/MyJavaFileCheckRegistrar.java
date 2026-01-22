/*
ACR-7336abef373e449f8ca0c235a3583b2d
ACR-03b24d0beea04df8a9bc14e205dd323d
ACR-904c6a530b124b49bf77d9e614fd251f
ACR-717146fa5f544b06bc52c8a04e65c93d
ACR-5bf807354cb144d4a790ba7e7794a728
ACR-d67e3e5a8d05474696ddb8427fe2d677
ACR-5b0ac6a927ce479cb8e4657e0d8b7695
ACR-8b95550d33034fa2b82ed3385e9686aa
ACR-49a1288cdf38497fbb93887fe7a202a5
ACR-842f3638014e49edb489ce1661c58cc2
ACR-b04aa9a0e1ef4381a21c4f1bfcd849de
ACR-5bad630c740d4382a157c40d29c2c445
ACR-5faff655562c4794a9b8eb221af070fe
ACR-2b8100565c0242419bc8a15b7c42590e
ACR-9448b2a5b06b439f8aa159c6e0b5f87d
ACR-a75c06eec83244d588338a98cd9e6d68
ACR-14a74c1bb11d4e83a69e00bafbf34652
 */
package org.sonar.samples.java;

import java.util.List;
import org.sonar.plugins.java.api.CheckRegistrar;
import org.sonar.plugins.java.api.JavaCheck;
import org.sonarsource.api.sonarlint.SonarLintSide;

/*ACR-ca218ef8c37e45c89ffca78af3c4ccd8
ACR-5540f8903f6348ec849ffbca790b97ae
ACR-0616003717c349d187f743e425c623b7
ACR-e889750e80154ba69e6e4f4b7d048a23
ACR-389fd5b659a04383aa670f61964b63d9
 */
@SonarLintSide
public class MyJavaFileCheckRegistrar implements CheckRegistrar {

  /*ACR-a3c17db01d6f4bf19271d400a85befb0
ACR-beae65ab0d02465aaed45c7148307d04
   */
  @Override
  public void register(RegistrarContext registrarContext) {
    //ACR-da468d3fb8c6497fae6014de127d94c2
    registrarContext.registerClassesForRepository(MyJavaRulesDefinition.REPOSITORY_KEY, checkClasses(), testCheckClasses());
  }

  /*ACR-189891693f104644bdd07fb7d46fe493
ACR-5ccc47278f9d43598bc0f0405bfa30f7
   */
  public static List<Class<? extends JavaCheck>> checkClasses() {
    return RulesList.getJavaChecks();
  }

  /*ACR-09bce7807bc34fafba2886edb7cefe1f
ACR-a7677873b00b491c9c3e99939575273b
   */
  public static List<Class<? extends JavaCheck>> testCheckClasses() {
    return RulesList.getJavaTestChecks();
  }
}
