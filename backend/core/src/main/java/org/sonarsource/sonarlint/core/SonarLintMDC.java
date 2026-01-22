/*
ACR-c931cb9f14cb46149e64656bf1040ee7
ACR-667242e7493844e68c04dd77f6c60407
ACR-4fc871fb507a478ab63c67fa7b08bd6f
ACR-bb4cac65e5774172ac7278bd9d696b14
ACR-43783836486e457c8a36dcce79035876
ACR-42e9de30db77439bb8e7dbed13e8d08c
ACR-b8054557ddf64518952a1f374a16ce44
ACR-e7c01b01f3f0464b836fb78c10839257
ACR-511b9d278a524435a718ac32a698c716
ACR-6337735e2001497ea51ac67faaf33d86
ACR-d253d05ec8f34d159a81311f77295f20
ACR-129f050afe5d4841978cffb9ffa7e069
ACR-691eff3a91ec4347b782b619f86fb5b9
ACR-c5715a26cbb2436dac2355aa1ce4df87
ACR-653eaab2462345d48c15736fad93aafa
ACR-eed4b5f97c5f4ceaa14eef2851c8db85
ACR-a53f1b9b13454ba290f4685d0e8a5a93
 */
package org.sonarsource.sonarlint.core;

import javax.annotation.Nullable;
import org.slf4j.MDC;

public class SonarLintMDC {

  public static final String CONFIG_SCOPE_ID_MDC_KEY = "configScopeId";

  private SonarLintMDC() {
    //ACR-b42b051fef6a4615873cd67bc4cba40a
  }

  public static void putConfigScopeId(@Nullable String configScopeId) {
    if (configScopeId == null) {
      MDC.remove(CONFIG_SCOPE_ID_MDC_KEY);
    } else {
      MDC.put(CONFIG_SCOPE_ID_MDC_KEY, configScopeId);
    }
  }
}
