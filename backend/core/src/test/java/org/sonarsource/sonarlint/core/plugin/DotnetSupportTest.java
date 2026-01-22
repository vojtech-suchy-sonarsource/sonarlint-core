/*
ACR-1a157d231fc5443d81b8e06529e77c51
ACR-4565e88d96d64ed090b390191d9e51ee
ACR-e2a4fd58caab4e308dae47122bffa17a
ACR-9a77bece5d2e4eeea7b6b58186b9a47c
ACR-6d4466fedd3542f380fa8eb3f82af4b3
ACR-169c475802d14cbea40848f42c41fcd2
ACR-b1fee213a6ff438f9b282339950f26a2
ACR-d0edb959d82e4351b9babfbaa9f781f3
ACR-217f5798ca4d4b64ae1f072123f763b1
ACR-43fa5f043fa14732a0030ff54ec8cf01
ACR-252cab15ce1a402db27002a007f6e19c
ACR-24b1fb5f065945679556da21799235a5
ACR-ec0cc598c9a24f42872a06545d7621d0
ACR-16ab1ccd2abe4d3dbe2401f8660a7e47
ACR-2db38484ac0044008c6ef91c644f821a
ACR-d851718f5b134f0db563778afbd9a04b
ACR-27e16ed5abef4200b9067477031a2261
 */
package org.sonarsource.sonarlint.core.plugin;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.InitializeParams;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Language;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DotnetSupportTest {
  private static final Path somePath = Paths.get("folder", "file.txt");
  private InitializeParams initializeParams;

  static Stream<Arguments> provideTestArguments() {
    return Stream.of(
      Arguments.of(Language.CS, null, true, true),
      Arguments.of(Language.VBNET, null, true, true),
      Arguments.of(Language.CS, somePath, true, false),
      Arguments.of(Language.VBNET, somePath, true, false),
      Arguments.of(Language.CS, somePath, false, true),
      Arguments.of(Language.VBNET, somePath, false, true),
      Arguments.of(Language.CS, somePath, false, false),
      Arguments.of(Language.VBNET, somePath, false, false),
      Arguments.of(Language.COBOL, somePath, false, false)
    );
  }

  @BeforeEach
  void prepare() {
    initializeParams = mock(InitializeParams.class);
  }

  @ParameterizedTest
  @MethodSource("provideTestArguments")
  void should_initialize_properties_as_expected(Language language, @Nullable Path csharpAnalyzerPath, boolean shouldUseCsharpEnterprise, boolean shouldUseVbNetEnterprise) {
    mockEnabledLanguages(language);

    var underTest = new DotnetSupport(initializeParams, csharpAnalyzerPath, shouldUseCsharpEnterprise, shouldUseVbNetEnterprise);

    assertThat(underTest.isSupportsCsharp()).isEqualTo(language == Language.CS);
    assertThat(underTest.isSupportsVbNet()).isEqualTo(language == Language.VBNET);
    assertThat(underTest.getActualCsharpAnalyzerPath()).isEqualTo(csharpAnalyzerPath);
    assertThat(underTest.isShouldUseCsharpEnterprise()).isEqualTo(shouldUseCsharpEnterprise);
    assertThat(underTest.isShouldUseVbNetEnterprise()).isEqualTo(shouldUseVbNetEnterprise);
  }

  private void mockEnabledLanguages(Language... languages) {
    when(initializeParams.getEnabledLanguagesInStandaloneMode()).thenReturn(Set.of(languages));
  }
}
