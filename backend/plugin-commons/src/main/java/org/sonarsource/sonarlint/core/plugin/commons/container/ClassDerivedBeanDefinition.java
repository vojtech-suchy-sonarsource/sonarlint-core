/*
ACR-90d0b3bc036d4b5892c63def82a741a6
ACR-be13f323bac14ea8a73c87c0fae74ced
ACR-f561edb3263647b19c86e53fd2cd48cb
ACR-b0c35ef89a25476fbcf5c1306ebe71a3
ACR-7c1143c7536d47d5a46fa730fbaafebf
ACR-f75f6a08fb8746b08cbefc9d5e66d416
ACR-d0f836798b104d6c83eb6fffd0b000a4
ACR-31227f43fa26420eab447f0fba13e0e1
ACR-81caedcba5f14370a9f6ae70f5655ea0
ACR-ef6c118c32fc40458ec29a088e1cd9d5
ACR-1a54e73619ee4de19272ffbe827171ad
ACR-784607f186c14344965de191e99f1b8f
ACR-defbe49def9345909e2b82a5492add20
ACR-cff3d886920d499eac3445823550def9
ACR-36cce8cdad874210a0d3b58bb329836d
ACR-5953867496cc47d58864831d1c6e2d9d
ACR-a817af575eb24ff3851b6fb8b24b450b
 */
package org.sonarsource.sonarlint.core.plugin.commons.container;

import java.lang.reflect.Constructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.lang.Nullable;

/*ACR-2b4fe43fcc664836be257ac467eaa8b4
ACR-f2da1f03746f4bd6b02bad0b9f4bf5cd
ACR-808a2689191c41e9a37da367178219d3
ACR-461ef599ef8d49a7912840e83b23a1dc
 */
public class ClassDerivedBeanDefinition extends RootBeanDefinition {
  public ClassDerivedBeanDefinition(Class<?> beanClass) {
    super(beanClass);
  }

  public ClassDerivedBeanDefinition(ClassDerivedBeanDefinition original) {
    super(original);
  }

  /*ACR-fbe0bd99a38c49cb95906f6a1623e9b5
ACR-ce4197f2fd9f4023a691f31f49203373
ACR-33e05b277ab74498b5c75223122518a5
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
