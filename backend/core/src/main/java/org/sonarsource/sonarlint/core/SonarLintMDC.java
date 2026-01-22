/*
ACR-478d3fecd05241b9bffae26c3c4e2fdc
ACR-6988698c20a14d04981b38a0cdb74804
ACR-f92fecb8cf4d4bf386afc71de1472cd9
ACR-c5f3d1009a344e2ca551c7a5e914f55a
ACR-2fd9a4bf13fb4e88bb26401574d9f750
ACR-c69f1e928a1c464295d4a40b03708650
ACR-8062965eb61e40fcadc45ea85fcf9856
ACR-22045be9dc1e41fd9d847b094278d783
ACR-82ec2356dd8f4921857f45a8cbdc89e8
ACR-67769de8eb0742a09e8a6cd3caf3a5a0
ACR-d649cc7f878945f98d654f84199c3488
ACR-e0cfdc4a695843efb2db7c4870f54f72
ACR-6939ad02b59a41fc8ca4fe700879b83b
ACR-789eca8840c849549753d14577570b5a
ACR-76740fea83524d508bbef593a4646c20
ACR-0889ec1dbe49416195d41846f4a6843f
ACR-d987cb78708a49d9b31dd19397b7602a
 */
package org.sonarsource.sonarlint.core;

import javax.annotation.Nullable;
import org.slf4j.MDC;

public class SonarLintMDC {

  public static final String CONFIG_SCOPE_ID_MDC_KEY = "configScopeId";

  private SonarLintMDC() {
    //ACR-ea0c1268c32f43278daafd5d2f3fd123
  }

  public static void putConfigScopeId(@Nullable String configScopeId) {
    if (configScopeId == null) {
      MDC.remove(CONFIG_SCOPE_ID_MDC_KEY);
    } else {
      MDC.put(CONFIG_SCOPE_ID_MDC_KEY, configScopeId);
    }
  }
}
