/*
ACR-64456ced6c944d06bbac7871ca4d59df
ACR-3fbbbc95d5f34d37ae5cf5d535833dbd
ACR-9a46a54b29bb471b864c59a6971575c6
ACR-b0e304c39439447ca9c189e119f1686b
ACR-d89898d98f2e4bfe9ee41f982f838363
ACR-40d9cdfcd94a4bd1b8c715a65d7c1f25
ACR-f7dee6e6cf59487ba19981cf12002e16
ACR-c4155fccb26c46d9a015707708b89cf3
ACR-50cd51f2c11648218f73b1770715cd0f
ACR-109e8479c3c9436ca2204fe03b00780c
ACR-f91db864c2e74e6bafb2e541314c83dd
ACR-ebb5a07886244491abd9fd8a5beb90af
ACR-4a2dce9e4c934102b16bb113cda1f3fe
ACR-d0f231ba1fdc48a8bb3486e06fe5ca94
ACR-f3205f4ceda241f89a93ad57df687ea7
ACR-6579eb1679ce40008356ffc1dd1da029
ACR-7f8a1ee7c210419899ebd8b18a169003
 */
package org.sonarsource.sonarlint.core;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.branch.SonarProjectBranchTrackingService;
import org.sonarsource.sonarlint.core.commons.Binding;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.commons.dogfood.DogfoodEnvironmentDetectionService;
import org.sonarsource.sonarlint.core.commons.util.git.ProcessWrapperFactory;
import org.sonarsource.sonarlint.core.event.ConfigurationScopesAddedWithBindingEvent;
import org.sonarsource.sonarlint.core.event.BindingConfigChangedEvent;
import org.sonarsource.sonarlint.core.fs.ClientFileSystemService;
import org.sonarsource.sonarlint.core.repository.config.ConfigurationRepository;
import org.sonarsource.sonarlint.core.repository.connection.ConnectionConfigurationRepository;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcClient;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.BackendCapability;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.InitializeParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.connection.GetCredentialsParams;
import org.springframework.context.event.EventListener;

/*ACR-de6751250e2c484ba275cd47fb9bedf3
ACR-e25f6eb4931443c18e10ae1043be5570
ACR-fff6a2349a874041b1be62bb6fc88671
ACR-2dfdba3bf04646cfa4ce2b7956f5c2e3
 */
public class SonarCodeContextService {

  private static final SonarLintLogger LOG = SonarLintLogger.get();
  private static final String SONAR_CODE_CONTEXT_DIR = ".sonar-code-context";
  private static final String CLI_EXECUTABLE = "sonar-code-context";
  private static final String SONAR_MD_FILENAME = "SONAR.md";
  private static final String CURSOR_MDC_FILENAME = "sonar-code-context.mdc";

  private final ClientFileSystemService clientFileSystemService;
  private final ConfigurationRepository configurationRepository;
  private final ConnectionConfigurationRepository connectionConfigurationRepository;
  private final SonarProjectBranchTrackingService branchTrackingService;
  private final SonarLintRpcClient client;
  private final ProcessWrapperFactory processWrapperFactory = new ProcessWrapperFactory();
  private final boolean isEnabled;

  private final Set<String> initializedScopes = new HashSet<>();
  private final Set<String> mdcInstalledScopes = new HashSet<>();

  public SonarCodeContextService(DogfoodEnvironmentDetectionService dogfoodEnvDetectionService,
    ClientFileSystemService clientFileSystemService,
    ConfigurationRepository configurationRepository,
    ConnectionConfigurationRepository connectionConfigurationRepository,
    SonarProjectBranchTrackingService branchTrackingService,
    SonarLintRpcClient client, InitializeParams params) {
    this.clientFileSystemService = clientFileSystemService;
    this.configurationRepository = configurationRepository;
    this.connectionConfigurationRepository = connectionConfigurationRepository;
    this.branchTrackingService = branchTrackingService;
    this.client = client;
    this.isEnabled = dogfoodEnvDetectionService.isDogfoodEnvironment()
      && params.getBackendCapabilities().contains(BackendCapability.CONTEXT_GENERATION);
  }

  @EventListener
  public void onConfigurationScopesAdded(ConfigurationScopesAddedWithBindingEvent event) {
    if (!isEnabled) {
      return;
    }

    for (var configScopeId : event.getConfigScopeIds()) {
      var baseDir = clientFileSystemService.getBaseDir(configScopeId);
      //ACR-3aa0382567474197ab13a15c5a655ba5
      var bindingOpt = configurationRepository.getConfiguredBinding(configScopeId);
      if (baseDir != null && bindingOpt.isPresent()) {
        handleGeneration(configScopeId, baseDir, bindingOpt.get());
      } else {
        LOG.debug("No baseDir for configuration scope '{}' - skipping SonarCodeContext CLI", configScopeId);
      }
    }
  }

  @EventListener
  public void onBindingChanged(BindingConfigChangedEvent event) {
    if (!isEnabled) {
      return;
    }

    var configScopeId = event.configScopeId();
    var baseDir = clientFileSystemService.getBaseDir(configScopeId);
    var bindingOpt = configurationRepository.getConfiguredBinding(configScopeId);
    if (baseDir != null && bindingOpt.isPresent()) {
      handleGeneration(configScopeId, baseDir, bindingOpt.get());
    }
  }

