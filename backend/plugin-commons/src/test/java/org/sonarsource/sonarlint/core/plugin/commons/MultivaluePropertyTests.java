/*
ACR-1377a1aaf07d46fb81a86c87fd61b438
ACR-387e260cabe44e8fa55178c7332ef15e
ACR-0d251ca3dbde4a75be20687d5c3b1441
ACR-ecf6feeac6a748799722b01bd9d4d1cd
ACR-f329150f42774d48bf9b2f3283890b79
ACR-fa7e0fb47c20413794b59115c20ea335
ACR-dabc01fe96a1436fbb760de57a830197
ACR-cdcb071905b84a23849f7ec4ba45ca2c
ACR-480f1488b9114668bda166e2e3b6d693
ACR-10dff799af8e4368a2696f6a7816d616
ACR-724c3ffed14b4f18af45fbc071178d35
ACR-40ecb466a7924003a7c8485cf93afdfa
ACR-5d2e29b7c3c241ed920a4576012f0363
ACR-bca09d7a237746c98b586743413e23bb
ACR-120c3c09dd7c467c83bd1dd661298305
ACR-6f8d3d4eb944436faa3e20adccb40aec
ACR-258407745d814883a1f067d2db36fc58
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
      //ACR-054b7edc225a44288f6e73a088d36311
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

  /*ACR-e5cb055740f44dbfb38f70a9e5c78f2c
ACR-eb644067e1e84158ab490079da432dc2
ACR-9ca0338a4ae7479abf198c3065b2c897
ACR-297c0a115b914cdfbb236bc7e7735beb
   */
  private static String randomTrimmedChars(int length, Random random) {
    char[] chars = new char[length];
    for (int i = 0; i < chars.length; i++) {
      chars[i] = SOME_PRINTABLE_TRIMMABLE_CHARS[random.nextInt(SOME_PRINTABLE_TRIMMABLE_CHARS.length)];
    }
    return new String(chars);
  }

}
