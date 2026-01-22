/*
ACR-e6310568f18b40b7a98508b364c0b266
ACR-ab402b2fa5ea4dbd810ee5acd8607c33
ACR-0897c5643db3411e887c0cdea3d22a41
ACR-63599513fe044cf6b185369b212cc640
ACR-9da72b9d568c47978a7c87256a465b9e
ACR-01df20d649dc45069b13bb83a0b5f7b3
ACR-3bc601d476a7431d976a5172060834ee
ACR-8ea4a1a5cc164648b56627886a98fe58
ACR-ea2724b8e03e491884c9c50efcc2e772
ACR-0f6a54dff86540fc9509a581dad0a944
ACR-8ac5f7ce25504bd28caf60623899ab14
ACR-d0467016028a4af1b124ba4951f5a42a
ACR-9da82182780c4cd9a2f14b91bc7c7ff2
ACR-caac796903684455a73997c1c84f668b
ACR-bc85aaa408394da2bd8117423fbff8f2
ACR-1ab813c1e7974d9285bea3ab77b531b2
ACR-77fbe60c7d1a4a18ad14fd3acd693153
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
