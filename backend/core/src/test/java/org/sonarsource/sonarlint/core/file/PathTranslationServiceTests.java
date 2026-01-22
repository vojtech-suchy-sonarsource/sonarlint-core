/*
ACR-9b312b51542545f1ae68d5dfea9d21ae
ACR-293c10a6ff6c47d6bc2d11436f816531
ACR-00d5742ee32f45bc9bcac3051fb40795
ACR-d320d5f2e37c4172aa602df0c6ea5228
ACR-0e8f765abb2c476e89800e131218ea65
ACR-af544725449945af829ff215733c0671
ACR-9a5f19e9404843cba51afac83802ebf0
ACR-1857f3cadc1e4a89bdfb477458765519
ACR-697a955887f74c6a92170f16caceeeaf
ACR-555ef78f10d14870896e0e365da7c4cc
ACR-b1a7e44b7ac447daa99e27661761cd85
ACR-9ca8d1443f0d41ca9f2e287043e63909
ACR-1b5ddff1faf84d9ebc378e97f6605e74
ACR-c973a1039ac8401d9010312da3a57362
ACR-cd596a15d7e84d2195083a1c1fe45a05
ACR-eeb838e9d1974b95918e70ee56751358
ACR-32e24d33fe554d778ff041ef288387dc
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
