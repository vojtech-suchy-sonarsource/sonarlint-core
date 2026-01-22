/*
ACR-a762e3d529b3426887b32aa226aca316
ACR-3b29209112ec47f9a4606e7cf373d7a4
ACR-81212e098a6f4d8a8aa0f738f563d531
ACR-bc5c48d595a643189bb03c2c6f35fd3e
ACR-73596b0d96a64c44bca4d3e3af711ba4
ACR-67fb4ceda9ec478f9010fbfb8eac7016
ACR-7c295cef8b9640038307e4e2857ba7e8
ACR-9556355c6e6b429685ccdf0a0f8f3d3a
ACR-d3b180d42e56415586e5c649126eeed3
ACR-7de639c21c074a15bba9e7c4769f21ed
ACR-17ccc3fb69084f5c8e3a8cf3c32f7f1a
ACR-eb646c622def4cb9974b14c6b3a2ee89
ACR-a225fe17746544f78d4f7f86ee3e8f03
ACR-faef387116c54fc29b036d8b3b7357da
ACR-2ec955b6617c4cc582b54235d755cd30
ACR-381d5c0538af4c4caf7834c74db3dc4d
ACR-ed58cb5af5da44ebb373553586f16527
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.analysis;

import java.util.List;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis.AnalyzeFilesAndTrackParams;

/*ACR-b51a6a722d01498d94f89cc458aa465c
ACR-c962a820ce144fcf846b6ddaccece720
ACR-e6a3f059e22448c988fded0d76278750
 */
@Deprecated(since = "10.2")
public class QuickFixDto {

  private final List<FileEditDto> inputFileEdits;
  private final String message;

  public QuickFixDto(List<FileEditDto> inputFileEdits, String message) {
    this.inputFileEdits = inputFileEdits;
    this.message = message;
  }

  public List<FileEditDto> fileEdits() {
    return inputFileEdits;
  }

  public String message() {
    return message;
  }
}
