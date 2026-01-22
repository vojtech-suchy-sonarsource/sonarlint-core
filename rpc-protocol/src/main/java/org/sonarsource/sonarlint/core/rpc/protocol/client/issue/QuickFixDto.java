/*
ACR-73ee6bc064fc46b282fe280331bb7d2a
ACR-047825eb66644a718458650db02ea6a4
ACR-63d75ef1cae04576b7d91b9062d1232c
ACR-055def2f9baf43baafbfec54791c05d4
ACR-110f003bffbe429680ff2f21aaad95e4
ACR-bc41b929670448d6a8cd2f486fb0a679
ACR-cc0af6ef35c54e498cb0728f40a2dd32
ACR-65bbd10824014e729f6ec6904d7dcdd2
ACR-30e646f54b9443d0a0652eb90c876007
ACR-dd566f35fa5c42d3a63a2c1a78a34d87
ACR-66731d57de654b5cb8660d0c45a286ab
ACR-615435a23ff146bc8c15bad89d0d9049
ACR-e8769aaba19b4e55ad22c59c4f49d6e3
ACR-58ae71beaea246a08a126bfd58ec2712
ACR-32f78b697b8a4567aa17bf5e72b38f7d
ACR-13868541ce2a4ed9bc314a957e68a857
ACR-f99942828bb34156bd645741a535647e
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.issue;

import java.util.List;

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
