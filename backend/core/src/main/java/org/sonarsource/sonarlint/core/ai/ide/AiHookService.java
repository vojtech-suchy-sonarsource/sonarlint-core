/*
ACR-95badf0c714449b2bbeb4aa0569f1b3d
ACR-a26d7ae4cbc94cccb6c0237fe8f1aa28
ACR-1383ea32fdfc4df6ae5583096ac48ac0
ACR-0dc125bf617c4b47b00e86884420e190
ACR-df52493ee7ae4727bf12e0b9d3740151
ACR-7c2469abff864fa59e49e7e3528296eb
ACR-e5668a2e821f449cbf50623bb3dbd69f
ACR-66d03c1c69084e8583cb56b062d5fc67
ACR-5a6a6452d2b44bf4a6d2dace4036ee20
ACR-b6c6070f8a424f7fb64f1a51846547fa
ACR-4f2244725d9f4fdba08e9f2b96fd858b
ACR-9d83253c74ad4e17a26e9e50a9c93405
ACR-f683b8d6ce2444a19785360aca88d67f
ACR-7049056199744294baad9fbaf6baf3e0
ACR-76db0d7bc419477aa606c05617880ebb
ACR-b3cc6e2fc7e34dc698f34b0c87d06a73
ACR-70330a5e630044689007fa564168dba8
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

  //ACR-c469b090e0d7479287b45baeadc0f96f
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

