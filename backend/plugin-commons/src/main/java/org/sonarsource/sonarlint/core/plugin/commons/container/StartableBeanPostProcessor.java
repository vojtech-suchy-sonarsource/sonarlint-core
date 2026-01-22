/*
ACR-c7465f7645b34966836bbc42ada6437d
ACR-fd05218a91a44ff3871049eba2fc54b3
ACR-c76f33556d9a4840ab8fa3c79240650e
ACR-3d2cef34244546149b9a7495b7342154
ACR-38d9f550048a4101bc096ba8a62042d6
ACR-d2f0ac0a7bed46fda94243d522ca5c06
ACR-13474b2f25134d3b9a3d38d3bcf3d222
ACR-8b9a7bf4a70b4c2384151c633682b90b
ACR-01fccd610f4e4d9fb709d3f19f27bafe
ACR-1426e07e6c7741329aac10588e0cb9d6
ACR-4934a6247b1244c18bd53410b65149a1
ACR-9e2ea15700ab41d09ce6622cac799e3c
ACR-765690a1109b4d3fa355a610d61d94d8
ACR-76cc13b650424c72883e501a621de3a9
ACR-9ec808b315da4ffb8c6e6c3b7803a0a4
ACR-f891ccb241744c0f97a20dd1a65e9e3a
ACR-584c252e20aa489493edcd306ce07bef
 */
package org.sonarsource.sonarlint.core.plugin.commons.container;

import org.sonar.api.Startable;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.lang.Nullable;

public class StartableBeanPostProcessor implements DestructionAwareBeanPostProcessor {
  @Override
  @Nullable
  public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
    if (bean instanceof Startable startable) {
      startable.start();
    }
    return bean;
  }

  @Override
  public boolean requiresDestruction(Object bean) {
    return bean instanceof Startable;
  }

  @Override
  public void postProcessBeforeDestruction(Object bean, String beanName) throws BeansException {
    try {
      //ACR-65075754ae924fc48af4ef0f45e3bfe3
      if (bean instanceof Startable startable) {
        startable.stop();
      }
    } catch (Exception e) {
      SonarLintLogger.get()
        .warn("Dispose of component {} failed", bean.getClass().getCanonicalName(), e);
    }
  }
}
