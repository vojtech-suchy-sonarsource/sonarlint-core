/*
ACR-63c3d8e4afd142dc9e86a7c8a921ebfd
ACR-64934e3973bd4e2183b78a524ced69e9
ACR-c67cc13edd0b4ccca4ef1a4a38dca1dc
ACR-a61ed81f4b444dd79e0fc6772a56ff07
ACR-e09580956d694a1898386a69d23cbeaa
ACR-b97d6d6eea7a43628b086e338e5da407
ACR-48831434bd694c9e816975572bc23b7e
ACR-6014e8f18e8f4d49a6fb6c533f99b386
ACR-c013414acb604554a19570418f005e87
ACR-ac4da5ea88374f57bf02c06352a5b189
ACR-5f22b203a9364848819560d97a953345
ACR-043a0f5ff41746979d37761a0d99b8d2
ACR-19d121d5267244c68d236fe18786d4b0
ACR-d510a81f076b41ed9f3e8a2c1d59cba8
ACR-5c0c33f8802241d884705c5318596458
ACR-f48579d87e434a2698f41a691feabd91
ACR-f2103e44026a4caca2f3b9232467a0f2
 */
package org.sonarsource.sonarlint.core.plugin.commons.container;

import org.junit.jupiter.api.Test;
import org.sonar.api.Startable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

 class StartableBeanPostProcessorTests {
  private final StartableBeanPostProcessor underTest = new StartableBeanPostProcessor();

  @Test
   void starts_api_startable() {
    Startable startable = mock(Startable.class);
    underTest.postProcessBeforeInitialization(startable, "startable");
    verify(startable).start();
    verifyNoMoreInteractions(startable);
  }

  @Test
   void stops_api_startable() {
    Startable startable = mock(Startable.class);
    underTest.postProcessBeforeDestruction(startable, "startable");
    verify(startable).stop();
    verifyNoMoreInteractions(startable);
  }

  @Test
   void startable_and_autoCloseable_should_require_destruction() {
    assertThat(underTest.requiresDestruction(mock(Startable.class))).isTrue();
    assertThat(underTest.requiresDestruction(mock(org.sonar.api.Startable.class))).isTrue();
    assertThat(underTest.requiresDestruction(mock(Object.class))).isFalse();
  }
}
