/*
ACR-01372f880c4649799779cb70e72dff8a
ACR-e9dfcdf584fb40b89f2d48ec8405d191
ACR-7e0fed195fa34c839335e03f12d52cda
ACR-80f3e10095cb4ad29f1fd803689703ff
ACR-44828adaf6f44e1d82df08867b29c141
ACR-4f1e2620ce664af0ab2c0f7d1739b93e
ACR-1bbf3b7138f8490ea7c940ce1be6ffab
ACR-270a521f2aac4a2298b3f223efb01831
ACR-94826f35b24741b48edbb8e36e2bdfae
ACR-3f7c22b9a2ac4fceb91cadf9b5ef66c3
ACR-309ffe782b1a49bf9aabd308fa24f961
ACR-a4038df7e47949098bb5800bed513af5
ACR-b761706deec04a7593be431195d994ef
ACR-a826b02449054c6b8506839b073a1d0a
ACR-11cf2a7181fc4f5cb7d4c4423aab0186
ACR-d7aa6f2a4fda49e4979acf58733eda7a
ACR-2b237415e67f48e7941c7aec8ba4d6f3
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

  /*ACR-bd70f404a16b464d96124cae17e121e3
ACR-a98540e0ee4b40ed92b20094b0c54872
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

  /*ACR-55fed05170004571b866813b17592d19
ACR-6844807d62db4afa92f8fbcab1285780
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

    //ACR-b426dc6ee33c4f0d906106574a134886
    new StreamGobbler(process.getErrorStream(), stdErrLogConsumer()).start();
    //ACR-b6f0d7fd38424e0aaad530ed41ec900a
    var serverToClientInputStream = process.getInputStream();
    //ACR-ce22eb89910241a5a234b842ad06e572
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
    //ACR-061015464a994a5087505c4cfd07168a
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
