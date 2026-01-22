/*
ACR-713346156afd457d8d71be16f3ef0a3b
ACR-c1e8ea76bdae4c45a1ec54331ba71046
ACR-d42afac3b35145f890165f6b91fbb518
ACR-c25e8f9f42834ecea444a26ef706af26
ACR-572a59b8e5e446afb1f7044bd1a71fb9
ACR-13fbabd9c95e411da97b56682ac6483a
ACR-702ca071c3c84fd49a1fb5f825ad4468
ACR-61af57d1c3be4bdebf12b87ed0a38d31
ACR-229843ed59614a4e886b7f72ebf98aec
ACR-698e2a25853b41bca07fc67ff78579bb
ACR-39af541661794b3494ee5d33a0b62f81
ACR-f17ef6b1d10a4c3995dbc39476ded95b
ACR-a8494fff45894945bc73668f91330e2b
ACR-496dfeefeda3465fa39f02f6cb3bc330
ACR-84d5c68076f341dfaf5abed92773942e
ACR-c216661890e744568892e5486c4489c0
ACR-da5abc28e81d415d9fa191dedfaf596a
 */
package org.sonarsource.sonarlint.core.test.utils.plugins.src;

import org.sonar.api.Plugin;

public class DefaultPlugin implements Plugin {
  @Override
  public void define(Context context) {
    context.addExtension(DefaultRulesDefinition.class);
    context.addExtension(DefaultSensor.class);
  }
}
