/*
ACR-68ee0f387d3445b8a26ebdc6be958f89
ACR-af0ed11e81da41be886951f5636ba58e
ACR-638142a8ceb244f7a38977f468b877b4
ACR-dca0e0d5c41b4ca39699785666766033
ACR-2124ddd330f843989a49f0a66f6949c0
ACR-ad200d14b2ad4efd866410c050f60525
ACR-f6a88640f771428e9e2079bd60c31db0
ACR-a968ddf6477640a089a447720de5a63a
ACR-3ddf57df18394877a4c33c734915e8b7
ACR-5f3ceef561c14560a90d865655cb914e
ACR-80036f58f6ee471f9ef30fa17f8b93a7
ACR-3d3dc169a5dc4a50902f6d4743db03a5
ACR-9ee2d65e538c44f68973ea0947bad3a3
ACR-a77789dfa091491ea425e8e637a55cfc
ACR-bcc4286c9dea4a6c980a63ef47c9b5b1
ACR-cdf69eed63d24ca49f3efb03d9329491
ACR-b4cbebc9a560429c8ef94a56f8eabdd7
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
    //ACR-ded8f92e727b4a19b204b3ac62e7c001
    beanFactory.addBeanPostProcessor(new AutowiredAnnotationBeanPostProcessor());
    //ACR-307992ff19d14e2d80cbb95350e25623
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
