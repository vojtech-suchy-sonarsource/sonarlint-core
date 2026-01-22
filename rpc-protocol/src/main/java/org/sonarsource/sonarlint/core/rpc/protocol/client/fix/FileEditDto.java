/*
ACR-8b6e3bf533a64c718174613407fe3655
ACR-1394fe4f63c441fcbbe67a8363fdb253
ACR-aac49b705b2a454595a88aba5a002938
ACR-0be2e0832235425c8c18b2c42efc99bb
ACR-511dafc29ca443ceaae7eca6e51a73c4
ACR-16c105ba14294cfab7e8092423f637bd
ACR-299e7e4283544566ac6e9ee3f5f8b120
ACR-dd347282877d4e85b05fcfdd6e3f9bb1
ACR-a991d9c13ea94b5d816aba0def5af018
ACR-cfe3748795004882a5289ba84c61170a
ACR-75241b771b664369b4e060ba0af64bc6
ACR-728e8f7ede0645058e7a851895058240
ACR-291971893f944b0ba47efba767461c5e
ACR-9c7a17b7315e4fba92c3a5ec73667ad3
ACR-40982ab0796d4a6f89c6164b881d751a
ACR-fb0d7e6ef06541c29038405565bb9dfc
ACR-5467745ce1ac4527b275c7bef3bced3c
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
