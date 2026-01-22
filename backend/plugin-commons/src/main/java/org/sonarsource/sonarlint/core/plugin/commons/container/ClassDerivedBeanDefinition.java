/*
ACR-aed0b95cfad94a3baa32419882b166f4
ACR-36cfec6403a94a92a765667df7e48577
ACR-044536d57d3142cf943f897858ac8362
ACR-048ad650f1e64ff4845a560b14809f99
ACR-7ec0bc2ac978452cb44cf59ee9306a8f
ACR-7f49649036fe4d5bafc3656cb09e91c9
ACR-57e6ac9abe654cdcabb6798ce647a8e6
ACR-7fe175cd33764554a49908ab7ac8959b
ACR-4268fe72771d4a5bac9e92fe251b7006
ACR-8eccf725b6d148e7b90af3ca1a9b6c26
ACR-a1f7c67fb68f4fb695807a111d87f7fb
ACR-0609b4c8b87d418781de01c0dcc354da
ACR-9212c340d54747a090a6b54b5635cf41
ACR-ce3cccb8cfe448bd9ab1fa594be3c0b4
ACR-877a7d9ca68a49a9a979a154363e8608
ACR-33a855d7840a4ff0b8883bfe19c823b7
ACR-f1bd56b6825c44c3a68c5dd0b056d197
 */
package org.sonarsource.sonarlint.core.plugin.commons.container;

import java.lang.reflect.Constructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.lang.Nullable;

/*ACR-0ee045453f1f443a9c2abe266e8bf746
ACR-d4c825a8cf05455d9a13c10e81cb5f44
ACR-d1bf5589de894f3ab4c57d9525696ee1
ACR-987cc78d3bc5444a9a1e6bad42389b96
 */
public class ClassDerivedBeanDefinition extends RootBeanDefinition {
  public ClassDerivedBeanDefinition(Class<?> beanClass) {
    super(beanClass);
  }

  public ClassDerivedBeanDefinition(ClassDerivedBeanDefinition original) {
    super(original);
  }

  /*ACR-20c572cbe67741069c3bc3b5f9fa06ac
ACR-5bb4e2aa639345cc9fb7c1b7f2e20303
ACR-5dfa63b8acd44a56a3e61a9a892875ad
   */
  @Override
  @Nullable
  public Constructor<?>[] getPreferredConstructors() {
    Class<?> clazz = getBeanClass();
    Constructor<?> primaryCtor = BeanUtils.findPrimaryConstructor(clazz);
    if (primaryCtor != null) {
      return new Constructor<?>[] {primaryCtor};
    }
    Constructor<?>[] publicCtors = clazz.getConstructors();
    if (publicCtors.length > 0) {
      return publicCtors;
    }
    return null;
  }

  @Override
  public RootBeanDefinition cloneBeanDefinition() {
    return new ClassDerivedBeanDefinition(this);
  }
}
