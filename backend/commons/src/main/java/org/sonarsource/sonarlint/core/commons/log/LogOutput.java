/*
ACR-ea0bd72d549c4a2b80e4706153e763e0
ACR-e559316711d34a1584f10ebb5c495a4a
ACR-0e9a68e6b6384a5c903ea26a8e208405
ACR-21d2ec90889d4cfab1dca041d425aef2
ACR-8a19a1b516274bbc88bc622375b5a078
ACR-23472465140a4ef185ae6083315c38fd
ACR-e6383504bccd47de8f64ee6e5c41b2fe
ACR-3f563732495845699adba0170dd31297
ACR-12eeb6a7aa884ffd8810c058dfd8335b
ACR-b4ae3f2f585b40dbb5061e3af18bcc0f
ACR-b099ca471fd9419790e9cb1e586d4284
ACR-6e30e727ba704c53b24781688030ef30
ACR-9a73da30e31e4c008e8d9f84f82369b9
ACR-d0bc35256e4549b0bb8c24ffff598f63
ACR-d2f60d785a1643d3a14483117f7589f1
ACR-ab70f4a162174877b4bcd2223e2ba018
ACR-862482cea84747768234216c6a63d875
 */
package org.sonarsource.sonarlint.core.commons.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import javax.annotation.Nullable;

/*ACR-2d44fc932b944436a4b93ff3873ff6a7
ACR-4d752ce16e9c4dd0b5e6f1ae04a98049
 */
public interface LogOutput {

  /*ACR-8a9dcf0e01c54a0dbd70aa81e3eecd46
ACR-e0ed729630bc4b6cb9c990000915f1af
   */
  @Deprecated(since = "10.0")
  default void log(String formattedMessage, Level level) {
    log(formattedMessage, level, null);
  }

  default void log(@Nullable String formattedMessage, Level level, @Nullable String stacktrace) {
    if (formattedMessage != null) {
      log(formattedMessage, level);
    }
    if (stacktrace != null) {
      log(stacktrace, level);
    }
  }

  enum Level {
    OFF, ERROR, WARN, INFO, DEBUG, TRACE;

    public boolean isMoreVerboseOrEqual(Level targetLevel) {
      return this.ordinal() >= targetLevel.ordinal();
    }
  }

  static String stackTraceToString(Throwable t) {
    var stringWriter = new StringWriter();
    var printWriter = new PrintWriter(stringWriter);
    t.printStackTrace(printWriter);
    return stringWriter.toString();
  }
}
