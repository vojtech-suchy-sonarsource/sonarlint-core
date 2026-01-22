/*
ACR-9e1f6a95472d464983ed651ae614e8d3
ACR-06f0e30ee53f46e7bea6ab791e1b09ae
ACR-9bbd7dc8112a4412ae7e0f4d8fa814c6
ACR-3bd53026ff1040dc8a9d737c83f1caa3
ACR-6e2c14190dfb42aa86a34cfa84b330df
ACR-a32164b2a01544a08364fd4cae97a761
ACR-242e9c41f70d4bb3bc1a5d23c8eddfd1
ACR-7172184592d045b88d2a93f9fbd66bfb
ACR-0e653f09b0b1432684147107b1c46d7f
ACR-978589b5c0b0430793755b2c5ab29374
ACR-9da64d2cfb7648e6b0f67b05791ac886
ACR-f5d4e403e693440d89b63b74ea762b48
ACR-1cdc6a65761447d699f44c3b84728bf3
ACR-2ffe1137c2a04a56ab5fec45b0f8be74
ACR-49362ed2d8944d11825004e070508d21
ACR-638d9a73d1de42e0b3c27a80e211dd8e
ACR-cecd09564d0d430aba43b1a81f16f185
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
