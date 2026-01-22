/*
ACR-a81f987019a74c609a27309db3b65673
ACR-7c66771eaef5432fa37ffb255b3fc090
ACR-a2af3d22e699495781ab12a99ce8ee69
ACR-e027c2bf6da14821b1fa47712ba855b4
ACR-53b762ba82504780a147604e004c09c0
ACR-f5cf3c1493cf4a48bfd733e427a06457
ACR-8ebbc3c3a8a54e98a3efd0708e4926a4
ACR-4360f6a4928b47cb89831e6f764e50b0
ACR-ae877d2a2dbf425ca2fa5e82e08af6d8
ACR-b8a6c23b00794b8f88a28565c36a24e0
ACR-714c28b0642b460bbd212d687f1de8af
ACR-41823f0e5443431ca03abcb0edbbe674
ACR-97cc87a7e0bf4255aa385db7e8db11b5
ACR-984a2c0b8565413bb777d68981554771
ACR-748475ca079143f9b1a82bf0b0d54585
ACR-f6cdf3661d194361aa40f35112f44090
ACR-59a4554b67504c46bf0491e697a8d253
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.analysis;

import org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis.AnalyzeFilesAndTrackParams;
import org.sonarsource.sonarlint.core.rpc.protocol.common.TextRangeDto;

/*ACR-27b758eeb6a9453dae2614bcbf1fbd47
ACR-0f79863c5eb3466e866d3e7ad3a92ec8
ACR-dee13ae238e946b0bd09428c47c67ff3
 */
@Deprecated(since = "10.2")
public class TextEditDto {
  private final TextRangeDto range;
  private final String newText;

  public TextEditDto(TextRangeDto range, String newText) {
    this.range = range;
    this.newText = newText;
  }

  public TextRangeDto range() {
    return range;
  }

  public String newText() {
    return newText;
  }

}
