/*
ACR-65e018f0459e4517a49a1fbd2bf188b0
ACR-63c5443aed62454dbfb84e9ee6ed076c
ACR-700f82b2acb64a06b3949b195bbc3354
ACR-dde1f61b771f4ad7aec55a20412d0030
ACR-e825bafb38104cdfbfb2f4b1d58d0d77
ACR-00c909355df64d6eb0236895b54b6665
ACR-21dde9fc01e74fbcbb6dae9ca387c8c9
ACR-bdab2041d36e4392911334b1b9922e12
ACR-222a466a6f3c4d07be50e5aa9d2ffe5c
ACR-c54aba794463487388b180e9295a82b4
ACR-b983a2d84c98433b9433dccdd97a151a
ACR-17024a543f714d8a9f92b27d65cb46df
ACR-959555c6ec2e4b9abe385cad6abcb463
ACR-f347c34244cb4c17b65ce9ac102ed13e
ACR-5dc251c8a477456cba319fcf5c8498e6
ACR-12c37e4bdcc94557b5b8aeca463d8d8f
ACR-c0f65a1ff66a4ab79a7b4256b49d1eef
 */
package utils;

import java.io.Serializable;
import org.apache.juli.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.String.valueOf;

/*ACR-d4d9844f85934ff8856720c57e05b247
ACR-0916e6fee510406694028cc35d6d9a9d
 */
public class JuliSLF4JDelegatingLog implements Log, Serializable {

  private transient volatile Logger log;

  /*ACR-f94dbd2338194616aff76da28919acb6
ACR-6c6b9ee8c3884d46b93321f58c34a99b
ACR-a6fa4b10feb24d58acd6b1308a863ff4
ACR-44e0843c17904eca856e16ab84704f94
ACR-6ee7c5b0fc8840af8bef9eaee0fae180
   */
  public JuliSLF4JDelegatingLog() {
    super();
  }

  /*ACR-d14447a3a2524423aba9004afbddcc2f
ACR-bd4f10d83d5541e88c3a8ca78edc7361
ACR-a07c2a5b8ae944a499746bc5834c8d97
ACR-cb6d5048f3c24688baf1065e04b979e2
ACR-14d068174237426885fffd5c74e03e80
ACR-4d8017f1dcb949c8b29d95205563596d
ACR-52323e8201734535894ca1a2e563a84f
ACR-fbff586f9cb14d868b8ebe8a6987229f
ACR-ac52400a91c948afa3d43bdcc555286c
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

  /*ACR-9e9e1baf2e5b4dd2bc8959ee9c7b2f45
ACR-591dac317eb540be9dc9edeef10c1cd6
ACR-7570fa0769d3495599ca34264afb08a4
ACR-b72d38bf5c2e4647bc68e4a6a1024566
ACR-ea64092412e240d8abddfe512b2417e2
ACR-e1100d8b698c46039b6316e40d88adf6
ACR-a5cd68f2a09842b1badbdffade3ad8e3
ACR-a57c379523ae443189873be5f524df62
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

  /*ACR-42422956e20d4b4dae02795cc9250695
ACR-2380ca9a5a74416d9f3fae2687e7ea0b
ACR-6b71df99108b447a85806450c9b7b720
ACR-7c2c92f67758403b859bf6fad8b94f88
ACR-a55ade1fafe14e7caeca719cfc083de6
ACR-216947ad466c4108bbfbc2524bafff6f
ACR-7c1653da4a87499f9a6ea93506c159bb
ACR-56e48af9f16f453e9036a735252592aa
   */
  @Override
  public void fatal(final Object msg) {
    error(msg, null);
  }

  /*ACR-877c8da898c64f71ae73f77f4c09d58f
ACR-7cc8e12784e249b598ecfbfb9fab0ebb
ACR-0a8c2bf78d1749d9ae5f347667be98ec
ACR-07d9d0b169314ba68f5a41876cf2d42c
ACR-5cd1e77d70e54033a459883b0b9a9cab
ACR-3ee564dc3ea84166ac2c6865365d9ef5
ACR-59f7e63378524499bbe5869a45dc4f85
ACR-e811d662cdc94aa795aa39948eb27042
ACR-b62e139210234319883b496c0fdea7cd
ACR-2b1753e11e0c40afb25a1e3057203eee
ACR-55a361acc1e149bbb008b72e9ead74fb
   */
  @Override
  public void fatal(final Object msg, final Throwable thrown) {
    error(msg, thrown);
  }

}
