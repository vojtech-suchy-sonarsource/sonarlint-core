/*
ACR-29a4f3f579fc4d519bd17c7ae2994a03
ACR-ee86c94c4d114ccab0a384d8a6496387
ACR-f0ac02de015843d5b71597217a89d2d2
ACR-4d839b2bdc184e5b9fe3e0d1c4bac38b
ACR-48abff89cae14f4f872da3877e2e36aa
ACR-4199c0c279d140abaf5c6a4c0db4c56a
ACR-c54b3ad9e3254390b2e76165055b3df4
ACR-b7463e46b21046238f7fac181092bbc5
ACR-2ecd1d516c0642549b1f019cce11a7d4
ACR-262e9192b28d4a4b9295bb10dd160d3d
ACR-7e8a6e4f4987471ca01cfd4b3fa01485
ACR-86769ae6c839499789dc708f105b6a18
ACR-3583b3f7f9f64bd29966bd1cc0ee4f74
ACR-22f71400ee6d4f478d8d037f249f16b9
ACR-202e2388fbf64e229798434e0fea5760
ACR-4b870c4ab7a8469dadf06ea639a802ee
ACR-d5d85ba034894044850f30d24de436f2
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
