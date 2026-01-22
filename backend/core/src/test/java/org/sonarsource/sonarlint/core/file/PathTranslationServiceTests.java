/*
ACR-a691ed7d9c1e4d14847a5688ef783988
ACR-bb3b1677c97c474e9c18691b8aab3ac7
ACR-2444b184022a43e497867e36fc631ca8
ACR-c737e6f8af084aa5b75ecc27dcb06000
ACR-7a2c0a3c625540c790ff7c4797a061ad
ACR-e899137d9ec24cdfad80589e9cf9ed5a
ACR-d7251df5bad74ce5bb154508bc5ac7ac
ACR-571567bd72424d399ade352c0fde3d40
ACR-49bd37e1163347c19f39e083e1de54f4
ACR-76c945595077488693e497fa1a15eea0
ACR-24a869381e5d483eb3bb6d2ff96ce74e
ACR-2f8240612efc445584208253eac4fc16
ACR-5e4c0d9e5f5a4fefa8f9e36b7ec9d53c
ACR-b2bfcb02abb5401f88ddb60a0fae7ec6
ACR-fe8d21d782834baaa595b5282c3db7a1
ACR-0d4d35de3cb24c84af22c1fe87948a84
ACR-be853122163e4f54bb5629a492663061
 */
package org.sonarsource.sonarlint.core.file;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonarsource.sonarlint.core.commons.Binding;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.event.BindingConfigChangedEvent;
import org.sonarsource.sonarlint.core.fs.ClientFile;
import org.sonarsource.sonarlint.core.fs.ClientFileSystemService;
import org.sonarsource.sonarlint.core.repository.config.ConfigurationRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PathTranslationServiceTests {

  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();
  private static final String CONFIG_SCOPE = "configScopeA";
  private static final Binding BINDING = new Binding("connectionA", "sonarProjectA");
  private final ClientFileSystemService clientFs = mock(ClientFileSystemService.class);
  private final ConfigurationRepository configurationRepository = mock(ConfigurationRepository.class);
  private final ServerFilePathsProvider serverFilePathsProvider = mock(ServerFilePathsProvider.class);
  private final PathTranslationService underTest = new PathTranslationService(clientFs, configurationRepository, serverFilePathsProvider);

  @BeforeEach
  void prepare() {
    when(configurationRepository.getEffectiveBinding(CONFIG_SCOPE)).thenReturn(Optional.of(BINDING));
  }

  @Test
  void shouldComputePathTranslations() {
    mockServerFilePaths(BINDING, "moduleA/src/Foo.java");
    mockClientFilePaths("src/Foo.java");

    var result = underTest.getOrComputePathTranslation(CONFIG_SCOPE);

    assertThat(result).isPresent();
    assertThat(result.get())
      .usingRecursiveComparison()
      .isEqualTo(new FilePathTranslation(Paths.get(""), Paths.get("moduleA")));
  }

  private void mockServerFilePaths(Binding binding, String... paths) {
    when(serverFilePathsProvider.getServerPaths(eq(binding), any(SonarLintCancelMonitor.class)))
      .thenReturn(Optional.of(Arrays.stream(paths).map(Paths::get).toList()));
  }

  @Test
  void shouldCachePathTranslations() {
    mockServerFilePaths(BINDING, "moduleA/src/Foo.java");
    mockClientFilePaths("src/Foo.java");

    var result1 = underTest.getOrComputePathTranslation(CONFIG_SCOPE);

    assertThat(result1).isPresent();
    assertThat(result1.get())
      .usingRecursiveComparison()
      .isEqualTo(new FilePathTranslation(Paths.get(""), Paths.get("moduleA")));

    var result2 = underTest.getOrComputePathTranslation(CONFIG_SCOPE);

    assertThat(result2).isPresent();
    assertThat(result2.get())
      .usingRecursiveComparison()
      .isEqualTo(new FilePathTranslation(Paths.get(""), Paths.get("moduleA")));

    verify(clientFs, times(1)).getFiles(any());
  }

  @Test
  void shouldRecomputePathTranslationsAfterBindingChange() {
    mockServerFilePaths(BINDING, "moduleA/src/Foo.java");
    mockClientFilePaths("src/Foo.java");

    var result1 = underTest.getOrComputePathTranslation(CONFIG_SCOPE);

    assertThat(result1).isPresent();
    assertThat(result1.get())
      .usingRecursiveComparison()
      .isEqualTo(new FilePathTranslation(Paths.get(""), Paths.get("moduleA")));

    Binding newBinding = mock(Binding.class);
    when(configurationRepository.getEffectiveBinding(CONFIG_SCOPE)).thenReturn(Optional.of(newBinding));
    mockServerFilePaths(newBinding, "moduleB/src/Foo.java");

    underTest.onBindingChanged(new BindingConfigChangedEvent(CONFIG_SCOPE, null, null));

    var result2 = underTest.getOrComputePathTranslation(CONFIG_SCOPE);

    assertThat(result2).isPresent();
    assertThat(result2.get())
      .usingRecursiveComparison()
      .isEqualTo(new FilePathTranslation(Paths.get(""), Paths.get("moduleB")));
  }

  private void mockClientFilePaths(String... paths) {
    doReturn(Arrays.stream(paths)
      .map(path -> new ClientFile(null, null, Paths.get(path), null, null, null, null, true))
      .toList())
      .when(clientFs)
      .getFiles(CONFIG_SCOPE);
  }
}
