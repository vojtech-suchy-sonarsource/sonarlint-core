/*
ACR-2fd7ccfd80ac408cb9ee94e58ae4e311
ACR-96a718d435ac49149f749510055c1c68
ACR-2613ce23e8c54b55a2034e422477810a
ACR-7fe35001641c4054bfadac5946ff0710
ACR-3063b78a9a524342a136721156c3dabd
ACR-be234278374b4b58a53e00a2736bc1d0
ACR-c3e6fea0d9c64110921ec6a7e1577e38
ACR-c5620ba3e07c43ce8c400af4abd9a6f3
ACR-4a7ce1edb62c4fd6b28958847f320a03
ACR-ad55efab19a24ad192f9406e1c38db14
ACR-26a172ec747d474dbc138c89f681e6d4
ACR-dfdc1a5cfb6d4730a522f770f4e318b5
ACR-981a1155356c4902b5a0d03103d50356
ACR-759f2bc42a644870aab1253984f631a8
ACR-b4bf20a7398c493aa2476ab2dcc55893
ACR-c6ad88ac459c4cdbb95a062c78eb3367
ACR-a690e29333074b8a8ad87acb6ee1bc5e
 */
package org.sonarsource.sonarlint.core.plugin.commons.container;

import javax.annotation.Nullable;
import org.sonar.api.Startable;
import org.springframework.beans.factory.config.BeanDefinition;

public class LazyUnlessStartableStrategy extends SpringInitStrategy {
  @Override
  protected boolean isLazyInit(BeanDefinition beanDefinition, @Nullable Class<?> clazz) {
    return clazz == null || !Startable.class.isAssignableFrom(clazz);
  }
}
