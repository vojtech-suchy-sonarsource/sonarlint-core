/*
ACR-7bbd27b099f34bcb8df4ebd42e38b123
ACR-51a10419466840dab7d35104ef0d7b0d
ACR-eac7bc78c1f647c0a8a4afdaeb53ea5e
ACR-b886e9828d214eca82b55d548c4c48f1
ACR-1cac2e7b044948228fbc3216d2c37fab
ACR-baff518180f24c3fadaa8f4a6f5d62a6
ACR-588cfb1342ae4678bf3cc4a19dac2195
ACR-a821473716d646c69c07fac4eb9176f4
ACR-7708c17c6cda4c0f9333af5437cc9f19
ACR-830b4aea822d49f8be6681c7b9c922fa
ACR-d05cf4e6e987485fb5a60652830de999
ACR-b6b8d4194c66443891b216532056e810
ACR-7b0f577dcd1547e09c2779dbf369a8fd
ACR-ee23a7e21ce74bbbb60f2f48b24d2b2c
ACR-bfb38008c18a43d186618be85c218a85
ACR-8e3262e78ec04133838d909d1168573f
ACR-bbafcdb94af94f78bb9362882a87d475
 */
package org.sonarsource.sonarlint.core.fs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.net.URI;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mockito;
import org.sonarsource.sonarlint.core.commons.Binding;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.analysis.api.TriggerType;
import org.sonarsource.sonarlint.core.file.PathTranslationService;
import org.sonarsource.sonarlint.core.repository.config.ConfigurationRepository;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcClient;
import org.sonarsource.sonarlint.core.rpc.protocol.client.analysis.GetFileExclusionsParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.analysis.GetFileExclusionsResponse;
import org.sonarsource.sonarlint.core.serverconnection.AnalyzerConfigurationStorage;
import org.sonarsource.sonarlint.core.serverconnection.ConnectionStorage;
import org.sonarsource.sonarlint.core.serverconnection.SonarProjectStorage;
import org.sonarsource.sonarlint.core.storage.StorageService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;

class FileExclusionServiceTests {

  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();

  private ConfigurationRepository configRepo;
  private StorageService storageService;
  private ClientFileSystemService clientFileSystemService;
  private FileExclusionService underTest;
  private SonarLintRpcClient client;

  @BeforeEach
  void setup() {
    configRepo = mock(ConfigurationRepository.class);
    storageService = mock(StorageService.class);
    var pathTranslationService = mock(PathTranslationService.class);
    clientFileSystemService = mock(ClientFileSystemService.class);
    client = mock(SonarLintRpcClient.class);
    
    underTest = new FileExclusionService(configRepo, storageService, pathTranslationService, clientFileSystemService, client);
  }

  @Test
  void should_return_false_and_log_warning_when_analyzer_storage_is_not_valid() {
    var fileUri = URI.create("file:///path/to/file.java");
    var configScopeId = "configScope1";
    var connectionId = "connectionId";
    var projectKey = "projectKey";
    var clientFile = mock(ClientFile.class);

    when(clientFile.getConfigScopeId()).thenReturn(configScopeId);
    var binding = new Binding(connectionId, projectKey);

    var connectionStorage = mock(ConnectionStorage.class);
    var projectStorage = mock(SonarProjectStorage.class);
    var analyzerStorage = mock(AnalyzerConfigurationStorage.class);
    
    //ACR-76305c7577ed41638fe95d1ea6a18b40
    when(clientFileSystemService.getClientFile(fileUri)).thenReturn(clientFile);
    when(configRepo.getEffectiveBinding(configScopeId)).thenReturn(Optional.of(binding));
    when(storageService.connection(connectionId)).thenReturn(connectionStorage);
    when(connectionStorage.project(projectKey)).thenReturn(projectStorage);
    when(projectStorage.analyzerConfiguration()).thenReturn(analyzerStorage);
    when(analyzerStorage.isValid()).thenReturn(false); //ACR-7e0daa4ee61e453b8da01d19073897f0
    var cancelMonitor = mock(SonarLintCancelMonitor.class);

    var result = underTest.computeIfExcluded(fileUri, cancelMonitor);

    assertThat(result).isFalse();
    assertThat(logTester.logs()).contains("Unable to read settings in local storage, analysis storage is not ready");
  }

  @Test
  void should_return_false_when_no_client_file_found() {
    var fileUri = URI.create("file:///path/to/nonexistent.java");
    var cancelMonitor = mock(SonarLintCancelMonitor.class);
    when(clientFileSystemService.getClientFile(fileUri)).thenReturn(null);

    var result = underTest.computeIfExcluded(fileUri, cancelMonitor);

    assertThat(result).isFalse();
    assertThat(logTester.logs()).contains("Unable to find client file for uri file:///path/to/nonexistent.java");
  }

