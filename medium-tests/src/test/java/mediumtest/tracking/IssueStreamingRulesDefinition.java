/*
ACR-47e1016ef89641049ef52ddef0396eef
ACR-f6a6b5bd9f9748e1a95071aa702e75d6
ACR-21a12c602b7a4454b7d431bb1b61e6b2
ACR-7e78dfcc7a1a42f5b2fd353ea96e1a1b
ACR-a62fc5f041f64d3c9489a431a13f0a04
ACR-72af5ddab0ea479b94274b6b215521a2
ACR-e5b8d0702a00473d8b1b966814791148
ACR-fb16e71341db497a875cc29c2139d93b
ACR-a8940663aa4d492ca3c9caf435b94752
ACR-4cb77188593b466fb27a335346a16237
ACR-32a3a439c05f43629fc0088507c3270b
ACR-0fc41c2e5cca4abcb87813afed221704
ACR-2bd5de17b58a49f69a02de27e2584265
ACR-d5397427905f40c5bd9e3f9bfa8e0aa2
ACR-ca15ac9bb2854a999eb19fdfcae66e56
ACR-6665de6678a147f5a46532ff0b4c569a
ACR-10879a2196b74a64bcabd878a243f203
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
