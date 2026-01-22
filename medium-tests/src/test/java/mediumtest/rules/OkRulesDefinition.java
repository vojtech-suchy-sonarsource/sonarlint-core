/*
ACR-acd9af22e0bd48988d6f7d5e9a52ba7d
ACR-fc812c28147d4189b39bd12003eee92a
ACR-3115330ba6f24f79b93144c02b019cb2
ACR-2dd4f1a0f8cf451fab898908ef921476
ACR-3d229f95e9ca4e71a9cc3082ace945d3
ACR-e5aa4b07c4014197a3a186ca1a7ef379
ACR-76c0416540b348288077a9d16a2ea194
ACR-7ae04badd06d4abb8c3c8055cbcf4043
ACR-e3fe6c843a5e44638e4a76c0dc1cc25d
ACR-60679e16fc4249fd86713a9287f78e52
ACR-29e0b5ae7f014a35a8197cc030b7f3b6
ACR-4ecc5b90250d4160b3f271135a6913c6
ACR-9c433e51c8d64656b80b3aa2db0fb9d6
ACR-d6d25101f37a4503884ca81f5dd15637
ACR-ed26e01fd9be4ef8bfe0596167ed7536
ACR-878748353ccc46619c1a1bdd4017340f
ACR-f45b1a8e43474c3db46de023ebb81b75
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
