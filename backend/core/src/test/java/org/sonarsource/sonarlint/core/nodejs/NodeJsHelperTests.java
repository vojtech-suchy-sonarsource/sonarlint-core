/*
ACR-0821fc5c3d7541c18742af6c02b6ec29
ACR-5b565ef0d8104361b2f4f46cfc24e983
ACR-b7b963fae2b14d2c83fce8254e4bac1e
ACR-73cfce09d38044ad8a8dc3b68138e145
ACR-92dc2b996e96481591ccad6404ec5711
ACR-fe2df739fd0745518b73907f75ea099e
ACR-f276e9bcb8324fd3a4c4188e3c2446a0
ACR-179d5a02763e430a9ce75a5035f72b8f
ACR-19d2fa22cdfc412796e13fc565a7d55b
ACR-2002e1ed438943d19e3f21c3628b2992
ACR-175fdcc6b35b484095db02a1586b039b
ACR-4ae611db95f54c3b93e2e4a821cb5e52
ACR-b5e1d2420cc440f0af03fe5a74e38191
ACR-d181ac9408794e8e85b6b30b4176f1ea
ACR-013847f85bcc4caeaf96987e9a4f2f29
ACR-6a84dec5e2824cd2b93312e2fee2ea90
ACR-ac58539e74af49978cdae69dffea5c10
 */
