/*
ACR-35383470207643c6a54d25280cd12d49
ACR-ad6a6fe7c14e4a528aa50065c1e1a727
ACR-8a4a18a0eff6469eb56e689d1c6d93f7
ACR-88c5f79bbe7541a09047c6f3e5998e02
ACR-5f7613d09f3f4f86a9de857b8adeb793
ACR-69bfaa8dd510400b8aa392b18c26cc27
ACR-67e875f575d0424b95272ea58384920c
ACR-f897b68f2301439f963e6f856878b806
ACR-37782294847a40aaaf5d4d7c78da7266
ACR-1aa9e00fbc27452eb3a651ab7fa1d48e
ACR-429639d1170b41cf841b3ebc9ab411eb
ACR-195ca1b907ff4113a95912c3b5c2bbb2
ACR-0421f3d3bd76425d8205a57f3d3bee40
ACR-d12828c9996a4af4b9fbf0e8a42a75c2
ACR-c65c25454b4d43e196a19a2cb2ede1ba
ACR-a13a9ec70b8f43e1ae590ea292ca8afd
ACR-1a69fe638ff64f83b1ecb06347709144
 */
package org.sonarsource.sonarlint.core.plugin.commons.sonarapi;

import java.util.Map;
import java.util.Random;
import java.util.stream.IntStream;
import org.assertj.core.api.Assertions;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sonar.api.Properties;
import org.sonar.api.Property;
import org.sonar.api.PropertyType;
import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.config.PropertyDefinitions;
import org.sonar.api.utils.System2;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.sonarsource.sonarlint.core.plugin.commons.Utils.randomAlphanumeric;

class MapSettingsTests {

  private PropertyDefinitions definitions;

  @Properties({
    @Property(key = "hello", name = "Hello", defaultValue = "world"),
    @Property(key = "date", name = "Date", defaultValue = "2010-05-18"),
    @Property(key = "datetime", name = "DateTime", defaultValue = "2010-05-18T15:50:45+0100"),
    @Property(key = "boolean", name = "Boolean", defaultValue = "true"),
    @Property(key = "falseboolean", name = "False Boolean", defaultValue = "false"),
    @Property(key = "integer", name = "Integer", defaultValue = "12345"),
    @Property(key = "array", name = "Array", defaultValue = "one,two,three"),
    @Property(key = "multi_values", name = "Array", defaultValue = "1,2,3", multiValues = true),
    @Property(key = "sonar.jira", name = "Jira Server", type = PropertyType.PROPERTY_SET),
    @Property(key = "newKey", name = "New key", deprecatedKey = "oldKey"),
    @Property(key = "newKeyWithDefaultValue", name = "New key with default value", deprecatedKey = "oldKeyWithDefaultValue", defaultValue = "default_value"),
    @Property(key = "new_multi_values", name = "New multi values", defaultValue = "1,2,3", multiValues = true, deprecatedKey = "old_multi_values")
  })
  private static class Init {
  }

  @BeforeEach
  void init_definitions() {
    definitions = new PropertyDefinitions(System2.INSTANCE);
    definitions.addComponent(Init.class);
  }

  @Test
  void set_accepts_empty_value_and_trims_it() {
    var random = new Random();
    var key = randomAlphanumeric(3);

    var underTest = new MapSettings(Map.of(key, blank(random)));

    assertThat(underTest.getString(key)).isEmpty();
  }

  @Test
  void default_values_should_be_loaded_from_definitions() {
    var settings = new MapSettings(definitions, Map.of());
    assertThat(settings.getDefaultValue("hello")).isEqualTo("world");
  }

  @Test
  void set_property_string_array_trims_key() {
    var key = randomAlphanumeric(3);

    var random = new Random();
    var blankBefore = blank(random);
    var blankAfter = blank(random);

    var underTest = new MapSettings(new PropertyDefinitions(System2.INSTANCE, singletonList(PropertyDefinition.builder(key).multiValues(true).build())),
      Map.of(blankBefore + key + blankAfter, "1,2"));

    assertThat(underTest.hasKey(key)).isTrue();
  }

