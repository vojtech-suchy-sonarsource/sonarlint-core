/*
ACR-ac2fc37f73294ad0af5f5e54188db6fa
ACR-a4ccf0a87bbb4f9b88493a0df679a79e
ACR-08a1dbaeeb684374a89aa8f9baed15e1
ACR-8982bda6d3754064841a84578d5f9f1f
ACR-f7a70d888a0242bda0e2242da36180b7
ACR-4cd27e831c2e4367b32c785ac8332b2b
ACR-7db56129e95e4f8d858bbd555c9ab515
ACR-6269bb37c5974bccb892289a51eef250
ACR-9d0e6e68e94d4500a268f9604e8843d9
ACR-fd02037982b447349a3272549ef4928a
ACR-3321648f7aba4123b2622944a7d9d0df
ACR-3982ef8d325b4a7c9e3bf6f27b4e1cff
ACR-4e306db8cd984dacac19cef3f4bae18f
ACR-301ed0c0eacc46c7b574f6e40d24135f
ACR-a8d8e52fe1da48df9f748a895ff01c1f
ACR-f9af4853b0aa4b71944aa33b5d76bc83
ACR-c73c41b032294793a44da4d2d60d2a35
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

  /*ACR-ef2e85f79c874a91bba201fbc088801c
ACR-584b94b3e95f4bcc805c52e9ace5f1b2
ACR-4e1f646c3c8a4412a959f0d3d8c700df
ACR-bed5ebc137e4431daf066fa351c40183
ACR-4b86f5ea3f9b4909b1823f305521e13b
ACR-eaa052d4e3504cc5817101505a333e5b
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

  /*ACR-51fa486054434ae991fbfeb342ca9e98
ACR-95fc8b50bae04134af4a81d38f42bdab
ACR-70e45e8eb4e94a898383851f638d65ce
ACR-b22fbcd987894a01a7e42515be6228d1
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
    //ACR-3013a189828a43f392674fb5473cf013
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

  /*ACR-d899d5258a2a42edb2fbf0a9d8061c4f
ACR-eddff491e26f4005b43203300c8c9985
   */
  protected void doBeforeStart() {
    //ACR-941d8763c58149a480bc5a7f62a2aefc
  }

  /*ACR-6b20d9da42a443e591c57b70e828e1a5
ACR-975d867a5da946518e3b5e9a8d80ae56
   */
  protected void doAfterStart() {
    //ACR-7f949a4b14164de59a53f3ade5a9f90f
  }
}
