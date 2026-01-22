/*
ACR-d3b52e577ef34072ac7ae6fbafaa1a64
ACR-bdc14df9e7e744aa8d7b4330ee7b11f8
ACR-e6f293b9e53b4447a93a82d7c06d277b
ACR-1041c96e43e345a99da27537a4475f5a
ACR-745d0b055e7d4e33992d8780a7d711e3
ACR-2b3596e7798f4d31a001d9d2a9a442e0
ACR-cccaf0946bc1438f9779013d906e386e
ACR-26dbd436d074461d8d509a7bbed9715e
ACR-54e628edbbc545868f2fc9c6abcba6db
ACR-09e87c11b5b54a4e86c8f0bd66f63cb7
ACR-a45ddc8cd5f64cad8eb72d50bb49a1bd
ACR-407671a5320940ce85f2b5beac7deef8
ACR-70b9087f1de147a49fa1fd03885538ed
ACR-53cc1ff971d64185bafbb63312545add
ACR-1c139eb287ad495e93ff0e4de543e29a
ACR-6f138879d8944320aea696d4aa6bb830
ACR-d41b1ea3d9d94c5b842e7da330e19018
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.issue;

import java.net.URI;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.rpc.protocol.common.TextRangeDto;

public class IssueLocationDto {
  private final TextRangeDto textRange;
  private final String message;
  private final URI fileUri;

  public IssueLocationDto(@Nullable TextRangeDto textRange, @Nullable String message, @Nullable URI fileUri) {
    this.textRange = textRange;
    this.message = message;
    this.fileUri = fileUri;
  }

  @CheckForNull
  public TextRangeDto getTextRange() {
    return textRange;
  }

  @CheckForNull
  public String getMessage() {
    return message;
  }

  @CheckForNull
  public URI getFileUri() {
    return fileUri;
  }
}
