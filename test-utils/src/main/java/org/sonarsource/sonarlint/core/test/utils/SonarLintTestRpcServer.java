/*
ACR-553058e74695442a8ebe99e03371dcb7
ACR-76ab74d744904d71b9b64e9b11fd5b21
ACR-965a6f6cc3b04742a176629408a4353b
ACR-947146c7b8b74599866145ce482596b4
ACR-4f236d68bce94ca3a57b05fad6b35fd5
ACR-544d139455764ebfb12bd6bfa092411e
ACR-c37ba2eb1a2d4d05b5c686a0c0e05b82
ACR-2492281fb4bc403c9ae30ced7217bfb8
ACR-0dfb8033ac8049abae1d8cf656458e5a
ACR-cc7710679c95414d8c717a9d1cbfbea6
ACR-ad2d2f3e1e004017ab46d95dbc096c9d
ACR-0f845063f1b047f4a4b757c7a4a8f97f
ACR-c1a43e1e49994c3989ca53b1b050339f
ACR-7aa3ea7ed88440e18060de1f5b8b82f5
ACR-b39993d8329d4e4292fdc085e5f8d205
ACR-1e91112b9e4c48a5ac4c06fdc46e16c0
ACR-af7e982fca044e0195af0d8bf26cd9e5
 */
package org.sonarsource.sonarlint.core.test.utils;

import com.google.gson.GsonBuilder;
import com.google.gson.ToNumberPolicy;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.NotNull;
import org.sonarsource.sonarlint.core.commons.storage.SonarLintDatabase;
import org.sonarsource.sonarlint.core.serverconnection.issues.LocalOnlyIssuesRepository;
import org.sonarsource.sonarlint.core.rpc.client.ClientJsonRpcLauncher;
import org.sonarsource.sonarlint.core.rpc.client.SonarLintRpcClientDelegate;
import org.sonarsource.sonarlint.core.rpc.impl.BackendJsonRpcLauncher;
import org.sonarsource.sonarlint.core.rpc.impl.SonarLintRpcServerImpl;
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
import org.sonarsource.sonarlint.core.storage.StorageService;
import org.sonarsource.sonarlint.core.commons.storage.adapter.LocalDateAdapter;
import org.sonarsource.sonarlint.core.commons.storage.adapter.LocalDateTimeAdapter;
import org.sonarsource.sonarlint.core.commons.storage.adapter.OffsetDateTimeAdapter;
import org.sonarsource.sonarlint.core.telemetry.TelemetryLocalStorage;

import static java.util.Objects.requireNonNull;

public class SonarLintTestRpcServer implements SonarLintRpcServer {
  private final SonarLintRpcServer serverUsingRpc;
  private final SonarLintRpcServerImpl serverUsingJava;
  private final BackendJsonRpcLauncher serverLauncher;
  private final ClientJsonRpcLauncher clientLauncher;
  private final JsonRpcSpyOutputStream clientToServerOutputStream;
  private final PipedInputStream clientToServerInputStream;
  private final PipedOutputStream serverToClientOutputStream;
  private final JsonRpcSpyInputStream serverToClientInputStream;
  private Path userHome;
  private Path workDir;
  private Path storageRoot;
  private String productKey;

  public SonarLintTestRpcServer(SonarLintRpcClientDelegate client) throws IOException {
    clientToServerOutputStream = new JsonRpcSpyOutputStream();
    clientToServerInputStream = new PipedInputStream(clientToServerOutputStream);

    serverToClientOutputStream = new PipedOutputStream();
    serverToClientInputStream = new JsonRpcSpyInputStream(serverToClientOutputStream);

    this.serverLauncher = new BackendJsonRpcLauncher(clientToServerInputStream, serverToClientOutputStream);
    this.clientLauncher = new ClientJsonRpcLauncher(serverToClientInputStream, clientToServerOutputStream, client);
    this.serverUsingRpc = clientLauncher.getServerProxy();
    this.serverUsingJava = serverLauncher.getServer();
  }

  @Override
  public CompletableFuture<Void> initialize(InitializeParams params) {
    this.productKey = params.getTelemetryConstantAttributes().getProductKey();
    this.userHome = Path.of(requireNonNull(params.getSonarlintUserHome()));
    this.workDir = requireNonNull(params.getWorkDir());
    this.storageRoot = requireNonNull(params.getStorageRoot());
    return serverUsingRpc.initialize(params);
  }

  @Override
  public ConnectionRpcService getConnectionService() {
    return serverUsingRpc.getConnectionService();
  }

  @Override
  public ConfigurationRpcService getConfigurationService() {
    return serverUsingRpc.getConfigurationService();
  }

  @Override
  public FileRpcService getFileService() {
    return serverUsingRpc.getFileService();
  }

  @Override
  public RulesRpcService getRulesService() {
    return serverUsingRpc.getRulesService();
  }

  @Override
  public BindingRpcService getBindingService() {
    return serverUsingRpc.getBindingService();
  }

  @Override
  public HotspotRpcService getHotspotService() {
    return serverUsingRpc.getHotspotService();
  }

  @Override
  public TelemetryRpcService getTelemetryService() {
    return serverUsingRpc.getTelemetryService();
  }

  @Override
  public AnalysisRpcService getAnalysisService() {
    return serverUsingRpc.getAnalysisService();
  }

  @Override
  public SonarProjectBranchRpcService getSonarProjectBranchService() {
    return serverUsingRpc.getSonarProjectBranchService();
  }

  @Override
  public IssueRpcService getIssueService() {
    return serverUsingRpc.getIssueService();
  }

  @Override
  public DependencyRiskRpcService getDependencyRiskService() {
    return serverUsingRpc.getDependencyRiskService();
  }

