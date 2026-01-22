/*
ACR-a41842cd0a1649c2bf882e7f27bd4c0c
ACR-74905a1e58294513ab8cfedfef7a3f7f
ACR-6e20a868392f4c04b7e6935a4887d843
ACR-3ae58159cc514e97bf798957a9eb21cf
ACR-532e7faca5a14120a74675e2a260ae09
ACR-888c42b542aa41b8b436bb4d18113ae2
ACR-0bb5c8e282ec49b3a592621e64363246
ACR-ae7cffbb67034813ad209cc25b07fd30
ACR-18fb4ce15d2844449974bcd6212de0c1
ACR-60616cb0cded4802a73fe6150e333414
ACR-70b0be1b03f449d3beb47a6bdfe4d5c2
ACR-99a087ff148040a58d7a11e084da7ba0
ACR-399f996e4e4641b89488bf3021b0ecea
ACR-ac78895f71194ca58e296765d7e5b18c
ACR-ca55abea5d2949589e45da753ea499d3
ACR-7e1945c69aa24d1c8e8e634f36367e9a
ACR-1be37a50826a462a89f1d355d068dbae
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sonar.api.batch.fs.InputFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InputFileCacheTests {
  private InputFileIndex cache;

  @BeforeEach
  void setUp() {
    cache = new InputFileIndex();
  }

  @Test
  void testFiles() {
    var file1 = mock(InputFile.class);
    when(file1.filename()).thenReturn("file1.java");
    when(file1.language()).thenReturn("lang1");
    var file2 = mock(InputFile.class);
    when(file2.filename()).thenReturn("file2");
    when(file2.language()).thenReturn("lang2");

    cache.doAdd(file1);
    cache.doAdd(file2);
    assertThat(cache.inputFiles()).containsOnly(file1, file2);

    assertThrows(UnsupportedOperationException.class, () -> cache.inputFile("file1.java"));

    assertThat(cache.getFilesByExtension("java")).containsOnly(file1);
    assertThat(cache.getFilesByExtension("")).containsOnly(file2);
    assertThat(cache.getFilesByName("file1.java")).containsOnly(file1);

    assertThat(cache.languages()).containsExactly("lang1", "lang2");

  }
}
