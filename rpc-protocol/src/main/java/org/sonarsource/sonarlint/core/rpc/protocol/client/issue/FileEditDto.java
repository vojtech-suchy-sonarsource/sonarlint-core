/*
ACR-611c4f30edf247578718dc4f31a42c24
ACR-22705861ab824a81aa56b1a89c5d7121
ACR-05ff5500b1a44fafba9d32f9fc31f4d7
ACR-4ec3c1a3248543b199cd3109122a4b10
ACR-a126b2708da34a47b02d1e99999ea4ec
ACR-fc9e1ec96c024e2cab08eebce27bc76e
ACR-946b96b422904df7bacb8c1a804b5c0d
ACR-87bc7103444d4820bb00ade9311f05b6
ACR-f3a8e823b04745c7b65536c39dc7bdef
ACR-6bd25bcd7a0a48939b11f68935524b2b
ACR-c07b88506a1e4db3868b67bb1ea4f567
ACR-4ec548641946480da7858d23d04cd988
ACR-5a20eed1ca934ebb96a320ed295391f7
ACR-e37b182295b0447bab2992e4aab22222
ACR-46a149f030264fa69283f761625b2204
ACR-a6ba695d96fa438786019c063ab0411e
ACR-a5d959d22a734a32bbf7e3284412805e
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.issue;

import java.net.URI;
import java.util.List;

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
