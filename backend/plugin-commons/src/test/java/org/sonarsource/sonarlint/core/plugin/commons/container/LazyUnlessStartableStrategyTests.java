/*
ACR-c51737f355af4691a717b2cc48bbbdd3
ACR-fb5a927c078042a1801ea76cb3aba40c
ACR-38d4e45c973d4f95b3528cb3101d806c
ACR-170c416e737f4e5b99d3958a1a0ddd22
ACR-4baac4f908774f0691e5c95d63b1970f
ACR-911b13f9718e4669bb4dccf7d33cbfc9
ACR-fb994e3b036444069820a0572baacaa5
ACR-c0c1b28a4f004a05b2ddc4099fe9f9cc
ACR-144f880e3e5640ce8cabee01d9d7bd38
ACR-85b1aba71fab4bc6a3013d720d38d0a4
ACR-dc3a19ebf6ba47bd98afeafd2bd36d40
ACR-21a6d9b5a29c4122962ebf253c590344
ACR-61bf6567423b489cadde0221b8c665a7
ACR-5a53260bb48b41e4857906f6df5429a9
ACR-61c347d1a8fd4a1c97a0af66cb5b2379
ACR-982ede7462684396bb2087454bf2a785
ACR-d5a7b63e953e4cc98f3d02c72edf98f7
 */
package org.sonarsource.sonarlint.core.plugin.commons.container;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;

import static org.assertj.core.api.Assertions.assertThat;

class LazyUnlessStartableStrategyTests {
  private final LazyUnlessStartableStrategy postProcessor = new LazyUnlessStartableStrategy();

  @Test
  void sets_all_beans_lazy() {
    DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
    beanFactory.registerBeanDefinition("bean1", new RootBeanDefinition());
    assertThat(beanFactory.getBeanDefinition("bean1").isLazyInit()).isFalse();

    postProcessor.postProcessBeanFactory(beanFactory);
    assertThat(beanFactory.getBeanDefinition("bean1").isLazyInit()).isTrue();
  }

}
