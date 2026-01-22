/*
ACR-dbe6a2409274473f9de735cf122847c9
ACR-3fa08c7cbd1c4c039d06ebb7b9265652
ACR-08a5b5ea0d2d4fd48f0380cb681574e2
ACR-4532a4ecb5a146e181d7a73c7ac84bea
ACR-e75ad78e5be44be4a801d5e82316ae4f
ACR-97ac76c8f84849e8bbadad4ba4b65343
ACR-a782582e71ec45b9b0b73c3cd0b1a0a1
ACR-54c4f0af8abf421180c005bfe8d6a0b0
ACR-857ad9bad87c421f9daef503f4bcbe74
ACR-e41679f840014be29c57bedc62fb0920
ACR-f886b630e68b4c149848f6d3365bc4e3
ACR-d84f80c5968f4df58cda726a7b2fd9ac
ACR-869997b9de95442691312c6b80fe4c01
ACR-cd2a40c43a604bc4a295dcd5561ac3a1
ACR-126e8229ce26474ea14a5e00404ea434
ACR-9960a73eff1e40539b821dd256f9fc48
ACR-694db83654a14dbfa3d4e2d9cef557cb
 */
package org.sonarsource.sonarlint.core.client.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

/*ACR-dc8e8c81a9514923b9e8954aacd5c812
ACR-795b804897eb4be0b2eae455290a454d
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
