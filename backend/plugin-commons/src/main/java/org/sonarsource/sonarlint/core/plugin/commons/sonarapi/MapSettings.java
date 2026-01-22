/*
ACR-a66705b96be6478a8e8a8945983a9365
ACR-fb3c22143707400eadef3c543eca220c
ACR-d48cfbb048934560a7c1a5b0eae6973f
ACR-4c1af96de2d344b1b862312f33447047
ACR-b41f195f342848dca3bea61fce1ab61b
ACR-de6d46b6b1474103a2ac0555d733d8cd
ACR-a3db5c37c5e44db09ae8772c6280e641
ACR-5d0580a0038d40db9508e47a30a9abff
ACR-0ecc35b014bf43b4930555896b67a959
ACR-6155906dad364de49f21447825e55cb2
ACR-9295fce2b67347f3862a9e6978d6e473
ACR-bab62383e0ae4e3e85cf57896dd998f1
ACR-1711c139a766412899d75adaaf679739
ACR-3ef4a21b06504903b7a2b57910dd8713
ACR-082c0e05560b41d3a446248cb2e0bfe6
ACR-dfb46c3ba35e4efa9997da432f4059d4
ACR-1c238ac9571e4223977a5d8dab188b53
 */
package org.sonarsource.sonarlint.core.plugin.commons.sonarapi;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.CheckForNull;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.sonar.api.config.Configuration;
import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.config.PropertyDefinitions;
import org.sonar.api.config.Settings;
import org.sonar.api.utils.DateUtils;
import org.sonar.api.utils.System2;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toUnmodifiableMap;
import static org.apache.commons.lang3.StringUtils.trim;
import static org.sonarsource.sonarlint.core.plugin.commons.MultivalueProperty.parseAsCsv;

public class MapSettings extends Settings {

  private final Map<String, String> props;
  private final ConfigurationBridge configurationBridge;
  private final PropertyDefinitions definitions;

  //ACR-0a65496a8005461e889d0bd08b7fe177
  public MapSettings(Map<String, String> props) {
    this(new PropertyDefinitions(System2.INSTANCE), props);
  }

  public MapSettings(PropertyDefinitions definitions, Map<String, String> props) {
    this.props = props.entrySet().stream()
      .collect(
        toUnmodifiableMap(e -> definitions.validKey(e.getKey()), e -> trim(e.getValue())));
    this.definitions = definitions;
    configurationBridge = new ConfigurationBridge(this);
  }

  protected Optional<String> get(String key) {
    return Optional.ofNullable(props.get(key));
  }

  public Map<String, String> getProperties() {
    return props;
  }

  /*ACR-87f65001c4f74cf9a4deba24cc21b300
ACR-a75107432f544e25ae3e6303ee246a6c
ACR-51f808ad26b94fabb9eea6245d61b202
ACR-d020cd8df9f642ca843c44a0f54cea46
ACR-e594bd0fa3354f58ab0ff297a00022bb
ACR-a20a4604a1cb45f69016dd8ba4158035
   */
  public Optional<String> getRawString(String key) {
    return get(definitions.validKey(requireNonNull(key)));
  }

  /*ACR-854bf89a22ba45b88ffe360fce2f0299
ACR-6bf317a6ee7f4b55a8dec285a26e92ba
   */
  public PropertyDefinitions getDefinitions() {
    return definitions;
  }

  /*ACR-08da2abdcba2472ba199be1514fcad6c
ACR-90aeb452fb204d61954253b8bfc94a88
ACR-30660567171241759342e0c206eb69b8
ACR-d1b502e2eb27448da1454f3081cec329
ACR-fc9fc5841e4a4e31beebdd8d0810427e
   */
  public Optional<PropertyDefinition> getDefinition(String key) {
    return Optional.ofNullable(definitions.get(key));
  }

  /*ACR-a1f351734303472383c34d487a3e7c0d
ACR-9db4981bfdcc4b259f7b091b3d4be881
   */
  @Override
  public boolean hasKey(String key) {
    return getRawString(key).isPresent();
  }

