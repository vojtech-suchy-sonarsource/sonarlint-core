/*
ACR-41d299b2365846db953d97ba4c0ac031
ACR-dc653bb17bad4fb2a10c2c76ab7be4bc
ACR-ce365369a8b641aeb98773e27de7f586
ACR-5e22460f0f594c7299b8e25652adba54
ACR-b5bc5b258d0446a0abfbc8f3f2199ff2
ACR-f0fd20ea8683404f9fc2ed15e6746cbc
ACR-e903ba3248f74adbb26f8e4ed4818557
ACR-59ed800c82e14842b1f3e380c7f75012
ACR-9561b6e9fb594855905d813f3ae1351e
ACR-99b5881c1a87451b9ee7eed0f334cd42
ACR-560284d879244d408bc6b6885e2709f8
ACR-8df15bed698b44dd883820ed7c113a9f
ACR-bb894a3147084a41ad0e6e083fecc146
ACR-83b145ff7dbc450ea0bde10ce5fab505
ACR-6976e4ee217d488285cca0b2e46c0194
ACR-bd594015fd844cd39f52c005db538a9c
ACR-58fecfdd7e824ce5a0dcaa59f833ece1
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
