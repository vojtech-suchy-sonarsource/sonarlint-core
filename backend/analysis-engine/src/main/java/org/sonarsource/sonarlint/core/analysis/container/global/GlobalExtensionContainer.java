/*
ACR-019bb17fa27e4125a3fdab92b8b176db
ACR-1c3caa9b573548eea07a20c0d680c7e2
ACR-f57eeb2485e740428eec082dd15eb41d
ACR-6802d065c34543c8bc0a60941a32b0fb
ACR-1db38cfca8aa40b182f0eca2e25b30a6
ACR-3fda70e1ce4643bba04340b6c768582b
ACR-03ece03789024878af5b1fe2adda56f0
ACR-0e4f1ba504a64952bfa5347671c3b657
ACR-5e0e42eb0d6249d695f23b4ebc98dc71
ACR-4a530de512694c489793a9248db72827
ACR-78dd2afe3a714eeeb2c22710130463d6
ACR-0565ac4b66414120af4fbf6df82b831a
ACR-15c4c2096043454fba1c50048dab2f9d
ACR-ee6f4517d4dd4173a5391cf32fe159bf
ACR-bbe22fb7d38f43059741750ac3725dbe
ACR-aa81367f292c4891bda4d729391c0dae
ACR-54cd6cf63add4313b3ca1385b82ad5b6
 */
package org.sonarsource.sonarlint.core.analysis.container.global;

import org.sonarsource.sonarlint.core.analysis.container.ContainerLifespan;
import org.sonarsource.sonarlint.core.plugin.commons.container.SpringComponentContainer;

/*ACR-da5c01188adb4a5a84733c672cb8f401
ACR-5a5b51b4a4f74dd3bd9ef1b803ea6349
 */
public class GlobalExtensionContainer extends SpringComponentContainer {

  public GlobalExtensionContainer(SpringComponentContainer parent) {
    super(parent);
  }

  @Override
  protected void doBeforeStart() {
    getParent().getComponentByType(AnalysisExtensionInstaller.class).install(this, ContainerLifespan.INSTANCE);
  }

}
