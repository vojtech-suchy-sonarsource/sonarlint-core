/*
ACR-faffd24b0736409a9fa09de3f724cb6d
ACR-374955082dc44b7698069a1cf604f0ca
ACR-69257d86406a43f0ab26512068337993
ACR-a50d5ae4541749b0a56ad9c2cc7db8ad
ACR-6ae137509f1445deb12428d4b5c0f201
ACR-6970a972242d4f19ad16e3d7239dfc9e
ACR-ca3192aacd8f4397bc1a2780fc587101
ACR-0c70271eecaa4642831ea1ae5de54085
ACR-ae872ebaf11544388be58226c2a0b6f5
ACR-37e381227aa14139a4e194fd0feb79bc
ACR-d377132c481c4ef4a61bacc325492cd5
ACR-ae58c873ca314178b63c13bb7953f346
ACR-2fa485f6f4a14fd0921d541c59afa33c
ACR-1631e4e567b14c95b39a8c18f7ba3509
ACR-ea1ebb7bb0a94c0282db35f039c414dc
ACR-c0a9fc913d604de78b3c2564093f2277
ACR-c786bd9043e74f499298768085994f98
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
    //ACR-12b70cce84864a60a157d96cd2dcb4b2
    var emptyArgs = new Object[0];
    logger.error("msg", emptyArgs);
    logger.error("msg {}", "a");
    logger.error("msg {} {}", "a", "a");
    //ACR-02b0793973394518b3eff934a8e420b8
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
    //ACR-2fd5a8e88316425ebf21443f7443344c
    var emptyArgs = new Object[0];
    logger.warn("msg", emptyArgs);
    logger.warn("msg {}", "a");
    logger.warn("msg {} {}", "a", "a");
    //ACR-f4257fb18ade4d91b8fa226e6c252ed8
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
    //ACR-438634ee9bfc4388b9787ebee0ac9f40
    var emptyArgs = new Object[0];
    logger.info("msg", emptyArgs);
    logger.info("msg {}", "a");
    logger.info("msg {} {}", "a", "a");
    //ACR-f60647507eb9438882b6e9211a409643
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
    //ACR-e9e1410dfe444a2f8819d529f973f162
    var emptyArgs = new Object[0];
    logger.debug("msg", emptyArgs);
    logger.debug("msg {}", "a");
    logger.debug("msg {} {}", "a", "a");
    //ACR-bdd8dd50ccdd407a8fbd64ca156fcdd5
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
    //ACR-57694af18bb240319a9d0cbfa846c319
    var emptyArgs = new Object[0];
    logger.trace("msg", emptyArgs);
    logger.trace("msg {}", "a");
    logger.trace("msg {} {}", "a", "a");
    //ACR-742a2c07bec9488895b44c145ff0cd49
    var args = new Object[] {"b"};
    logger.trace("msg {}", args);

    var inOrder = Mockito.inOrder(output);
    inOrder.verify(output).log("msg1", Level.TRACE, null);
    inOrder.verify(output, times(2)).log("msg", Level.TRACE, null);
    inOrder.verify(output).log("msg a", Level.TRACE, null);
    inOrder.verify(output).log("msg a a", Level.TRACE, null);
    inOrder.verify(output).log("msg b", Level.TRACE, null);
  }

  //ACR-09f7ceb5d9914bf1bd7c669ed854f71c
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
