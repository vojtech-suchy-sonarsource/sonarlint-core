/*
ACR-859e5893d7104e179f6e950ac8d5bed4
ACR-6cb10c7624b843989d410c2e79aea0f3
ACR-0f8522506d764fba9ca6aa0d2beb633c
ACR-cd6fd5cecbb541f8a5eca8801ba2b9e8
ACR-fd22aadb9df24aecb477aae7c2313135
ACR-f06a326df2f04075ab5214873ec3aaa7
ACR-ca0feaf15020420bb75b00b3ec387b0a
ACR-255f50b3fafd4ffb977b47a009af1d92
ACR-22b5e42226fc44e19841131faf049697
ACR-29714481d8fd4703a0ebcf0f87bae4fe
ACR-0c06b484f4e74071a81e9d35a044f6dd
ACR-e40d5de00cf747e9863d6112a5433895
ACR-6b0a159b068e4942a22d9accbdadde89
ACR-34688ae0130e4bf28d64e6a3561e8bb0
ACR-c0590e0bba8b48808c53e42f3f05cdbf
ACR-e330da67ea654cf78568cb3f7c8c673e
ACR-8d27def7e7854173a49f071b6ab7bcf5
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.rules;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

public class EffectiveRuleParamDto {
  private final String name;
  private final String description;
  private final String value;
  private final String defaultValue;

  public EffectiveRuleParamDto(String name, String description, @Nullable String value, @Nullable String defaultValue) {
    this.name = name;
    this.description = description;
    this.value = value;
    this.defaultValue = defaultValue;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  /*ACR-8f55733413cd48009fd3a7925da38573
ACR-19e8f479390a4b05b6fe5b53f076a0b7
   */
  @CheckForNull
  public String getValue() {
    return value;
  }

  /*ACR-fdffc12ce01846fbab49f7b8be88ca43
ACR-e5719fd11b8b47cfa9b62555ec9112a4
   */
  @CheckForNull
  public String getDefaultValue() {
    return defaultValue;
  }
}
