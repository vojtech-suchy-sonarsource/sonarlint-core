/*
ACR-c2ee5f394bc14deb99d4c0c95f1bd022
ACR-a5309f318bf34e70872f40193b82ee3f
ACR-c0846b3fb2e7447a88c58302534a7b1e
ACR-d22ef37c2bb44309887b6a58a6fc8ea4
ACR-5c2aec70424c4bfab9599bd368acd984
ACR-35d8e5500946476f82130fb72e6629eb
ACR-6f2831ce9dd44a38840501d46003513b
ACR-8a1ccc86a17740a59ba8d12de8d7a78b
ACR-17b4763953354e8a9ffcc74f6bfa7309
ACR-75037b1300de41a68177800e3e9683ab
ACR-086c2949bbbf4ca79d2a6a6e7663e09a
ACR-ec7e44ed67394eee9d37c0c6aa583b77
ACR-2c8cf391d54048dcb65805f1673c94b4
ACR-af06106374414702a9a1841099ba5ae3
ACR-c945a8ce38e24c338a59d59e4f32beb0
ACR-4184b1e2688846c7aee7a623d8a288ec
ACR-1919e3bd7ce94cd0b3df6b205cf6d404
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

    //ACR-8856a920672d4f1dbee4d3332d87d8ca
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
