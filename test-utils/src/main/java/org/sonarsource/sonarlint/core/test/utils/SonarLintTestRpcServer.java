/*
ACR-bf9bd0e4240d4c35b62d6956f6d612c0
ACR-e2b13a387b8a4e6b8dc42b118cea5bf5
ACR-5b0e86c70ee14527a84f6a98eecff3f2
ACR-91d1e12077844b6fa909efbbfe205eaf
ACR-691ec515327942f4ac8e73664e2ba55a
ACR-dc25751919614d1f862df15fa2390233
ACR-8db438eba1de443193711f4254956ece
ACR-c48f7d5451684d748bd04982263a4a4f
ACR-f3ef1b6fc29d4314a140d9bfcbae816c
ACR-166a33b5ab554a89a2f08412c9272f24
ACR-1520c063eebe4eaf8580f4dbbf081265
ACR-605b4b01f9ac4b73b4af452329e688c8
ACR-5f17d5088f9745a58023f32780500abb
ACR-c3582d47880b432581fc569af8121076
ACR-8246b500943f468491e72f61aa88999b
ACR-372a5133136d41b780abef125ac9cc3d
ACR-fbb2f130f3a84387bb004991576ba33a
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
      //ACR-e43a8b95e7e7470caa487bd1a470eada
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
        //ACR-5febdfb92306496a8e11180b4831d7c7
        //ACR-bef1c290e3bd479c9eea5d856f2df88a
        var relevantString = new String(relevantBytes, StandardCharsets.UTF_8);

        mem.replace(0, relevantString.length() + 2, "");
        nextContentSize = -1;
        System.out.println("--> " + content + "\n");
      }
    }
  }
}
