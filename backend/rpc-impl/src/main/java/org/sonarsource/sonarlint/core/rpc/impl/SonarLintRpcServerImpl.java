/*
ACR-4cb13499b0a1493ea054fab1c20531ba
ACR-0e57f5cc0ff745189f87557abc3e8d58
ACR-a66cc9c2769442c5a8fdb355348dc942
ACR-5e6b0f1f58294befaaed6d6f97c6289b
ACR-bb88436e5a1c48a2972084a5ee986aff
ACR-683583a08a394f049d81351b93a90a64
ACR-025e838d6cb042e58d83b1fa79abdd46
ACR-d47a19fa6fa64d44b1f5bf80aec78439
ACR-b3856602c4f044cc9446c5fb5d268e63
ACR-4773026f6cb24ec9b36d632947c97825
ACR-b71447e5684d44df862eaf5767441328
ACR-fd45971480a44e9da0589c5b1ed00fc5
ACR-5304b43f3ed7416db2eb2f4583c823f3
ACR-d9be6efb106346e390fdb513434837df
ACR-8071304151bd46b1bd83be7d7b12a515
ACR-07a8609620a145d5a2cc8e9df5d5768f
ACR-4de20e45aed04b119fef994f1e598434
 */
package org.sonarsource.sonarlint.core.rpc.impl;

import ch.qos.logback.classic.Level;
import com.google.common.util.concurrent.MoreExecutors;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import jetbrains.exodus.core.execution.JobProcessor;
import jetbrains.exodus.core.execution.ThreadJobProcessorPool;
import org.eclipse.lsp4j.jsonrpc.CompletableFutures;
import org.eclipse.lsp4j.jsonrpc.ResponseErrorException;
import org.eclipse.lsp4j.jsonrpc.messages.ResponseError;
import org.eclipse.lsp4j.jsonrpc.messages.ResponseErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.commons.progress.ExecutorServiceShutdownWatchable;
import org.sonarsource.sonarlint.core.commons.storage.SonarLintDatabase;
import org.sonarsource.sonarlint.core.embedded.server.EmbeddedServer;
import org.sonarsource.sonarlint.core.log.LogService;
import org.sonarsource.sonarlint.core.rpc.protocol.RpcErrorHandler;
import org.sonarsource.sonarlint.core.rpc.protocol.SingleThreadedMessageConsumer;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintLauncherBuilder;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcClient;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcErrorCode;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcServer;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.ai.AiAgentRpcService;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis.AnalysisRpcService;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.binding.BindingRpcService;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.branch.SonarProjectBranchRpcService;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.ConfigurationRpcService;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.ConnectionRpcService;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.dogfooding.DogfoodingRpcService;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.file.FileRpcService;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.flightrecorder.FlightRecordingRpcService;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.hotspot.HotspotRpcService;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.InitializeParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.issue.IssueRpcService;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.labs.IdeLabsRpcService;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.log.LogRpcService;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.newcode.NewCodeRpcService;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.progress.TaskProgressRpcService;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.remediation.aicodefix.AiCodeFixRpcService;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.rules.RulesRpcService;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.sca.DependencyRiskRpcService;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.telemetry.TelemetryRpcService;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.tracking.TaintVulnerabilityTrackingRpcService;
import org.sonarsource.sonarlint.core.serverapi.exception.ServerRequestException;
import org.sonarsource.sonarlint.core.serverconnection.issues.LocalOnlyIssuesRepository;
import org.sonarsource.sonarlint.core.spring.SpringApplicationContextInitializer;
import org.sonarsource.sonarlint.core.storage.StorageService;
import org.springframework.context.ConfigurableApplicationContext;

public class SonarLintRpcServerImpl implements SonarLintRpcServer {

  private static final Logger LOG = LoggerFactory.getLogger(SonarLintRpcServerImpl.class);
  private final SonarLintRpcClient client;
  private final AtomicBoolean initializeCalled = new AtomicBoolean(false);
  private final AtomicBoolean initialized = new AtomicBoolean(false);
  private final Future<Void> clientListener;
  private final ExecutorServiceShutdownWatchable<ExecutorService> requestsExecutor;
  private final ExecutorService requestAndNotificationsSequentialExecutor;
  private final RpcClientLogOutput logOutput;
  private final ExecutorService messageReaderExecutor;
  private final ExecutorService messageWriterExecutor;
  private SpringApplicationContextInitializer springApplicationContextInitializer;

