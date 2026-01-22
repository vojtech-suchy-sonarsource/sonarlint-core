/*
ACR-a29e4514ab094c37b98223a762fcd145
ACR-824fafb8c7d64e878a510cfec5630542
ACR-8e5a225c0ba84c758b825e73576257b5
ACR-1c7938e740b94506a11e5c12c3b295d3
ACR-ec43c405d255453cb6a811d415a677d3
ACR-626c58775f054413aead314fe89a1973
ACR-2e8b8e47b60d4cb8aa265d95f6e058fa
ACR-043b33e248f74df49aca4c874477de1c
ACR-0e1b17b41dec4e6c921219fadd335ce2
ACR-f871dae2f06443168359bce039d81f4f
ACR-10b107b8bf894221b0b3e73756a7a183
ACR-4babcc45fb0b4c96899285efae1c5004
ACR-614092327ca9401c909a5a45bb1d3192
ACR-6a0c337a62754db9b136df12effce0ea
ACR-468b8fde9ca24334932b6624b0f390d3
ACR-735a147a3fce44c7853512fc982ff08a
ACR-bdefe7c27587443281266aa66f232d9d
 */
package org.sonarsource.sonarlint.core.analysis.container.module;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.io.TempDir;
import org.sonar.api.batch.fs.InputFile;
import org.sonarsource.sonarlint.core.analysis.api.ClientInputFile;
import org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem.FileMetadata;
import org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem.LanguageDetection;
import org.sonarsource.sonarlint.core.commons.api.SonarLanguage;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import testutils.FileUtils;
import testutils.OnDiskTestClientInputFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class ModuleInputFileBuilderTests {
  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();

  private final LanguageDetection langDetection = mock(LanguageDetection.class);
  private final FileMetadata metadata = new FileMetadata();

  @TempDir
  private Path tempDir;

  @Test
  void testCreate() throws IOException {
    when(langDetection.language(any(InputFile.class))).thenReturn(SonarLanguage.JAVA);

    var path = tempDir.resolve("file");
    Files.write(path, "test".getBytes(StandardCharsets.ISO_8859_1));
    ClientInputFile file = new OnDiskTestClientInputFile(path, "file", true, StandardCharsets.ISO_8859_1);

    var builder = new ModuleInputFileBuilder(langDetection, metadata);
    var inputFile = builder.create(file);

    assertThat(inputFile.type()).isEqualTo(InputFile.Type.TEST);
    assertThat(inputFile.file()).isEqualTo(path.toFile());
    assertThat(inputFile.absolutePath()).isEqualTo(FileUtils.toSonarQubePath(path.toString()));
    assertThat(inputFile.language()).isEqualTo("java");
    assertThat(inputFile.key()).isEqualTo(path.toUri().toString());
    assertThat(inputFile.lines()).isEqualTo(1);
  }

  @Test
  void testCreateWithLanguageSet() throws IOException {
    var path = tempDir.resolve("file");
    Files.write(path, "test".getBytes(StandardCharsets.ISO_8859_1));
    ClientInputFile file = new OnDiskTestClientInputFile(path, "file", true, StandardCharsets.ISO_8859_1, SonarLanguage.CPP);

    var builder = new ModuleInputFileBuilder(langDetection, metadata);
    var inputFile = builder.create(file);

    assertThat(inputFile.language()).isEqualTo("cpp");
    verifyNoInteractions(langDetection);
  }

  @Test
  void testCreate_lazy_error() throws IOException {
    when(langDetection.language(any(InputFile.class))).thenReturn(SonarLanguage.JAVA);
    ClientInputFile file = new OnDiskTestClientInputFile(Paths.get("INVALID"), "INVALID", true, StandardCharsets.ISO_8859_1);

    var builder = new ModuleInputFileBuilder(langDetection, metadata);
    var slFile = builder.create(file);

    //ACR-de1b796c74f54fef8048c110448ed08d
    var thrown = assertThrows(IllegalStateException.class, () -> slFile.selectLine(1));
    assertThat(thrown).hasMessageStartingWith("Failed to open a stream on file");

  }
}
