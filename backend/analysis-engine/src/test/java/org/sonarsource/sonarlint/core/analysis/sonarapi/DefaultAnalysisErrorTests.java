/*
ACR-85160823ab1147deb8b7ac34ad0a59aa
ACR-b18776a8fe384ee0b192cca0fd06685a
ACR-7d8765e031354b08bd83b6e5de231034
ACR-078b5d839e054abc9b500ca3f142ef3c
ACR-28b0493515684186a71823ba69170853
ACR-567204cc451a41caa2aa38d7c726cb6f
ACR-c7128046b49d47b69968c2a6ddb1f8ab
ACR-bc7e8591d66a402c9a8655ba4d63eb1b
ACR-a9de62d4733740e49abeb26bcfd06b6c
ACR-87e0b8cf4c984e4c8d60d0217096e8d0
ACR-ed6a33abc2f241c1af34b157fe0fd608
ACR-dbfa0f1371c74792ade9b96090ef6d0b
ACR-332a9713fefb449e9b0d9ce0f02ea543
ACR-b282ed46478c459a946f48029ddb5bc3
ACR-054b71f881804cae9ed0c7a32d379f6e
ACR-2e8aba69fcdb4ff1a8317b2452614e8a
ACR-0624949307d74c54bd26efaa4a2ab540
 */
package org.sonarsource.sonarlint.core.analysis.sonarapi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.TextPointer;
import org.sonar.api.batch.sensor.internal.SensorStorage;
import org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem.DefaultTextPointer;
import testutils.TestInputFileBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class DefaultAnalysisErrorTests {
  private InputFile inputFile;
  private SensorStorage storage;
  private TextPointer textPointer;

  @BeforeEach
  void setUp() {
    inputFile = new TestInputFileBuilder("src/File.java").build();
    textPointer = new DefaultTextPointer(5, 2);
    storage = mock(SensorStorage.class);
  }

  @Test
  void test_analysis_error() {
    var analysisError = new DefaultAnalysisError(storage);
    analysisError.onFile(inputFile)
      .at(textPointer)
      .message("msg");

    assertThat(analysisError.location()).isEqualTo(textPointer);
    assertThat(analysisError.message()).isEqualTo("msg");
    assertThat(analysisError.inputFile()).isEqualTo(inputFile);
  }

  @Test
  void test_save() {
    var analysisError = new DefaultAnalysisError(storage);
    analysisError.onFile(inputFile).save();

    verify(storage).store(analysisError);
    verifyNoMoreInteractions(storage);
  }

  @Test
  void test_no_storage() {
    var analysisError = new DefaultAnalysisError();
    assertThrows(NullPointerException.class, () -> analysisError.onFile(inputFile).save());
  }

  @Test
  void test_validation() {
    assertThrows(IllegalArgumentException.class, () -> new DefaultAnalysisError(storage).onFile(null));
    assertThrows(IllegalStateException.class, () -> new DefaultAnalysisError(storage).onFile(inputFile).onFile(inputFile));
    assertThrows(IllegalStateException.class, () -> new DefaultAnalysisError(storage).at(textPointer).at(textPointer));
    assertThrows(NullPointerException.class, () -> new DefaultAnalysisError(storage).save());
  }
}
