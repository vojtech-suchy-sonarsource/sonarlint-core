/*
ACR-3faceede6d4b455781705539c31051ff
ACR-b98a7dfa937041dfbbcddd0e9d07919a
ACR-92f04cd952404ad89dd765793488ce4a
ACR-35b49a70849d4617bd770d6483b6d91f
ACR-bd8294a42c8a4d44932a9f5b85950546
ACR-9b85ffa41fc64eb3b05ffa95ae070a7c
ACR-46204e6de1264cdcaa9389859e5c6b6d
ACR-01cafab9d4f74ede82063a5d06674934
ACR-eb332d6d7152402b8b168433334eb766
ACR-616fee3f82284556b765af0004bd8764
ACR-70350050f67740cbbc2bb3d87e3c4a5e
ACR-aa91559b30084b358cb7bdd77dafe6c5
ACR-f30e1648460a423398f10dce3307f23a
ACR-ce7d7a3fb48a4f969b530ffc779a659b
ACR-d832d98fa0cb4542a05a5fa59b90a57c
ACR-a961944ea69b46cba214acd02958c042
ACR-b391a217c6334be59cdeafc84b81f422
 */
package org.sonarsource.sonarlint.core.client.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

/*ACR-ae3ea0c2800445299be8c1118194c5d4
ACR-a345e87bcba345f2bdf87adf6eb399da
 */
public interface ClientLogOutput {

  void log(String formattedMessage, Level level);

  enum Level {
    ERROR, WARN, INFO, DEBUG, TRACE
  }

  static String stackTraceToString(Throwable t) {
    var stringWriter = new StringWriter();
    var printWriter = new PrintWriter(stringWriter);
    t.printStackTrace(printWriter);
    return stringWriter.toString();
  }
}
