/*
ACR-5cc17cf023324b308ad7d96652e7ce89
ACR-c82aa34eaf014e5c9b7799a3e6653be5
ACR-8d1ba814e7d24e51aeeb16c4bb243e41
ACR-5c5978d5c4044ffd814f74dc60f494c6
ACR-8c266990cef94caaa3bab803bdc10a7c
ACR-7d8bed4bc2a243a381fcc4969649a76f
ACR-31d93216ca9c459d81d5b05b5a77d76b
ACR-ae152f30bca84cb08c2cea9073f60b11
ACR-ca3b3d54034d4efe83be638eed0d73d6
ACR-24fc4dbeedd2415ba058ee91be10a6d3
ACR-33084076d7f0470fb9304e6986cd1b52
ACR-c454360983ab4727986bbabb7cb2e7a6
ACR-491fa7e43b044bfe9956247fa4c870a4
ACR-cf54585ecf9344c7bcfd797b672e0ec0
ACR-e3ddef9ec2534e77aac8a1c8b08c4c8c
ACR-94bf3e2674d548d39e30aa4947ca65ff
ACR-ab48db3258bb4396bbddb7a867e5ae23
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
