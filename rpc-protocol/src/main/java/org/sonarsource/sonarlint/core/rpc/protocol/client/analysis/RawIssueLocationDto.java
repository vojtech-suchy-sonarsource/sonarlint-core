/*
ACR-7f98d06d40ac4e9da6f3645fdd1a9498
ACR-d88fa676203044e3bac6161c9134e59c
ACR-bee90677b7cd446c8762c95a9771e43e
ACR-86911186f66445bb88e62a920e951897
ACR-9e20181c619344bc909168f4b1a455c6
ACR-ac4eea7baa7d4a5fa49ffac2eb27a27e
ACR-4d3c2ff0d47f429b9335c5adf7ef5e89
ACR-80b00f25fcb949b7ae4fdd5808c8cc4c
ACR-6953002942a642e2ae9cd731c790792b
ACR-5988d770ba084feb9c4cce89e6ae8ff6
ACR-5652a942d2cd467aae920959fc17e45c
ACR-dab16f1ee64142759c2ad0cd65f2a6a1
ACR-6fe7a79ac7154eb4bbb9e81e6a916402
ACR-f04249e4208440bd8a345dff63211d86
ACR-f6870b4dfbc64e04aa8bdc809b76f406
ACR-a98a9d856f1d49e49dcc3640df470504
ACR-caf47b8ccd3b4993a18947bb98bcf4dc
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.analysis;

import java.net.URI;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis.AnalyzeFilesAndTrackParams;
import org.sonarsource.sonarlint.core.rpc.protocol.common.TextRangeDto;

/*ACR-2990fb5493254ddb83b4465d879a95bc
ACR-830be47eb0244b12b200d35031567b90
ACR-ce5701bf65ad40fbb585e9e4dd7309d2
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
