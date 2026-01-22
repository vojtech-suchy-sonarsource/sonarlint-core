/*
ACR-e83c94271edd46a2a8ff94a55edb61fd
ACR-b50b0ceca6b843248c1c37ebfb8e55c2
ACR-cf209aa6985843d1a5bb4e6a88d77851
ACR-51aab77ed67441f58a990d286ac9e701
ACR-f0794367b7bb413e99ae3cdb4da0503d
ACR-178974719091434e9a3df76c000889e1
ACR-2121887f22864e2bbd0557b68c059bec
ACR-7594d8a76ef5458a9f858d783db0677d
ACR-94db52c10a7d41c3afce83d32e530bd0
ACR-991d3d17a9364268a6ae220f40da6fd6
ACR-28a3ae1c732b4a5383820810680aa557
ACR-09ba5b9319c14eb182fcbce1fca94daa
ACR-4fd94aa2520b454eae4e8e532e8e2bb6
ACR-cc6ef0c712404826900e08a07555dfd0
ACR-01d774ee167543169c0e93c43f7d64e9
ACR-9f12ed1d244d4ebf9357b21aca13d471
ACR-39019b8981af4a28b63460b7efcb4f30
 */
package org.sonarsource.sonarlint.core.commons;

import java.nio.file.Path;
import java.nio.file.Paths;
import javax.annotation.Nullable;

public class SonarLintUserHome {

  public static final String SONARLINT_USER_HOME_ENV = "SONARLINT_USER_HOME";

  private SonarLintUserHome() {
    //ACR-e66e09ef3b2f4f009a3a62bc518f8586
  }

  public static Path get() {
    return home(System.getenv(SONARLINT_USER_HOME_ENV));
  }

  static Path home(@Nullable String slHome) {
    if (slHome != null) {
      return Paths.get(slHome);
    }
    return Paths.get(System.getProperty("user.home")).resolve(".sonarlint");
  }
}
