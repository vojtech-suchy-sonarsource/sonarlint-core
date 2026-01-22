/*
ACR-44cb12289ede40959da0f003a8045907
ACR-9e7f9e925488408dbf5b159825d5472d
ACR-d867ffea661b4d9c8679d4db10cf88de
ACR-103583b007aa47778caac8206b26fc07
ACR-a7d805efde624074aba3e35f8f07d65a
ACR-30995e6c04ad47eaa1c19f2fb2713759
ACR-a30ac5272d6c42f48d63c4b361c1f7b5
ACR-f5c0d22c393141de9d0eca5351bde7bc
ACR-077b70a9497c4443a0f62742fe79622f
ACR-b1be4aed96c94c02914ea05786e019bf
ACR-42f9c410d45b47d59b4ef2ac0ba46b20
ACR-be78856217b6432f8f4e7b50743df14a
ACR-701d3487226a40d591105bc707529a96
ACR-c3bdee91e92c4ebe843cc049a2099f7b
ACR-07687b5542ca4bd4986b18d2d4d5a12d
ACR-16d7d4326d574d47994c94129fad98f6
ACR-7efc5e39455e4968a6fcc9d2b8924723
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
