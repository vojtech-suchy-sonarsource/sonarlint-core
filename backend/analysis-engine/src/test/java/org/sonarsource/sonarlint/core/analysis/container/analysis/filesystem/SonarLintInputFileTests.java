/*
ACR-d5e52de602cd4bfc943a3f10bb0b2474
ACR-05b42e53a7eb4cf98732905987fc3dea
ACR-979d5c110d364d33a4d42c12cf58f375
ACR-bb2f6cafbcea4ff68bd73cd935f6599e
ACR-47f72da6323e49c2be4f23ae1436c668
ACR-3e3ddffdf239475f8834af4dd336873c
ACR-e62af2cb20f84d99b3a90ab780e22f1b
ACR-133766f65393437db3a5e3210a9173ba
ACR-521254c26e6448af88667098a94da009
ACR-cbb3f8a9f4dc431fa372627447854d38
ACR-ebb067a734a549a1bb5d583da440b3c9
ACR-84269f34e7164ed5accdd7cdef162305
ACR-5cdf5c2908584a4abdc05da61d94be6a
ACR-8311a851a3e840cebc2b4d2d1bfd634c
ACR-b2572936292b4c52ae3102a5457e79cd
ACR-ccff16a2d3814f4cbc9df26c2dcd2862
ACR-0bc5d9e4cbe646a0a12c14c30dc18ad3
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
    //ACR-c45e47f9e13c4b08bf85225f7670cdbd
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

    //ACR-683fa128d90f4e81ad1fafd3d61de44f
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

    //ACR-89592e9254e84953b1e84c87374fe217
    assertThat(file.newRange(file.newPointer(1, 0), file.newPointer(1, 1)).overlap(file.newRange(file.newPointer(1, 0), file.newPointer(1, 1)))).isTrue();
    assertThat(file.newRange(file.newPointer(1, 0), file.newPointer(1, 1)).overlap(file.newRange(file.newPointer(1, 0), file.newPointer(1, 2)))).isTrue();
    assertThat(file.newRange(file.newPointer(1, 0), file.newPointer(1, 1)).overlap(file.newRange(file.newPointer(1, 1), file.newPointer(1, 2)))).isFalse();
    assertThat(file.newRange(file.newPointer(1, 2), file.newPointer(1, 3)).overlap(file.newRange(file.newPointer(1, 0), file.newPointer(1, 2)))).isFalse();
  }
}
