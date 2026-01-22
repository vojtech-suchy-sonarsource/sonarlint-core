/*
ACR-9b4a5d768a8f43a9849b5fe29d5edfe9
ACR-25efad5bd9bc41939aef505e4b087c5d
ACR-20809d97afc84953aa9c5ba063002646
ACR-85c70eaa2e3c4a5bb03e403bd6fdf281
ACR-142dae1ab0794d5bb86bff642d0e721b
ACR-9f2fefc6462e40da8a954609877f3977
ACR-5a48ae36a56b4de3bd696450148546f5
ACR-f42fa3a8d3684f6fa126d9c19a438042
ACR-b6c6b6deca2d4a14a2956b22a7cf6826
ACR-858353b1648c4010b68f6afa30d2a1d0
ACR-a9f146fce8714330b8dd9c87bc1bab54
ACR-433cf9e726d44d409a1301b558662989
ACR-3c97dde9383a43d0881bd8dc3038bc79
ACR-e4504d459171418d84c140698451d842
ACR-980a28afd40f45418d965fa5cbca2981
ACR-7c99009ba5ac492f9a8c23b3260b9eea
ACR-83958aa3dafb427eb4e1839e2a663e9d
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
