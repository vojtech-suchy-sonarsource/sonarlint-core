/*
ACR-7de20566330f40f5b52479ea123d687b
ACR-f23101f8dd4f4788ae48020f66fd3d1f
ACR-00a35da784d64102bc3a0c6cd6f3c29e
ACR-1baa4a59051548fdb52133310d842ccc
ACR-a7b780bb7e174e2db53a80a35b85f66f
ACR-3b6cf5f4c2f34197b52ef64c1804e4ef
ACR-d164e056b80f47e299811994a41debf3
ACR-ccc4d086ecb14e949e94f11060dda6a0
ACR-c1f732a4c4274be4af2657230c5bcc7b
ACR-c08c084ad5d34bd7b055fcdbd6657caa
ACR-10375720153540ba8fe0b1962fe3b5d1
ACR-87498e70cfb440928fff878e1540b9ee
ACR-3356cc8abc6041f78cf56a45a715326e
ACR-ffd96c24527149ffadbfbd72eb1da59d
ACR-642a7ff2e81b4a8c86522e2690379c19
ACR-e9db5082520341e297717791db75e99a
ACR-283393e2068f4fb08ddb5f2fb4ff233d
 */
package org.sonar.samples.java;

import org.sonar.api.Plugin;

/*ACR-f5d1a4d4e8404cec96d444cbc8f690d3
ACR-a363bb4186e44f5d953fd94a1900a9c7
 */
public class MyJavaRulesPlugin implements Plugin {

  @Override
  public void define(Context context) {
    //ACR-e99db27932e442b1a3d36ddfad089abf
    context.addExtension(MyJavaRulesDefinition.class);

    //ACR-1cd86f052e4f44fbae1e343997952e27
    context.addExtension(MyJavaFileCheckRegistrar.class);

  }

}
