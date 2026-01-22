/*
ACR-ac56cbdf84774a5eaacfa75dd60c743d
ACR-7b5bf3261880475282e4ab4c3e59b416
ACR-0fed275dda5043839439cd9d6863bf96
ACR-5a4e0b0fa9004342a10eac4e49ebd564
ACR-3161452856704aa18148cbe6ebacfbc6
ACR-3791817268c1422d949d2ac2966fa917
ACR-9ffad33f06244bc1a86b0cc561abb312
ACR-327e9547eee64c0d81f990d3616067ab
ACR-028e56013f7d4aa3ae8f244f1ad375ca
ACR-504c4485f6c14eb5833f04b3d2a06eec
ACR-89045cac13b64f01be40ac7057d48618
ACR-a35ce3ac60f84bc5a97473b8b9d589c9
ACR-34131335f5e84ca3a7ae5d2965ff8fa3
ACR-89503a0581194f68b644aa87816e834e
ACR-4f20959f678b485e9c24e0534daf9c6e
ACR-8eb2b752d3a9465abd367298763db8ea
ACR-b5465f94935640dc805980ee7ad3977d
 */
package org.sonarsource.sonarlint.core.plugin.commons.container;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

public interface ExtensionContainer extends Container {
  ExtensionContainer addExtension(@Nullable String pluginKey, Object extension);

  ExtensionContainer declareProperties(Object extension);

  @Override
  @CheckForNull
  ExtensionContainer getParent();
}
