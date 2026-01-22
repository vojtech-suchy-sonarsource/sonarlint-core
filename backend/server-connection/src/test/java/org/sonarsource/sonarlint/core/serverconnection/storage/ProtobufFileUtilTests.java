/*
ACR-c82ce4f19ade4942ab5553a54d687394
ACR-c7eb3c58c05f4b08ad34947278eca116
ACR-2df0a17b6cdd40f097a74d8a7a162fe9
ACR-97dd51274e504dd096a4cb3c6f4e73f8
ACR-e84d83068698495fb5baf3daf0e16591
ACR-b91314db467949498a58199f47533eda
ACR-eaf6ee83df364e1c845bfae73a25599e
ACR-1fe624b8b6964d5189ea5469f60c5634
ACR-326e28ace4b6477f876e346a917cbeed
ACR-6a486b684d0645418008a3d57ca9daa1
ACR-9914bd90a737483bb4ac8b8aa56cdfdc
ACR-cd462289e47c4cffafd83664bb0d380d
ACR-ee1e1e2ec9b445f9af508cbe76a73b6a
ACR-210871b8b8e44dcc941b4c40dae94e94
ACR-12b34885832f44f296cfc7272acdbe15
ACR-c15b42bdb35640c9a77404c8a6de2ae6
ACR-d132ec3493c0416bbd54d7fa14fd459e
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
