/*
ACR-1c3118cb92fb4b66991ec5c5cfc4123c
ACR-03faae12990c4a09b8087421f6404dba
ACR-775328008d694ce5b10e196f5bfa167d
ACR-59fbc6b08fca47ac8b336fed75e594b9
ACR-8c3cda71587b4102b8fa8cdc953f44c9
ACR-05b63aeeb7a846429858b7d15c38b2aa
ACR-edc54064c82f4947ad53719d5c479f61
ACR-a7183df617794ba5a14d98cc9a28c316
ACR-b927ea559a9e4231958d0ac0e2fd693a
ACR-2dcd8d18637c4179bcf5986a30ca3dc7
ACR-10ed0281d928417f8549f4823cbc09ed
ACR-7ac6ba15dcfe42989ec0c92a8cd3415d
ACR-eacc8d1e2dc74b9d8987fc0162f51113
ACR-ef21a5ded75f497dbe83c07ac075fe3a
ACR-1c41368e198345fe970407799455d9a1
ACR-e5522a6343d541ef8113a115399c8eef
ACR-d4b9fed255794fb7add65b06d0505bc4
 */
package org.sonarsource.sonarlint.core.commons.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import javax.annotation.Nullable;

/*ACR-c4b4e5e60bad4dd1a63cb050e5c3f357
ACR-603cb1ead7f74a3e9aa62bdf0e4b967e
 */
public interface LogOutput {

  /*ACR-28599b88ff1241d6a0e79bc4ebb81b07
ACR-bf78cddaecc44a29aabebdd3b828157b
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