  public SonarLintRpcServerImpl(InputStream in, OutputStream out) {
    this.messageReaderExecutor = Executors.newCachedThreadPool(r -> {
      var t = new Thread(r);
      t.setName("Server message reader");
      return t;
    });
    this.messageWriterExecutor = Executors.newCachedThreadPool(r -> {
      var t = new Thread(r);
      t.setName("Server message writer");
      return t;
    });
    this.requestAndNotificationsSequentialExecutor = Executors.newSingleThreadExecutor(r -> new Thread(r, "SonarLint Server RPC sequential executor"));
    this.requestsExecutor = new ExecutorServiceShutdownWatchable<>(Executors.newCachedThreadPool(r -> new Thread(r, "SonarLint Server RPC request executor")));
    var launcher = new SonarLintLauncherBuilder<SonarLintRpcClient>()
      .setLocalService(this)
      .setRemoteInterface(SonarLintRpcClient.class)
      .setInput(in)
      .setOutput(out)
      .setExecutorService(messageReaderExecutor)
      .wrapMessages(m -> new SingleThreadedMessageConsumer(m, messageWriterExecutor, System.err::println))
      .traceMessages(getMessageTracer())
      .setExceptionHandler(this::handleError)
      .create();

    this.client = launcher.getRemoteProxy();
    this.logOutput = new RpcClientLogOutput(client);

    //ACR-d2b3482d09a44658a8452614e6d06a0f
    SLF4JBridgeHandler.removeHandlersForRootLogger();
    //ACR-c364bc45a78148daa4180bffbb1c4ece
    //ACR-5461851fbb0c4f56a4513aefc62eae68
    SLF4JBridgeHandler.install();

    var rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    rootLogger.detachAndStopAllAppenders();
    var rpcAppender = new SonarLintRpcClientLogbackAppender(client);
    rpcAppender.start();
    rootLogger.addAppender(rpcAppender);

    this.clientListener = launcher.startListening();
  }

  private ResponseError handleError(Throwable throwable) {
    if (shouldSkipExceptionCapture(throwable)) {
      return new ResponseError(ResponseErrorCode.RequestFailed, throwable.getMessage(), toStringStacktrace(throwable));
    }
    return RpcErrorHandler.handleError(throwable);
  }

  private static boolean shouldSkipExceptionCapture(Throwable throwable) {
    return throwable instanceof ServerRequestException
      || (throwable instanceof CompletionException && throwable.getCause() instanceof ServerRequestException);
  }

  private static String toStringStacktrace(Throwable throwable) {
    var sw = new java.io.StringWriter();
    throwable.printStackTrace(new PrintWriter(sw));
    return sw.toString();
  }

  private static PrintWriter getMessageTracer() {
    if ("true".equals(System.getProperty("sonarlint.debug.rpc"))) {
      try {
        return new PrintWriter(Paths.get(System.getProperty("user.home")).resolve(".sonarlint").resolve("rpc_backend_session.log").toFile(), StandardCharsets.UTF_8);
      } catch (IOException e) {
        System.err.println("Cannot write rpc debug logs file");
        e.printStackTrace();
      }
    }
    return null;
  }

  public Future<Void> getClientListener() {
    return clientListener;
  }

  @Override
  public CompletableFuture<Void> initialize(InitializeParams params) {
    return CompletableFutures.computeAsync(requestAndNotificationsSequentialExecutor, cancelChecker -> {
      SonarLintLogger.get().setLevel(LogService.convert(params.getLogLevel()));
      SonarLintLogger.get().setTarget(logOutput);
      //ACR-7938a8fac5974c928e51a1f4142beb68
      setLogbackRootLogger(params);
      if (initializeCalled.compareAndSet(false, true) && !initialized.get()) {
        springApplicationContextInitializer = new SpringApplicationContextInitializer(client, params);
        initialized.set(true);
      } else {
        var error = new ResponseError(SonarLintRpcErrorCode.BACKEND_ALREADY_INITIALIZED, "Backend already initialized", null);
        throw new ResponseErrorException(error);
      }
      return null;
    });
  }

  private static void setLogbackRootLogger(InitializeParams params) {
    var root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    var logLevel = switch (params.getLogLevel()) {
      case OFF -> Level.OFF;
      case ERROR -> Level.ERROR;
      case WARN -> Level.WARN;
      case INFO -> Level.INFO;
      case DEBUG -> Level.DEBUG;
      case TRACE -> Level.TRACE;
    };
    root.setLevel(logLevel);
  }

  public ConfigurableApplicationContext getInitializedApplicationContext() {
    if (!initialized.get()) {
      throw new IllegalStateException("Backend is not initialized");
    }
    return springApplicationContextInitializer.getInitializedApplicationContext();
  }

  @Override
  public ConnectionRpcService getConnectionService() {
    return new ConnectionRpcServiceDelegate(this);
  }

  @Override
  public ConfigurationRpcService getConfigurationService() {
    return new ConfigurationRpcServiceDelegate(this);
  }

  @Override
  public FileRpcService getFileService() {
    return new FileRpcServiceDelegate(this);
  }

  @Override
  public HotspotRpcService getHotspotService() {
    return new HotspotRpcServiceDelegate(this);
  }

