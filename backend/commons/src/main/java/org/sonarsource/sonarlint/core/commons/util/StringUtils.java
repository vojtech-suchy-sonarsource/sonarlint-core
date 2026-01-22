/*
ACR-1023ff1592d44808ad9bdb938a1eb355
ACR-4cbc522a0c144864be28e3abbb2c8b65
ACR-ab8b8e402c6048c484a9ff0ee7f50012
ACR-e5398b35d66046218a8476d649b1f595
ACR-e9551136e2fa42c1b434da980d8bf90d
ACR-c2a41638aa3b46c69a3a3e4da90e0765
ACR-5b33ee463e214c00a7c8d1f83750685a
ACR-e2e657ad52f641629fcf68ddafbaf5a8
ACR-fbba6b404362448ab2bf27b531d09e56
ACR-396ab19570f7457fa1438326befe5107
ACR-208cf12cae43496f9fac804ab55584f7
ACR-530f3b204cd9481a96e9fc9ca35dc631
ACR-f16e5eea9ec041f689f63d4301880088
ACR-c3a1c75653d94487a8dd9e7cda2ea9ed
ACR-ae1441addf634c2e9ac522dc4e3c0989
ACR-62d04b0dc9b141839c1f742dea6e99e9
ACR-7dca8c854e704ec9a40062da2f6d7b7b
 */
package org.sonarsource.sonarlint.core.commons.util;

import javax.annotation.Nullable;

public class StringUtils {

  private static final char RTLO = '\u202E';

  public static String pluralize(long count, String word) {
    var pluralizedWord = count == 1 ? word : (word + 's');
    return count + " " + pluralizedWord;
  }

  public static String sanitizeAgainstRTLO(@Nullable String input) {
    if (input == null) {
      return null;
    }
    return input.replaceAll(String.valueOf(RTLO), "");
  }

  private StringUtils() {
    //ACR-e972e736e3a340779b182aa903fd5aa4
  }
}
