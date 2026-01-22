/*
ACR-c3f9aa7f7d1140e4b73a0cdbb7650d06
ACR-384eeb388240415788a8a2fbdaba7dce
ACR-a3304be3beef4e0fa81652cfd341ab80
ACR-66fcaa4d13544cbb87de87d54509bcd9
ACR-67c4ff3b2d4a4cd9b98a275c683394af
ACR-08baedb2e9a742269fb5282c60588b00
ACR-362c36f1d8d7455fa473755dbf4cb928
ACR-0e979544d62941fcaced23ebdbc8c582
ACR-922a8d3ce8e14e6e87002a293ffb908e
ACR-6b36bf30ed774ff18aee505fcf2bdf6a
ACR-79764d5dfe8e4e87861ba926f6937429
ACR-d2e6a9fefcb24a9c829ef10b4f4fbadd
ACR-6b58f67dea044ab7b068ee58b89563c8
ACR-633784d8d90e46ab8c74b30101a67082
ACR-76722937cdc74f7d945d64aa457914b1
ACR-e1eb557b577844dab24582019ead31fe
ACR-ec962aacf4c54559a444412d2a1ab038
 */
package org.sonarsource.sonarlint.core.ai.ide;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import org.sonarsource.sonarlint.core.embedded.server.EmbeddedServer;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.ai.AiAgent;
import org.sonarsource.sonarlint.core.telemetry.TelemetryService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AiHookServiceTests {

  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();

  @Test
  void it_should_generate_nodejs_script_when_nodejs_detected() {
    var embeddedServer = mock(EmbeddedServer.class);
    var telemetryService = mock(TelemetryService.class);
    var executableLocator = mock(ExecutableLocator.class);

    when(embeddedServer.getPort()).thenReturn(64120);
    when(executableLocator.detectBestExecutable()).thenReturn(Optional.of(HookScriptType.NODEJS));

    var service = new AiHookService(embeddedServer, telemetryService, executableLocator);
    var response = service.getHookScriptContent(AiAgent.WINDSURF);

    assertThat(response.getScriptFileName()).isEqualTo("sonarqube_analysis_hook.js");
    assertThat(response.getScriptContent())
      .contains("#!/usr/bin/env node")
      .contains("hostname: 'localhost'")
      .contains("STARTING_PORT = 64120")
      .contains("ENDING_PORT = 64130")
      .contains("path: '/sonarlint/api/analysis/files'")
      .contains("path: '/sonarlint/api/status'");
    assertThat(response.getConfigFileName()).isEqualTo("hooks.json");
    assertThat(response.getConfigContent())
      .contains("\"post_write_code\"")
      .contains("{{SCRIPT_PATH}}")
      .contains("\"show_output\": true");
    verify(telemetryService).aiHookInstalled(AiAgent.WINDSURF);
  }

  @Test
  void it_should_generate_python_script_when_python_detected() {
    var embeddedServer = mock(EmbeddedServer.class);
    var telemetryService = mock(TelemetryService.class);
    var executableLocator = mock(ExecutableLocator.class);

    when(embeddedServer.getPort()).thenReturn(64121);
    when(executableLocator.detectBestExecutable()).thenReturn(Optional.of(HookScriptType.PYTHON));

    var service = new AiHookService(embeddedServer, telemetryService, executableLocator);
    var response = service.getHookScriptContent(AiAgent.WINDSURF);

    assertThat(response.getScriptFileName()).isEqualTo("sonarqube_analysis_hook.py");
    assertThat(response.getScriptContent())
      .contains("#!/usr/bin/env python3")
      .contains("STARTING_PORT = 64120")
      .contains("ENDING_PORT = 64130")
      .contains("/sonarlint/api/analysis/files")
      .contains("/sonarlint/api/status");
    assertThat(response.getConfigFileName()).isEqualTo("hooks.json");
    assertThat(response.getConfigContent())
      .contains("\"post_write_code\"")
      .contains("{{SCRIPT_PATH}}")
      .contains("\"show_output\": true");
    verify(telemetryService).aiHookInstalled(AiAgent.WINDSURF);
  }

  @Test
  void it_should_generate_bash_script_when_bash_detected() {
    var embeddedServer = mock(EmbeddedServer.class);
    var telemetryService = mock(TelemetryService.class);
    var executableLocator = mock(ExecutableLocator.class);

    when(embeddedServer.getPort()).thenReturn(64122);
    when(executableLocator.detectBestExecutable()).thenReturn(Optional.of(HookScriptType.BASH));

    var service = new AiHookService(embeddedServer, telemetryService, executableLocator);
    var response = service.getHookScriptContent(AiAgent.WINDSURF);

    assertThat(response.getScriptFileName()).isEqualTo("sonarqube_analysis_hook.sh");
    assertThat(response.getScriptContent())
      .contains("#!/bin/bash")
      .contains("STARTING_PORT=64120")
      .contains("ENDING_PORT=64130")
      .contains("/sonarlint/api/analysis/files")
      .contains("/sonarlint/api/status");
    assertThat(response.getConfigFileName()).isEqualTo("hooks.json");
    assertThat(response.getConfigContent())
      .contains("\"post_write_code\"")
      .contains("{{SCRIPT_PATH}}")
      .contains("\"show_output\": true");
    verify(telemetryService).aiHookInstalled(AiAgent.WINDSURF);
  }

  @Test
  void it_should_throw_exception_when_embedded_server_not_started() {
    var embeddedServer = mock(EmbeddedServer.class);
    var telemetryService = mock(TelemetryService.class);
    var executableLocator = mock(ExecutableLocator.class);

    when(embeddedServer.getPort()).thenReturn(-1);

    var service = new AiHookService(embeddedServer, telemetryService, executableLocator);

    assertThatThrownBy(() -> service.getHookScriptContent(AiAgent.WINDSURF))
      .isInstanceOf(IllegalStateException.class)
      .hasMessageContaining("Embedded server is not started");
  }

  @Test
  void it_should_throw_exception_when_no_executable_found() {
    var embeddedServer = mock(EmbeddedServer.class);
    var telemetryService = mock(TelemetryService.class);
    var executableLocator = mock(ExecutableLocator.class);

    when(embeddedServer.getPort()).thenReturn(64120);
    when(executableLocator.detectBestExecutable()).thenReturn(Optional.empty());

    var service = new AiHookService(embeddedServer, telemetryService, executableLocator);

    assertThatThrownBy(() -> service.getHookScriptContent(AiAgent.WINDSURF))
      .isInstanceOf(IllegalStateException.class)
      .hasMessageContaining("No suitable executable found");
  }

  @Test
  void it_should_embed_correct_agent_in_script_comment_for_windsurf() {
    var embeddedServer = mock(EmbeddedServer.class);
    var telemetryService = mock(TelemetryService.class);
    var executableLocator = mock(ExecutableLocator.class);

    when(embeddedServer.getPort()).thenReturn(64120);
    when(executableLocator.detectBestExecutable()).thenReturn(Optional.of(HookScriptType.NODEJS));

    var service = new AiHookService(embeddedServer, telemetryService, executableLocator);
    var response = service.getHookScriptContent(AiAgent.WINDSURF);

    assertThat(response.getScriptContent())
      .contains("SonarQube for IDE Windsurf Hook")
      .contains("EXPECTED_IDE_NAME = 'Windsurf'");
    verify(telemetryService).aiHookInstalled(AiAgent.WINDSURF);
  }

  @Test
  void it_should_throw_exception_for_unsupported_cursor_agent() {
    var embeddedServer = mock(EmbeddedServer.class);
    var telemetryService = mock(TelemetryService.class);
    var executableLocator = mock(ExecutableLocator.class);

    when(embeddedServer.getPort()).thenReturn(64120);
    when(executableLocator.detectBestExecutable()).thenReturn(Optional.of(HookScriptType.PYTHON));

    var service = new AiHookService(embeddedServer, telemetryService, executableLocator);

    assertThatThrownBy(() -> service.getHookScriptContent(AiAgent.CURSOR))
      .isInstanceOf(UnsupportedOperationException.class)
      .hasMessageContaining("hook configuration not yet implemented");
  }

  @Test
  void it_should_throw_exception_for_unsupported_kiro_agent() {
    var embeddedServer = mock(EmbeddedServer.class);
    var telemetryService = mock(TelemetryService.class);
    var executableLocator = mock(ExecutableLocator.class);

    when(embeddedServer.getPort()).thenReturn(64120);
    when(executableLocator.detectBestExecutable()).thenReturn(Optional.of(HookScriptType.PYTHON));

    var service = new AiHookService(embeddedServer, telemetryService, executableLocator);

    assertThatThrownBy(() -> service.getHookScriptContent(AiAgent.KIRO))
      .isInstanceOf(UnsupportedOperationException.class)
      .hasMessageContaining("hook configuration not yet implemented");
  }

  @Test
  void it_should_throw_exception_for_unsupported_github_copilot_agent() {
    var embeddedServer = mock(EmbeddedServer.class);
    var telemetryService = mock(TelemetryService.class);
    var executableLocator = mock(ExecutableLocator.class);

    when(embeddedServer.getPort()).thenReturn(64120);
    when(executableLocator.detectBestExecutable()).thenReturn(Optional.of(HookScriptType.BASH));

    var service = new AiHookService(embeddedServer, telemetryService, executableLocator);

    assertThatThrownBy(() -> service.getHookScriptContent(AiAgent.GITHUB_COPILOT))
      .isInstanceOf(UnsupportedOperationException.class)
      .hasMessageContaining("GitHub Copilot does not support hooks");
  }

}

