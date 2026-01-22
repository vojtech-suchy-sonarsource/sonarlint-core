/*
ACR-251c90ffacaa431a948bf2f0431c122f
ACR-7402df972b6d4c1db87a31f781e3fa98
ACR-51e9c22597124c17a4a15040ece119e3
ACR-362ea50d700b4ec1a0da43b2cda687ee
ACR-8123813bc13647e19263eafcc7183352
ACR-f9a9ba472e9f452ba7dda764a94910aa
ACR-2759371c638d440eb0eabd7ce6149d36
ACR-c136410e04b14d558ee70e4a6b9f106a
ACR-438f84d0b0a54147abe9c5e435176aa1
ACR-7c73810a903c442ca32303b5ba140fd7
ACR-74b43bcad3434fbab7244a9487b89006
ACR-6a6da36266a147078afe9b0f7d125244
ACR-0998666b9c264836b90e41b9df9e5889
ACR-6d32d22441574e3bac7915bf1c665db6
ACR-1d5106066daf4563a99e8b80d60b16d4
ACR-2e8bf3589b674a09bccc698d9607a97a
ACR-69ff401431e44036b0d77ec41ba884b7
 */
package org.sonarsource.sonarlint.core.plugin.commons;

import java.util.Random;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static java.util.function.UnaryOperator.identity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.sonarsource.sonarlint.core.plugin.commons.MultivalueProperty.parseAsCsv;
import static org.sonarsource.sonarlint.core.plugin.commons.MultivalueProperty.trimFieldsAndRemoveEmptyFields;
import static org.sonarsource.sonarlint.core.plugin.commons.Utils.randomAlphanumeric;

class MultivaluePropertyTests {
  private static final String[] EMPTY_STRING_ARRAY = {};

  @ParameterizedTest
  @MethodSource("testParseAsCsv")
  void parseAsCsv_for_coverage(String value, String[] expected) {
    assertThat(parseAsCsv("key", value))
      .isEqualTo(parseAsCsv("key", value, identity()))
      .isEqualTo(expected);
  }

  @Test
  void parseAsCsv_fails_with_ISE_if_value_can_not_be_parsed() {
    assertThatThrownBy(() -> parseAsCsv("multi", "\"a ,b"))
      .isInstanceOf(IllegalStateException.class)
      .hasMessage("Property: 'multi' doesn't contain a valid CSV value: '\"a ,b'");
  }

  public static Stream<Arguments> testParseAsCsv() {
    return Stream.of(
      Arguments.of("", EMPTY_STRING_ARRAY),
      Arguments.of("a", arrayOf("a")),
      Arguments.of(" a", arrayOf("a")),
      Arguments.of("a ", arrayOf("a")),
      Arguments.of(" a, b", arrayOf("a", "b")),
      Arguments.of("a,b ", arrayOf("a", "b")),
      Arguments.of("a,,,b,c,,d", arrayOf("a", "b", "c", "d")),
      Arguments.of("a,\n\tb,\n   c,\n   d\n", arrayOf("a", "b", "c", "d")),
      Arguments.of("a\n\tb\n   c,\n   d\n", arrayOf("a\nb\nc", "d")),
      Arguments.of("\na\n\tb\n   c,\n   d\n", arrayOf("a\nb\nc", "d")),
      Arguments.of("a,\n,\nb", arrayOf("a", "b")),
      Arguments.of(" , \n ,, \t", EMPTY_STRING_ARRAY),
      Arguments.of("\" a\"", arrayOf(" a")),
      Arguments.of("\",\"", arrayOf(",")),
      //ACR-03e29cb6631447b5a02c44c6fca3faf5
      Arguments.of("\"\"\"\"", arrayOf("\"")));
  }

  private static String[] arrayOf(String... strs) {
    return strs;
  }

  @Test
  void trimFieldsAndRemoveEmptyFields_throws_NPE_if_arg_is_null() {
    assertThatThrownBy(() -> trimFieldsAndRemoveEmptyFields(null))
      .isInstanceOf(NullPointerException.class);
  }

