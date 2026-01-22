/*
ACR-d176cc33892945378e9019fda059c6e6
ACR-231a9bc5f9b645f88534245523fa5f85
ACR-368ff75df2124c6aaf1c59648a720a5d
ACR-ddad29e9e24e4e12bb5845d74e8dadfa
ACR-1bbaadd312f14ca48d53e40d05898ab5
ACR-46b28e37009549d1a25512115b58e61b
ACR-5c124111efa54fd48788375c75f2d095
ACR-3e9f8ebbd73d4f72b453a9378a517b63
ACR-9156397ad6a8475dbcf474641d727a9f
ACR-ac15633796384ba4bd2b0b48dd7090e3
ACR-f431dd2cd478431f96c31882be4875ee
ACR-64647a2eb7aa492f87b5bb2c6f8e1587
ACR-f9b8f3f876504f10a55582ff9d9bc920
ACR-e31aa9be3f224791bca82c132811b761
ACR-2ac1eee9cda14e22ad04ef43110763e5
ACR-e244d85027004678bf3c44568093e856
ACR-2c569ec25dcd4671b6bc20de2c414afb
 */
package org.sonarsource.sonarlint.core.telemetry;

/*ACR-7bc70c67870d43398d6644e7c4e3f34a
ACR-a8d8025c1e1648afbbf2f82c24421df1
ACR-afc8b59756644575b0485f0611f1cd5a
ACR-166d20b5b0cb415aaba63c74ab150d69
 */
public class InternalDebug {

  static final String INTERNAL_DEBUG_ENV = "SONARLINT_INTERNAL_DEBUG";

  private static boolean isEnabled = "true".equals(System.getenv(INTERNAL_DEBUG_ENV));

  private InternalDebug() {
    //ACR-c993b301f7aa46fe8f3679570d8ebcfa
  }

  public static boolean isEnabled() {
    return isEnabled;
  }

  //ACR-6a65c3762aa7465691d82d27ad9f0c1d
  public static void setEnabled(boolean isEnabled) {
    InternalDebug.isEnabled = isEnabled;
  }
}
