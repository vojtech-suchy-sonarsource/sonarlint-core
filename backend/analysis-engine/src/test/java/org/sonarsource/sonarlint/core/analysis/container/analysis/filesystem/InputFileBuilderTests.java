/*
ACR-a2f48f9c518b46b99707a1c19bb11ebc
ACR-151a4cf6a9ba4ce683a9508b830fe5da
ACR-e3eab818dd5a40e59a6628b32c0b49e8
ACR-edd6009520ab4840900c8b74422c279b
ACR-65d819a9cb614f609f7572f77a3cec8c
ACR-7fc9018913fb4fb2b04b97d735af3860
ACR-7f671cd39f8b42ad95d4913a5ab0ff0b
ACR-84f3edd049f04c3083b60d53d5f625e0
ACR-07e8f0341f5a449aa0ea2e78f9269508
ACR-9c564c1eaf2d424c96cebc183e56677b
ACR-3ffa7fa0e1a94956a33f17dddee1a8a8
ACR-834b2afe9c1c4996a85d09e5b6efd2d4
ACR-31e91108ae834ed593f081ea7830f431
ACR-483f3eb5a5a24d60b81016cb74275c2c
ACR-37700480139149cbaac36afe4490b735
ACR-b75791231b334e6b924c57b7c9bbb389
ACR-e1e1e24daa7c477da543ea8a30ec74ae
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem;

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
import org.sonarsource.sonarlint.core.analysis.container.analysis.issue.ignore.scanner.IssueExclusionsLoader;
import org.sonarsource.sonarlint.core.commons.api.SonarLanguage;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import testutils.FileUtils;
import testutils.OnDiskTestClientInputFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class InputFileBuilderTests {
  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();

  private final LanguageDetection langDetection = mock(LanguageDetection.class);
  private final IssueExclusionsLoader issueExclusionsLoader = mock(IssueExclusionsLoader.class);
  private final FileMetadata metadata = new FileMetadata();

  @TempDir
  private Path tempDir;

  @Test
  void testCreate() throws IOException {
    when(langDetection.language(any(InputFile.class))).thenReturn(SonarLanguage.JAVA);

    var path = tempDir.resolve("file");
    Files.write(path, "test".getBytes(StandardCharsets.ISO_8859_1));
    ClientInputFile file = new OnDiskTestClientInputFile(path, "file", true, StandardCharsets.ISO_8859_1);

    var builder = new InputFileBuilder(langDetection, metadata, issueExclusionsLoader);
    var inputFile = builder.create(file);

    assertThat(inputFile.type()).isEqualTo(InputFile.Type.TEST);
    assertThat(inputFile.file()).isEqualTo(path.toFile());
    assertThat(inputFile.absolutePath()).isEqualTo(FileUtils.toSonarQubePath(path.toString()));
    assertThat(inputFile.language()).isEqualTo("java");
    assertThat(inputFile.key()).isEqualTo(path.toUri().toString());
    assertThat(inputFile.lines()).isEqualTo(1);

    verify(issueExclusionsLoader).createCharHandlerFor(inputFile);
  }

  @Test
  void testCreateWithLanguageSet() throws IOException {
    var path = tempDir.resolve("file");
    Files.write(path, "test".getBytes(StandardCharsets.ISO_8859_1));
    ClientInputFile file = new OnDiskTestClientInputFile(path, "file", true, StandardCharsets.ISO_8859_1, SonarLanguage.CPP);

    var builder = new InputFileBuilder(langDetection, metadata, issueExclusionsLoader);
    var inputFile = builder.create(file);

    assertThat(inputFile.language()).isEqualTo("cpp");
    verifyNoInteractions(langDetection);
  }

  @Test
  void testCreate_lazy_error() throws IOException {
    when(langDetection.language(any(InputFile.class))).thenReturn(SonarLanguage.JAVA);
    ClientInputFile file = new OnDiskTestClientInputFile(Paths.get("INVALID"), "INVALID", true, StandardCharsets.ISO_8859_1);

    var builder = new InputFileBuilder(langDetection, metadata, issueExclusionsLoader);
    var slFile = builder.create(file);

    //ACR-ab6517f83ef741d19c909032bdd3d1f2
    var thrown = assertThrows(IllegalStateException.class, () -> slFile.selectLine(1));
    assertThat(thrown).hasMessageStartingWith("Failed to open a stream on file");
  }
}
