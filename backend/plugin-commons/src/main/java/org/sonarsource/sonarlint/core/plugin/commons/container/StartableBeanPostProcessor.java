/*
ACR-e888e93dc13c4af2a2829ef2fdc5763e
ACR-2bc4fdb2dc2148fb8bb1f5bd3ff3a151
ACR-3ff85da75acd4a49b9dba4661756bf94
ACR-c16d82c0fa2146e49824b0d3cd9d34f9
ACR-3e49cad270a64cb99e405069481cc119
ACR-6df69bdd696741e6a071219909bee050
ACR-d7b05b843cf3421a82cb5ce213a94bd6
ACR-85470551639a41a5be0abb21ec272396
ACR-117874fcdac148b888ecd2f5eed4e579
ACR-92343c05ff904c1faaa594c4818a8f5a
ACR-f6b490f1129844cea3eb49576ddd80fd
ACR-145fa3adfe5e4e4ea70e43e1f31153ff
ACR-747ff7f6647648f3a5045634b94f52d6
ACR-acc5c62ff7564fd5bdbc1032b4f8c047
ACR-faac94a6b7e748b1b601d9350d459954
ACR-530a211f19a84b8694fa02e79e6f1d0d
ACR-1e9dd90e97344636941c75e3e043f2eb
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
      //ACR-feab824e293f4dd78b9f0639b5b92f7c
      if (bean instanceof Startable startable) {
        startable.stop();
      }
    } catch (Exception e) {
      SonarLintLogger.get()
        .warn("Dispose of component {} failed", bean.getClass().getCanonicalName(), e);
    }
  }
}
