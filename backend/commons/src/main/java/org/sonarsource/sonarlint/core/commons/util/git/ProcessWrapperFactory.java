/*
ACR-d13f236f04c0442c9191225a9d5430b3
ACR-4bae242b718343a3a533caa75410cc59
ACR-72b4562ad33a4fcf931dd9f70adf1744
ACR-ad35c88650b649058bc8363a5dff00d2
ACR-79118c1833b6414faa477dae6d64555b
ACR-869d2c1acf49438094da316d68bb4d04
ACR-95818d7dfe8840e18ab5016b41756915
ACR-b7976372ef57494082acf568a5d2ce77
ACR-9bdb0765c55141d599777c734789f98b
ACR-b575757e833643ac81e93d0ac4cfa1d8
ACR-ded634ac35e543b3a066a027237c6d00
ACR-74b0bd87266547358042191a7ffd2188
ACR-f2d84816dbe6447d90ccc8eee3044638
ACR-9762c1560d284b12a0c68a126b8b1033
ACR-74d0267c67834c75bd40aee8342c4c23
ACR-7c5ae9803330442ea22095b02b55f4c7
ACR-b0d55a0fb4ba463e8f2a45547db07a62
 */
package org.sonarsource.sonarlint.core.commons.util.git;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;

import static java.lang.String.format;
import static java.lang.String.join;
import static java.nio.charset.StandardCharsets.UTF_8;

public class ProcessWrapperFactory {
  private static final SonarLintLogger LOG = SonarLintLogger.get();

  public ProcessWrapperFactory() {
    //ACR-faa5267b6d5b47a49a106f111ebbd0ef
  }

  public ProcessWrapper create(@Nullable Path baseDir, Consumer<String> lineConsumer, String... command) {
    return new ProcessWrapper(baseDir, lineConsumer, command);
  }

  public static class ProcessWrapper {

    private final Path baseDir;
    private final Consumer<String> lineConsumer;
    private final String[] command;

    ProcessWrapper(@Nullable Path baseDir, Consumer<String> lineConsumer, String... command) {
      this.baseDir = baseDir;
      this.lineConsumer = lineConsumer;
      this.command = command;
    }

    void processInputStream(InputStream inputStream, Consumer<String> stringConsumer) throws IOException {
      try (var reader = new BufferedReader(new InputStreamReader(inputStream, UTF_8))) {
        String line;
        while ((line = reader.readLine()) != null) {
          stringConsumer.accept(line);
        }
      }
    }

    public ProcessExecutionResult execute() {
      Process p;
      try {
        p = createProcess();
      } catch (IOException e) {
        LOG.warn(format("Could not execute command: [%s]", join(" ", command)), e);
        return new ProcessExecutionResult(-2);
      }
      try {
        return runProcessAndGetOutput(p);
      } catch (InterruptedException e) {
        LOG.warn(format("Command [%s] interrupted", join(" ", command)), e);
        Thread.currentThread().interrupt();
      } catch (Exception e) {
        LOG.warn(format("Command failed: [%s]", join(" ", command)), e);
      } finally {
        p.destroy();
      }
      return new ProcessExecutionResult(-1);
    }

    Process createProcess() throws IOException {
      return new ProcessBuilder()
        .command(command)
        .directory(baseDir != null ? baseDir.toFile() : null)
        .start();
    }

    ProcessExecutionResult runProcessAndGetOutput(Process p) throws InterruptedException, IOException {
      processInputStream(p.getInputStream(), lineConsumer);
      processInputStream(p.getErrorStream(), line -> {
        if (!line.isBlank()) {
          LOG.debug(line);
        }
      });
      int exit = p.waitFor();
      return new ProcessExecutionResult(exit);
    }
  }

  public record ProcessExecutionResult(int exitCode) {
  }
}
