/*
ACR-b5613cb085dd4d8cbb6de96ee885fa8c
ACR-290941f2b2d14dc4aaab43701df63551
ACR-62e3bbd884a947d8b14d6750221f520c
ACR-ad7fc1cf5e0b4fb59d73fb3d6bcd0d65
ACR-54cd59c092514fe29a253470305c514a
ACR-06c94478fe9a4eec8aeffa1c67cceff9
ACR-5d29770b95214557ac28e11eabf20fcc
ACR-28a3a8ce4df84a0f88bb7035f34f5092
ACR-bf437f3b89ac4eb3944952f3c704cb88
ACR-dfd75bcfa53d43529dc0a45a9433ea83
ACR-42fbc0464c13439db3089c8f6d55ccc2
ACR-34e36d8a66f64333b057ef7c3892e81f
ACR-20c3eb11c20b48309181111ebdab7df8
ACR-326fe5e8f7764f94a1eadc1ef0890ff3
ACR-b77c17beb7bd4e239d3133fba691611d
ACR-7ca6073d3eb74f6f976ccf3d2b014030
ACR-1666900e27e74a5bb6007d110df2a449
 */
package org.sonarsource.sonarlint.core.plugin.commons.container;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;

public class PriorityBeanFactory extends DefaultListableBeanFactory {
  /*ACR-d2939aadb0694fe191387269ef29c228
ACR-9d3eaf41a1b94d39a638a94153ae4064
ACR-8e53d57770294700a28f68d091c37cc0
ACR-44e7cdda762d454cb2eb156655316667
ACR-4f43edae38df4e8f9d77110d07ae104f
ACR-eb2477d09df44ba8935ed407e0a48b17
ACR-4d859679c5af479c98012d8791db819c
ACR-a3c723cad8ee41bc821465fe1c6764fd
   */
  @Override
  @Nullable
  protected String determineHighestPriorityCandidate(Map<String, Object> candidates, Class<?> requiredType) {
    List<Bean> candidateBeans = candidates.entrySet().stream()
      .filter(e -> e.getValue() != null)
      .map(e -> new Bean(e.getKey(), e.getValue()))
      .toList();

    List<Bean> beansAfterPriority = highestPriority(candidateBeans, b -> getPriority(b.getInstance()));
    if (beansAfterPriority.isEmpty()) {
      return null;
    } else if (beansAfterPriority.size() == 1) {
      return beansAfterPriority.get(0).getName();
    }

    List<Bean> beansAfterHierarchy = highestPriority(beansAfterPriority, b -> getHierarchyPriority(b.getName()));
    if (beansAfterHierarchy.size() == 1) {
      return beansAfterHierarchy.get(0).getName();
    }

    return null;
  }

  private static List<Bean> highestPriority(List<Bean> candidates, Function<PriorityBeanFactory.Bean, Integer> priorityFunction) {
    List<Bean> highestPriorityBeans = new ArrayList<>();
    Integer highestPriority = null;

    for (Bean candidate : candidates) {
      Integer candidatePriority = priorityFunction.apply(candidate);
      if (candidatePriority == null) {
        candidatePriority = Integer.MAX_VALUE;
      }
      if (highestPriority == null) {
        highestPriority = candidatePriority;
        highestPriorityBeans.add(candidate);
      } else if (candidatePriority < highestPriority) {
        highestPriorityBeans.clear();
        highestPriority = candidatePriority;
        highestPriorityBeans.add(candidate);
      } else if (candidatePriority.equals(highestPriority)) {
        highestPriorityBeans.add(candidate);
      }
    }
    return highestPriorityBeans;
  }

  @CheckForNull
  private Integer getHierarchyPriority(String beanName) {
    DefaultListableBeanFactory factory = this;
    var i = 1;
    while (factory != null) {
      if (factory.containsBeanDefinition(beanName)) {
        return i;
      }
      factory = (DefaultListableBeanFactory) factory.getParentBeanFactory();
      i++;
    }
    return null;
  }

  /*ACR-4ad557191134466c87ba4ccb104a0b13
ACR-d8eef0a221ab4a15800916b274f6511f
ACR-1dc61b290f51421d9efc166ebf767eee
ACR-ae04e55c6e544cb5b1338eb832ed1681
   */
  @Override
  protected BeanWrapper instantiateBean(String beanName, RootBeanDefinition mbd) {
    if (mbd.hasBeanClass() && mbd.getBeanClass().getConstructors().length > 1) {
      throw new IllegalStateException("Constructor annotations missing in: " + mbd.getBeanClass());
    }
    return super.instantiateBean(beanName, mbd);
  }

  private static class Bean {
    private final String name;
    private final Object instance;

    public Bean(String name, Object instance) {
      this.name = name;
      this.instance = instance;
    }

    public String getName() {
      return name;
    }

    public Object getInstance() {
      return instance;
    }
  }

}