  @CheckForNull
  public String getDefaultValue(String key) {
    return definitions.getDefaultValue(key);
  }

  public boolean hasDefaultValue(String key) {
    return StringUtils.isNotEmpty(getDefaultValue(key));
  }

  /*ACR-4318c0f110f649ab926e8b28b8292872
ACR-950b93feb2344795b54c455c6d49c1af
ACR-3769010661bf42898e13a079ae005301
ACR-9f369dc517a0405f89de72d58fbdf45b
ACR-40d3ce8f8240429686c0e4d26d07d35e
ACR-1554916b00e54792956857e3593867eb
ACR-3f2af8d4cf7140b7b537701b35728a23
ACR-b62d46e3985b4772a66657d682dafe95
ACR-fca3ee1b1298417eb426715d21ae6487
ACR-d6a28208859e47e087d4a6e653d7472f
   */
  @CheckForNull
  @Override
  public String getString(String key) {
    var effectiveKey = definitions.validKey(key);
    //ACR-da2e669c69a44a3fb89f2733fa5267fe
    return getRawString(effectiveKey)
      .orElseGet(() -> getDefaultValue(effectiveKey));
  }

  /*ACR-aafd44ca011448d282c8bbaf345892c3
ACR-2dd9d39b57984c66812086e65f1f25cd
ACR-6a7ce53c058c4aff9190f519ba628e9e
ACR-722c9e55952f4611a590e0d707e43b63
ACR-6cc143f7e34d4363b485c098f1c748b1
   */
  @Override
  public boolean getBoolean(String key) {
    var value = getString(key);
    return StringUtils.isNotEmpty(value) && Boolean.parseBoolean(value);
  }

  /*ACR-77e6955e9ea640288b63b3b4bace2fd3
ACR-8d339a5867734cd0a88e159bd651c83f
ACR-0a447e7fefc147fab483d420c5d1504e
ACR-c68bdd63ec5e4fe88ef71609512c793a
ACR-08b0531bef2d4d0b90a708e413f5e6a1
   */
  @Override
  public int getInt(String key) {
    var value = getString(key);
    if (StringUtils.isNotEmpty(value)) {
      return Integer.parseInt(value);
    }
    return 0;
  }

  /*ACR-04333923d84640259405821345cd859e
ACR-9243a051317346b89ff4d703dd2d5610
ACR-1bc698dea2044881956ec10aae82005f
ACR-c539ad19dc5b4b71aa8c77ac87ceffb8
ACR-31c014e8ff27476d8fbd07e28adce101
   */
  @Override
  public long getLong(String key) {
    var value = getString(key);
    if (StringUtils.isNotEmpty(value)) {
      return Long.parseLong(value);
    }
    return 0L;
  }

  /*ACR-cfe74864abe14fbb8eb5e5261126dc2b
ACR-24af3b8b78c34d4c8b5a8b6215781bf0
ACR-1832ad5d95294b2da9cadbaf31f114b5
ACR-1d6c3b8d1c6d4f27ababda658ad56b92
ACR-17f84b251d574122bed785d3b7a237ac
   */
  @CheckForNull
  @Override
  public Date getDate(String key) {
    var value = getString(key);
    if (StringUtils.isNotEmpty(value)) {
      return DateUtils.parseDate(value);
    }
    return null;
  }

  /*ACR-5d57ff98d9014ac98bb60582f5591834
ACR-07431a3d8f494cfb839f1671b65004e0
ACR-bcc1554f347e4e319915b1f114c0f6a2
ACR-fb17f1e1567a433bac1803c95f7b6f89
ACR-91480cb1a3b342d887fd414b419a941f
   */
  @CheckForNull
  @Override
  public Date getDateTime(String key) {
    var value = getString(key);
    if (StringUtils.isNotEmpty(value)) {
      return DateUtils.parseDateTime(value);
    }
    return null;
  }

