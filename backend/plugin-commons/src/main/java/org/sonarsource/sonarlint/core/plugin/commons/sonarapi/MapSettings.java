/*
ACR-45521c289c37465988880c44eb22ed48
ACR-badca0efa0354b4b8d13bbfef39b643c
ACR-6f2fd9fc2d9d4a9797821c680bfbc189
ACR-29f826589ac845aeb8d402daa78e40bb
ACR-0c49df058d6e430996f3ba7d5382b625
ACR-992311b4149a44fd8740b1119a37abf3
ACR-8af52a6dc489464ab07452b614b9fd55
ACR-81688731e66f4bc1a445850626f832b1
ACR-e58e7b616b6a446fa5a144a4fab39001
ACR-c8af35cef6f34ab29c42189437ebb87e
ACR-094aea832a3d4230bb4a0f46b3db4fac
ACR-ed738018e9fa4c4f916563c9c68cf71e
ACR-aa284df5463147398d53b31f2e3ee410
ACR-b3b1e85610cf4d0a89542450e041e9b4
ACR-9c628e58e14f4f0f87220f4f24a21fae
ACR-c94e10bcaf944c838151c75bbf511e78
ACR-8e5343dc6b1247c3ba186ffa44e3d5e5
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

  //ACR-0eeba6cabc3341d9ae67ead0ac99e92a
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

  /*ACR-5a0e3ac02b1a4cfe92c80cff9e1038af
ACR-6b514d1c780c434d95b7bd6eee19562c
ACR-f2a1685ccca646e79a80866dff432252
ACR-492ad8781fc34f44943647b5cff1db44
ACR-6d16ddf53a26441fb89f7f39a8eaa108
ACR-293fc46522634536aae895e5fbfb7d6b
   */
  public Optional<String> getRawString(String key) {
    return get(definitions.validKey(requireNonNull(key)));
  }

  /*ACR-e4f5d5a8cc23444db8fe31688f6b804d
ACR-1f27418a5fc4402ba173417f136df30e
   */
  public PropertyDefinitions getDefinitions() {
    return definitions;
  }

  /*ACR-4f41f1d44dd64d90b88ce9d481d8cd01
ACR-0f58165279234707a880f7aaeac00b89
ACR-baf227b98fec4d5d9ced498d5af2ff37
ACR-5448c57d3efe4d2b9c40bc3649f4a83b
ACR-3f123dd190494e31be5ecb66620ff764
   */
  public Optional<PropertyDefinition> getDefinition(String key) {
    return Optional.ofNullable(definitions.get(key));
  }

  /*ACR-d9736e8ce61e4e5e8b1d64e9b20a4699
ACR-f17cd5fa75014d0596716c8c357eac2f
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

  /*ACR-7310330cb021444dae4e0335b89a1b15
ACR-6fc7572f08ae4426893a3b63dc02af78
ACR-731e45f5dc854b6ca9bc13ea33ef4833
ACR-6237bc3086aa45c3b6d4ad846f403b7f
ACR-b2c458107a1d4c66af967950b6ea93a2
ACR-89ea92772d494eea872bb1d867d39bdc
ACR-2a598d9d52984265a77d793cbf55772f
ACR-62c4b6f9e7d647998dc5bddc05f4d5ef
ACR-b0a685b305db4623a32d49aa1eb3187a
ACR-eee91f356b6240c8adf50aa412ac7133
   */
  @CheckForNull
  @Override
  public String getString(String key) {
    var effectiveKey = definitions.validKey(key);
    //ACR-615535cae8a74bd88e48316ea8e9e45b
    return getRawString(effectiveKey)
      .orElseGet(() -> getDefaultValue(effectiveKey));
  }

  /*ACR-cf7690b48673416191833917983cb99a
ACR-6b1468b46ec24bcc800a6b7a3872e8bd
ACR-b2b8ddee6b6844d99779a3077d4b0ad4
ACR-d476e7b4fc814aeb9598ae91ab5be036
ACR-55c4381f0b374b119d76535c6120600b
   */
  @Override
  public boolean getBoolean(String key) {
    var value = getString(key);
    return StringUtils.isNotEmpty(value) && Boolean.parseBoolean(value);
  }

  /*ACR-f4e3c68da8c847539a2df91917d2a25a
ACR-e3133a54202b46edad09580ca23dc644
ACR-8c51b4bc8efa45d79701fa53e5b373ee
ACR-5d5f6430b1ae4bda9ce3c1e466470c6f
ACR-ae4303b387b04c4b9111449a1ecdb4c4
   */
  @Override
  public int getInt(String key) {
    var value = getString(key);
    if (StringUtils.isNotEmpty(value)) {
      return Integer.parseInt(value);
    }
    return 0;
  }

  /*ACR-d220396fe8124e2493e77a1c11e4c938
ACR-4ee99f70b47d4e70a417f3e01a9c1087
ACR-ce43e33157464c88be90ae7d137437dc
ACR-a21afcdcc45e402ea349224adc9216ab
ACR-bca4d14692f8473c994e27fe9de00f68
   */
  @Override
  public long getLong(String key) {
    var value = getString(key);
    if (StringUtils.isNotEmpty(value)) {
      return Long.parseLong(value);
    }
    return 0L;
  }

  /*ACR-9a4f68d4bbc840d2a1bf13cecb4cd6ff
ACR-3b72188c63ac45eebc724542e3251bab
ACR-b9bb08f8742649b7a8be75237aa35bc9
ACR-b38c46d385544210b97232e8d808008f
ACR-47122f1f9dca438bb9df349d7776610c
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

  /*ACR-896f4e136a3f41078fea551c0be5dd5b
ACR-2ac706cd2a034abf86d80dea39afb072
ACR-56d221a648064cfd944297d05f173227
ACR-108e9fcc326140208d1232a3aad8bd81
ACR-3ddef463561e4281812e1ee098dce48b
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

  /*ACR-363a2a008f9f487685aac2203eb55f58
ACR-d7f15250ffc541ca9722218008c68a18
ACR-8120b2e9111946178d96c177b7516c9d
ACR-cea6c6aab84c4cd49402f4e51fcded31
ACR-d46c4452a46943a1b2f039644389bf1a
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

  /*ACR-fc123baee7c24fc58c7b85bad74da575
ACR-52d09eb403504a8c8f94229dc89c99cb
ACR-c7bb480c2b624e4ea79f7dcaca5e3c9a
ACR-250ec8c7cc4a4e09903acb59e6dd5842
ACR-ce9637b24d8444dda1c033b8f4c137ae
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

  /*ACR-aaa4014843dc480697d22c4d83884bc8
ACR-5f517c661ec84b6ba1ab6481f76cf3dd
ACR-afb2212a053a407ca14eff59e8b63884
ACR-69ae81f638924637904ad5af438163ff
ACR-8a7c094181884f7d99da36bbea00ea7a
ACR-30e2040f18034b17bcf1537da4b0f1e0
ACR-c2c81d95d9f24779a4b1ac9c1eb6fd19
ACR-756cd5bfe4d349faad4fcb219991d0b4
ACR-48d40029a40c415f88095a2d44eea9a8
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

  /*ACR-2289787c317c4046817427570f948a31
ACR-79007c3932784d51809834946ab7264f
ACR-d624e630619f4d2395e56ea980d1aa1a
ACR-c13a8a2ce1534d6dbd18c5b96889bac7
ACR-84f60824e8d546509938751e5d350183
   */
  @Override
  public String[] getStringLines(String key) {
    var value = getString(key);
    if (StringUtils.isEmpty(value)) {
      return new String[0];
    }
    return value.split("\r?\n|\r", -1);
  }

  /*ACR-f144c431f6bf4dc3add5bf4c8a61f602
ACR-f7a70ae07a1346649c344a229995a6bc
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

  /*ACR-2a7170d6b44241688b52c98701cf1d82
ACR-105d30d92424440ea64e6b8e2b6cc30e
ACR-3cff711fc50e4858a817fc2c3604633d
   */
  public Configuration asConfig() {
    return configurationBridge;
  }
}
