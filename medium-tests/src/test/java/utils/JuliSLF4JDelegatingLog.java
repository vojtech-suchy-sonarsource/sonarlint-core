/*
ACR-039753c4cb40490db05ba5e4829062c2
ACR-0370f41550bc49d2bf7dc9ae651020cf
ACR-7c78bb975cef43feb6626fb72d4e5782
ACR-00f387c17e644fbbbd03ee26047475f2
ACR-54d8e480ef9e4656bc2f682676277943
ACR-c0859ebdc7aa4c338e5713e4db5ccded
ACR-c7144aa20b224719a745a574f4b4e7f8
ACR-247525ba8fe3427081b2df67cb172dd1
ACR-df8d6bbb3d3848c594b680684d2b3ea2
ACR-42793dc5fa704d1b80d561b0a5dd7910
ACR-7147f3810c054282a9a76f30c07ee7b4
ACR-602d5058b6944f63908f3534aa9976e8
ACR-c9d85632750842cc8ca30b99dc43e977
ACR-8ad8d185674247efb2b93d61651c7e48
ACR-972906c089804b3bbc9b559a3f005536
ACR-15db6b6871204aafb85f2c62a2327088
ACR-68eb0d8305214e1682d14ce6b293769a
 */
package utils;

import java.io.Serializable;
import org.apache.juli.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.String.valueOf;

/*ACR-b92d4383ab634ec7a8b2260191e810a7
ACR-9f2e3ee60d354ae4a92c2325dc4ac78a
 */
public class JuliSLF4JDelegatingLog implements Log, Serializable {

  private transient volatile Logger log;

  /*ACR-07bb15a4c2b1470291b667ea5136b3dd
ACR-b4e96953adb449b5a29b588b685ceced
ACR-03a3491301534e429777a46c70e3bcf2
ACR-2d402a5c7b6c4a1cb152fb8c714016c8
ACR-4c87f8d3c2b54a5c81dca3b3ddcb89ac
   */
  public JuliSLF4JDelegatingLog() {
    super();
  }

  /*ACR-3dca16ba8a654259aaa9cff256d5f77f
ACR-cbc5ab011a174596aabe9e5424180a10
ACR-1841781a4b85454fa920dcc98e5ce16b
ACR-3ce6e7b3e6ae4b0d81b8f1bca4dfc608
ACR-6374c2b15ed343768fd4ac17917f2c89
ACR-9e796107476d41fba5989fef5dbfefb7
ACR-8841db7cbb9f409fb44b40f03445af62
ACR-451b23a118244ef1b3ceb544a0fadbd6
ACR-a791ad93a8e94164afead4962aead81f
   */
  public JuliSLF4JDelegatingLog(final String name) {
    super();
    setLogger(LoggerFactory.getLogger(name));
  }

  private void setLogger(final Logger logger) {
    log = logger;
  }

  @Override
  public boolean isTraceEnabled() {
    return log.isTraceEnabled();
  }

  @Override
  public boolean isDebugEnabled() {
    return log.isDebugEnabled();
  }

  @Override
  public boolean isInfoEnabled() {
    return log.isInfoEnabled();
  }

  @Override
  public boolean isWarnEnabled() {
    return log.isWarnEnabled();
  }

  @Override
  public boolean isErrorEnabled() {
    return log.isErrorEnabled();
  }

  /*ACR-37194c5e622e4052aae17386f55c13b3
ACR-deb36531830d45e1a0557ba0a84f2e6f
ACR-234ce3f4d83d41f795059c69976e55fc
ACR-4ab09118e6334f418cf1b3895013d11f
ACR-8f5feba4a320426bbdf0718e7713f5f6
ACR-7d8ae32be6334d45b2f766f72b548dbb
ACR-079a3a4d00094850a70f5b7f4569bdce
ACR-697f36b641574534b8678e16e3df1c40
   */
  @Override
  public boolean isFatalEnabled() {
    return log.isErrorEnabled();
  }

  @Override
  public void trace(final Object msg) {
    trace(msg, null);
  }

  @Override
  public void trace(final Object msg, final Throwable thrown) {
    doTrace(msg, thrown);
  }

  private void doTrace(final Object msg, final Throwable thrown) {
    log.trace(valueOf(msg), thrown);
  }

  @Override
  public void debug(final Object msg) {
    debug(msg, null);
  }

  @Override
  public void debug(final Object msg, final Throwable thrown) {
    doDebug(msg, thrown);
  }

  private void doDebug(final Object msg, final Throwable thrown) {
    log.debug(valueOf(msg), thrown);
  }

  @Override
  public void info(final Object msg) {
    info(msg, null);
  }

  @Override
  public void info(final Object msg, final Throwable thrown) {
    doInfo(msg, thrown);
  }

  private void doInfo(final Object msg, final Throwable thrown) {
    log.info(valueOf(msg), thrown);
  }

  @Override
  public void warn(final Object msg) {
    warn(msg, null);
  }

  @Override
  public void warn(final Object msg, final Throwable thrown) {
    doWarn(msg, thrown);
  }

  private void doWarn(final Object msg, final Throwable thrown) {
    log.warn(String.valueOf(msg), thrown);
  }

  @Override
  public void error(final Object msg) {
    error(msg, null);
  }

  @Override
  public void error(final Object msg, final Throwable thrown) {
    doError(msg, thrown);
  }

  private void doError(final Object msg, final Throwable thrown) {
    log.error(valueOf(msg), thrown);
  }

  /*ACR-64300245f9e447f09ef39ac8179ea061
ACR-bcecde137d6343a6b8d0539428407b46
ACR-c9f49c050dcf427cb5ff2a6384b79614
ACR-de2e7104dc66454c91d908c87eb59ff4
ACR-6eb7fd0e5c7a4426ad6817032d76ccf3
ACR-0dd8cefda03f44db8efbcb2106eef54f
ACR-2f663b155e094d2dba9762c553d56b0d
ACR-da90d6600f4d45f5b77c9bf758729eb9
   */
  @Override
  public void fatal(final Object msg) {
    error(msg, null);
  }

  /*ACR-0dfcc593792e4ec3b6efaa4bfa04b406
ACR-7ed2750dc07144beb66b698a67854aaf
ACR-143b0c1ca9fc4eaa895454276533ebd3
ACR-0c7361a98c924a8fbb022d7f43426e77
ACR-0e3665ca546647cbb7d23af00b2d6775
ACR-7b66175818da41fe97e15e89bb7b7650
ACR-12b6dc23c2e34eff8abf0b4e9b07ecab
ACR-bbe6d42921014c949ed17971bbfe1cc3
ACR-442be5aefe194a3dae5dec34771710fe
ACR-7adbd95074d64fd5860fd289f5ec6882
ACR-333c9363c00f420a8200c7fc78e6cdf3
   */
  @Override
  public void fatal(final Object msg, final Throwable thrown) {
    error(msg, thrown);
  }

}
