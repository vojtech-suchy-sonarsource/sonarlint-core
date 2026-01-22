/*
ACR-a2c8be1f961c4a5db6c931bbade7b1b9
ACR-dc47d100a94542e5accb5ec783fb8041
ACR-8f3c25d407d140bc83864bb8d5e25d79
ACR-a85a700ff3fb42c8ace0863f7f854662
ACR-6910bedb889d47ea9738e69565226839
ACR-86153fa52e434968af2c6613d2878705
ACR-63151a077f014192ac6155e7eb26857a
ACR-47f771f29a5b420bb9de31d70ed97977
ACR-3cbbff6adb0e4c0f860236ba11a814f5
ACR-40a998bbeabe47f38658aafc67edd7f5
ACR-d116703b10c84050844773505f5741e6
ACR-30f8fe8f14cf4881901a0b9170bf1058
ACR-348f79cf0be14cb5b4404ac8dee81b0b
ACR-47c3784d7f7a4c21b114527d7ff204b2
ACR-95f3bf8221be429ea52498d3bee89c72
ACR-61f019fcb1914767b2eb054dead41bf8
ACR-a84956fbf95043b49c3dea38c76a3579
 */
package mediumtest.tracking;

import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RulesDefinition;

public class IssueStreamingRulesDefinition implements RulesDefinition {

  @Override
  public void define(Context context) {
    var repository = context.createRepository("repo", "java");
    repository.createRule("rule")
      .setType(RuleType.BUG)
      .setName("Rule")
      .setActivatedByDefault(true)
      .setHtmlDescription("desc");
    repository.done();
  }
}
