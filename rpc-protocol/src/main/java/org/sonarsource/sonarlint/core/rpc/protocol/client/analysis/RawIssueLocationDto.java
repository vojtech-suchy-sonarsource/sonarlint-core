/*
ACR-762372366cb1408f90349b7263b35c23
ACR-327de24014304353a62f9e9cdc5bee13
ACR-3ae918d7944a449f8a53483d3374892e
ACR-0a940034553d4a4dad69e88a90b5780d
ACR-e265f2a7ac8c46e6acc6739b6b353bf0
ACR-5e1672e92ae14775a4bc7d5558c4989f
ACR-6bac6fdb46914803846eb2ad84e169ec
ACR-c676edd45adf4a0ead2417f85cb0b40e
ACR-ce6f15d5d93141d2901757e7c51bd3d0
ACR-46870cdf3cba4b478227cc6ea97ff7b3
ACR-aa7057782c0449c1b9415b326c1edb67
ACR-a5fcbb23e12b4725a62fa345e258b6af
ACR-2b1a71d1f14c4a32bdfa289369bf98cc
ACR-91bbed7acd9d4cd99ab183e1785b1b9e
ACR-ea330ebfbcb442bb9ea8cedc4ddcffa4
ACR-65bf7fe4844e49f7ba1667a33a20d794
ACR-cf1f4dfef90c4f9587b8d23c22872bfd
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.analysis;

import java.net.URI;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis.AnalyzeFilesAndTrackParams;
import org.sonarsource.sonarlint.core.rpc.protocol.common.TextRangeDto;

/*ACR-f0138a911e7a43398005020ad6b4f63f
ACR-1720beb719314daca2cea670b62b3bd4
ACR-dda04b53d787443698cea00faacbc8a4
 */
@Deprecated(since = "10.2")
public class RawIssueLocationDto {
  private final TextRangeDto textRange;
  private final String message;
  private final URI fileUri;

  public RawIssueLocationDto(@Nullable TextRangeDto textRange, @Nullable String message, @Nullable URI fileUri) {
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