  @Override
  public TelemetryRpcService getTelemetryService() {
    return new TelemetryRpcServiceDelegate(this);
  }

  @Override
  public AnalysisRpcService getAnalysisService() {
    return new AnalysisRpcServiceDelegate(this);
  }

  @Override
  public RulesRpcService getRulesService() {
    return new RulesRpcServiceDelegate(this);
  }

  @Override
  public BindingRpcService getBindingService() {
    return new BindingRpcServiceDelegate(this);
  }

  public SonarProjectBranchRpcService getSonarProjectBranchService() {
    return new SonarProjectBranchRpcServiceDelegate(this);
  }

  @Override
  public IssueRpcService getIssueService() {
    return new IssueRpcServiceDelegate(this);
  }

  @Override
  public NewCodeRpcService getNewCodeService() {
    return new NewCodeRpcServiceDelegate(this);
  }

  @Override
  public TaintVulnerabilityTrackingRpcService getTaintVulnerabilityTrackingService() {
    return new TaintVulnerabilityTrackingRpcServiceDelegate(this);
  }

  @Override
  public DogfoodingRpcService getDogfoodingService() {
    return new DogfoodingRpcServiceDelegate(this);
  }

  @Override
  public AiCodeFixRpcService getAiCodeFixRpcService() {
    return new AiCodeFixRpcServiceDelegate(this);
  }

  @Override
  public TaskProgressRpcService getTaskProgressRpcService() {
    return new TaskProgressRpcServiceDelegate(this);
  }

  @Override
  public DependencyRiskRpcService getDependencyRiskService() {
    return new DependencyRiskRpcServiceDelegate(this);
  }

  @Override
  public FlightRecordingRpcService getFlightRecordingService() {
    return new FlightRecordingRpcServiceDelegate(this);
  }

  @Override
  public AiAgentRpcService getAiAgentService() {
    return new AiAgentRpcServiceDelegate(this);
  }

  @Override
  public LogRpcService getLogService() {
    return new LogServiceDelegate(this);
  }

  @Override
  public IdeLabsRpcService getIdeLabsService() {
    return new IdeLabsRpcServiceDelegate(this);
  }

  @Override
  public CompletableFuture<Void> shutdown() {
    LOG.info("SonarLint backend shutting down, instance={}", this);
    var executor = Executors.newSingleThreadExecutor(r -> new Thread(r, "SonarLint Server shutdown"));
    CompletableFuture<Void> future = CompletableFutures.computeAsync(executor, cancelChecker -> {
      SonarLintLogger.get().setTarget(logOutput);
      var wasInitialized = initialized.getAndSet(false);
      MoreExecutors.shutdownAndAwaitTermination(requestsExecutor, 1, TimeUnit.SECONDS);
      MoreExecutors.shutdownAndAwaitTermination(requestAndNotificationsSequentialExecutor, 1, TimeUnit.SECONDS);
      if (wasInitialized) {
        try {
          springApplicationContextInitializer.close();
        } catch (Exception e) {
          SonarLintLogger.get().error("Error while closing Spring context", e);
        }
      }
      ThreadJobProcessorPool.getProcessors().forEach(JobProcessor::finish);
      shutdownReaderAndWriter();
      return null;
    });
    executor.shutdown();
    return future;
  }

  public void shutdownReaderAndWriter() {
    messageReaderExecutor.shutdownNow();

    //ACR-5033e99a20a14db3bb6aa73a97023c17
    var scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    scheduledExecutorService.schedule(() -> {
      messageWriterExecutor.shutdownNow();
      disconnectFromClient();
    }, 1, TimeUnit.SECONDS);
    scheduledExecutorService.shutdown();
  }

  private void disconnectFromClient() {
    clientListener.cancel(true);
  }

  public boolean isReaderShutdown() {
    return messageReaderExecutor.isShutdown();
  }

  public int getEmbeddedServerPort() {
    return getInitializedApplicationContext().getBean(EmbeddedServer.class).getPort();
  }

  public StorageService getIssueStorageService() {
    return getInitializedApplicationContext().getBean(StorageService.class);
  }

  public LocalOnlyIssuesRepository getLocalOnlyIssuesRepository() {
    return getInitializedApplicationContext().getBean(LocalOnlyIssuesRepository.class);
  }

  public SonarLintDatabase getDatabase() {
    return getInitializedApplicationContext().getBean(SonarLintDatabase.class);
  }

  ExecutorServiceShutdownWatchable<ExecutorService> getRequestsExecutor() {
    return requestsExecutor;
  }

  ExecutorService getRequestAndNotificationsSequentialExecutor() {
    return requestAndNotificationsSequentialExecutor;
  }

  RpcClientLogOutput getLogOutput() {
    return logOutput;
  }
}
