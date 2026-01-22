/*
ACR-cda71828e2d340d7bc12ebb23eaf3df4
ACR-7e9c90d639a046a080acb1dc2a7df483
ACR-e6dcb602049546328a9e77061902d50c
ACR-eaee80fcb2fa4b05a650aafe9c949e9b
ACR-0d54ae00b29546978f12b6ae68f138f0
ACR-cd7059a8bcd949f5a632e78d29e4be8f
ACR-79c780d0157a473ea217aaf205595549
ACR-cbceb019af6d40fba6abbe749d8c5c78
ACR-b15fa9cb17f84dcaa66e2009a7ac23a8
ACR-ce52d656ba4749a686c11faf37a6e2b8
ACR-f74a12393f9447f1bad112a654a8b08a
ACR-398df320a67d40f99352a9f1ef981b53
ACR-cbfc46cf866947948a8b3995042b9495
ACR-82f6e1117dce4eb38e9c7549fb4472a6
ACR-6ed64d4885b348b3b5f22501a89d79a6
ACR-78ca5658102c45c4bbd2e840d97e82bc
ACR-2061bb7cf01348d79b0d417b3666483e
 */
package org.sonarsource.sonarlint.core.plugin.commons.container;

import javax.annotation.Nullable;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

public abstract class SpringInitStrategy implements BeanFactoryPostProcessor {
  @Override
  public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    for (String beanName : beanFactory.getBeanDefinitionNames()) {
      var beanDefinition = beanFactory.getBeanDefinition(beanName);
      Class<?> rawClass = beanDefinition.getResolvableType().getRawClass();
      beanDefinition.setLazyInit(isLazyInit(beanDefinition, rawClass));
    }
  }

  protected abstract boolean isLazyInit(BeanDefinition beanDefinition, @Nullable Class<?> clazz);
}
