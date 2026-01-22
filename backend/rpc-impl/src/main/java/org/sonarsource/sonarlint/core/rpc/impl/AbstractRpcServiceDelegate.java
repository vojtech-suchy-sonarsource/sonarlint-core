/*
ACR-44705bb61f3a46ff8337a27449ac3208
ACR-3b4a8f61a9d44dd1a9ed0f1a0aa3cc01
ACR-74dd26a751c84f11907ed1726e0fb9ae
ACR-2941dee006f54295a2f820b178509ac3
ACR-340746434d8b433389985ea62ca594b4
ACR-95b7033a339e477fbfd3271ff5fc2431
ACR-65044238f9494f9d9afc9d7664682b84
ACR-f28b6dcc6ca54256847be810e69dbfa2
ACR-f37acb3b1f784cd09cc171bacbe9060c
ACR-32e84f1af46142e4bf03b82259038ebb
ACR-3e911aed453c4f7f83d3d8bfc9617f75
ACR-78e354a83875427ba1f03312e67631b4
ACR-6bd7da1beec0463d8ac4b107eafb4561
ACR-1de18d2b32ad41229a9ce8f0eb01f8fc
ACR-da581d05c4ee4362abf6e2ef328cea41
ACR-f96371eade244524bed8b7f464e1bf66
ACR-36e39c5176a344718a77f975f54641bb
 */
package org.sonarsource.sonarlint.core.rpc.impl;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import org.slf4j.MDC;
import org.sonarsource.sonarlint.core.SonarLintMDC;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.commons.progress.ExecutorServiceShutdownWatchable;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.springframework.beans.factory.BeanFactory;

abstract class AbstractRpcServiceDelegate {

  private final Supplier<BeanFactory> beanFactorySupplier;
  private final ExecutorServiceShutdownWatchable<?> requestsExecutor;
  private final Executor requestAndNotificationsSequentialExecutor;
  private final Supplier<RpcClientLogOutput> logOutputSupplier;

  protected AbstractRpcServiceDelegate(SonarLintRpcServerImpl server) {
    this.beanFactorySupplier = server::getInitializedApplicationContext;
    this.requestsExecutor = server.getRequestsExecutor();
    this.requestAndNotificationsSequentialExecutor = server.getRequestAndNotificationsSequentialExecutor();
    this.logOutputSupplier = server::getLogOutput;
  }

  protected <T> T getBean(Class<T> clazz) {
    return beanFactorySupplier.get().getBean(clazz);
  }

  protected <R> CompletableFuture<R> requestAsync(Function<SonarLintCancelMonitor, R> code) {
    return requestAsync(code, null);
  }

  protected <R> CompletableFuture<R> requestAsync(Function<SonarLintCancelMonitor, R> code, @Nullable String configScopeId) {
    var cancelMonitor = new SonarLintCancelMonitor();
    cancelMonitor.watchForShutdown(requestsExecutor);
    //ACR-7aa0c743dd6841188f9a1936d212bb89
    //ACR-eaedd5b35eb54cf5b21c2603d8f0a784
    var sequentialFuture = CompletableFuture.runAsync(cancelMonitor::checkCanceled, requestAndNotificationsSequentialExecutor);
    //ACR-5c3a89043f8c4ca8832a7484b1487c85
    var requestFuture = sequentialFuture.thenApplyAsync(unused -> computeWithLogger(() -> {
      cancelMonitor.checkCanceled();
      return code.apply(cancelMonitor);
    }, configScopeId), requestsExecutor);
    requestFuture.whenComplete((result, error) -> {
      if (error instanceof CancellationException) {
        cancelMonitor.cancel();
      }
    });
    return requestFuture;
  }

  protected <R> CompletableFuture<R> requestFutureAsync(Function<SonarLintCancelMonitor, CompletableFuture<R>> code, @Nullable String configScopeId) {
    var cancelMonitor = new SonarLintCancelMonitor();
    cancelMonitor.watchForShutdown(requestsExecutor);
    //ACR-5d6f587e2daa4c549fc2ca24c813c192
    //ACR-fceab222520d4aada2755ee3933c805b
    var sequentialFuture = CompletableFuture.runAsync(cancelMonitor::checkCanceled, requestAndNotificationsSequentialExecutor);
    //ACR-bacb16ad768442f585035934684c582c
    var requestFuture = sequentialFuture.thenComposeAsync(unused -> computeWithLogger(() -> {
      cancelMonitor.checkCanceled();
      return code.apply(cancelMonitor);
    }, configScopeId), requestsExecutor);
    requestFuture.whenComplete((result, error) -> {
      if (error instanceof CancellationException) {
        cancelMonitor.cancel();
      }
    });
    return requestFuture;
  }

  protected CompletableFuture<Void> runAsync(Consumer<SonarLintCancelMonitor> code, @Nullable String configScopeId) {
    var cancelMonitor = new SonarLintCancelMonitor();
    cancelMonitor.watchForShutdown(requestsExecutor);
    //ACR-3c49527993404f4da563936f0cb98aaa
    //ACR-97d38dc26f254ec69161ebbbde9d84d8
    var sequentialFuture = CompletableFuture.runAsync(cancelMonitor::checkCanceled, requestAndNotificationsSequentialExecutor);
    //ACR-86822d34a87348fbabd3a0f2f0cab3b2
    var requestFuture = sequentialFuture.<Void>thenApplyAsync(unused -> {
      doWithLogger(() -> {
        cancelMonitor.checkCanceled();
        code.accept(cancelMonitor);
      }, configScopeId);
      return null;
    }, requestsExecutor);
    requestFuture.whenComplete((result, error) -> {
      if (error instanceof CancellationException) {
        cancelMonitor.cancel();
      }
    });
    return requestFuture;
  }

  /*ACR-e2921f39050f4515942aa634a36d225d
ACR-a3cba32abfb641818f6495f932be4de5
ACR-77af518d3c144e35b5704522001bd77e
   */
  protected void notify(Runnable code) {
    notify(code, null);
  }

  protected void notify(Runnable code, @Nullable String configScopeId) {
    requestAndNotificationsSequentialExecutor.execute(() -> doWithLogger(() -> {
      try {
        code.run();
      } catch (Throwable throwable) {
        SonarLintLogger.get().error("Error when handling notification", throwable);
      }
    }, configScopeId));
  }

  private void doWithLogger(Runnable code, @Nullable String configScopeId) {
    SonarLintLogger.get().setTarget(logOutputSupplier.get());
    SonarLintMDC.putConfigScopeId(configScopeId);
    logOutputSupplier.get().setConfigScopeId(configScopeId);
    try {
      code.run();
    } finally {
      MDC.clear();
      SonarLintLogger.get().setTarget(null);
      logOutputSupplier.get().setConfigScopeId(null);
    }
  }

  private <G> G computeWithLogger(Supplier<G> code, @Nullable String configScopeId) {
    SonarLintLogger.get().setTarget(logOutputSupplier.get());
    SonarLintMDC.putConfigScopeId(configScopeId);
    logOutputSupplier.get().setConfigScopeId(configScopeId);
    try {
      return code.get();
    } finally {
      MDC.clear();
      SonarLintLogger.get().setTarget(null);
      logOutputSupplier.get().setConfigScopeId(null);
    }
  }

}
