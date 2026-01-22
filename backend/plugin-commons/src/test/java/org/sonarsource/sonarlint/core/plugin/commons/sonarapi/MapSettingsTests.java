/*
ACR-497b1fb0d12b4f5882f90a2f47f1a619
ACR-0c8f90e276904fada318c2c790efacfe
ACR-4e938ec753144ae3b586a72c62e39ced
ACR-33927d25e9fb451280ae7052d7d629a2
ACR-975d311be2e84b158b2017ae4f5b7240
ACR-8a00ee4af0f54e0da21eac57e96e4c0c
ACR-ac17e4bb707949efbe510692c5f77f36
ACR-680f4940527442cd9a44f2d95b2df07d
ACR-661b3b97928641339605bdda02422670
ACR-6a5be193d4634df1a063ebbae0706911
ACR-c2f71574854a494c9fcc18c147286dca
ACR-69fcd9d6fac64b30ae3b2b183427244a
ACR-a89ba21b5a494d0092cb2e356c4f5272
ACR-1971f8d1bf6a4c829e71581eb7923fd5
ACR-f8ee2c593c60413fa5b92d1afa060e5b
ACR-a1582c117ace43c9a0cc15decbd664ba
ACR-32b768b59e674cfd9bd333a90eb99a58
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
      //ACR-e5ae3944789a4642b292e0b6b1625abe
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

    //ACR-636c356ab6a94cc4bcd6d6fde18d7afe
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
    //ACR-0acc898cc6324ae99f67810c2c4a4373
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
