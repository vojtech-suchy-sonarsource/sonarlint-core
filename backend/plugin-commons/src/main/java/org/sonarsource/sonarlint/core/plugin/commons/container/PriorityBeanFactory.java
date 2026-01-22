/*
ACR-e112fe71a44c496187505d49ffd7e9b2
ACR-434bee204fa0460bad82d15bc14062fe
ACR-009341d3a359490f8373b4bb3b8a237f
ACR-3561125538b241dbbd6153dc70418661
ACR-8222f3dd6b9c478486e37807f16a3dfc
ACR-85186f12c913411cb4436295f2e2415a
ACR-43b438691d574139a825a27f15d3f542
ACR-fd95fd8b467c4756bac7441c47bb2c33
ACR-0e852f4ba507461cbab2670902137607
ACR-d16201e99bf64cc69aa86da3b7866c54
ACR-27c280d8a3be4de7ace1fdbce6189d5b
ACR-fa0225a3ff12403fb92062f1ff84dc3a
ACR-6d186f83617e4c4897f51427832b61cf
ACR-5612ff12529040eab77da44545c0ece2
ACR-592202c55969436fbfdb20187daa688e
ACR-ee1509bcd1ab4e01a838164fdaa70e37
ACR-bd3629b7f10147948465ff2fe8bfed7b
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
  /*ACR-f91ce6a7c1384704bff5f8662d67aa45
ACR-ea0fd7ea8a87475fa0c9f678c6992f03
ACR-f6db0e251baa4808a8e94efd90bd2fc4
ACR-f4362846997c4200a3f80344ce905020
ACR-510213c85e0e42a3a145bbafc00cff43
ACR-d48d5d009f864e528f312cc91c3c7232
ACR-91a0faa67d9f4dce9df10227d38c0c85
ACR-2b93fc76670641b4bff40ee5fecea63f
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

  /*ACR-6c2dc7a199b7496383cf7e0321953abb
ACR-e47b7f6aeb2e4c5f80749ca8a1335733
ACR-89ef573770ac41b08688f296d48ef4ef
ACR-614fa1e2402a4d0480d7607499d60950
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