  @ParameterizedTest
  @MethodSource("plains")
  void trimFieldsAndRemoveEmptyFields_ignores_EmptyFields(String str) {
    assertThat(trimFieldsAndRemoveEmptyFields("")).isEmpty();
    assertThat(trimFieldsAndRemoveEmptyFields(str)).isEqualTo(str);

    assertThat(trimFieldsAndRemoveEmptyFields(',' + str)).isEqualTo(str);
    assertThat(trimFieldsAndRemoveEmptyFields(str + ',')).isEqualTo(str);
    assertThat(trimFieldsAndRemoveEmptyFields(",,," + str)).isEqualTo(str);
    assertThat(trimFieldsAndRemoveEmptyFields(str + ",,,")).isEqualTo(str);

    assertThat(trimFieldsAndRemoveEmptyFields(str + ',' + str)).isEqualTo(str + ',' + str);
    assertThat(trimFieldsAndRemoveEmptyFields(str + ",,," + str)).isEqualTo(str + ',' + str);
    assertThat(trimFieldsAndRemoveEmptyFields(',' + str + ',' + str)).isEqualTo(str + ',' + str);
    assertThat(trimFieldsAndRemoveEmptyFields("," + str + ",,," + str)).isEqualTo(str + ',' + str);
    assertThat(trimFieldsAndRemoveEmptyFields(",,," + str + ",,," + str)).isEqualTo(str + ',' + str);

    assertThat(trimFieldsAndRemoveEmptyFields(str + ',' + str + ',')).isEqualTo(str + ',' + str);
    assertThat(trimFieldsAndRemoveEmptyFields(str + ",,," + str + ",")).isEqualTo(str + ',' + str);
    assertThat(trimFieldsAndRemoveEmptyFields(str + ",,," + str + ",,")).isEqualTo(str + ',' + str);

    assertThat(trimFieldsAndRemoveEmptyFields(',' + str + ',' + str + ',')).isEqualTo(str + ',' + str);
    assertThat(trimFieldsAndRemoveEmptyFields(",," + str + ',' + str + ',')).isEqualTo(str + ',' + str);
    assertThat(trimFieldsAndRemoveEmptyFields(',' + str + ",," + str + ',')).isEqualTo(str + ',' + str);
    assertThat(trimFieldsAndRemoveEmptyFields(',' + str + ',' + str + ",,")).isEqualTo(str + ',' + str);
    assertThat(trimFieldsAndRemoveEmptyFields(",,," + str + ",,," + str + ",,")).isEqualTo(str + ',' + str);

    assertThat(trimFieldsAndRemoveEmptyFields(str + ',' + str + ',' + str)).isEqualTo(str + ',' + str + ',' + str);
    assertThat(trimFieldsAndRemoveEmptyFields(str + ',' + str + ',' + str)).isEqualTo(str + ',' + str + ',' + str);
  }

  public static Object[][] plains() {
    return new Object[][] {
      {randomAlphanumeric(1)},
      {randomAlphanumeric(2)},
      {randomAlphanumeric(3 + new Random().nextInt(5))}
    };
  }

