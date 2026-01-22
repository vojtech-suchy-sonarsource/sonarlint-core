/*
ACR-325e81f9faf54132b1c0b1f4a6347d7d
ACR-ea6ad0b03afe4b54acf3e3267d75a35e
ACR-a1688babf216464397b9f89e43e05e3b
ACR-470b475ecfb14825a50145c977fa3ce3
ACR-480c4021de4e4912bf485490b1d5345a
ACR-e969597aa59e4524b3626757bce4fc79
ACR-629d3224b3204ebd9453f4a4e4b02879
ACR-356e4887da1747f7838cc57da036b467
ACR-a44f33fcec3644e790d212b0831defbe
ACR-28e6eb76ff324b26afaa74d74e127922
ACR-802eb4c50bf543d6988c6d34fb035035
ACR-4cc15b53bbf048e695878ae0378a6b6e
ACR-908f978d814f40c49c969dbb79de744d
ACR-3ce9a25959134a77af6e2ed2eb4d1643
ACR-04a7c2e2113b4d9680ec0783ea7ea8ef
ACR-311d320edc754e87a29d2848bcb1265a
ACR-19979df1c5704c009168e029af78909d
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.fix;

import java.nio.file.Path;
import java.util.List;

public class FileEditDto {

  private final Path idePath;
  private final List<ChangesDto> changes;

  public FileEditDto(Path idePath, List<ChangesDto> changes) {
    this.idePath = idePath;
    this.changes = changes;
  }

  public Path idePath() {
    return idePath;
  }

  public List<ChangesDto> changes() {
    return changes;
  }

}
