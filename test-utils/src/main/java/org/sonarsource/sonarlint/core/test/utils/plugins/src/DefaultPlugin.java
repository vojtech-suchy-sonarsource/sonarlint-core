/*
ACR-09bdde12d4714295b7bf52ff7087ce0f
ACR-8e0963cae40840f6a59390cd11354259
ACR-4f12bfee956944b58a1f574dde2110a0
ACR-d3ff5bb990624755a94107c79476a228
ACR-59326cc9d62542f996e6a83ef0dc1f18
ACR-6a6e15e892f0488ab44f00b7ce8730f3
ACR-6629e36f43ee4c76b10a1c242728db9a
ACR-9230f9dc338a466a9ac5450cfb010bef
ACR-b662d4ec2b2146cca1131d66f17f65e2
ACR-7e1fdd84966147b7994183c6cbaf4869
ACR-d4cae4bd4d104d11a72b042447aa2049
ACR-537d5bb11f194cb68c01f7d3d601f61f
ACR-8367755dcc87465d9ace0ef64b75bfe7
ACR-f009b126312248b48e94d86a6dd41883
ACR-b58330848d204e22b63f1ab6778e83bd
ACR-426675339d0e446582a3045afb9b2b42
ACR-e7f86f31f82242dfaa5703e8da744b61
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
