/*
ACR-b236c1d8a6764b19a97f475a7f935636
ACR-aa99286026b249e0927fa242d59ac804
ACR-0ca27f38e0844ca1929a80abb5bbb022
ACR-08487d5fe7cf4fabb1e904974c653f03
ACR-623b10d9005b42889fc57980aed4f1de
ACR-01cec308be5e40a6a4aba326b948655b
ACR-e487d3baa0ff46be97ad459169dea34b
ACR-c71de1c2c40e47e18d476513afaa45ca
ACR-17500ced22a6405d9e8a61622518ac8f
ACR-38162edc63224c1ab1ae7b3ea5c45aa8
ACR-966aa93c02c34de88fd4e62ac5680eda
ACR-1214214362334056a0cb3d9648e9aa2f
ACR-ebaef0b3bf7141cdbab5735726c102cf
ACR-cc31b2c821c84f18b0ccf33bf637ce3a
ACR-5213d9e446104b1181ced88821c1a622
ACR-cc65bc6683ce4094b098aaef874911c8
ACR-60a18e8172244ea684d08b1462d10572
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
    //ACR-f7e5253f23614c6989ae3f3b425f9836
    //ACR-277c2101295641119d6408a5b8d58144
    var sequentialFuture = CompletableFuture.runAsync(cancelMonitor::checkCanceled, requestAndNotificationsSequentialExecutor);
    //ACR-d8a5ce15ad95448590f60ae142a853ef
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
    //ACR-c27a9a3d8f6e431793f95f78a72e8c6d
    //ACR-6813ae19286843a79be90a03ef1993c8
    var sequentialFuture = CompletableFuture.runAsync(cancelMonitor::checkCanceled, requestAndNotificationsSequentialExecutor);
    //ACR-50a0be1dadc4494f9e9cb6d98907f271
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
    //ACR-9aea406c0e4c4ef9b86eeb8b9c4465c4
    //ACR-439ec6da55f34ed9b301b801a3855e47
    var sequentialFuture = CompletableFuture.runAsync(cancelMonitor::checkCanceled, requestAndNotificationsSequentialExecutor);
    //ACR-fe0cd7f8dfb842d39589526eca73b25a
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

  /*ACR-66644a0f4d7d47248b50c04a282a3142
ACR-822ed78008444092a671a2ca6928f153
ACR-776ff03a5fb643aa8eda0f727858430f
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