  private static String blank(Random random) {
    var b = new StringBuilder();
    IntStream.range(0, random.nextInt(3)).mapToObj(s -> " ").forEach(b::append);
    return b.toString();
  }

  @Test
  void setProperty_methods_trims_value() {
    var random = new Random();
    var blankBefore = blank(random);
    var blankAfter = blank(random);
    var key = randomAlphanumeric(3);
    var value = randomAlphanumeric(3);

    var underTest = new MapSettings(Map.of(key, blankBefore + value + blankAfter));

    assertThat(underTest.getString(key)).isEqualTo(value);
  }

  @Test
  void set_property_int() {
    var settings = new MapSettings(Map.of("foo", "123"));
    assertThat(settings.getInt("foo")).isEqualTo(123);
    assertThat(settings.getString("foo")).isEqualTo("123");
    assertThat(settings.getBoolean("foo")).isFalse();
  }

  @Test
  void default_number_values_are_zero() {
    var settings = new MapSettings(Map.of());
    assertThat(settings.getInt("foo")).isZero();
    assertThat(settings.getLong("foo")).isZero();
  }

  @Test
  void getInt_value_must_be_valid() {
    var settings = new MapSettings(Map.of("foo", "not a number"));

    assertThrows(NumberFormatException.class, () -> settings.getInt("foo"));
  }

  @Test
  void all_values_should_be_trimmed_set_property() {
    var settings = new MapSettings(Map.of("foo", "   FOO "));
    assertThat(settings.getString("foo")).isEqualTo("FOO");
  }

  @Test
  void test_get_default_value() {
    var settings = new MapSettings(definitions, Map.of());
    assertThat(settings.getDefaultValue("unknown")).isNull();
  }

  @Test
  void test_get_string() {
    var settings = new MapSettings(definitions, Map.of("hello", "Russia"));
    assertThat(settings.getString("hello")).isEqualTo("Russia");
  }

  @Test
  void test_get_date() {
    var settings = new MapSettings(definitions, Map.of());
    assertThat(settings.getDate("unknown")).isNull();
    assertThat(settings.getDate("date").getDate()).isEqualTo(18);
    assertThat(settings.getDate("date").getMonth()).isEqualTo(4);
  }

  @Test
  void test_get_date_not_found() {
    var settings = new MapSettings(definitions, Map.of());
    assertThat(settings.getDate("unknown")).isNull();
  }

  @Test
  void test_get_datetime() {
    var settings = new MapSettings(definitions, Map.of());
    assertThat(settings.getDateTime("unknown")).isNull();
    assertThat(settings.getDateTime("datetime").getDate()).isEqualTo(18);
    assertThat(settings.getDateTime("datetime").getMonth()).isEqualTo(4);
    assertThat(settings.getDateTime("datetime").getMinutes()).isEqualTo(50);
  }

  @Test
  void test_get_double() {
    var settings = new MapSettings(Map.of("from_string", "3.14159"));
    assertThat(settings.getDouble("from_string")).isEqualTo(3.14159, Offset.offset(0.00001));
    assertThat(settings.getDouble("unknown")).isNull();
  }

  @Test
  void test_get_float() {
    var settings = new MapSettings(Map.of("from_string", "3.14159"));
    assertThat(settings.getDouble("from_string")).isEqualTo(3.14159f, Offset.offset(0.00001));
    assertThat(settings.getDouble("unknown")).isNull();
  }

  @Test
  void test_get_bad_float() {
    var settings = new MapSettings(Map.of("foo", "bar"));

    var thrown = assertThrows(IllegalStateException.class, () -> settings.getFloat("foo"));
    assertThat(thrown).hasMessage("The property 'foo' is not a float value");
  }

  @Test
  void test_get_bad_double() {
    var settings = new MapSettings(Map.of("foo", "bar"));

    var thrown = assertThrows(IllegalStateException.class, () -> settings.getDouble("foo"));
    assertThat(thrown).hasMessage("The property 'foo' is not a double value");
  }

  @Test
  void getStringArray() {
    var settings = new MapSettings(definitions, Map.of());
    var array = settings.getStringArray("array");
    assertThat(array).isEqualTo(new String[] {"one", "two", "three"});
  }

