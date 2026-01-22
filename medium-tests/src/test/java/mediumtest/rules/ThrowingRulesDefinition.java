/*
ACR-be2b6c25a14d4475ab1d19c76d31c6c3
ACR-bc62d84efb4e4112ad777a960998d263
ACR-c3988e6435e54aad844e1772905a84b7
ACR-fe859f2351184dadb24892254955c649
ACR-95cc9eaf4f0646e8bcfe74c606cc9de3
ACR-1206853d40ca4a02a0c1168be1ddbada
ACR-464f5c6cc44a492f8f4840848651d43f
ACR-f482600657cf4e47ae1948ef7c3edca7
ACR-3bc1f2cf975640fe9f774980b54e0d28
ACR-6c4450024c2a415ca93924131f2bd33a
ACR-897f4d72617941eb90ad7ec7a87afed6
ACR-24f10d211f554fad8178e3b9181d8daf
ACR-4879c2eaf2224c1d84e324dea0c1f530
ACR-828158760cdc426d871f8fc8923baf1b
ACR-11147746a9fe4790bddb0781eee2c9bc
ACR-618a76fd212a40c2a8b1ca58e3a2223f
ACR-d4d8fc101a7943fe89d35cd29fc7c89b
 */
package mediumtest.rules;

import org.sonar.api.server.rule.RulesDefinition;

class ThrowingRulesDefinition implements RulesDefinition {
  @Override
  public void define(Context ignored) {
    throw new IllegalStateException("Nope, not gonna happen");
  }
}
