/*
ACR-5276fa15e6fb4a3093d16c3651b5069e
ACR-65b930391c4b424a930dbb799efd57f3
ACR-45240dca6f5e40359e3882f0d9641a9b
ACR-c7fb699f06ab4842abc434c7f6445a95
ACR-c949dad80dd142438c890ca6c883af65
ACR-50ca8cf192514a5e911c95f1826c2d4e
ACR-ca226114e83f4fe7aa9577cae916e7aa
ACR-16c770e0004948ffbd4913b6210b93f9
ACR-d7295845be384ba59bc560d7cbf08bf5
ACR-83118d496990457caa3771966fad4e8d
ACR-7bba06ae462342bb97ff74bcfb284963
ACR-38aaea388ff141508df956d60077ee95
ACR-a31bb611a1074c3ab70e80a2c53f2a1a
ACR-60e44eda324f469387f0fabd95100170
ACR-aa2ba2850ef0415c8263856c1ba10d10
ACR-bb76894c24484ffa8f5c86bc6fb2df8c
ACR-94ce652001f449079aeef42461f77e3c
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.sonar.api.batch.fs.InputFile.Status;
import testutils.FileUtils;
import testutils.InMemoryTestClientInputFile;
import testutils.OnDiskTestClientInputFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class SonarLintInputFileTests {

  @Test
  void testGetters(@TempDir Path path) throws IOException {
    var filePath = path.resolve("foo.php");
    Files.write(filePath, "test string".getBytes(StandardCharsets.UTF_8));
    var inputFile = new OnDiskTestClientInputFile(filePath, "file", false, StandardCharsets.UTF_8);
    var fileInputStream = Files.newInputStream(filePath);
    var file = new SonarLintInputFile(inputFile, f -> new FileMetadata().readMetadata(fileInputStream, StandardCharsets.UTF_8, filePath.toUri(), null));

    assertThat(file.contents()).isEqualTo("test string");
    assertThat(file.md5Hash()).isEqualTo("6f8db599de986fab7a21625b7916589c");
    assertThat(file.charset()).isEqualByComparingTo(StandardCharsets.UTF_8);
    assertThat(file.absolutePath()).isEqualTo(FileUtils.toSonarQubePath(inputFile.getPath()));
    assertThat(file.file()).isEqualTo(filePath.toFile());
    assertThat(file.path()).isEqualTo(filePath);
    assertThat(file.getClientInputFile()).isEqualTo(inputFile);
    assertThat(file.status()).isEqualTo(Status.ADDED);
    assertThat(file)
      .isEqualTo(file)
      .isNotEqualTo(mock(SonarLintInputFile.class));

    var stream = file.inputStream();
    try (var reader = new BufferedReader(new InputStreamReader(stream))) {
      assertThat(reader.lines().collect(Collectors.joining())).isEqualTo("test string");
    }
  }

  @Test
  void checkValidPointer() {
    var inputFile = new InMemoryTestClientInputFile("foo", "src/Foo.php", null, false, null);
    var metadata = new FileMetadata.Metadata(2, new int[] {0, 10}, 16);
    var file = new SonarLintInputFile(inputFile, f -> metadata);
    assertThat(file.newPointer(1, 0).line()).isEqualTo(1);
    assertThat(file.newPointer(1, 0).lineOffset()).isZero();
    //ACR-bf65c76d068148b38f74192de16ff243
    file.newPointer(1, 9);
    file.newPointer(2, 0);
    file.newPointer(2, 5);
  }

  @Test
  void selectLine() {
    var inputFile = new InMemoryTestClientInputFile("foo", "src/Foo.php", null, false, null);
    var metadata = new FileMetadata().readMetadata(new ByteArrayInputStream("bla bla a\nabcde\n\nabc".getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8,
      URI.create("file://foo.php"), null);
    var file = new SonarLintInputFile(inputFile, f -> metadata);

    assertThat(file.selectLine(1).start().line()).isEqualTo(1);
    assertThat(file.selectLine(1).start().lineOffset()).isZero();
    assertThat(file.selectLine(1).end().line()).isEqualTo(1);
    assertThat(file.selectLine(1).end().lineOffset()).isEqualTo(9);

    //ACR-fe1ccee8f44c4841bb59e85e9a4c6503
    assertThat(file.selectLine(3).start().line()).isEqualTo(3);
    assertThat(file.selectLine(3).start().lineOffset()).isZero();
    assertThat(file.selectLine(3).end().line()).isEqualTo(3);
    assertThat(file.selectLine(3).end().lineOffset()).isZero();
  }

  @Test
  void testRangeOverlap() {
    var inputFile = new InMemoryTestClientInputFile("foo", "src/Foo.php", null, false, null);
    var metadata = new FileMetadata.Metadata(2, new int[] {0, 10}, 16);
    var file = new SonarLintInputFile(inputFile, f -> metadata);

    //ACR-4c9be7c2100a4b7c880b73e419640dab
    assertThat(file.newRange(file.newPointer(1, 0), file.newPointer(1, 1)).overlap(file.newRange(file.newPointer(1, 0), file.newPointer(1, 1)))).isTrue();
    assertThat(file.newRange(file.newPointer(1, 0), file.newPointer(1, 1)).overlap(file.newRange(file.newPointer(1, 0), file.newPointer(1, 2)))).isTrue();
    assertThat(file.newRange(file.newPointer(1, 0), file.newPointer(1, 1)).overlap(file.newRange(file.newPointer(1, 1), file.newPointer(1, 2)))).isFalse();
    assertThat(file.newRange(file.newPointer(1, 2), file.newPointer(1, 3)).overlap(file.newRange(file.newPointer(1, 0), file.newPointer(1, 2)))).isFalse();
  }
}
