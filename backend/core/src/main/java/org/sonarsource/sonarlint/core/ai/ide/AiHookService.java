/*
ACR-5efcbe4ff4034ff48fd3518a39312218
ACR-c8b53245b0f54a27bc322dd456ef21ad
ACR-2a2a9188bf134476a3b6318c0588cbcb
ACR-f15c9dc52901488bb57effa4af00369f
ACR-83450148230b45b4b05a828e92ed28d9
ACR-ce1ee5a716984bf09466afdf612610c8
ACR-d0177fb0fc4c49129b1da7742a8dc05f
ACR-6cb2620a14474a8b8862d17994b0abd7
ACR-d0d5488351ee4e2f836b5921ef608bdf
ACR-3954c04a53e54b4399004bc160694ac1
ACR-dc6fc3b4d0e6458d98dfc2794c63b87d
ACR-663709f46be5471b8179fc0b1f15dc95
ACR-f17d77591b45413f8e52760ece91861e
ACR-600a82c98b744476b78f57bf99e4e63d
ACR-23bfde139f624b1c9d8e64ad73877ae3
ACR-6906205105454c59bac7c0848d53941d
ACR-4308b417329e4fe7a40dc5649eeb6052
 */
package org.sonarsource.sonarlint.core.ai.ide;

import jakarta.inject.Inject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.embedded.server.EmbeddedServer;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.ai.AiAgent;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.ai.GetHookScriptContentResponse;
import org.sonarsource.sonarlint.core.telemetry.TelemetryService;

public class AiHookService {

  private static final SonarLintLogger LOG = SonarLintLogger.get();

  private static final String WINDSURF_HOOK_CONFIG = """
      {
        "hooks": {
          "post_write_code": [
            {
              "command": "{{SCRIPT_PATH}}",
              "show_output": true
            }
          ]
        }
      }
      """;

  private final EmbeddedServer embeddedServer;
  private final ExecutableLocator executableLocator;
  private final TelemetryService telemetryService;

  @Inject
  public AiHookService(EmbeddedServer embeddedServer, TelemetryService telemetryService) {
    this(embeddedServer, telemetryService, new ExecutableLocator());
  }

  //ACR-83b2f84892a5419e9b99e5a3f8e53dca
  AiHookService(EmbeddedServer embeddedServer, TelemetryService telemetryService, ExecutableLocator executableLocator) {
    this.embeddedServer = embeddedServer;
    this.telemetryService = telemetryService;
    this.executableLocator = executableLocator;
  }

  public GetHookScriptContentResponse getHookScriptContent(AiAgent agent) {
    var port = embeddedServer.getPort();
    if (port <= 0) {
      throw new IllegalStateException("Embedded server is not started. Cannot generate hook script.");
    }

    var hookScriptType = executableLocator.detectBestExecutable()
      .orElseThrow(() -> new IllegalStateException("No suitable executable found for hook script generation. " +
        "Please ensure Node.js, Python, or Bash is available on your system."));

    var scriptContent = loadTemplateAndReplacePlaceholders(hookScriptType.getFileName(), port, agent);
    var configContent = generateHookConfiguration(agent);
    var configFileName = getConfigFileName(agent);

    telemetryService.aiHookInstalled(agent);

    return new GetHookScriptContentResponse(scriptContent, hookScriptType.getFileName(), configContent, configFileName);
  }

  private static String generateHookConfiguration(AiAgent agent) {
    return switch (agent) {
      case WINDSURF -> WINDSURF_HOOK_CONFIG;
      case CURSOR, KIRO -> throw new UnsupportedOperationException(agent + " hook configuration not yet implemented");
      case GITHUB_COPILOT -> throw new UnsupportedOperationException("GitHub Copilot does not support hooks");
    };
  }

  private static String getConfigFileName(AiAgent agent) {
    return switch (agent) {
      case WINDSURF -> "hooks.json";
      case CURSOR, KIRO -> throw new UnsupportedOperationException(agent + " hook configuration not yet implemented");
      case GITHUB_COPILOT -> throw new UnsupportedOperationException("GitHub Copilot does not support hooks");
    };
  }

  private static String loadTemplateAndReplacePlaceholders(String templateFileName, int port, AiAgent agent) {
    var resourcePath = "/ai/hooks/" + templateFileName;
    try (var inputStream = AiHookService.class.getResourceAsStream(resourcePath)) {
      if (inputStream == null) {
        throw new IllegalStateException("Hook script template not found: " + resourcePath);
      }
      var template = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
      return template
        .replace("{{PORT}}", String.valueOf(port))
        .replace("{{AGENT}}", getIdeName(agent));
    } catch (IOException e) {
      LOG.error("Failed to load hook script template: {}", templateFileName, e);
      throw new IllegalStateException("Failed to load hook script template: " + templateFileName, e);
    }
  }

  private static String getIdeName(AiAgent agent) {
    return switch (agent) {
      case WINDSURF -> "Windsurf";
      case CURSOR -> "Cursor";
      case KIRO -> "Kiro";
      case GITHUB_COPILOT -> throw new UnsupportedOperationException("GitHub Copilot does not support hooks");
    };
  }

}