  private void handleGeneration(String configScopeId, Path baseDir, Binding binding) {
    try {
      var paramsOpt = prepareCliParams(binding, configScopeId);
      if (paramsOpt.isPresent()) {
        var workingDir = computeWorkingBaseDir(baseDir);
        if (initializedScopes.add(configScopeId)) {
          runInit(workingDir);
        }
        runGenerateGuidelines(workingDir, paramsOpt.get());
        runMergeMd(workingDir);
        if (mdcInstalledScopes.add(configScopeId)) {
          runInstall(workingDir);
        }
      } else {
        LOG.debug("Missing parameters for SonarCodeContext CLI, skipping for configuration scope '{}'", configScopeId);
      }
    } catch (Exception e) {
      LOG.debug("[DOGFOOD] Failed to run code context CLI", e.getMessage());
    }
  }

  private Optional<CliParams> prepareCliParams(Binding binding, String configScopeId) {
    var connection = connectionConfigurationRepository.getConnectionById(binding.connectionId());
    if (connection == null) {
      return Optional.empty();
    }
    var url = connection.getUrl();
    var token = getTokenForConnection(binding.connectionId());
    if (token.isEmpty()) {
      return Optional.empty();
    }
    var branch = branchTrackingService.awaitEffectiveSonarProjectBranch(configScopeId).orElse(null);
    return Optional.of(new CliParams(url, token.get(), binding.sonarProjectKey(), branch));
  }

  private Optional<String> getTokenForConnection(String connectionId) {
    try {
      var creds = client.getCredentials(new GetCredentialsParams(connectionId)).join().getCredentials();
      if (creds != null && creds.isLeft()) {
        var tokenDto = creds.getLeft();
        return Optional.ofNullable(tokenDto.getToken());
      }
      return Optional.empty();
    } catch (Exception e) {
      LOG.debug("Unable to retrieve token for connection '{}'", connectionId, e);
      return Optional.empty();
    }
  }

  private void runInit(Path baseDir) {
    var command = new ArrayList<>(List.of(resolveCliExecutable(), "init"));
    execute(baseDir, command);
    var settings = baseDir.resolve(SONAR_CODE_CONTEXT_DIR).resolve("settings.json");
    if (Files.exists(settings)) {
      LOG.debug("Initialized SonarCodeContext settings at {}", settings);
    }
  }

  private void runGenerateGuidelines(Path baseDir, CliParams params) {
    var command = new ArrayList<>(List.of(
      resolveCliExecutable(),
      "generate-md-guidelines",
      "--sq-url=" + params.sqUrl,
      "--sq-token=" + params.sqToken,
      "--sq-project-key=" + params.projectKey
    ));
    if (params.sqBranch() != null) {
      command.add("--sq-branch=" + params.sqBranch());
    }
    execute(baseDir, command);
  }

  private void runMergeMd(Path baseDir) {
    var command = new ArrayList<>(List.of(resolveCliExecutable(), "merge-md"));
    execute(baseDir, command);
    var merged = baseDir.resolve(SONAR_CODE_CONTEXT_DIR).resolve(SONAR_MD_FILENAME);
    if (Files.exists(merged)) {
      LOG.debug("Merged {} at {}", SONAR_MD_FILENAME, merged);
    } else {
      LOG.debug("{} was not generated under {}", SONAR_MD_FILENAME, baseDir.resolve(SONAR_CODE_CONTEXT_DIR));
    }
  }

  private void runInstall(Path baseDir) {
    var command = new ArrayList<>(List.of(resolveCliExecutable(), "install", "--force", "--cursor-mdc"));
    execute(baseDir, command);
    var cursorRule = baseDir.resolve(".cursor").resolve("rules").resolve(CURSOR_MDC_FILENAME);
    if (Files.exists(cursorRule)) {
      LOG.debug("Generated {} at {}", CURSOR_MDC_FILENAME, cursorRule);
    }
  }

  private void execute(Path baseDir, List<String> command) {
    var result = processWrapperFactory.create(baseDir, LOG::debug, command.toArray(new String[0])).execute();
    if (result.exitCode() != 0) {
      LOG.debug("Command '{}' exited with code {} in {}", String.join(" ", command), result.exitCode(), baseDir);
    }
  }

  private record CliParams(String sqUrl, String sqToken, String projectKey, @Nullable String sqBranch) {}

  private static Path computeWorkingBaseDir(Path baseDir) {
    try {
      var current = baseDir;
      while (current != null) {
        if (Files.isDirectory(current.resolve(".git"))) {
          return current;
        }
        current = current.getParent();
      }
    } catch (Exception e) {
      //ACR-8242a521c15a4bbd931ce40eeea4360c
    }
    return baseDir;
  }

  private static String resolveCliExecutable() {
    //ACR-1716dec9e3ae47aca66f9b5fb6262949
    var prop = System.getProperty("sonar.code.context.executable");
    if (prop != null && !prop.isBlank()) {
      return prop;
    }
    return CLI_EXECUTABLE;
  }

}
