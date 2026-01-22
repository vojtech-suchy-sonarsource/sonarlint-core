/*
ACR-f3aa157c237441908bb98ad6766d9c67
ACR-98e13f5a6c5840f28f0352bae276834c
ACR-6198070f42f74a9080ef03ac179e135f
ACR-2c70af52f91e459d824db502017ca87b
ACR-af04d8fdab4c4048bb3f909372b5ed35
ACR-dce2886ff623436cbfa7923dbdf580a9
ACR-b7605f218e94436a917bd1116aaf2e68
ACR-c57fb4df0faf43f1bde51298a6a7281f
ACR-4f6bdd87c86f44f0ab32ba9a95a08110
ACR-8e7ede3752f14db4b35c0ba29ae8d1a9
ACR-01d895de49614791837bd98913326d10
ACR-a291703658914ccb80160f14b6f05806
ACR-32606c3c26cb45c48b6445a85ed32103
ACR-e5f1b9c3fcf44f7596d7458dc8f4577c
ACR-499d4961c42f4371b4f09cabd967e0fc
ACR-c0aacdb029a74cdea1852f91cd68cd2f
ACR-454cd1fe0efc46d3a44cb588c8becdbc
 */
package org.sonarsource.plugins.example;

import org.sonar.api.server.rule.RuleParamType;
import org.sonar.api.server.rule.RulesDefinition;

public final class GlobalRulesDefinition implements RulesDefinition {

  static final String RULE_KEY = "inc";
  static final String KEY = "global";
  static final String NAME = "Global";

  @Override
  public void define(Context context) {
    NewRepository repository = context.createRepository(KEY, GlobalLanguage.LANGUAGE_KEY).setName(NAME);
    NewRule rule = repository.createRule(RULE_KEY)
      .setActivatedByDefault(true)
      .setName("Increment")
      .setHtmlDescription("Increment message after every analysis");
    rule.createParam("stringParam")
      .setType(RuleParamType.STRING)
      .setName("String parameter")
      .setDescription("An example of string parameter");
    rule.createParam("textParam")
      .setType(RuleParamType.TEXT)
      .setDefaultValue("text\nparameter")
      .setName("Text parameter")
      .setDescription("An example of text parameter");
    rule.createParam("intParam")
      .setType(RuleParamType.INTEGER)
      .setDefaultValue("42")
      .setName("Int parameter")
      .setDescription("An example of int parameter");
    rule.createParam("boolParam")
      .setType(RuleParamType.BOOLEAN)
      .setDefaultValue("true")
      .setName("Boolean parameter")
      .setDescription("An example boolean parameter");
    rule.createParam("floatParam")
      .setType(RuleParamType.FLOAT)
      .setDefaultValue("3.14159265358")
      .setName("Float parameter")
      .setDescription("An example float parameter");
    rule.createParam("enumParam")
      .setType(RuleParamType.singleListOfValues("enum1", "enum2", "enum3"))
      .setDefaultValue("enum1")
      .setName("Enum parameter")
      .setDescription("An example enum parameter");
    rule.createParam("enumListParam")
      .setType(RuleParamType.multipleListOfValues("list1", "list2", "list3"))
      .setDefaultValue("list1,list2")
      .setName("Enum list parameter")
      .setDescription("An example enum list parameter");
    rule.createParam("multipleIntegersParam")
      .setType(RuleParamType.parse("INTEGER,multiple=true,values=\"80,120,160\""))
      .setName("Enum list of integers")
      .setDescription("An example enum list of integers");

    repository.done();
  }

}
