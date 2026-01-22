/*
ACR-c08ed05df2684589b060a6b9a2622e7b
ACR-a1ea814b186c436cba5b7f0ded9cecf0
ACR-30894cc99181408e9fb7ff7f1c6a8aad
ACR-161b2277691041acbe5e256fbf2d782b
ACR-50f94735730a45249e3f43108a42ea21
ACR-e56c83e37ae24f1eb1c223f3d0abcb39
ACR-320d8daad68a4cc9bdca32ec2b80e7ce
ACR-9d79742f4ba7458cb7a8f758ca0e80fe
ACR-5dd33e66daf74647a77a87380350be4e
ACR-ff25f9843aa14dfb9f0adead76765227
ACR-6041b51db5ce4162a8e24be9f86b29d5
ACR-ecacb65b78324c4f8d0ac7f822a77adb
ACR-5da4809b72794c33bfd5624fdae61eb9
ACR-235114759a834ad3a4a4129d5fc6b34f
ACR-9acb2e03ec604e6c8f6902c77c045a15
ACR-735570a75c124ad7a7c356e836fe413a
ACR-9136293f8f0046a6b9d5d273ea5a5308
 */
package its.utils;

import com.sonar.orchestrator.junit5.OrchestratorExtension;
import com.sonar.orchestrator.junit5.OrchestratorExtensionBuilder;

import static its.utils.ItUtils.SONAR_VERSION;

public class OrchestratorUtils {
  public static OrchestratorExtensionBuilder defaultEnvBuilder() {
    return OrchestratorExtension.builderEnv()
      .defaultForceAuthentication()
      .keepBundledPlugins()
      .useDefaultAdminCredentialsForBuilds(true)
      .setSonarVersion(SONAR_VERSION);
  }
}
