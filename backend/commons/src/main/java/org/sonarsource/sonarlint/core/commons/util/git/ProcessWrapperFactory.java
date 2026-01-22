/*
ACR-5c33dfe408e443e0a507f8c7af9f0e55
ACR-8ead89e668404a97921452de3523bc75
ACR-be460b9862eb49499b70eac0ffa951ba
ACR-e1e398eb86164b978c3c79e270257ce0
ACR-8c6773276ef54d5ea24fd4ae7a8ba87d
ACR-2d7a7c415cf54c95aab7087bfeaaa89a
ACR-601c1f3e49f94824b1216ff226e26c3a
ACR-fa19dc0dd3fc4d82935513abb0c3794e
ACR-fa26001b29704f7a9bf6bb3558f36204
ACR-0fbb1015c1b145d18fef589955ca84ae
ACR-733a4c3ade7d4d7f8aeaa9c080e4a2a3
ACR-7168aee5276e40a8ad6608dd0c6023e3
ACR-384bb4f27d834fb69f6802a2aad6ca57
ACR-d679919e05db400ebed3413b4017e724
ACR-58e1082e5acc473c921c0d9964c73edf
ACR-4e5b474aa4a14a37a155ceebe1e29620
ACR-82e98a538e764d1b9d06b9a107d943b5
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
    //ACR-32ed4151d5a14b2caf36924ee797e479
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
