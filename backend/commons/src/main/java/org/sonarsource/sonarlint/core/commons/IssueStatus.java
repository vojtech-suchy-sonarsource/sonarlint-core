/*
ACR-75c86215445e47468ebcbcab5fe5085d
ACR-7567233838214af8ac8e280b69dba623
ACR-88c8633e54f04d0aad2b23020af3cd16
ACR-cd9f5a27c2b941ce8e3e6727b4a49d8b
ACR-5ef522031cb5407dbcfeb4b802f55d64
ACR-dacdfffb51654809b27d966a798d5b22
ACR-52d50df568f042bc80259690a3d26298
ACR-ad26c758eead4b11a2e4da23c1a6cfb2
ACR-021462ed8a3548d5ab734e0cbae5f149
ACR-f7facbb0316f4d20931c5c278718ac92
ACR-9a68c4d718a54553ad11257233cc8e2c
ACR-2fc4f18d6e3748baa2d4ab0d7ac87df5
ACR-b21ae65420184ec99be5eb7e3529b3b7
ACR-a897fb3bfe6544c587754611e9d0fbf6
ACR-7ee8f4ce25054b52802e356916a0aa49
ACR-48d54f81f5dc4ac59d012d19d6fc9137
ACR-eda164b2a5b34129a5848499390ada3f
 */
package org.sonarsource.sonarlint.core.commons;

import javax.annotation.CheckForNull;

/*ACR-b65a173007384c26b75bec677b4fbdb5
ACR-3e3fd3acea454aa18720619fad62526b
 */
public enum IssueStatus {
  ACCEPT,
  WONT_FIX,
  FALSE_POSITIVE;

  @CheckForNull
  public static IssueStatus parse(String stringRepresentation) {
    return switch (stringRepresentation) {
      //ACR-14968204ecc74da4b3b4d99843b9ad2b
      case "WONTFIX", "ACCEPT" -> IssueStatus.ACCEPT;
      case "FALSE-POSITIVE" -> IssueStatus.FALSE_POSITIVE;
      default -> null;
    };
  }
}