  @Override
  public NewCodeRpcService getNewCodeService() {
    return serverUsingRpc.getNewCodeService();
  }

  @Override
  public TaintVulnerabilityTrackingRpcService getTaintVulnerabilityTrackingService() {
    return serverUsingRpc.getTaintVulnerabilityTrackingService();
  }

  @Override
  public DogfoodingRpcService getDogfoodingService() {
    return serverUsingRpc.getDogfoodingService();
  }

  @Override
  public AiCodeFixRpcService getAiCodeFixRpcService() {
    return serverUsingRpc.getAiCodeFixRpcService();
  }

  @Override
  public TaskProgressRpcService getTaskProgressRpcService() {
    return serverUsingRpc.getTaskProgressRpcService();
  }

  @Override
  public FlightRecordingRpcService getFlightRecordingService() {
    return serverUsingJava.getFlightRecordingService();
  }

  @Override
  public AiAgentRpcService getAiAgentService() {
    return serverUsingJava.getAiAgentService();
  }

  @Override
  public LogRpcService getLogService() {
    return serverUsingRpc.getLogService();
  }

  @Override
  public IdeLabsRpcService getIdeLabsService() {
    return serverUsingRpc.getIdeLabsService();
  }

  public Path getWorkDir() {
    return workDir;
  }

  public Path getUserHome() {
    return userHome;
  }

  public Path getStorageRoot() {
    return storageRoot;
  }

  public Path telemetryFilePath() {
    return userHome.resolve("telemetry").resolve(productKey).resolve("usage");
  }

  public TelemetryLocalStorage telemetryFileContent() {
    try {
      return readTelemetryFile(telemetryFilePath());
    } catch (IOException e) {
      //ACR-d7d077d0f8f4438082ac3adf3ab67c48
      throw new AssertionError("Failed to read telemetry file", e);
    }
  }

  private static TelemetryLocalStorage readTelemetryFile(Path path) throws IOException {
    var fileContent = Files.readString(path, StandardCharsets.UTF_8);
    var decoded = new String(Base64.getDecoder().decode(fileContent), StandardCharsets.UTF_8);
    var gson = new GsonBuilder()
      .registerTypeAdapter(OffsetDateTime.class, new OffsetDateTimeAdapter().nullSafe())
      .registerTypeAdapter(LocalDate.class, new LocalDateAdapter().nullSafe())
      .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter().nullSafe())
      .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
      .create();
    return gson.fromJson(decoded, TelemetryLocalStorage.class);
  }

  public LocalOnlyIssuesRepository getLocalOnlyIssuesRepository() {
    return serverUsingJava.getLocalOnlyIssuesRepository();
  }

  public StorageService getIssueStorageService() {
    return serverUsingJava.getIssueStorageService();
  }

  public SonarLintDatabase getSonarLintDatabase() {
    return serverUsingJava.getDatabase();
  }

  @Override
  public CompletableFuture<Void> shutdown() {
    try {
      serverUsingRpc.shutdown().get();
    } catch (Exception e) {
      throw new IllegalStateException(e);
    } finally {
      try {
        serverLauncher.close();
      } catch (Exception e) {
        e.printStackTrace(System.err);
      }
      try {
        clientLauncher.close();
      } catch (Exception e) {
        e.printStackTrace(System.err);
      }
    }
    try {
      this.clientToServerOutputStream.close();
      this.serverToClientOutputStream.close();
      this.clientToServerInputStream.close();
      this.serverToClientInputStream.close();
    } catch (Exception e) {
      e.printStackTrace(System.err);
    }
    return CompletableFuture.completedFuture(null);
  }

  public int getEmbeddedServerPort() {
    return serverUsingJava.getEmbeddedServerPort();
  }

  private static class JsonRpcSpyInputStream extends PipedInputStream {

    public JsonRpcSpyInputStream(PipedOutputStream outputStream) throws IOException {
      super(outputStream);
    }

    @Override
    public synchronized int read(byte[] b, int off, int len) throws IOException {
      int readLength = super.read(b, off, len);
      if (readLength > 0) {
        System.out.println("<-- " + new String(b, off, readLength, StandardCharsets.UTF_8) + "\n");
      }
      return readLength;
    }
  }

  private static class JsonRpcSpyOutputStream extends PipedOutputStream {
    private final StringBuilder mem = new StringBuilder();
    private int nextContentSize = -1;

    @Override
    public void write(@NotNull byte[] b) throws IOException {
      var content = new String(b, StandardCharsets.UTF_8);
      mem.append(content);
      flushIfNeeded(content);
      super.write(b);
    }

    private void flushIfNeeded(String b) {
      int cr = mem.indexOf("\r\n");
      if (cr != -1 && nextContentSize < 0) {
        var contentLength = mem.substring(0, cr);
        mem.replace(0, cr + 2, "");
        nextContentSize = Integer.parseInt(contentLength.substring("Content-Length: ".length()));
      }
      if (nextContentSize > 0 && mem.length() >= nextContentSize + 2) {
        var content = b.trim();
        var bytes = mem.toString().getBytes(StandardCharsets.UTF_8);
        var relevantBytes = new byte[nextContentSize];
        System.arraycopy(bytes, 0, relevantBytes, 0, nextContentSize);
        //ACR-6693391443bf407d9a908678de8dfb86
        //ACR-28be7e25db54410da7765069b0245b22
        var relevantString = new String(relevantBytes, StandardCharsets.UTF_8);

        mem.replace(0, relevantString.length() + 2, "");
        nextContentSize = -1;
        System.out.println("--> " + content + "\n");
      }
    }
  }
}