  @Test
  void getStringArray_no_value() {
    var settings = new MapSettings(Map.of());
    var array = settings.getStringArray("array");
    assertThat(array).isEmpty();
  }

  @Test
  void shouldTrimArray() {
    var settings = new MapSettings(Map.of("foo", "  one,  two, three  "));
    var array = settings.getStringArray("foo");
    assertThat(array).isEqualTo(new String[] {"one", "two", "three"});
  }

  @Test
  void shouldKeepEmptyValuesWhenSplitting() {
    var settings = new MapSettings(Map.of("foo", "  one,  , two"));
    var array = settings.getStringArray("foo");
    assertThat(array).isEqualTo(new String[] {"one", "", "two"});
  }

  @Test
  void testDefaultValueOfGetString() {
    var settings = new MapSettings(definitions, Map.of());
    assertThat(settings.getString("hello")).isEqualTo("world");
  }

  @Test
  void set_property_boolean() {
    var settings = new MapSettings(Map.of("foo", "true", "bar", "false"));
    assertThat(settings.getBoolean("foo")).isTrue();
    assertThat(settings.getBoolean("bar")).isFalse();
    assertThat(settings.getString("foo")).isEqualTo("true");
    assertThat(settings.getString("bar")).isEqualTo("false");
  }

  @Test
  void ignore_case_of_boolean_values() {
    var settings = new MapSettings(Map.of("foo", "true", "bar", "TRUE",
      //ACR-5216f575e20f42a1bf34453401ff817c
      "baz", "True"));

    assertThat(settings.getBoolean("foo")).isTrue();
    assertThat(settings.getBoolean("bar")).isTrue();
    assertThat(settings.getBoolean("baz")).isTrue();
  }

  @Test
  void get_boolean() {
    var settings = new MapSettings(definitions, Map.of());
    assertThat(settings.getBoolean("boolean")).isTrue();
    assertThat(settings.getBoolean("falseboolean")).isFalse();
    assertThat(settings.getBoolean("unknown")).isFalse();
    assertThat(settings.getBoolean("hello")).isFalse();
  }

  @Test
  void shouldCreateByIntrospectingComponent() {
    var settings = new MapSettings(Map.of());
    settings.getDefinitions().addComponent(MyComponent.class);

    //ACR-a02b589b4ee045a9b82541fa50aae973
    assertThat(settings.getDefaultValue("foo")).isEqualTo("bar");
  }

  @Property(key = "foo", name = "Foo", defaultValue = "bar")
  public static class MyComponent {

  }

  @Test
  void getStringLines_no_value() {
    Assertions.assertThat(new MapSettings(Map.of()).getStringLines("foo")).isEmpty();
  }

  @Test
  void getStringLines_single_line() {
    var settings = new MapSettings(Map.of("foo", "the line"));
    assertThat(settings.getStringLines("foo")).isEqualTo(new String[] {"the line"});
  }

  @Test
  void getStringLines_linux() {
    var settings = new MapSettings(Map.of("foo", "one\ntwo"));
    assertThat(settings.getStringLines("foo")).isEqualTo(new String[] {"one", "two"});

    settings = new MapSettings(Map.of("foo", "one\ntwo\n"));
    assertThat(settings.getStringLines("foo")).isEqualTo(new String[] {"one", "two"});
  }

  @Test
  void getStringLines_windows() {
    var settings = new MapSettings(Map.of("foo", "one\r\ntwo"));
    assertThat(settings.getStringLines("foo")).isEqualTo(new String[] {"one", "two"});

    settings = new MapSettings(Map.of("foo", "one\r\ntwo\r\n"));
    assertThat(settings.getStringLines("foo")).isEqualTo(new String[] {"one", "two"});
  }

  @Test
  void getStringLines_mix() {
    var settings = new MapSettings(Map.of("foo", "one\r\ntwo\nthree"));
    assertThat(settings.getStringLines("foo")).isEqualTo(new String[] {"one", "two", "three"});
  }

