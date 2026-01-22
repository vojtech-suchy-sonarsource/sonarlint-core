/*
ACR-2527eaf0c17746979dc0c34d22db62f0
ACR-43326f09579041959361464d36e122ab
ACR-ea0793ed9eb54409abce946ee2b1c091
ACR-86855b17ddc84d3da36e3ed4aa3732f2
ACR-b8afdcaab60a4e3f89077f75cbe32c20
ACR-23471b4bc39e40538198d5c6f7326e67
ACR-b652b86b2b024f64a641d05cab20aaaf
ACR-f802ee7d070041d18ca542eaf5f0ef6d
ACR-af5e20b5ab2742bd8e939278927fd931
ACR-3bb23fbcf59b4a949a985ba7de98bc97
ACR-f8e741c398274e05b0ad925fe4d594ea
ACR-e4e71f368e664902967a9c6f9cf9f3fc
ACR-a1816e59d76c44c8981b6389315eda3a
ACR-df79b1abd3534358b96163f3cfa358a8
ACR-fb6aeac8ef4b467f9b5c98d3e61ade92
ACR-0638934061c24087aaf91e1e66578c31
ACR-6247627d2d1248f5b2ef4ecf33144949
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