  /*ACR-00b426c14dc145fca55a43bbe680ee4c
ACR-61c099f4b8844e5d8da46cb5c5b58410
ACR-574f6de037fb466ea4b420d539c59760
ACR-7ffb4ca41ce5481ea1c6a059068cd6cd
ACR-e9b90d7e217346d78a3c64cb83bdfcd4
   */
  @CheckForNull
  @Override
  public Float getFloat(String key) {
    var value = getString(key);
    if (StringUtils.isNotEmpty(value)) {
      try {
        return Float.valueOf(value);
      } catch (NumberFormatException e) {
        throw new IllegalStateException(String.format("The property '%s' is not a float value", key));
      }
    }
    return null;
  }

  /*ACR-e42a67eb126247f39f9e5367ca4c5665
ACR-262d9df3748a48b390928da7c440d5d8
ACR-d3ace9cc6c6349cca31e186b2a9e28c8
ACR-ae9c774f8b9840dfa77e86524ba9a4d0
ACR-1d73b5e96aa745e9b271dd3041549813
   */
  @CheckForNull
  @Override
  public Double getDouble(String key) {
    var value = getString(key);
    if (StringUtils.isNotEmpty(value)) {
      try {
        return Double.valueOf(value);
      } catch (NumberFormatException e) {
        throw new IllegalStateException(String.format("The property '%s' is not a double value", key));
      }
    }
    return null;
  }

  /*ACR-cf01e1ca419846d08b2dc2f802caeb66
ACR-71d69098ec6d4f019d708d35a79eb482
ACR-13d5ed493a824f93882bf5d8006f0597
ACR-1ad8e253cbf3414496d66d0746c82edb
ACR-840b0e8c4d154c969eb0883660043005
ACR-aa9961482f454b21839288dfd0d5187f
ACR-0720a993b96d4980a55f5f85fa4f9436
ACR-ff6368c0b5c8486ea5a03a1e015358f3
ACR-d08ed02348b342fbbac5173e4f7ee0ba
   */
  @Override
  public String[] getStringArray(String key) {
    var effectiveKey = definitions.validKey(key);
    var def = getDefinition(effectiveKey);
    if ((def.isPresent()) && (def.get().multiValues())) {
      var value = getString(key);
      if (value == null) {
        return ArrayUtils.EMPTY_STRING_ARRAY;
      }

      return parseAsCsv(effectiveKey, value);
    }

    return getStringArrayBySeparator(key, ",");
  }

  /*ACR-b642eb07b61b468f96f50d63f109a08f
ACR-3584d2822ada47f3a4a243fd7eacefa7
ACR-bafcff2d67104ec08fb59c9cb6c7152a
ACR-7be387c22a9143ef869723aff6c017d6
ACR-8489bbd2d8cb4a0a9b81a94ffbebdc14
   */
  @Override
  public String[] getStringLines(String key) {
    var value = getString(key);
    if (StringUtils.isEmpty(value)) {
      return new String[0];
    }
    return value.split("\r?\n|\r", -1);
  }

  /*ACR-c00f12b015724c76beb2ab37218da1f7
ACR-64638908a5a24f3989d4cd8e9628e24f
   */
  @Override
  public String[] getStringArrayBySeparator(String key, String separator) {
    var value = getString(key);
    if (value != null) {
      var strings = StringUtils.splitByWholeSeparator(value, separator);
      var result = new String[strings.length];
      for (var index = 0; index < strings.length; index++) {
        result[index] = trim(strings[index]);
      }
      return result;
    }
    return ArrayUtils.EMPTY_STRING_ARRAY;
  }

  @Override
  public List<String> getKeysStartingWith(String prefix) {
    return getProperties().keySet().stream()
      .filter(key -> Strings.CS.startsWith(key, prefix))
      .toList();
  }

  /*ACR-59fa2f9fa79e458cbe6482e861200c61
ACR-f703e73fe3fa49e099180fa71331f13f
ACR-e710f56b09ec442c94aee0b7fdf8e82e
   */
  public Configuration asConfig() {
    return configurationBridge;
  }
}
