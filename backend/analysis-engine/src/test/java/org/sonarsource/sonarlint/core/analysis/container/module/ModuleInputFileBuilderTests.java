/*
ACR-e3de038b00604439ae22d721bc6ec9c4
ACR-bcd9c5a0f73f4e039d95cceb2c06a7a3
ACR-bdd61a69f194445e9ce70a3a13d5c4ac
ACR-e87e280d84fd486c82293deb0201dbf3
ACR-2c92c130bebd4710aaa997a48e9a0476
ACR-7b6c5ee015274255a03bcbcb5677aa6a
ACR-febf9e4da9ee456aa254f8056290430c
ACR-077be522b3204be1951a3361d4435d40
ACR-eb29d21c65b94b6198192155b88b5cb1
ACR-6dbbb2435ff3452b8809bcc5a4f91ffb
ACR-f3a268c564b14cb0b9fbdfccbcd58521
ACR-c430d76c226744f5973a3bff42115ebd
ACR-666054959da74c5b9281a3ef1d298802
ACR-f04b1d48726e4f1391f9c9e8b4efce2d
ACR-7ed166e6359441ee9c6cec05a01630ff
ACR-413bd4c326bd4bd1aa2ef7c2ad06c608
ACR-d0804d5bb5d740f9b73f6e583cc0ae85
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

    //ACR-c8ce7c3e38ec4f39b18b7c886e836a35
    var thrown = assertThrows(IllegalStateException.class, () -> slFile.selectLine(1));
    assertThat(thrown).hasMessageStartingWith("Failed to open a stream on file");

  }
}
