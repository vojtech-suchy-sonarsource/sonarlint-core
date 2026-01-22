/*
ACR-f8d0aad7ba764bc78e2f08d92d07d6ec
ACR-457c975a8fc34abf84b1c59e251fa219
ACR-7718fce4e58741dca59d0e984eb6a0a5
ACR-9719f2937b16438db07bb6d208b75f03
ACR-3093d6e08eee43d188b0736e543a12c4
ACR-fca240d04eaf4e10ab9a8b1508bc14ac
ACR-5319ff60a8a147ee89b81e5f18e1b228
ACR-5ef6b43c889141f1847e1c568447a6fc
ACR-1a4fd273be1645ffbf8d2742989a96b1
ACR-3321a480956c470da2aa12419c2d8bc5
ACR-c75a046150f74bbb9acc7285c09cd9ad
ACR-45ad74c932ae4a49836b43121550ab2b
ACR-87fdd3a7bf90485b937e3bf69945187d
ACR-863ea4e4254645b1b17ec5e94f9f165c
ACR-9cfece443f664299acb72a9462dd9c24
ACR-00db14b3d5df4f0594ca2a809eba9bd9
ACR-0464f25e8ef14443aeff0dc4b39fd360
 */
package org.sonarsource.sonarlint.core.commons;

import javax.annotation.CheckForNull;

/*ACR-9f30e8c1cd6e4127bb8c5c0bbd0f56b2
ACR-931263a9eaf64d3d9d7448bc42a5a43e
 */
public enum IssueStatus {
  ACCEPT,
  WONT_FIX,
  FALSE_POSITIVE;

  @CheckForNull
  public static IssueStatus parse(String stringRepresentation) {
    return switch (stringRepresentation) {
      //ACR-c8601e7eabf144c799e83d0cad6c1d57
      case "WONTFIX", "ACCEPT" -> IssueStatus.ACCEPT;
      case "FALSE-POSITIVE" -> IssueStatus.FALSE_POSITIVE;
      default -> null;
    };
  }
}
