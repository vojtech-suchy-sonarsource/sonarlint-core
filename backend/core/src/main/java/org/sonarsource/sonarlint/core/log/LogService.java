/*
ACR-a8b10ae3be2046b0ae5387656e87b9ac
ACR-f3b19bfb2f674922b7f81606867d31c7
ACR-e94e140a918b41bf9cc9f892500146ec
ACR-d09cbf982f824c278ff099d111286915
ACR-ab63e388c308470db78ea8f09ed57585
ACR-239a78c4d53a48e68e7dd6f7c45ccef1
ACR-61dac28e190c45d88aa4c8247cad4e4f
ACR-47a2b19954c843b39d8b8e49194f6a78
ACR-fb13663680b640b687338c7644651c55
ACR-7265c0b8a0034c6381bfd763dd08e79c
ACR-91bb1ba3fab548919c063fddc9d45e47
ACR-c8239f4ef7b349398b11442ace49112f
ACR-07867ac8c9d143c691bd3d91d8cfc24d
ACR-0d5553add7e446868e4f90e88f314a78
ACR-44b68f9de16b44858067c05c0eaaf268
ACR-76514d1abe6841d5b0564a793a5bdfc0
ACR-462ac483046c49f38b6b266db79cf7e3
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
