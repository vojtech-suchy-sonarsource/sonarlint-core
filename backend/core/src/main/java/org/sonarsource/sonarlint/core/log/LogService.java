/*
ACR-e5051132b62c4f80b9d49b9fabfbd4b9
ACR-a39e7a525921484291dca3378aaf8fb6
ACR-92fe1cce3c8b4e25aed32fc672ad76dc
ACR-b145c56dcd6c4fc9af64d0439e2ea100
ACR-170a05d0c76d4d668d76c50434f749d9
ACR-b586f933842e4ae3a28d6de2a12ebc91
ACR-40175b4b927d4926a0ce0b81104f45db
ACR-e47cd37edce84659801283c54c110433
ACR-5299b2182f8a4d1fb6cd11a61c2e6089
ACR-976349b562354558aee6ee131512911f
ACR-b6d93b9feda64cd588394fe7f5df7295
ACR-1a5c7b6f1d744d1598716f94daa2449f
ACR-b5f4c7bf9e144011a8bc35463df59bc1
ACR-f1f8504b6eca4dfab05efd49a21b3b26
ACR-c74b3ba74c724eecb948fbc0aec2fa08
ACR-9c0acaffc04049f78fbd64e0f58aaf50
ACR-1649df02c9a744839f37333a8ba3be89
 */
package org.sonarsource.sonarlint.core.log;

import org.sonarsource.sonarlint.core.commons.log.LogOutput;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.log.LogLevel;

public class LogService {

  public void setLogLevel(LogLevel newLevel) {
    SonarLintLogger.get().setLevel(convert(newLevel));
  }

  public static LogOutput.Level convert(LogLevel level) {
    return switch (level) {
      case OFF -> LogOutput.Level.OFF;
      case ERROR -> LogOutput.Level.ERROR;
      case INFO -> LogOutput.Level.INFO;
      case WARN -> LogOutput.Level.WARN;
      case DEBUG -> LogOutput.Level.DEBUG;
      case TRACE -> LogOutput.Level.TRACE;
    };
  }

}
