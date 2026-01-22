/*
ACR-aa82f66f09584f98a6d4dd7a960c1002
ACR-a57f030d82274eb6b9030e95fb81a554
ACR-9e7b90a89350439989c7feb1edd01b8c
ACR-0854895e9bf0401c8f2d03fc26ac1d97
ACR-b9a269f459b54d3ca0437c4121000d9f
ACR-4a47570562f74230a0212e17a7fc68b2
ACR-126b641e7bd043adbb5f93823451b98e
ACR-ac2325afe06d477680dda1700ee0411e
ACR-a31ea36af51945c8a24140666584610c
ACR-baf52e4f386c4586b5eae0cbf4558fcc
ACR-1a6f18bf31484771a5d811ae71c9b185
ACR-f19d149343f3436fa1cdd78113ed5268
ACR-b8be200929814b5fb76b969f01527cf9
ACR-00b31cb8db55460baa7ab5f9049848f3
ACR-b263d5bcea9f45158d1cd26ca8145631
ACR-78f57e79bc2742719a42ddbbdcbbb0f7
ACR-da38f118e40c4991b57418c3470686e2
 */
package org.sonarsource.sonarlint.core.serverconnection.storage;

import com.google.protobuf.Parser;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;
import org.sonarsource.sonarlint.core.serverconnection.proto.Sonarlint;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProtobufFileUtilTests {

  private static final Sonarlint.PluginReferences SOME_MESSAGE = Sonarlint.PluginReferences.newBuilder().build();
  private static final Parser<Sonarlint.PluginReferences> SOME_PARSER = Sonarlint.PluginReferences.parser();

  @Test
  void test_readFile_error() {
    var p = Paths.get("invalid_non_existing_file");
    var thrown = assertThrows(StorageException.class, () -> ProtobufFileUtil.readFile(p, SOME_PARSER));
    assertThat(thrown).hasMessageStartingWith("Failed to read file");
  }

  @Test
  void test_writeFile_error() {
    var p = Paths.get("invalid", "non_existing", "file");
    var thrown = assertThrows(StorageException.class, () -> ProtobufFileUtil.writeToFile(SOME_MESSAGE, p));
    assertThat(thrown).hasMessageStartingWith("Unable to write protocol buffer data to file");
  }
}
