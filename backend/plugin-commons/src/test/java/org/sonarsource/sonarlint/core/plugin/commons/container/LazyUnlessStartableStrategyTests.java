/*
ACR-91d4cf094dcb4104bb24e6fe7902a99c
ACR-8496f13a6f6140749ff6344a14cdc540
ACR-7db605b4ca3548288d7451eeca50be2f
ACR-42c95f49cc7b4a5aaee58e0cbca5a58e
ACR-ed88e9fbc02243e1aebdba450298b693
ACR-57380e761cc34b27b5611451e1000b09
ACR-f55bafec976a4215a3087d64fec93a20
ACR-461193d28b7543afaf7c3e6a70ec17f4
ACR-3bbd8e99082c4541a297105f727ae1fb
ACR-808efc20e6ce49aea48ec4e98d537ff0
ACR-2a9b2179deb74b87956142e919b5edc9
ACR-34ead21dce4b44db85aa7642ce112917
ACR-0ca51d8affe2497091de6db8c54c2357
ACR-8ceb833459504977a478683d7dae868f
ACR-48d20bac260b40fe9adf48d1983746a7
ACR-4be1a209d84c4012b846700528169274
ACR-9a06bda316e54476b4ddfd92f00f1508
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
