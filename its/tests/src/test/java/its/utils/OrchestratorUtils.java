/*
ACR-f441df3e2b674a78a08e9e75cf4b6915
ACR-cb0d8b9e9c7544cfbf8a49ab3a1938ec
ACR-7c32bc1b07984eb78676719e09bd26c1
ACR-fbf5ba1292fe47bf807e7ab3e8276ce9
ACR-271a6e0a416d4915b1936af4a1e3bd50
ACR-117b87a48ea340bd853e00299ae4e198
ACR-5229660299de468a95832a647e003695
ACR-dabdbdf5982f420697a0398dbdb52a2a
ACR-db7d4236410442f8958e5da329ca0d0c
ACR-732d48d1270b458fb687ad5d6bea46b9
ACR-878ec2aed8ae4238bf8364e9349d5efb
ACR-a21961f0ea1940b9bb8b06bce005fe65
ACR-899f3b1da31246e892d361e674a79ae9
ACR-6964c09c07bc48039182ef0c652e646a
ACR-5a53a904be8a42ff959766fbff028d1d
ACR-8c1a13afc0dc46e886cc56255feffc0f
ACR-9d72fc9e870a478da5dbd7481f3eb84f
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
