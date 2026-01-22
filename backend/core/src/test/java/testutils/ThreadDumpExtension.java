/*
ACR-2fa1f5d8ec8243dd870ef0c66a5c50d9
ACR-1cd32ec6ca704da1a04c2ebd01206dfa
ACR-46781a5b3d964e93a7c61acba5370622
ACR-8806cd7dfcf54073899d0382f44023c1
ACR-26ffdc551dfb4ba29130ebdedeacfde8
ACR-c815ddb5cbc0434cb3579fdd8e1dbf60
ACR-fc9f06eacc9744f98ad7ebe3090b00de
ACR-df24eef26e32465e9ababfae872913bb
ACR-99a6420ff46e4e2282554a02e1520940
ACR-38a2173e77104f109df97a8fb4f392df
ACR-7ec8a6c29bf04c9aa7f6bc066d75a1e2
ACR-1c4f5a36701d4e80a81a497a8b27a8f2
ACR-a8a394bf9a6d4609870d113e8873fa9d
ACR-ed871d0add134cb29d708ed76c568719
ACR-7356fd3df5b845be90845b520f9d8127
ACR-09427ab366304e6e96f86d16f676ad2b
ACR-dda9bf7f6eaa4349a2a2d22d8e5e094d
 */
package testutils;

import java.lang.reflect.Method;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;

import static testutils.TestUtils.printThreadDump;

public class ThreadDumpExtension implements InvocationInterceptor {
  private static final ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);

  @Override
  public void interceptTestMethod(Invocation<Void> invocation, ReflectiveInvocationContext<Method> invocationContext, ExtensionContext extensionContext) throws Throwable {
    var timeout = invocationContext.getExecutable().getAnnotation(TakeThreadDumpAfter.class);
    var clazz = invocationContext.getExecutable().getDeclaringClass();
    while (timeout == null && clazz != Object.class) {
      timeout = clazz.getAnnotation(TakeThreadDumpAfter.class);
      clazz = clazz.getSuperclass();
    }
    if (timeout == null || timeout.seconds() <= 0) {
      invocation.proceed();
      return;
    }
    var seconds = timeout.seconds();
    var caller = Thread.currentThread();
    var timedOut = new AtomicBoolean();
    Future<Void> future = exec.schedule(() -> {
      System.out.println("**** TIMEOUT ERROR: TEST EXCEEDED " + seconds + " SECONDS ****");
      printThreadDump();
      timedOut.set(true);
      caller.interrupt();
      return null;
    }, seconds, TimeUnit.SECONDS);
    Exception caught = null;
    try {
      invocation.proceed();
    } catch (Exception ex) {
      caught = ex;
    } finally {
      future.cancel(true);
      if (timedOut.get()) {
        if (!timeout.expectTimeout()) {
          Exception ex = new TimeoutException("Test exceeded timeout of " + seconds + " seconds");
          if (caught != null) {
            ex.addSuppressed(caught);
          }
          throw ex;
        }
      } else if (caught != null) {
        throw caught;
      } else if (timeout.expectTimeout()) {
        throw new RuntimeException("Test expected to timeout at " + seconds + " but didn't");
      }
    }
  }
}
