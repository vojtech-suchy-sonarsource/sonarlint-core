/*
ACR-1e2e978a7f2141498f40695fa878f934
ACR-0396df6d04a945058584386a3d2e8928
ACR-1a5437c3323e412ba64b769de1fdbc47
ACR-48aaedf2e3324cd389566e177384eb40
ACR-6ec4aadb20004092afe1f036bbde9852
ACR-dbbe6483fd4848c1b991ab81281e39cb
ACR-3e1b0106ddec4b7781ebde6316ac480f
ACR-d90304020c384b2cbc3dd50992ed33af
ACR-1e2cffe5387849dd8fd6fbafa6e8725c
ACR-3f6bce226d12411a960e40d3016ea6b4
ACR-38b7c33133954b949362a48ead4cb547
ACR-fb7a4d2e61ce42c6aab98618f4129632
ACR-5372eeab811645e2bc2e9425ed320eab
ACR-a3b3d53a2a6f454295ac867fa427896e
ACR-22c1b02ed9da41d6bdcd673e5e412b32
ACR-8808b350dcfc4763a7bfee673f6e979f
ACR-9773b0fccdc44a3284f2ab428b14b23c
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

  //ACR-0295a7c9a6794beb912e944f574bc83d
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

    //ACR-9792f2db76864695a03759967b8b70cf
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

    //ACR-13e568a2d7a4449c82966842dd631621
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
