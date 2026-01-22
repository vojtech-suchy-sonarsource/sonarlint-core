/*
ACR-8320bd0971be4deaaa2bd480d780032e
ACR-8874509f7012458a87fe2733cc685edc
ACR-b559bda8c5d1475a9f3463222162e424
ACR-5a8a6465213341d2b632751a0937802f
ACR-9f3aec4dfabe4d758ebcffb50640dfeb
ACR-6645c0ee632c48bcb3b55740f9130c65
ACR-659eac005cae4436b994b36eb0130139
ACR-6dd3bacaad6c4aa4a697b4c92383831b
ACR-a7ed00255c9c492da122f41cfc8da826
ACR-b5743e5da138408b87d1adcf6bce2f8f
ACR-9c9102fdb8c343b5be2a13ea43817094
ACR-5e429bc5304343c08977f162a1799d47
ACR-8e2733e168fe42b0bb5b641024525519
ACR-c2da5a7fcf2544b88a95375cf6077f88
ACR-eb06f995c5e34af09d5cd74919d01887
ACR-a0767b4d639849a9bee606162391ec14
ACR-550fbd540686466ca05ee72cbbd3ac22
 */
package org.sonarsource.sonarlint.core.analysis.api;

import org.junit.jupiter.api.Test;
import org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem.DefaultTextPointer;
import org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem.DefaultTextRange;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class DefaultLocationTests {
  @Test
  void verify_accessors() {
    var inputFile = mock(ClientInputFile.class);
    var message = "fummy";
    var sqApiTextRange = new DefaultTextRange(new DefaultTextPointer(1, 2), new DefaultTextPointer(3, 4));
    var defaultLocation = new DefaultLocation(inputFile, sqApiTextRange, message);

    assertThat(defaultLocation.getInputFile()).isSameAs(inputFile);
    assertThat(defaultLocation.getMessage()).isSameAs(message);
    assertThat(defaultLocation.getTextRange().getStartLine()).isEqualTo(1);
    assertThat(defaultLocation.getTextRange().getStartLineOffset()).isEqualTo(2);
    assertThat(defaultLocation.getTextRange().getEndLine()).isEqualTo(3);
    assertThat(defaultLocation.getTextRange().getEndLineOffset()).isEqualTo(4);
  }

  @Test
  void text_range_can_be_null() {
    var inputFile = mock(ClientInputFile.class);
    var message = "fummy";
    var defaultLocation = new DefaultLocation(inputFile, null, message);

    assertThat(defaultLocation.getTextRange()).isNull();
  }
}
