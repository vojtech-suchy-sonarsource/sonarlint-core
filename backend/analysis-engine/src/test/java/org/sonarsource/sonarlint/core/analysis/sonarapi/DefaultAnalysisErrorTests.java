/*
ACR-c27f9ec1bae54f7e82b4224eb0df8d1c
ACR-0a0e61dd60504ba98de1829a0eb50736
ACR-cba5bc9555304fa7af9788614b9ab97d
ACR-9c70716ccb67414cb4049dddd8e32bc9
ACR-4577d96f37e54471a3324c47dcdaaba7
ACR-a1f5e23c4e03461bb0bacd600491dee0
ACR-d19b2c5ff6744931bc9a41b9e959e13c
ACR-50cc33a7b21f4be7b1c4c3679ff17e99
ACR-05af5146613941379c16688a2c0a8829
ACR-c82378f41bb74addb4f1a34561bce62f
ACR-28251fb9e3d44fcda192c5dd8f944e70
ACR-493e6f0abfbe43d5a94f49f14e0777bc
ACR-e1b0c13929ab457da1c20e082a048378
ACR-35c87136d6d54d5f93dc194b46fcde0e
ACR-a7ee84d72f284be1aa4a7bdc66af1c88
ACR-f16bd58ea5c546e5890ab8c7fbd3ea3a
ACR-165115d025a24c51b23aa10d5db088b2
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