  @Test
  void should_return_false_when_no_effective_binding() {
    var fileUri = URI.create("file:///path/to/file.java");
    var configScopeId = "configScope1";
    var cancelMonitor = mock(SonarLintCancelMonitor.class);
    var clientFile = mock(ClientFile.class);
    when(clientFile.getConfigScopeId()).thenReturn(configScopeId);
    when(clientFileSystemService.getClientFile(fileUri)).thenReturn(clientFile);
    when(configRepo.getEffectiveBinding(configScopeId)).thenReturn(Optional.empty());

    var result = underTest.computeIfExcluded(fileUri, cancelMonitor);

    assertThat(result).isFalse();
  }

  @Test
  void should_filter_out_files_exceeding_5mb_in_auto_trigger() throws IOException {
    var configScopeId = "scope";
    var baseDir = Files.createTempDirectory("sl-auto-size-base");

    //ACR-9633d9a3fce24db397dcf5d25afc4bd0
    var smallFile = baseDir.resolve("small.js");
    var largeFile = baseDir.resolve("large.js");
    Files.write(smallFile, new byte[10 * 1024]);
    Files.write(largeFile, new byte[6 * 1024 * 1024]);

    var smallUri = smallFile.toUri();
    var largeUri = largeFile.toUri();

    var smallClientFile = mock(ClientFile.class);
    when(smallClientFile.getUri()).thenReturn(smallUri);
    when(smallClientFile.getClientRelativePath()).thenReturn(Paths.get("small.js"));
    when(smallClientFile.isUserDefined()).thenReturn(true);

    var largeClientFile = mock(ClientFile.class);
    when(largeClientFile.getUri()).thenReturn(largeUri);
    when(largeClientFile.getClientRelativePath()).thenReturn(Paths.get("large.js"));
    when(largeClientFile.isUserDefined()).thenReturn(true);

    when(clientFileSystemService.getClientFiles(configScopeId, smallUri)).thenReturn(smallClientFile);
    when(clientFileSystemService.getClientFiles(configScopeId, largeUri)).thenReturn(largeClientFile);
    when(client.getFileExclusions(any(GetFileExclusionsParams.class))).thenReturn(CompletableFuture.completedFuture(new GetFileExclusionsResponse(Collections.emptySet())));
    when(smallClientFile.isLargerThan(anyLong())).thenReturn(false);
    when(largeClientFile.isLargerThan(anyLong())).thenReturn(true);

    //ACR-c652f47d85604265a42369e0b706e353
    var spy = Mockito.spy(underTest);
    Mockito.doReturn(false).when(spy).isExcludedFromServer(any(URI.class));

    var result = spy.refineAnalysisScope(configScopeId, Set.of(smallUri, largeUri), TriggerType.AUTO, baseDir);

    assertThat(result).extracting(ClientFile::getUri).containsExactlyInAnyOrder(smallUri);
    assertThat(logTester.logs()).anySatisfy(s -> assertThat(s).contains("Filtered out URIs exceeding max allowed size"));
  }

  @Test
  void should_not_filter_out_large_files_in_forced_trigger() throws IOException {
    var configScopeId = "scope";
    var baseDir = Files.createTempDirectory("sl-forced-size-base");

    var largeFile = baseDir.resolve("large2.js");
    Files.write(largeFile, new byte[6 * 1024 * 1024]);

    var largeUri = largeFile.toUri();

    var largeClientFile = mock(ClientFile.class);
    when(largeClientFile.getUri()).thenReturn(largeUri);
    when(largeClientFile.getClientRelativePath()).thenReturn(Paths.get("large2.js"));
    when(largeClientFile.isUserDefined()).thenReturn(true);

    when(clientFileSystemService.getClientFiles(configScopeId, largeUri)).thenReturn(largeClientFile);
    when(client.getFileExclusions(any(GetFileExclusionsParams.class))).thenReturn(CompletableFuture.completedFuture(new GetFileExclusionsResponse(Collections.emptySet())));

    var spy = Mockito.spy(underTest);
    Mockito.doReturn(false).when(spy).isExcludedFromServer(any(URI.class));

    var result = spy.refineAnalysisScope(configScopeId, Set.of(largeUri), TriggerType.FORCED, baseDir);

    assertThat(result).extracting(ClientFile::getUri).containsExactly(largeUri);
  }

}
