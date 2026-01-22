/*
ACR-7d53e9078f5842cfaa0010b6cafbae49
ACR-d6840c3351584576abdc560fce5e6a53
ACR-be828a2cbc014cd8a55893ec2a37bf4a
ACR-923ad1ce891d4fd2bfd45c4c33ff81c0
ACR-0ce0e7c50f1f4a20a484e3f8853df9a3
ACR-099003c003bf4e27992c5245cb389402
ACR-f73f41c0c1784b5aa7e92d8de47ac891
ACR-e69a1e68670d4430a07f87d98e641ad3
ACR-0bfcca20595f461b947ef8feb720e150
ACR-e92100473b744d53b0bc0e316557280d
ACR-eb2d1af28d634032a0ddf6e4fbb10b85
ACR-cc92d9cc7f4846579f6e1a0894d6a382
ACR-a371ef03c9f647e3b8ca09ee19ab7fea
ACR-90933816bd41405c81ac5a444d55068e
ACR-07f0ff2aae4b4f5e98212d3dedfb7b5c
ACR-b6dd7b3e027a4f0b9f5302bff212029a
ACR-f7a74484a9fd435d9251f4b5d9bf746e
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
