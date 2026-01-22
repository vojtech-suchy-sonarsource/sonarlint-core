/*
ACR-1e6ffbbef1d941c4a760855ed965d870
ACR-5dc18592668146378ef7826e9e356e00
ACR-59738555f89b48d89bacc7706fa10025
ACR-a213d988d3f1461dafbdd86b86f81599
ACR-e9d0914159a9479cb7f9794a84d2fd9a
ACR-000aa622b787432a8129ce5a3f857e08
ACR-5ea682a512994ef1a9f32935e12fe351
ACR-6496a8733b0a467fbad2eb053f03874f
ACR-e177c9a9836c4667aa3c28dc14d28bf2
ACR-cbbf95fec82e4be288c441895485357e
ACR-b55957e53fa94d9dba3a4c0386b0d5e4
ACR-998939ea351e42c4bcbb91f4663f56b3
ACR-47e12853f862483fb0b4390d9c148323
ACR-8337ced56d044b58a79b9329378e95aa
ACR-f959fd3e568146e1a2840cd6de7ae762
ACR-61cd6286a6a54dd4b02ace6a26ea8bd3
ACR-87e4965a853e46b1ab6236949ff59a45
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

    //ACR-916d9a552ee247cca23e2698daa58e2b
    var thrown = assertThrows(IllegalStateException.class, () -> slFile.selectLine(1));
    assertThat(thrown).hasMessageStartingWith("Failed to open a stream on file");
  }
}
