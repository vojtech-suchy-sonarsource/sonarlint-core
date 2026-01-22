/*
ACR-502b68329e3742e98ac2af2b4632435f
ACR-7ed0f1fdefe34a97a306a613691f88f4
ACR-eef79e2ed8c34c13a20ff5b4efbc06a5
ACR-04bf4853380c4d7a957c53db7b70df4a
ACR-60ba90fe090b4a0786c66f26a46c169e
ACR-8d746b1e638a4095bad64f5dad29b44f
ACR-bc3decb6629347618a9b8a5205144a0b
ACR-894a9f12d1f94b6c82992eb2b18cd6c2
ACR-0d2f3e2b79284ae3accef0c428f2d37e
ACR-954cfede277843728556a792ca8ad3b6
ACR-c763f867988a488e8a9b8b30432c6e54
ACR-65338037979f402a8ce770e867b93cbd
ACR-a242ade85dc1438f9934f2e053a9d456
ACR-bc0888ab3a16413799f265d20ad0cf4a
ACR-7612e9da7b5842c0a1799db193181bd4
ACR-a2e25bf730844abda2811b55f8c64247
ACR-9de44e1806c54aefaae3e3de872c96b7
 */
package mediumtest.rules;

import org.sonar.api.server.rule.RulesDefinition;

class OkRulesDefinition implements RulesDefinition {
  @Override
  public void define(Context context) {
    var repo = context.createRepository("ok-rules", "php");
    repo.createRule("S001")
      .setName("This rule is OK")
      .setHtmlDescription("This rule is OK");
    repo.done();
  }
}
