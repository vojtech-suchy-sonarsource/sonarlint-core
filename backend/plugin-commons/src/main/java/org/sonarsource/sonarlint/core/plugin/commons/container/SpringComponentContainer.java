/*
ACR-09453dde110a448989f3c47408ec6a4f
ACR-dae27d92bdc646b4a97a9ad0b7b236d4
ACR-56d65d6e7a0e4d5e8f4091b20deb3077
ACR-3f8b6c4773f043b38e74b9f8e12739ef
ACR-4ae303cccd5e4937b31731c94d0b5132
ACR-5a0bb69f25254e76bd486008914becc3
ACR-30d196d8ac664f548d506d0e94040968
ACR-fcf0aefdf44d45e9b6855721158cfe5e
ACR-b6eecc4c47f948a2a0091d753685e665
ACR-5b43dc1135374eb4a7762a9544470817
ACR-ed64bc4ad8894ba5a198c771bd4efdcd
ACR-cc5cd9cad5e6485cabf5d6a399ce6904
ACR-7f47b396e4c44ab7a59258e6839b2ed6
ACR-e267159dc2aa4e4fbdfcdf6c41d1e218
ACR-194ebe8dd0dd4906881e158bd5582bb0
ACR-4f73cd5ad32f4260beb21e6af4cfc9d9
ACR-f56d2a1ed3da4b8ba4d4d51ae6ea53f0
 */
package org.sonarsource.sonarlint.core.plugin.commons.container;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.sonar.api.config.PropertyDefinitions;
import org.sonar.api.utils.System2;
import org.sonarsource.sonarlint.core.commons.tracing.Step;
import org.sonarsource.sonarlint.core.commons.tracing.Trace;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static java.util.Collections.emptyList;
import static org.sonarsource.sonarlint.core.commons.tracing.Trace.startChildren;

public class SpringComponentContainer implements StartableContainer {

  protected final AnnotationConfigApplicationContext context;
  @Nullable
  protected final SpringComponentContainer parent;
  protected final List<SpringComponentContainer> children = new ArrayList<>();

  private final PropertyDefinitions propertyDefinitions;
  private final ComponentKeys componentKeys = new ComponentKeys();
  @Nullable
  private Trace trace;

  public SpringComponentContainer() {
    this(null, new PropertyDefinitions(System2.INSTANCE), emptyList(), new LazyUnlessStartableStrategy());
  }

  protected SpringComponentContainer(List<?> externalExtensions) {
    this(null, new PropertyDefinitions(System2.INSTANCE), externalExtensions, new LazyUnlessStartableStrategy());
  }

  protected SpringComponentContainer(SpringComponentContainer parent) {
    this(parent, parent.propertyDefinitions, emptyList(), new LazyUnlessStartableStrategy());
  }

  protected SpringComponentContainer(SpringComponentContainer parent, SpringInitStrategy initStrategy) {
    this(parent, parent.propertyDefinitions, emptyList(), initStrategy);
  }

  protected SpringComponentContainer(@Nullable SpringComponentContainer parent, PropertyDefinitions propertyDefs, List<?> externalExtensions, SpringInitStrategy initStrategy) {
    this.parent = parent;
    this.propertyDefinitions = propertyDefs;
    this.context = new AnnotationConfigApplicationContext(new PriorityBeanFactory());
    this.context.setAllowBeanDefinitionOverriding(false);
    ((AbstractAutowireCapableBeanFactory) context.getBeanFactory()).setParameterNameDiscoverer(null);
    if (parent != null) {
      context.setParent(parent.context);
      parent.children.add(this);
    }
    add(initStrategy);
    add(this);
    add(new StartableBeanPostProcessor());
    add(externalExtensions);
    add(propertyDefs);
  }

  /*ACR-389b1b6492ec4ef8b0a51757d8fdfe2b
ACR-3fffe133e95e46d29a474d23d1e412f0
ACR-997d0b5db2444780a854cffa7a2d2cc6
ACR-7e9d839db1b7442dbaa425dedcce4d0c
ACR-0f789c973fce48b6843e603949f4dddf
ACR-86abad7ff2ba4a57bd627c80315476d9
   */
  @Override
  public Container add(Object... objects) {
    for (Object o : objects) {
      if (o instanceof Class<?> clazz) {
        context.registerBean(componentKeys.ofClass(clazz), clazz);
        declareProperties(o);
      } else if (o instanceof Iterable) {
        ((Iterable<?>) o).forEach(this::add);
      } else {
        registerInstance(o);
        declareProperties(o);
      }
    }
    return this;
  }