package org.sonarsource.sonarlint.core.nodejs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.stubbing.Answer;
import org.sonar.api.utils.System2;
import org.sonar.api.utils.command.Command;
import org.sonar.api.utils.command.CommandExecutor;
import org.sonar.api.utils.command.StreamConsumer;
import org.sonarsource.sonarlint.core.commons.Version;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class NodeJsHelperTests {
  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();
  private static final Path DUMMY_FILE_HELPER_LOCATION = Paths.get("");

  private static final Path FAKE_NODE_PATH = Paths.get("foo/node");

  private final System2 system2 = mock(System2.class);

  private CommandExecutor commandExecutor;

  private final Map<Predicate<Command>, BiFunction<StreamConsumer, StreamConsumer, Integer>> registeredCommandAnswers = new LinkedHashMap<>();

  @BeforeEach
  void prepare() {
    commandExecutor = mock(CommandExecutor.class);
    when(commandExecutor.execute(any(), any(), any(), anyLong())).thenAnswer((Answer<Integer>) invocation -> {
      var c = invocation.getArgument(0, Command.class);
      for (Entry<Predicate<Command>, BiFunction<StreamConsumer, StreamConsumer, Integer>> answer : registeredCommandAnswers.entrySet()) {
        if (answer.getKey().test(c)) {
          var stdOut = invocation.getArgument(1, StreamConsumer.class);
          var stdErr = invocation.getArgument(2, StreamConsumer.class);
          return answer.getValue().apply(stdOut, stdErr);
        }
      }
      return fail("No answers registered for command: " + c.toString());
    });
  }

  @Test
  void usePropertyWhenProvidedToResolveNodePath() {

    registerNodeVersionAnswer("v10.5.4");

    var underTest = new NodeJsHelper(system2, DUMMY_FILE_HELPER_LOCATION, commandExecutor);
    var result = underTest.detect(FAKE_NODE_PATH);

    assertThat(logTester.logs()).containsExactly(
      "Node.js path provided by configuration: " + FAKE_NODE_PATH,
      "Checking node version...",
      "Execute command '" + FAKE_NODE_PATH + " -v'...",
      "Command '" + FAKE_NODE_PATH + " -v' exited with 0\nstdout: v10.5.4",
      "Detected node version: 10.5.4");
    assertThat(result).isNotNull();
    assertThat(result.getPath()).isEqualTo(FAKE_NODE_PATH);
    assertThat(result.getVersion()).isEqualTo(Version.create("10.5.4"));
  }

  @Test
  void supportNightlyBuilds() {

    registerNodeVersionAnswer("v15.0.0-nightly20200921039c274dde");

    var underTest = new NodeJsHelper(system2, DUMMY_FILE_HELPER_LOCATION, commandExecutor);
    var result = underTest.detect(FAKE_NODE_PATH);

    assertThat(logTester.logs()).containsExactly(
      "Node.js path provided by configuration: " + FAKE_NODE_PATH,
      "Checking node version...",
      "Execute command '" + FAKE_NODE_PATH + " -v'...",
      "Command '" + FAKE_NODE_PATH + " -v' exited with 0\nstdout: v15.0.0-nightly20200921039c274dde",
      "Detected node version: 15.0.0-nightly20200921039c274dde");
    assertThat(result).isNotNull();
    assertThat(result.getPath()).isEqualTo(FAKE_NODE_PATH);
    assertThat(result.getVersion()).isEqualTo(Version.create("15.0.0-nightly20200921039c274dde"));
  }

  @Test
  void ignoreCommandExecutionError() {
    registeredCommandAnswers.put(c -> true, (stdOut, stdErr) -> {
      stdErr.consumeLine("error");
      return -1;
    });

    var underTest = new NodeJsHelper(system2, DUMMY_FILE_HELPER_LOCATION, commandExecutor);
    var result = underTest.detect(FAKE_NODE_PATH);

    assertThat(logTester.logs()).containsExactly(
      "Node.js path provided by configuration: " + FAKE_NODE_PATH,
      "Checking node version...",
      "Execute command '" + FAKE_NODE_PATH + " -v'...",
      "Command '" + FAKE_NODE_PATH + " -v' exited with -1\nstderr: error",
      "Unable to query node version");
    assertThat(result).isNull();
  }

  @Test
  void handleErrorDuringVersionCheck() {
    registerNodeVersionAnswer("wrong_version");

    var underTest = new NodeJsHelper(system2, DUMMY_FILE_HELPER_LOCATION, commandExecutor);
    var result = underTest.detect(FAKE_NODE_PATH);

    assertThat(logTester.logs()).containsExactly(
      "Node.js path provided by configuration: " + FAKE_NODE_PATH,
      "Checking node version...",
      "Execute command '" + FAKE_NODE_PATH + " -v'...",
      "Command '" + FAKE_NODE_PATH + " -v' exited with 0\nstdout: wrong_version",
      "Unable to parse node version: wrong_version",
      "Unable to query node version");
    assertThat(result).isNull();
  }

  @Test
  void useWhichOnLinuxToResolveNodePath() {
    registerWhichAnswer(FAKE_NODE_PATH.toString());
    registerNodeVersionAnswer("v10.5.4");

    var underTest = new NodeJsHelper(system2, DUMMY_FILE_HELPER_LOCATION, commandExecutor);
    var result = underTest.detect(null);

    assertThat(logTester.logs()).containsExactly(
      "Looking for node in the PATH",
      "Execute command '/usr/bin/which node'...",
      "Command '/usr/bin/which node' exited with 0\nstdout: " + FAKE_NODE_PATH,
      "Found node at " + FAKE_NODE_PATH,
      "Checking node version...",
      "Execute command '" + FAKE_NODE_PATH + " -v'...",
      "Command '" + FAKE_NODE_PATH + " -v' exited with 0\nstdout: v10.5.4",
      "Detected node version: 10.5.4");
    assertThat(result).isNotNull();
    assertThat(result.getPath()).isEqualTo(FAKE_NODE_PATH);
    assertThat(result.getVersion()).isEqualTo(Version.create("10.5.4"));
  }

  @Test
  void handleErrorDuringPathCheck() {
    registeredCommandAnswers.put(c -> true, (stdOut, stdErr) -> {
      stdErr.consumeLine("error");
      return -1;
    });

    var underTest = new NodeJsHelper(system2, DUMMY_FILE_HELPER_LOCATION, commandExecutor);
    var result = underTest.detect(null);

    assertThat(logTester.logs()).containsExactly(
      "Looking for node in the PATH",
      "Execute command '/usr/bin/which node'...",
      "Command '/usr/bin/which node' exited with -1\nstderr: error",
      "Unable to locate node");
    assertThat(result).isNull();
  }

  @Test
  void handleEmptyResponseDuringPathCheck() {
    when(system2.isOsWindows()).thenReturn(true);

    registerWhereAnswer();

    var underTest = new NodeJsHelper(system2, DUMMY_FILE_HELPER_LOCATION, commandExecutor);
    var result = underTest.detect(null);

    assertThat(logTester.logs()).containsExactly(
      "Looking for node in the PATH",
      "Execute command 'C:\\Windows\\System32\\where.exe $PATH:node.exe'...",
      "Command 'C:\\Windows\\System32\\where.exe $PATH:node.exe' exited with 0",
      "Unable to locate node");
    assertThat(result).isNull();
  }

  @Test
  void useWhereOnWindowsToResolveNodePath() {
    when(system2.isOsWindows()).thenReturn(true);

    registerWhereAnswer(FAKE_NODE_PATH.toString());
    registerNodeVersionAnswer("v10.5.4");

    var underTest = new NodeJsHelper(system2, DUMMY_FILE_HELPER_LOCATION, commandExecutor);
    var result = underTest.detect(null);

    assertThat(logTester.logs()).containsExactly(
      "Looking for node in the PATH",
      "Execute command 'C:\\Windows\\System32\\where.exe $PATH:node.exe'...",
      "Command 'C:\\Windows\\System32\\where.exe $PATH:node.exe' exited with 0\nstdout: " + FAKE_NODE_PATH,
      "Found node at " + FAKE_NODE_PATH,
      "Checking node version...",
      "Execute command '" + FAKE_NODE_PATH + " -v'...",
      "Command '" + FAKE_NODE_PATH + " -v' exited with 0\nstdout: v10.5.4",
      "Detected node version: 10.5.4");
    assertThat(result).isNotNull();
    assertThat(result.getPath()).isEqualTo(FAKE_NODE_PATH);
    assertThat(result.getVersion()).isEqualTo(Version.create("10.5.4"));
  }

  //ACR-944d1741a6d7480e926b654866790374
  @Test
  void whereOnWindowsCanReturnMultipleCandidates() {
    when(system2.isOsWindows()).thenReturn(true);

    var fake_node_path2 = Paths.get("foo2/node");

    registerWhereAnswer(FAKE_NODE_PATH.toString(), fake_node_path2.toString());
    registerNodeVersionAnswer("v10.5.4");

    var underTest = new NodeJsHelper(system2, DUMMY_FILE_HELPER_LOCATION, commandExecutor);
    var result = underTest.detect(null);

    assertThat(logTester.logs()).containsExactly(
      "Looking for node in the PATH",
      "Execute command 'C:\\Windows\\System32\\where.exe $PATH:node.exe'...",
      "Command 'C:\\Windows\\System32\\where.exe $PATH:node.exe' exited with 0\nstdout: "
        + FAKE_NODE_PATH + "\n" + fake_node_path2,
      "Found node at " + FAKE_NODE_PATH,
      "Checking node version...",
      "Execute command '" + FAKE_NODE_PATH + " -v'...",
      "Command '" + FAKE_NODE_PATH + " -v' exited with 0\nstdout: v10.5.4",
      "Detected node version: 10.5.4");
    assertThat(result).isNotNull();
    assertThat(result.getPath()).isEqualTo(FAKE_NODE_PATH);
    assertThat(result.getVersion()).isEqualTo(Version.create("10.5.4"));
  }

  @Test
  void usePathHelperOnMacToResolveNodePath(@TempDir Path tempDir) throws IOException {
    when(system2.isOsMac()).thenReturn(true);

    registerPathHelperAnswer("PATH=\"/usr/local/bin:/usr/bin:/bin:/usr/sbin:/sbin:/usr/local/bin/node\"; export PATH;");
    registerWhichAnswerIfPathIsSet(FAKE_NODE_PATH.toString(), "/usr/local/bin:/usr/bin:/bin:/usr/sbin:/sbin:/usr/local/bin/node");
    registerNodeVersionAnswer("v10.5.4");

    //ACR-6ffcb6931d5f477fbe61845ae479f144
    var fakePathHelper = tempDir.resolve("path_helper.sh");
    Files.createFile(fakePathHelper);
    var underTest = new NodeJsHelper(system2, fakePathHelper, commandExecutor);
    var result = underTest.detect(null);

    assertThat(logTester.logs()).containsExactly(
      "Looking for node in the PATH",
      "Execute command '" + fakePathHelper + " -s'...",
      "Command '" + fakePathHelper + " -s' exited with 0\nstdout: PATH=\"/usr/local/bin:/usr/bin:/bin:/usr/sbin:/sbin:/usr/local/bin/node\"; export PATH;",
      "Execute command '/usr/bin/which node'...",
      "Command '/usr/bin/which node' exited with 0\nstdout: " + FAKE_NODE_PATH,
      "Found node at " + FAKE_NODE_PATH,
      "Checking node version...",
      "Execute command '" + FAKE_NODE_PATH + " -v'...",
      "Command '" + FAKE_NODE_PATH + " -v' exited with 0\nstdout: v10.5.4",
      "Detected node version: 10.5.4");
    assertThat(result).isNotNull();
    assertThat(result.getPath()).isEqualTo(FAKE_NODE_PATH);
    assertThat(result.getVersion()).isEqualTo(Version.create("10.5.4"));
  }

  @Test
  void ignoreWrongPathHelperOutputOnMac(@TempDir Path tempDir) throws IOException {
    when(system2.isOsMac()).thenReturn(true);
    registerPathHelperAnswer("wrong \n output");
    registerWhichAnswerIfPathIsSet(FAKE_NODE_PATH.toString(), System.getenv("PATH"));
    registerNodeVersionAnswer("v10.5.4");

    //ACR-8fd2149ed900452ba75eddbce2089de4
    var fakePathHelper = tempDir.resolve("path_helper.sh");
    Files.createFile(fakePathHelper);
    var underTest = new NodeJsHelper(system2, fakePathHelper, commandExecutor);
    var result = underTest.detect(null);

    assertThat(logTester.logs()).containsExactly(
      "Looking for node in the PATH",
      "Execute command '" + fakePathHelper + " -s'...",
      "Command '" + fakePathHelper + " -s' exited with 0\nstdout: wrong \n output",
      "Execute command '/usr/bin/which node'...",
      "Command '/usr/bin/which node' exited with 0\nstdout: " + FAKE_NODE_PATH,
      "Found node at " + FAKE_NODE_PATH,
      "Checking node version...",
      "Execute command '" + FAKE_NODE_PATH + " -v'...",
      "Command '" + FAKE_NODE_PATH + " -v' exited with 0\nstdout: v10.5.4",
      "Detected node version: 10.5.4");
    assertThat(result).isNotNull();
    assertThat(result.getPath()).isEqualTo(FAKE_NODE_PATH);
    assertThat(result.getVersion()).isEqualTo(Version.create("10.5.4"));
  }

  @Test
  void ignorePathHelperOnMacIfMissing() {
    when(system2.isOsMac()).thenReturn(true);

    registerPathHelperAnswer("wrong \n output");
    registerWhichAnswerIfPathIsSet(FAKE_NODE_PATH.toString(), System.getenv("PATH"));
    registerNodeVersionAnswer("v10.5.4");

    var underTest = new NodeJsHelper(system2, Paths.get("not_exists"), commandExecutor);
    var result = underTest.detect(null);

    assertThat(logTester.logs()).containsExactly(
      "Looking for node in the PATH",
      "Execute command '/usr/bin/which node'...",
      "Command '/usr/bin/which node' exited with 0\nstdout: " + FAKE_NODE_PATH,
      "Found node at " + FAKE_NODE_PATH,
      "Checking node version...",
      "Execute command '" + FAKE_NODE_PATH + " -v'...",
      "Command '" + FAKE_NODE_PATH + " -v' exited with 0\nstdout: v10.5.4",
      "Detected node version: 10.5.4");
    assertThat(result).isNotNull();
    assertThat(result.getPath()).isEqualTo(FAKE_NODE_PATH);
    assertThat(result.getVersion()).isEqualTo(Version.create("10.5.4"));
  }

  @Test
  void logWhenUnableToGetNodeVersion() {
    var underTest = new NodeJsHelper();
    var result = underTest.detect(Paths.get("not_node"));

    assertThat(logTester.logs()).anyMatch(s -> s.startsWith("Unable to execute the command"));
    assertThat(result).isNull();
  }

  private void registerNodeVersionAnswer(String version) {
    registeredCommandAnswers.put(c -> c.toString().endsWith(FAKE_NODE_PATH + " -v"), (stdOut, stdErr) -> {
      stdOut.consumeLine(version);
      return 0;
    });
  }

  private void registerWhichAnswer(String whichOutput) {
    registeredCommandAnswers.put(c -> c.toString().endsWith("which node"), (stdOut, stdErr) -> {
      stdOut.consumeLine(whichOutput);
      return 0;
    });
  }

  private void registerWhichAnswerIfPathIsSet(String whichOutput, @Nullable String expectedPath) {
    registeredCommandAnswers.put(c -> c.toString().endsWith("which node") && Objects.equals(expectedPath, c.getEnvironmentVariables().get("PATH")), (stdOut, stdErr) -> {
      stdOut.consumeLine(whichOutput);
      return 0;
    });
  }

  private void registerWhereAnswer(String... whereOutput) {
    registeredCommandAnswers.put(c -> c.toString().endsWith("C:\\Windows\\System32\\where.exe $PATH:node.exe"), (stdOut, stdErr) -> {
      Stream.of(whereOutput).forEach(stdOut::consumeLine);
      return 0;
    });
  }

  private void registerPathHelperAnswer(String output) {
    registeredCommandAnswers.put(c -> c.toString().endsWith("path_helper.sh -s"), (stdOut, stdErr) -> {
      stdOut.consumeLine(output);
      return 0;
    });
  }

}
