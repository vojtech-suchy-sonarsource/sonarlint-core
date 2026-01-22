/*
ACR-d3c37cc1623e48aab061345160981baa
ACR-4b5a485b78ee436e89f40d49c1fc8d63
ACR-afdee759b09340acae9d44854b4e7b6e
ACR-fe29e1e305944f03a7f620570c968bbc
ACR-3c931ad8a76643a3b956bee74b32dae5
ACR-5a5e4f0f3c464dfe82037badb4b88a7a
ACR-cc324d34d4884b919d9f423fea886434
ACR-46c7c007cada4743a49d45d1d4261011
ACR-56ee49e91b594ea0ad91fff7a6379e4a
ACR-138387a7f3f1427798583b8c4357998a
ACR-3bb279d29ec74a7aaffba8ae30543136
ACR-c15be3190405407da066f5dd940b8dd2
ACR-aaf3472edf9b4c60a8b85c6716a22ddb
ACR-2f819904757f41d0b45ce6dcca17c2bf
ACR-768d4a08fd3041e095da1fa2588f39d1
ACR-e35d15ccc780462f8a8892dcf58263cb
ACR-7736f36ec10b4251b19004d93edd79cf
 */
package mediumtest.rules;

import org.sonar.api.server.rule.RulesDefinition;

class ThrowingRulesDefinition implements RulesDefinition {
  @Override
  public void define(Context ignored) {
    throw new IllegalStateException("Nope, not gonna happen");
  }
}
