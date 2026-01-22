/*
ACR-cc9d2ccd53444a7bb96af7eccbab3ef3
ACR-0e2dd6674f8145d8b3d10afecbbb4b98
ACR-83af6f36d3a5442aad68e004783e0ad1
ACR-1be55c7ccbe24517b28bc982f4cebb79
ACR-713e3cea405a4c3c902f00c2c58caadc
ACR-a5017e78a98146f2bde07d5df080acd2
ACR-0fcdbff8b66d4c3ebadb02ec2aa8327e
ACR-17f40787890a4453900ae9e08789523e
ACR-9dcafe1f35b44c78be2306b700f081d0
ACR-7432fcd746dc40b08e42adfccfb367d1
ACR-162af15a8b3e4e6eb880aa563e8f5ffc
ACR-87fe51078ec447e68088739e5f6ccff7
ACR-d73d62e877d4456ea1143869e82ba43e
ACR-b26003f9c81f4c6faf5d03c674c24137
ACR-5f4e796bf84f4dc9bafe4c83930b21ac
ACR-fe1a2e356a3b47a48596cb0f9a51ac4a
ACR-86e699cca71a4a499f89faea74f81a9b
 */
package org.sonarsource.sonarlint.core.plugin.commons.container;

import jakarta.annotation.Priority;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PriorityBeanFactoryTests {
  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();
  private final DefaultListableBeanFactory parentBeanFactory = new PriorityBeanFactory();
  private final DefaultListableBeanFactory beanFactory = new PriorityBeanFactory();

  @BeforeEach
  public void setUp() {
    //ACR-2898ceffc6a442c9816f335b2888eb98
    beanFactory.addBeanPostProcessor(new AutowiredAnnotationBeanPostProcessor());
    //ACR-7361433673664a8f9d65d398fd2e4836
    beanFactory.setDependencyComparator(new AnnotationAwareOrderComparator());
    beanFactory.setParentBeanFactory(parentBeanFactory);
  }

  @Test
  void give_priority_to_child_container() {
    parentBeanFactory.registerBeanDefinition("A1", new RootBeanDefinition(A1.class));

    beanFactory.registerBeanDefinition("A2", new RootBeanDefinition(A2.class));
    beanFactory.registerBeanDefinition("B", new RootBeanDefinition(B.class));

    assertThat(beanFactory.getBean(B.class).dep.getClass()).isEqualTo(A2.class);
  }

  @Test
  void follow_priority_annotations() {
    parentBeanFactory.registerBeanDefinition("A3", new RootBeanDefinition(A3.class));

    beanFactory.registerBeanDefinition("A1", new RootBeanDefinition(A1.class));
    beanFactory.registerBeanDefinition("A2", new RootBeanDefinition(A2.class));
    beanFactory.registerBeanDefinition("B", new RootBeanDefinition(B.class));

    assertThat(beanFactory.getBean(B.class).dep.getClass()).isEqualTo(A3.class);
  }

  @Test
  void throw_NoUniqueBeanDefinitionException_if_cant_find_single_bean_with_higher_priority() {
    beanFactory.registerBeanDefinition("A1", new RootBeanDefinition(A1.class));
    beanFactory.registerBeanDefinition("A2", new RootBeanDefinition(A2.class));
    beanFactory.registerBeanDefinition("B", new RootBeanDefinition(B.class));

    assertThatThrownBy(() -> beanFactory.getBean(B.class))
      .hasRootCauseInstanceOf(NoUniqueBeanDefinitionException.class);
  }

  private static class B {
    private final A dep;

    public B(A dep) {
      this.dep = dep;
    }
  }

  private interface A {

  }

  private static class A1 implements A {

  }

  private static class A2 implements A {

  }

  @Priority(1)
  private static class A3 implements A {

  }

}
