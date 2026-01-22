/*
ACR-3d0cc73b37b848efb835d28499a49c32
ACR-00fa68d7120d4ad081e59318aaea76dc
ACR-ad5cb117ffcb48d5a1715a9fdda3ed77
ACR-3a45d399cc8744168e246152c5c25aed
ACR-bf9283244e954b09a87e88d402c742ea
ACR-f9a3fbfed23d452cb1e76ccca0a51b65
ACR-0f490ec7b6dc43788978db835474c92f
ACR-59034ac83f5e4db580694e8f34aec0c9
ACR-41f02d4d800c4d2abe92eaac0ff8ca7b
ACR-88d875dd58554018945db897b85bf4dd
ACR-8e6f0ba8fa00466487a4e71c4a71e4bb
ACR-c256e351cf88444ab19d3251555b3d31
ACR-653befd65b1d4aff8786d1733e433029
ACR-cffece5a74e84e1191e90fe256dbc698
ACR-ac8fc951a0934b1799c59b3e56bfc978
ACR-8c51986d49ac4e1188608317401e7de2
ACR-71aac13c5b0e45c3a8fa2b513a8a8eda
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.analysis;

import java.net.URI;
import java.util.List;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis.AnalyzeFilesAndTrackParams;

/*ACR-7ecccdfaeb8d4f4ea919dc0180393a55
ACR-35394626def3460d9d2e44296cc7f633
ACR-2ce20b87da1c4991bfbca7004852b5ee
 */
public class FileEditDto {
  private final URI target;
  private final List<TextEditDto> textEdits;

  public FileEditDto(URI target, List<TextEditDto> textEdits) {
    this.target = target;
    this.textEdits = textEdits;
  }

  public URI target() {
    return target;
  }

  public List<TextEditDto> textEdits() {
    return textEdits;
  }
}
