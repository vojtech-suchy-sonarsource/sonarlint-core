/*
ACR-b5a4f675d40542fcab704e58c72b586f
ACR-5d418e41df8a4347ba770f6173375050
ACR-dc650cb9f2f54015994fcbf968b9c926
ACR-e73e3d213c5540cfbf4f6e0362559c95
ACR-31eacf27c20a4e4d97a0121aba9a9910
ACR-38d1690ec42049d086bed49c20247998
ACR-e019e598f58347d1b65e29212882020f
ACR-dce9ee42a4ee48ad824f2768cd44c6d2
ACR-560534c2b69b4dff922b56a877d14721
ACR-3be524fddc5d4dc69ddf063b8ba219e5
ACR-ca65cf417ef94edbbc87528a7547e44f
ACR-7d5306a322f448bcb55f1373d0778a94
ACR-0685be3d9aea4d7b9b42d9653596cfbe
ACR-281df257938d44309882f6a48c21de41
ACR-441cb9966ba545bbbe1dea1ac733d791
ACR-bf597d2f49e64e27a603862a12821940
ACR-07eb7d07d7f2430aac879d09df79da40
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.message;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

public class ShowMessageRequestResponse {

  //ACR-8a019e0c8c6b468da75c5ee7c6d9ce23
  @Nullable
  private final String selectedKey;

  public ShowMessageRequestResponse(@Nullable String selectedKey) {
    this.selectedKey = selectedKey;
  }

  @CheckForNull
  public String getSelectedKey() {
    return selectedKey;
  }
}
