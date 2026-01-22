/*
ACR-5c327d1570ae4f84a5e94f9b33c32f9e
ACR-77572dcab1d14c9da7d806dc0b3cd754
ACR-d98194063c1b4435903154c58748cb97
ACR-698e25599af048f496d80af07550d206
ACR-aa73895974464593ba7da13c772527ec
ACR-77fb26d11ca449bfacf28ad95f46f881
ACR-a129d652a9a54b9b9c6224471173a1da
ACR-8ef7d338e6d14887bc5791439032261b
ACR-9f1d7554a85f457f85b36ba3eca68d3b
ACR-551ec4a08c584d0c88a6ea18be28c175
ACR-cab4140c611b4acca0b4879e44ef1a0a
ACR-713c70ab2d364fc0984c07129ab8f4b8
ACR-1cdb90fde79840ba9bf7afb4167d4513
ACR-3014d25bb03f43be823ae3910dfb0eb4
ACR-6b649d08aae94063a0eded203c657bc9
ACR-91951f6c787343ceb19ed7c7e1db3905
ACR-7c630c04062443a4bd2381c38f7f818c
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

