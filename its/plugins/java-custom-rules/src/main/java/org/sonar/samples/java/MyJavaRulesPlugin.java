/*
ACR-eb126b4c4b33421c9d6565c51d47bd45
ACR-8dd3d0db45d74b058218e5d8f1125f45
ACR-8da0c0b482c543f3865ba33da91b1e82
ACR-2b3f408b4be94bebaf16d51bebb47761
ACR-1578e0d81a77426cbfc971ab164bbb38
ACR-c6b72bd7f9b04324896ed8039a87abdc
ACR-4e9d2aea30754c8c9a880171906555f6
ACR-beb40da75b174122a7776ec6257999ff
ACR-64e6af8478664ab48a8959f8ba8a878e
ACR-1c8ab8790b254c6ab2845f9708e35049
ACR-9c8a8cc3a88e4d2da5d033c346bda491
ACR-5c82225e19aa48d6a1fedc2f86864595
ACR-0023842516f94a739d4aafa1e039bf69
ACR-2003881c41c64bb29093e3df6ced8f9b
ACR-4ea9c32e66ba48d1b8fc3f1007a1985b
ACR-1b961771faae4a25b3af91fa0c591922
ACR-98f5728668cb4d8b9fa83c482d9c9d4b
 */
package org.sonar.samples.java;

import org.sonar.api.Plugin;

/*ACR-30d507b95bb3427dbf9e97685d7aa5ce
ACR-f8d4a7ad3fce4fc4bc1d402e7be203f8
 */
public class MyJavaRulesPlugin implements Plugin {

  @Override
  public void define(Context context) {
    //ACR-19a9b6ce278d4b388c3901b849dbbd42
    context.addExtension(MyJavaRulesDefinition.class);

    //ACR-f7887d49d75045cb87e649a0450312e6
    context.addExtension(MyJavaFileCheckRegistrar.class);

  }

}
