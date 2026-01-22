/*
ACR-a8c0e286b5ad44c18698b81e4100b5c6
ACR-9e8c51fa0a1e4aef84bb39d94b9844a7
ACR-0371103ba59e4e95957e5c126373afa9
ACR-1a555bd8e521408bb2e5f83ac9332b60
ACR-fbb0f6585f104d2b8a4b2a592edc31a4
ACR-2390111b435e4c029ce0392b9b4a5123
ACR-025056af8ad943c49563bd8df306161a
ACR-15d3ccf530924eb48ceac1d9d126f531
ACR-feddb03bd91e49c6aaae865ae3886f40
ACR-adc78beb53bf42a282835fcbc8f9773a
ACR-bde7d8af6bdc4afb8713d718534ea2fa
ACR-addd616995f44dca963f8767f4872350
ACR-3bd40628137243108e92a0dd55e8781e
ACR-d739c0f88259495799e383ec2b03dfa4
ACR-88e6824a19544204a0f53308b7ce917f
ACR-bb16d66dd57941328477aa729bf5c73d
ACR-70c9a509c16b4272a8c3c46a9f547d29
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.analysis;

import java.net.URI;
import java.util.List;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis.AnalyzeFilesAndTrackParams;

/*ACR-ec9b1b49c49940988c5cda1108ce324b
ACR-647593859446440a8c4b179cb1cbb292
ACR-b1f3436e760e420e87b6b1e1d5f8b2c3
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