  @ParameterizedTest
  @MethodSource("emptyAndtrimmable")
  void trimFieldsAndRemoveEmptyFields_ignores_empty_fields_and_trims_fields(String empty, String trimmable) {
    String expected = trimmable.trim();
    assertThat(empty.trim()).isEmpty();

    assertThat(trimFieldsAndRemoveEmptyFields(trimmable)).isEqualTo(expected);
    assertThat(trimFieldsAndRemoveEmptyFields(trimmable + ',' + empty)).isEqualTo(expected);
    assertThat(trimFieldsAndRemoveEmptyFields(trimmable + ",," + empty)).isEqualTo(expected);
    assertThat(trimFieldsAndRemoveEmptyFields(empty + ',' + trimmable)).isEqualTo(expected);
    assertThat(trimFieldsAndRemoveEmptyFields(empty + ",," + trimmable)).isEqualTo(expected);
    assertThat(trimFieldsAndRemoveEmptyFields(empty + ',' + trimmable + ',' + empty)).isEqualTo(expected);
    assertThat(trimFieldsAndRemoveEmptyFields(empty + ",," + trimmable + ",,," + empty)).isEqualTo(expected);

    assertThat(trimFieldsAndRemoveEmptyFields(trimmable + ',' + empty + ',' + empty)).isEqualTo(expected);
    assertThat(trimFieldsAndRemoveEmptyFields(trimmable + ",," + empty + ",,," + empty)).isEqualTo(expected);

    assertThat(trimFieldsAndRemoveEmptyFields(empty + ',' + empty + ',' + trimmable)).isEqualTo(expected);
    assertThat(trimFieldsAndRemoveEmptyFields(empty + ",,,," + empty + ",," + trimmable)).isEqualTo(expected);

    assertThat(trimFieldsAndRemoveEmptyFields(trimmable + ',' + trimmable)).isEqualTo(expected + ',' + expected);
    assertThat(trimFieldsAndRemoveEmptyFields(trimmable + ',' + trimmable + ',' + trimmable)).isEqualTo(expected + ',' + expected + ',' + expected);
    assertThat(trimFieldsAndRemoveEmptyFields(trimmable + "," + trimmable + ',' + trimmable)).isEqualTo(expected + ',' + expected + ',' + expected);
  }

  @Test
  void trimAccordingToStringTrim() {
    String str = randomAlphanumeric(4);
    for (int i = 0; i <= ' '; i++) {
      String prefixed = (char) i + str;
      String suffixed = (char) i + str;
      String both = (char) i + str + (char) i;
      assertThat(trimFieldsAndRemoveEmptyFields(prefixed)).isEqualTo(prefixed.trim());
      assertThat(trimFieldsAndRemoveEmptyFields(suffixed)).isEqualTo(suffixed.trim());
      assertThat(trimFieldsAndRemoveEmptyFields(both)).isEqualTo(both.trim());
    }
  }

  public static Object[][] emptyAndtrimmable() {
    Random random = new Random();
    String oneEmpty = randomTrimmedChars(1, random);
    String twoEmpty = randomTrimmedChars(2, random);
    String threePlusEmpty = randomTrimmedChars(3 + random.nextInt(5), random);
    String onePlusEmpty = randomTrimmedChars(1 + random.nextInt(5), random);

    String plain = randomAlphanumeric(1);
    String plainWithtrimmable = randomAlphanumeric(2) + onePlusEmpty + randomAlphanumeric(3);
    String quotedWithSeparator = '"' + randomAlphanumeric(3) + ',' + randomAlphanumeric(2) + '"';
    String quotedWithDoubleSeparator = '"' + randomAlphanumeric(3) + ",," + randomAlphanumeric(2) + '"';
    String quotedWithtrimmable = '"' + randomAlphanumeric(3) + onePlusEmpty + randomAlphanumeric(2) + '"';

    String[] empties = {oneEmpty, twoEmpty, threePlusEmpty};
    String[] strings = {plain, plainWithtrimmable,
      onePlusEmpty + plain, plain + onePlusEmpty, onePlusEmpty + plain + onePlusEmpty,
      onePlusEmpty + plainWithtrimmable, plainWithtrimmable + onePlusEmpty, onePlusEmpty + plainWithtrimmable + onePlusEmpty,
      onePlusEmpty + quotedWithSeparator, quotedWithSeparator + onePlusEmpty, onePlusEmpty + quotedWithSeparator + onePlusEmpty,
      onePlusEmpty + quotedWithDoubleSeparator, quotedWithDoubleSeparator + onePlusEmpty, onePlusEmpty + quotedWithDoubleSeparator + onePlusEmpty,
      onePlusEmpty + quotedWithtrimmable, quotedWithtrimmable + onePlusEmpty, onePlusEmpty + quotedWithtrimmable + onePlusEmpty
    };

    Object[][] res = new Object[empties.length * strings.length][2];
    int i = 0;
    for (String empty : empties) {
      for (String string : strings) {
        res[i][0] = empty;
        res[i][1] = string;
        i++;
      }
    }
    return res;
  }

