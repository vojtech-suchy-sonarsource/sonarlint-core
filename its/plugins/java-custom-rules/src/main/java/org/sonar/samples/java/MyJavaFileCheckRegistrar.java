/*
ACR-fc249369e2404c94a2afeee0782e0fe9
ACR-2ae548114ee8485a8867bf39d5b57fb6
ACR-2107106107914c48ba40c8d314a48279
ACR-c1ae45e1d493493bbf57299c96668834
ACR-0236a90d165b46218f850eb32de4bb5f
ACR-2e7cdb9b3a25485d8d96780587d2fc2f
ACR-47441ded8ef14c1b879eeaf82f1962bc
ACR-55bf777ada3f44a68f521b25d3399fbf
ACR-d60bb7b130ad409abed55ff7c24b115b
ACR-480fa237d6674ec6b0114a48e305fd49
ACR-775ac4db6751443496788ad4435fb0bd
ACR-a8f9e8094b5843d3b7b14582ab4ac17c
ACR-d4c0f4414b1b4f738f4d62f87d51d1cf
ACR-1b454bc9ba104c65b0fbc7c6bc942360
ACR-678cd92f7dda4bb4b8bee87cb3ab8708
ACR-999bec77b13f4e4d9d359e37968e069c
ACR-5bcd646fb4134990a7c20b1d45302718
 */
package org.sonar.samples.java;

import java.util.List;
import org.sonar.plugins.java.api.CheckRegistrar;
import org.sonar.plugins.java.api.JavaCheck;
import org.sonarsource.api.sonarlint.SonarLintSide;

/*ACR-c768316f7f314399acc5c30e6b85f35c
ACR-bcb28b4f0d9f4a16a7ff8686195e1828
ACR-f040870ccd2e4b4eadf1e9e5442c7f16
ACR-a9f10c1b06dd461589f7472367b4e401
ACR-975348671ea44d46a0c982341f56884a
 */
@SonarLintSide
public class MyJavaFileCheckRegistrar implements CheckRegistrar {

  /*ACR-b5f4082f2d784334b902ea78df7ef275
ACR-03009cfb107d4aa39e86f92c2fcbb154
   */
  @Override
  public void register(RegistrarContext registrarContext) {
    //ACR-8462cd23d4f14e38bc5fdce48247cc88
    registrarContext.registerClassesForRepository(MyJavaRulesDefinition.REPOSITORY_KEY, checkClasses(), testCheckClasses());
  }

  /*ACR-69a44457b0a748f58bfa818a81c07d62
ACR-64a04ee3a8c44113981dde0dcf9cb936
   */
  public static List<Class<? extends JavaCheck>> checkClasses() {
    return RulesList.getJavaChecks();
  }

  /*ACR-e358a6d2a9084c51b527491eb0c2c18b
ACR-4ccc9493cae54fabb5d1c0f2aae37722
   */
  public static List<Class<? extends JavaCheck>> testCheckClasses() {
    return RulesList.getJavaTestChecks();
  }
}
