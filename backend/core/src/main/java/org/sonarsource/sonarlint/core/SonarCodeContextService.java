/*
ACR-a35973dafc07423aa21f1cbaf39d1362
ACR-620999149b0d4841bc13346d648549e6
ACR-d82b773081d342cb84537fdf6ad85032
ACR-b32a619acf5d4ababda7372ee93d9d4b
ACR-3d91a39d120f431991d1b2a55a6f8834
ACR-9f626ce2de364437b1bee9de08063625
ACR-eb92a8374c934acca03c885e31834244
ACR-0b57ab50386f46faabaa8a1511aacff3
ACR-f7bc7fd96bad459fa5e376ab9d2df50d
ACR-530db7e7e7574263bedd82359d18c9f4
ACR-0d204d687df24e5eab09d2ba3db994b7
ACR-ea8b139a6bf24bd6aef6f832cb9c81ae
ACR-c48bd22aa5a449d1950c801bb1a7db84
ACR-1ed5275e518c41629aca357e2f57bac9
ACR-fab8964f28614295938b9413ff252af6
ACR-19f39034354b47c785ccab8afe63b44a
ACR-cab3f199a0c94335bec01de2118bb400
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

/*ACR-620bfdc9cebd4900b59f47b3d28f3eee
ACR-65b618e7c0934ad281a765872f1926b9
ACR-28edf4f2bf0848c39e8594d7e4d4f6ec
ACR-85465d32c9bd4a01b234c65ea6f2b9d0
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
      //ACR-b8f0ea49b7cc4867b34a7e355854a883
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
      //ACR-f98baf085e2b47b6b4ddc008f05aebbb
    }
    return baseDir;
  }

  private static String resolveCliExecutable() {
    //ACR-136f9487575f4ff28bc97f5083e5cafc
    var prop = System.getProperty("sonar.code.context.executable");
    if (prop != null && !prop.isBlank()) {
      return prop;
    }
    return CLI_EXECUTABLE;
  }

}
