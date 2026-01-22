/*
ACR-76e22e77f39c4cd985e64910a1cc0521
ACR-ae8d46851c5947a697e82203fb4eccd2
ACR-900ddf93057d46ee981439ed1383a500
ACR-b7b0c5186a83426c95b571c347440e6d
ACR-e25270424a754f1184594c62de39730d
ACR-21a2003864fe4ceab5553d3db543fa80
ACR-a6f77bb29ce0477d9f1008f85338c511
ACR-7c8fa8cda34d41b2903e2b11e53327db
ACR-a2383b76af7a4256be872108d3a7d9f6
ACR-d970c7cf36274cb0bcf7e78b751cd6d9
ACR-dfa839e036a5482bb0310057a65e837c
ACR-4165942ab81d4fc48f632e79edca1b19
ACR-0fb67a087eb445a4b24f5b5dee392c3e
ACR-e05be82962ad4d8bbdaa33fe415076e0
ACR-09fc410200e84812ad40aed5d6a0e5a6
ACR-8ba9b04ffdb643d4a95270a1837bd2e1
ACR-671bd96dff08427d93549d2dd11f659c
 */
package org.sonarsource.sonarlint.core.commons.log;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.sonarsource.sonarlint.core.commons.log.LogOutput.Level;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

class SonarLintLoggerTests {
  private static final NullPointerException THROWN = new NullPointerException();
  private final LogOutput output = mock(LogOutput.class);
  private final SonarLintLogger logger = new SonarLintLogger();

  @BeforeEach
  void prepare() {
    logger.setLevel(Level.TRACE);
    logger.setTarget(output);
  }

  @Test
  void should_log_error() {
    logger.error("msg1");
    logger.error("msg", (Object) null);
    //ACR-98041715d4c94b9fb1bcd0df8220f8a8
    var emptyArgs = new Object[0];
    logger.error("msg", emptyArgs);
    logger.error("msg {}", "a");
    logger.error("msg {} {}", "a", "a");
    //ACR-b716dff989e54f17a8f66f4552b8263d
    var args = new Object[] {"b"};
    logger.error("msg {}", args);
    logger.error("msg with ex", THROWN);

    var inOrder = Mockito.inOrder(output);
    inOrder.verify(output).log("msg1", Level.ERROR, null);
    inOrder.verify(output, times(2)).log("msg", Level.ERROR, null);
    inOrder.verify(output).log("msg a", Level.ERROR, null);
    inOrder.verify(output).log("msg a a", Level.ERROR, null);
    inOrder.verify(output).log("msg b", Level.ERROR, null);
    inOrder.verify(output).log(eq("msg with ex"), eq(Level.ERROR), argThat(arg -> arg.contains("NullPointerException")));
  }

  @Test
  void should_log_warn() {
    logger.warn("msg1");
    logger.warn("msg", (Object) null);
    //ACR-83bbdcd6386e4ce88f2d6295a3728aab
    var emptyArgs = new Object[0];
    logger.warn("msg", emptyArgs);
    logger.warn("msg {}", "a");
    logger.warn("msg {} {}", "a", "a");
    //ACR-cc27a131ed9c4df282964e68d17232ed
    var args = new Object[] {"b"};
    logger.warn("msg {}", args);
    logger.warn("msg with ex", THROWN);

    var inOrder = Mockito.inOrder(output);
    inOrder.verify(output).log("msg1", Level.WARN, null);
    inOrder.verify(output, times(2)).log("msg", Level.WARN, null);
    inOrder.verify(output).log("msg a", Level.WARN, null);
    inOrder.verify(output).log("msg a a", Level.WARN, null);
    inOrder.verify(output).log("msg b", Level.WARN, null);
    inOrder.verify(output).log(eq("msg with ex"), eq(Level.WARN), argThat(arg -> arg.contains("NullPointerException")));
  }

  @Test
  void should_log_info() {
    logger.info("msg1");
    logger.info("msg", (Object) null);
    //ACR-cdde79c73d28486ab41771d71b875340
    var emptyArgs = new Object[0];
    logger.info("msg", emptyArgs);
    logger.info("msg {}", "a");
    logger.info("msg {} {}", "a", "a");
    //ACR-e140fc7fffbb460cb5fb3b1c33845c13
    var args = new Object[] {"b"};
    logger.info("msg {}", args);

    var inOrder = Mockito.inOrder(output);
    inOrder.verify(output).log("msg1", Level.INFO, null);
    inOrder.verify(output, times(2)).log("msg", Level.INFO, null);
    inOrder.verify(output).log("msg a", Level.INFO, null);
    inOrder.verify(output).log("msg a a", Level.INFO, null);
    inOrder.verify(output).log("msg b", Level.INFO, null);
  }

  @Test
  void should_log_debug() {
    logger.debug("msg1");
    logger.debug("msg", (Object) null);
    //ACR-3073158e396a4e569f4028e4ba19f3ee
    var emptyArgs = new Object[0];
    logger.debug("msg", emptyArgs);
    logger.debug("msg {}", "a");
    logger.debug("msg {} {}", "a", "a");
    //ACR-c89f9c209d1f46deb1fc8662b4d095b2
    var args = new Object[] {"b"};
    logger.debug("msg {}", args);

    var inOrder = Mockito.inOrder(output);
    inOrder.verify(output).log("msg1", Level.DEBUG, null);
    inOrder.verify(output, times(2)).log("msg", Level.DEBUG, null);
    inOrder.verify(output).log("msg a", Level.DEBUG, null);
    inOrder.verify(output).log("msg a a", Level.DEBUG, null);
    inOrder.verify(output).log("msg b", Level.DEBUG, null);
  }

  @Test
  void should_log_trace() {
    logger.trace("msg1");
    logger.trace("msg", (Object) null);
    //ACR-cd057b057c24405c89f5f10bfb28e671
    var emptyArgs = new Object[0];
    logger.trace("msg", emptyArgs);
    logger.trace("msg {}", "a");
    logger.trace("msg {} {}", "a", "a");
    //ACR-7655d186ce344f4ca38591f65198b65d
    var args = new Object[] {"b"};
    logger.trace("msg {}", args);

    var inOrder = Mockito.inOrder(output);
    inOrder.verify(output).log("msg1", Level.TRACE, null);
    inOrder.verify(output, times(2)).log("msg", Level.TRACE, null);
    inOrder.verify(output).log("msg a", Level.TRACE, null);
    inOrder.verify(output).log("msg a a", Level.TRACE, null);
    inOrder.verify(output).log("msg b", Level.TRACE, null);
  }

  //ACR-f8987b4f9fff44df92fd7fdf958bf2e3
  @Test
  void extract_throwable_from_format_params() {
    var throwable = new Throwable("thrown");
    logger.error("msg", (Object) throwable);
    logger.error("msg {}", "a", throwable);
    logger.error("msg {} {}", "a", "a", throwable);

    var inOrder = Mockito.inOrder(output);
    inOrder.verify(output).log(eq("msg"), eq(Level.ERROR), argThat(arg -> arg.contains("thrown")));
    inOrder.verify(output).log(eq("msg a"), eq(Level.ERROR), argThat(arg -> arg.contains("thrown")));
    inOrder.verify(output).log(eq("msg a a"), eq(Level.ERROR), argThat(arg -> arg.contains("thrown")));
  }
}
