/*
ACR-a956f13e83a44926b3aa9d43f19960ac
ACR-21bde1af103c4e9f892ba3065801638c
ACR-6a29d63134f74888a7ab857c46f01154
ACR-ae360d797677415cb98dbd18c7a57cc9
ACR-476e677ee81648028302ecf780d9d57b
ACR-fffbddd24ea744c99247db0ce9de36b0
ACR-b056e7c548fc4180a21193a556f34329
ACR-52d0619a67944648a1b0e3e7e0092a4d
ACR-30f19b5d5f35424096e15f843cecbd08
ACR-792de135a56347279237cad24b7af832
ACR-f5ea43c5af23419db9de529b03e8d9e5
ACR-79df3568f5824d29b3f7842fd07474d5
ACR-5e3ce73dca5a42d78293e53f7b7f82f5
ACR-5f612a4af7174713b03e92864b2dfa8d
ACR-606b109e0d0540669206ed2158a9eaf7
ACR-0de7fb3952a041f9b0e26079df64dd42
ACR-1ebf50417ce1481fb3a19108f7c6cb53
 */
package org.sonarsource.sonarlint.core.rpc.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.rpc.protocol.client.log.LogLevel;
import org.sonarsource.sonarlint.core.rpc.protocol.client.log.LogParams;

public class SloopLauncher {
  public static final String SLOOP_CLI_ENTRYPOINT_CLASS = "org.sonarsource.sonarlint.core.backend.cli.SonarLintServerCli";
  private final SonarLintRpcClientDelegate rpcClient;
  private final Function<List<String>, ProcessBuilder> processBuilderFactory;
  private final Supplier<String> osNameSupplier;

  public SloopLauncher(SonarLintRpcClientDelegate rpcClient) {
    this(rpcClient, ProcessBuilder::new, () -> System.getProperty("os.name"));
  }

  SloopLauncher(SonarLintRpcClientDelegate rpcClient, Function<List<String>, ProcessBuilder> processBuilderFactory, Supplier<String> osNameSupplier) {
    this.rpcClient = rpcClient;
    this.processBuilderFactory = processBuilderFactory;
    this.osNameSupplier = osNameSupplier;
  }

  public Sloop start(Path distPath) {
    return start(distPath, null);
  }

  public Sloop start(Path distPath, @Nullable Path jrePath) {
    return start(distPath, jrePath, null);
  }

  /*ACR-038ea5423cac4362917d2e50959c75e5
ACR-846ae156a3ff4d60a7371cf3480bb096
   */
  public Sloop start(Path distPath, @Nullable Path jrePath, @Nullable String jvmOpts) {
    try {
      return execute(distPath, jrePath, jvmOpts);
    } catch (Exception e) {
      logToClient(LogLevel.ERROR, "Unable to start the SonarLint backend", stackTraceToString(e));
      throw new IllegalStateException("Unable to start the SonarLint backend", e);
    }
  }

  private static String stackTraceToString(Throwable t) {
    var stringWriter = new StringWriter();
    var printWriter = new PrintWriter(stringWriter);
    t.printStackTrace(printWriter);
    return stringWriter.toString();
  }

  /*ACR-52c46f836c8144ea8e215ba33670a7b7
ACR-6ee7c5db0017419484975e539b7fece9
   */
  private boolean isWindows() {
    var osName = osNameSupplier.get();
    if (osName == null) {
      return false;
    }
    return osName.startsWith("Windows");
  }

  private Sloop execute(Path distPath, @Nullable Path jrePath, @Nullable String jvmOpts) throws IOException {
    var jreHomePath = jrePath == null ? distPath.resolve("jre") : jrePath;
    logToClient(LogLevel.INFO, "Using JRE from " + jreHomePath, null);
    var binDirPath = jreHomePath.resolve("bin");
    var jreJavaExePath = binDirPath.resolve("java" + (isWindows() ? ".exe" : ""));
    if (!Files.exists(jreJavaExePath)) {
      throw new IllegalArgumentException("The provided JRE path does not exist: " + jreJavaExePath);
    }
    var processBuilder = processBuilderFactory.apply(createCommand(distPath, jreJavaExePath, jvmOpts));
    processBuilder.directory(binDirPath.toFile());
    processBuilder.redirectOutput(ProcessBuilder.Redirect.PIPE);
    processBuilder.redirectInput(ProcessBuilder.Redirect.PIPE);
    processBuilder.redirectError(ProcessBuilder.Redirect.PIPE);

    var process = processBuilder.start();

    //ACR-0c7cadd422d74b829f947f22dee43532
    new StreamGobbler(process.getErrorStream(), stdErrLogConsumer()).start();
    //ACR-20a42d0ae672484c9c32f13adf8b3511
    var serverToClientInputStream = process.getInputStream();
    //ACR-db008cefe145464abb3b90bc6c00744c
    var clientToServerOutputStream = process.getOutputStream();
    var clientLauncher = new ClientJsonRpcLauncher(serverToClientInputStream, clientToServerOutputStream, rpcClient);
    process.onExit().thenAccept(p -> clientLauncher.close());

    var serverProxy = clientLauncher.getServerProxy();
    return new Sloop(serverProxy, process);
  }

  private static List<String> createCommand(Path distPath, Path jreJavaExePath, @Nullable String clientJvmOpts) {
    var libFolderPath = distPath.resolve("lib");
    var classpath = libFolderPath.toAbsolutePath().normalize() + File.separator + '*';
    List<String> commands = new ArrayList<>();
    commands.add(jreJavaExePath.toAbsolutePath().normalize().toString());
    var sonarlintEnvJvmOpts = System.getenv("SONARLINT_JVM_OPTS");
    if (sonarlintEnvJvmOpts != null) {
      commands.addAll(Arrays.asList(sonarlintEnvJvmOpts.split(" ")));
    }
    if (clientJvmOpts != null) {
      commands.addAll(Arrays.asList(clientJvmOpts.split(" ")));
    }
    //ACR-55e7ea4f14a541ff921f6630a3b7727c
    commands.add("-Djava.awt.headless=true");
    commands.add("-classpath");
    commands.add(classpath);
    commands.add(SLOOP_CLI_ENTRYPOINT_CLASS);
    return commands;
  }

  private Consumer<String> stdErrLogConsumer() {
    return s -> logToClient(LogLevel.ERROR, "StdErr: " + s, null);
  }

  private void logToClient(LogLevel level, @Nullable String message, @Nullable String stacktrace) {
    rpcClient.log(new LogParams(level, message, null, Thread.currentThread().getName(),
      SloopLauncher.class.getName(), stacktrace, Instant.now()));
  }

  private static class StreamGobbler extends Thread {
    private final InputStream inputStream;
    private final Consumer<String> consumer;

    public StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
      this.inputStream = inputStream;
      this.consumer = consumer;
    }

    @Override
    public void run() {
      new BufferedReader(new InputStreamReader(inputStream)).lines()
        .forEach(consumer);
    }
  }
}