  @Test
  void getKeysStartingWith() {
    var settings = new MapSettings(Map.of("sonar.jdbc.url", "foo", "sonar.jdbc.username", "bar", "sonar.security", "admin"));

    assertThat(settings.getKeysStartingWith("sonar")).containsOnly("sonar.jdbc.url", "sonar.jdbc.username", "sonar.security");
    assertThat(settings.getKeysStartingWith("sonar.jdbc")).containsOnly("sonar.jdbc.url", "sonar.jdbc.username");
    assertThat(settings.getKeysStartingWith("other")).isEmpty();
  }

  @Test
  void should_fallback_deprecated_key_to_default_value_of_new_key() {
    var settings = new MapSettings(definitions, Map.of());

    assertThat(settings.getString("newKeyWithDefaultValue")).isEqualTo("default_value");
    assertThat(settings.getString("oldKeyWithDefaultValue")).isEqualTo("default_value");
  }

  @Test
  void should_fallback_deprecated_key_to_new_key() {
    var settings = new MapSettings(definitions, Map.of("newKey", "value of newKey"));

    assertThat(settings.getString("newKey")).isEqualTo("value of newKey");
    assertThat(settings.getString("oldKey")).isEqualTo("value of newKey");
  }

  @Test
  void should_load_value_of_deprecated_key() {
    //ACR-0c55747f966d45a7974f4029d095d025
    var settings = new MapSettings(definitions, Map.of("oldKey", "value of oldKey"));

    assertThat(settings.getString("newKey")).isEqualTo("value of oldKey");
    assertThat(settings.getString("oldKey")).isEqualTo("value of oldKey");
  }

  @Test
  void should_load_values_of_deprecated_key() {
    var settings = new MapSettings(definitions, Map.of("oldKey", "a,b"));

    assertThat(settings.getStringArray("newKey")).containsOnly("a", "b");
    assertThat(settings.getStringArray("oldKey")).containsOnly("a", "b");
  }

  @Test
  void should_support_deprecated_props_with_multi_values() {
    var settings = new MapSettings(definitions, Map.of("new_multi_values", " A , B "));
    assertThat(settings.getStringArray("new_multi_values")).isEqualTo(new String[] {"A", "B"});
    assertThat(settings.getStringArray("old_multi_values")).isEqualTo(new String[] {"A", "B"});
  }

  @Test
  void testParsingMultiValues() {
    assertThat(getStringArray("")).isEmpty();
    assertThat(getStringArray(",")).isEmpty();
    assertThat(getStringArray(",,")).isEmpty();
    assertThat(getStringArray("a")).containsExactly("a");
    assertThat(getStringArray("a b")).containsExactly("a b");
    assertThat(getStringArray("a , b")).containsExactly("a", "b");
    assertThat(getStringArray("\"a \",\" b\"")).containsExactly("a ", " b");
    assertThat(getStringArray("\"a,b\",c")).containsExactly("a,b", "c");
    assertThat(getStringArray("\"a\nb\",c")).containsExactly("a\nb", "c");
    assertThat(getStringArray("\"a\",\n  b\n")).containsExactly("a", "b");
    assertThat(getStringArray("a\n,b\n")).containsExactly("a", "b");
    assertThat(getStringArray("a\n,b\n,\"\"")).containsExactly("a", "b", "");
    assertThat(getStringArray("a\n,  \"  \"  ,b\n")).containsExactly("a", "  ", "b");
    assertThat(getStringArray("  \" , ,, \", a\n,b\n")).containsExactly(" , ,, ", "a", "b");
    assertThat(getStringArray("a\n,,b\n")).containsExactly("a", "b");
    assertThat(getStringArray("a,\n\nb,c")).containsExactly("a", "b", "c");
    assertThat(getStringArray("a,b\n\nc,d")).containsExactly("a", "b\nc", "d");
    assertThat(getStringArray("a,\"\",b")).containsExactly("a", "", "b");

    var thrown = assertThrows(IllegalStateException.class, () -> getStringArray("\"a ,b"));
    assertThat(thrown).hasMessage("Property: 'multi_values' doesn't contain a valid CSV value: '\"a ,b'");
  }

  private String[] getStringArray(String value) {
    var settings = new MapSettings(definitions, Map.of("multi_values", value));
    return settings.getStringArray("multi_values");
  }
}
