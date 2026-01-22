/*
ACR-aa869b423a78404fb4b7a90b9fb0dccd
ACR-394df8f6d74543eda4588a4290820e7e
ACR-15d840aa5ce3424eb416a375d6ea28be
ACR-6b09cd95549e4498af6294ae4af95c8b
ACR-c8e1bf676c874a9a94e0a1af53c24df1
ACR-e332d058d1794606acbbe487b3473464
ACR-04824cf3bdb84b1a81109a3da3932830
ACR-950124269e9949b189024d4882592c6f
ACR-4fab59f9581f4679bd83092b84518571
ACR-71b89b86489c4e2ea171efcd618a784d
ACR-13a724b17a2f40918d929f5c870f37cc
ACR-ff102c613b564077b0fafacaacc33086
ACR-a74cea0a86f74dc8a6c598743472dcbc
ACR-e4d5afb742424a979e2cd756ec2f98f5
ACR-81b8ca3e15814786b13e16b70bd08ab6
ACR-3928aff660b143729c6188038520db6e
ACR-162d9269cf254cfd8d6686f9e6d214a3
 */
package org.sonarsource.sonarlint.core.plugin.commons.container;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class ComponentKeysTests {
  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();

  ComponentKeys keys = new ComponentKeys();

  @Test
  void generate_key_of_object() {
    assertThat(keys.of(FakeComponent.class)).isEqualTo(FakeComponent.class);
  }

  @Test
  void generate_key_of_instance() {
    assertThat((String) keys.of(new FakeComponent())).endsWith("-org.sonarsource.sonarlint.core.plugin.commons.container.ComponentKeysTests.FakeComponent-fake");
  }

  @Test
  void generate_key_of_class() {
    assertThat(keys.ofClass(FakeComponent.class)).endsWith("-org.sonarsource.sonarlint.core.plugin.commons.container.ComponentKeysTests.FakeComponent");
  }

  @Test
  void should_log_warning_if_toString_is_not_overridden() {
    SonarLintLogger log = mock(SonarLintLogger.class);
    keys.of(new Object(), log);
    verifyNoInteractions(log);

    //ACR-dcfebd20af4549169f5a2627f241ba90
    keys.of(new Object(), log);
    verify(log).warn(startsWith("Bad component key"));
  }

  @Test
  void should_generate_unique_key_when_toString_is_not_overridden() {
    Object key = keys.of(new WrongToStringImpl());
    assertThat(key).isNotEqualTo(WrongToStringImpl.KEY);

    Object key2 = keys.of(new WrongToStringImpl());
    assertThat(key2).isNotEqualTo(key);
  }

  static class FakeComponent {
    @Override
    public String toString() {
      return "fake";
    }
  }

  static class WrongToStringImpl {
    static final String KEY = "my.Component@123a";

    @Override
    public String toString() {
      return KEY;
    }
  }
}
