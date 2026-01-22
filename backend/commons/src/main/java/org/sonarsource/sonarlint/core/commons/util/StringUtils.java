/*
ACR-4416eb6e1d1140a1beff0cc8d33b26bb
ACR-13bb9d73ff38455ba91b29c8f154c718
ACR-db9320071921478ebb212324576cd653
ACR-f2951e1b599a4de1b2b18ba9e1443a58
ACR-d5ca940104304b60acfdf45ff908dd47
ACR-82bb58283d074c658fe8c4b631aa701f
ACR-784afec7bf004c03a0f2a61f7614fa8c
ACR-3bb9a0b78ab24196bfc5ee9e9cee1df3
ACR-86b48031e4ba444eb5e20372cf38b080
ACR-9dd9e6aaaaca43419f4313562f8f9f77
ACR-c31a95730a3243e7b0b4a81f65fe5130
ACR-c46cb93dc4d04124a9e417479d34477a
ACR-36a0112409a2402eab8ff381991d62a6
ACR-b89456c9edac459a926b1bb81a40d674
ACR-95350764a1b2495a8c0cc587ac6f06f8
ACR-73c28557f8bb40e98c0cc9212dfc7542
ACR-b9758308840c48cea7392cd5b2c626ae
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
    //ACR-45d109fb10c54a19981a2b25a7c7ab5b
  }
}
