/*
ACR-e3a4637453fb4963930510ae7b814ac9
ACR-504905dd437240b6aa3fadde2ce0a961
ACR-89ae3aef63854569870dfdbf333f68e3
ACR-c55444fe15634796b2a2899bfa044dc1
ACR-68a6072e758441418fb4deca5f1bc63f
ACR-ab3ad455172549bb92f74601d85b7ec4
ACR-1ae698956a9f494d86e74f90b53d76de
ACR-53014d8585af4ab08ff59abf7209064c
ACR-35c7b2da30ce4ff8bb232ce0802a4227
ACR-382e493a2531422cab6f80881a33c139
ACR-e861a7418d804d6889efe33d8f4da9d4
ACR-1af1f240d84d488d85f0c93f464b158b
ACR-51ebb0d05c284b13a76ac727eed548ae
ACR-059e9b822bc0496d96717280970b3278
ACR-b673c52bbe8d4759a281ed3c8fafb981
ACR-55ba1f99308a498ebac2e9ff9857b9d0
ACR-2a2eb56e40674f30b29a93f2b2a2697e
 */
package org.sonarsource.sonarlint.core.plugin.commons.container;

import javax.annotation.Nullable;
import org.sonar.api.Startable;
import org.springframework.beans.factory.config.BeanDefinition;

public class LazyUnlessStartableStrategy extends SpringInitStrategy {
  @Override
  protected boolean isLazyInit(BeanDefinition beanDefinition, @Nullable Class<?> clazz) {
    return clazz == null || !Startable.class.isAssignableFrom(clazz);
  }
}