  @ParameterizedTest
  @MethodSource("emptys")
  void trimFieldsAndRemoveEmptyFields_quotes_allow_to_preserve_fields(String empty) {
    String quotedEmpty = '"' + empty + '"';

    assertThat(trimFieldsAndRemoveEmptyFields(quotedEmpty)).isEqualTo(quotedEmpty);
    assertThat(trimFieldsAndRemoveEmptyFields(',' + quotedEmpty)).isEqualTo(quotedEmpty);
    assertThat(trimFieldsAndRemoveEmptyFields(quotedEmpty + ',')).isEqualTo(quotedEmpty);
    assertThat(trimFieldsAndRemoveEmptyFields(',' + quotedEmpty + ',')).isEqualTo(quotedEmpty);

    assertThat(trimFieldsAndRemoveEmptyFields(quotedEmpty + ',' + quotedEmpty)).isEqualTo(quotedEmpty + ',' + quotedEmpty);
    assertThat(trimFieldsAndRemoveEmptyFields(quotedEmpty + ",," + quotedEmpty)).isEqualTo(quotedEmpty + ',' + quotedEmpty);

    assertThat(trimFieldsAndRemoveEmptyFields(quotedEmpty + ',' + quotedEmpty + ',' + quotedEmpty)).isEqualTo(quotedEmpty + ',' + quotedEmpty + ',' + quotedEmpty);
  }

  public static Object[][] emptys() {
    Random random = new Random();
    return new Object[][] {
      {randomTrimmedChars(1, random)},
      {randomTrimmedChars(2, random)},
      {randomTrimmedChars(3 + random.nextInt(5), random)}
    };
  }

  @Test
  void trimFieldsAndRemoveEmptyFields_supports_escaped_quote_in_quotes() {
    assertThat(trimFieldsAndRemoveEmptyFields("\"f\"\"oo\"")).isEqualTo("\"f\"\"oo\"");
    assertThat(trimFieldsAndRemoveEmptyFields("\"f\"\"oo\",\"bar\"\"\"")).isEqualTo("\"f\"\"oo\",\"bar\"\"\"");
  }

  @Test
  void trimFieldsAndRemoveEmptyFields_does_not_fail_on_unbalanced_quotes() {
    assertThat(trimFieldsAndRemoveEmptyFields("\"")).isEqualTo("\"");
    assertThat(trimFieldsAndRemoveEmptyFields("\"foo")).isEqualTo("\"foo");
    assertThat(trimFieldsAndRemoveEmptyFields("foo\"")).isEqualTo("foo\"");

    assertThat(trimFieldsAndRemoveEmptyFields("\"foo\",\"")).isEqualTo("\"foo\",\"");
    assertThat(trimFieldsAndRemoveEmptyFields("\",\"foo\"")).isEqualTo("\",\"foo\"");

    assertThat(trimFieldsAndRemoveEmptyFields("\"foo\",\",  ")).isEqualTo("\"foo\",\",  ");

    assertThat(trimFieldsAndRemoveEmptyFields(" a ,,b , c,  \"foo\",\"  ")).isEqualTo("a,b,c,\"foo\",\"  ");
    assertThat(trimFieldsAndRemoveEmptyFields("\" a ,,b , c,  ")).isEqualTo("\" a ,,b , c,  ");
  }

  private static final char[] SOME_PRINTABLE_TRIMMABLE_CHARS = {
    ' ', '\t', '\n', '\r'
  };

  /*ACR-f10d2993f040475b8af38223007e5de1
ACR-b005ca532f95485a99be31533ce08de3
ACR-abd66aefa2bf4b32a48ccf376e38e090
ACR-6e7667e058364d30a43172bc9bf4b737
   */
  private static String randomTrimmedChars(int length, Random random) {
    char[] chars = new char[length];
    for (int i = 0; i < chars.length; i++) {
      chars[i] = SOME_PRINTABLE_TRIMMABLE_CHARS[random.nextInt(SOME_PRINTABLE_TRIMMABLE_CHARS.length)];
    }
    return new String(chars);
  }

}
