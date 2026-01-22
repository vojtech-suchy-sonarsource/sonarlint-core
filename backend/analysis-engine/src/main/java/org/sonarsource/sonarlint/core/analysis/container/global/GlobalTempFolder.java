/*
ACR-f1abae4e18ce4084ad4d2c3b604862f5
ACR-e4a912bb259b46a4be5d222322c812a5
ACR-231b45e8273045fdbba2c791871ba2ba
ACR-55235b50e6b64b0eb5167f616ef016f9
ACR-6cb9fcbc1bdc462383e3278108c79acf
ACR-2d5666e9c0c84c419aab4afa4203188f
ACR-08adeec51c334578ae4e8fe8fd8f9f79
ACR-182786367d1b4fffbb3226e47d3892d5
ACR-b0879ce790384528ac670d00ca79e116
ACR-cb36a83f6087474d9a0643385d5d8759
ACR-b26a92295f2d4015bde21a04a987e546
ACR-d2e9e088d6cd4a0fac804858422069e6
ACR-84814034fd6e4dd1b50d7cc6a7b990e6
ACR-500875b0413348418dcab9fae11da824
ACR-79867f4b88f643d88808e4b0cc3a99f0
ACR-40d5593d5e3848789fb3de5ef973acd7
ACR-d2532f690a4e4318b210e33f2d8cbbe3
 */
package org.sonarsource.sonarlint.core.analysis.container.global;

import java.io.File;
import org.sonarsource.sonarlint.core.analysis.sonarapi.DefaultTempFolder;

public class GlobalTempFolder extends DefaultTempFolder {

  public GlobalTempFolder(File tempDir, boolean deleteOnExit) {
    super(tempDir, deleteOnExit);
  }

}