  private <T> void registerInstance(T instance) {
    Supplier<T> supplier = () -> instance;
    Class<T> clazz = (Class<T>) instance.getClass();
    context.registerBean(componentKeys.ofInstance(instance), clazz, supplier);
  }

  /*ACR-622fd07da97b42c388fd7e4b501b1bff
ACR-eea0011b0f014cccb67857e2eaead716
ACR-d562ad4208ac48fe96ad1f0e29f8fa58
ACR-bb08ce72b8de4dff8cdad8ce2c87eb12
   */
  private void addExtension(Object o) {
    if (o instanceof Class<?> clazz) {
      var bd = new ClassDerivedBeanDefinition(clazz);
      context.registerBeanDefinition(componentKeys.ofClass(clazz), bd);
    } else if (o instanceof Iterable) {
      ((Iterable<?>) o).forEach(this::addExtension);
    } else {
      registerInstance(o);
    }
  }

  @Override
  public <T> T getComponentByType(Class<T> type) {
    try {
      return context.getBean(type);
    } catch (Exception t) {
      throw new IllegalStateException("Unable to load component " + type, t);
    }
  }

  @Override
  public <T> Optional<T> getOptionalComponentByType(Class<T> type) {
    try {
      return Optional.of(context.getBean(type));
    } catch (NoSuchBeanDefinitionException t) {
      return Optional.empty();
    }
  }

  @Override
  public <T> List<T> getComponentsByType(Class<T> type) {
    try {
      return new ArrayList<>(context.getBeansOfType(type).values());
    } catch (Exception t) {
      throw new IllegalStateException("Unable to load components " + type, t);
    }
  }

  public AnnotationConfigApplicationContext context() {
    return context;
  }

  public void execute(@Nullable Trace trace) {
    this.trace = trace;
    RuntimeException r = null;
    try {
      startComponents();
    } catch (RuntimeException e) {
      r = e;
    } finally {
      try {
        stopComponents();
      } catch (RuntimeException e) {
        if (r == null) {
          r = e;
        }
      }
    }
    if (r != null) {
      throw r;
    }
  }

  @Override
  public SpringComponentContainer startComponents() {
    startChildren(trace, "startComponents",
      new Step("doBeforeStart", this::doBeforeStart),
      new Step("refresh", context::refresh),
      new Step("doAfterStart", this::doAfterStart)
    );
    return this;
  }

  public SpringComponentContainer stopComponents() {
    try {
      stopChildren();
      if (context.isActive()) {
        context.close();
      }
    } finally {
      if (parent != null) {
        parent.children.remove(this);
      }
    }
    return this;
  }

  private void stopChildren() {
    //ACR-45b74eed80de4ab8b71e1f77b10ff0a8
    var childrenCopy = new ArrayList<>(this.children);
    Collections.reverse(childrenCopy);
    childrenCopy.forEach(SpringComponentContainer::stopComponents);
  }

  public SpringComponentContainer createChild() {
    return new SpringComponentContainer(this);
  }

  @Override
  @CheckForNull
  public SpringComponentContainer getParent() {
    return parent;
  }

  @Override
  public SpringComponentContainer addExtension(@Nullable String pluginKey, Object extension) {
    try {
      addExtension(extension);
    } catch (Throwable t) {
      throw new IllegalStateException("Unable to register extension " + getName(extension) + (pluginKey != null ? (" from plugin '" + pluginKey + "'") : ""), t);
    }
    declareProperties(extension);
    return this;
  }

  private static String getName(Object extension) {
    if (extension instanceof Class) {
      return ((Class<?>) extension).getName();
    }
    return getName(extension.getClass());
  }

  @Override
  public SpringComponentContainer declareProperties(Object extension) {
    this.propertyDefinitions.addComponent(extension, "");
    return this;
  }

  /*ACR-654cd6d0fd8647a79fb9660a5a444791
ACR-02f815517c494a9bb9c48a155a346cd5
   */
  protected void doBeforeStart() {
    //ACR-5d2644288b3641478d55e0cc742b59f8
  }

  /*ACR-3f8b25df5ee34c9ab2d01f1bc71b4655
ACR-9eb73dc763214ad4a6ae4d8cd5c98883
   */
  protected void doAfterStart() {
    //ACR-e537c59ef6f8446b9b2a149ac20a9f42
  }
}
