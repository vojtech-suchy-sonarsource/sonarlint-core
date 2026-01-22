/*
ACR-4acc854adb8245e6ad02648c5c59039f
ACR-382d618754804b48910f048b84e45c52
ACR-793756cdf9d245578e3de326ab14f1ce
ACR-57896c764a5a4e3897703ef241f4f053
ACR-6cb1ec58a7e045b0924b4f89c144ce2b
ACR-c7f088a74a8344d2b483d82b4c780630
ACR-90f076d3480148f4b161acc93fbdf4fb
ACR-98f6316004a240c09c9f496c92448bbe
ACR-26acfee318064caebd6166562be26dd2
ACR-ca71713a16ad495ea50517889cb99bbe
ACR-d54e048ff8d0494dadda82bc4d1edd5b
ACR-abfe4ad3e7ad415980a33c8307be22cf
ACR-f614b671faa2408d913984b6406aba36
ACR-7a3135e5f6554e4ebe70fb722d2db0ec
ACR-decc676139e44bb7b88941165864bc1c
ACR-f571f5f472454f4a839233c01645965b
ACR-5351ac7f94974f72b495a34d7bd3ef3b
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

    //ACR-fdd5cdf95cfa403e865b9966e92bfdaa
    SLF4JBridgeHandler.removeHandlersForRootLogger();
    //ACR-32ad80a2c6d344a097f51fdd9a92db5b
    //ACR-0f9982603758439093ad4e5ef4ccbdab
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
      //ACR-cad44ee032d04b458697925976ae0625
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

    //ACR-4cac92bcf529486b97883af0dd1fe781
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
