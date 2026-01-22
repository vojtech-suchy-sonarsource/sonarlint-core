/*
ACR-589fa9aed10e401abca5afeec1e6576c
ACR-e851bcfa348c42b0b9deca9271ecef7c
ACR-bf01f58a96ad40daad4844371d783ebb
ACR-53fbce0ddd184267afd0502736b8635c
ACR-46a8ec1c6c704e178e6dc37888fd98ad
ACR-650db16408e7449a88bc94b9d8379ffd
ACR-9ed8b02ec0384d8e90cea4b9097df47b
ACR-db360ee9fa3c4c13880edfcf93c93a94
ACR-ceb0140c6de9416fb61ab4964dd3a37a
ACR-7a993cd530084974bcc794a537bd356b
ACR-2b52e4a97f1e47e39e2033b05fbe598e
ACR-a75434d7104447348b7a9d89e6f103e3
ACR-ecf61a9d3b124ce5836a1d4f77a05747
ACR-8dd9307dbf2f4cd8a4f2e1b13e13103f
ACR-0d894c5e76ee4e92830512e80b777ed5
ACR-ff3fd932fee44ab1939ccf914f359a5a
ACR-f7a43b4220e64f6dac7db664cc505dbf
 */
package testutils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.InputFile.Type;
import org.sonar.api.utils.PathUtils;
import org.sonarsource.sonarlint.core.analysis.api.ClientInputFile;
import org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem.FileMetadata;
import org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem.SonarLintInputFile;
import org.sonarsource.sonarlint.core.commons.api.SonarLanguage;

/*ACR-630ce9d0caf34a00a426ef1a71b2d649
ACR-97f1a4e28e2e4451b1a3d8315aac14eb
ACR-f720238590fe4227b428018fd0f8f051
ACR-804b059ff0c5445d942e47e9bb8721c2
ACR-defdeb7e2ba045479e9f0dccb57a38d8
ACR-c2b4a5c85d5246a0aa876bd4ca5887ce
ACR-5a7af3b738484e359680ff3b137c1b7e
ACR-ac1b4df5832c4f0bb074c426410bd5d8
ACR-5328c6d9c97643d0938c822aa720d992
ACR-7de7f09fe3684d689f347e7503acc00b
ACR-eb5cf7af2880470bab27828aa6283f20
ACR-9558f7762e91412daac0945e70cdce87
ACR-49540d7b325440cf8e0ac5df9da0630c
ACR-6244f1ef95c548c4aa70ffcbbb30fa05
ACR-278ec091487b48e3a6461a5fb743b7a7
 */
public class TestInputFileBuilder {
  private final String relativePath;
  @CheckForNull
  private Path baseDir;
  private SonarLanguage language;
  private InputFile.Type type = InputFile.Type.MAIN;
  private int lines = -1;
  private int[] originalLineStartOffsets = new int[0];
  private int lastValidOffset = -1;
  private String contents;

  /*ACR-a531153cfd7743e59ea43c121580b966
ACR-e58dff88435b42deb1b252b8da6cac67
ACR-4de67be6e4de4627a1a6f0091474e9cc
ACR-151eacb8284440c2b357ba34f6ef77f9
   */
  public TestInputFileBuilder(File baseDir, File filePath) {
    var relativePathStr = baseDir.toPath().relativize(filePath.toPath()).toString();
    setBaseDir(baseDir.toPath());
    this.relativePath = PathUtils.sanitize(relativePathStr);
  }

  public TestInputFileBuilder(String relativePath) {
    this.relativePath = PathUtils.sanitize(relativePath);
  }

  public static TestInputFileBuilder create(File moduleBaseDir, File filePath) {
    return new TestInputFileBuilder(moduleBaseDir, filePath);
  }

  public static TestInputFileBuilder create(String relativePath) {
    return new TestInputFileBuilder(relativePath);
  }

  public TestInputFileBuilder setBaseDir(Path baseDir) {
    this.baseDir = baseDir;
    return this;
  }

  public TestInputFileBuilder setLanguage(@Nullable SonarLanguage language) {
    this.language = language;
    return this;
  }

  public TestInputFileBuilder setType(InputFile.Type type) {
    this.type = type;
    return this;
  }

  public TestInputFileBuilder setLines(int lines) {
    this.lines = lines;
    return this;
  }

  /*ACR-8de142fd76f04b268d687937ecfd766a
ACR-f1b53cb12e194a0699b1d77aabf74258
ACR-e9089a8c86e34c54b0d6131e6a5b11ae
ACR-94abe7ab9aa74610ad0a35c46ebd3c68
   */
  public TestInputFileBuilder setContents(String content) {
    this.contents = content;
    initMetadata(content);
    return this;
  }

  public TestInputFileBuilder setLastValidOffset(int lastValidOffset) {
    this.lastValidOffset = lastValidOffset;
    return this;
  }

  public TestInputFileBuilder setOriginalLineStartOffsets(int[] originalLineStartOffsets) {
    this.originalLineStartOffsets = originalLineStartOffsets;
    return this;
  }

  public TestInputFileBuilder setMetadata(FileMetadata.Metadata metadata) {
    this.setLines(metadata.lines());
    this.setLastValidOffset(metadata.lastValidOffset());
    this.setOriginalLineStartOffsets(metadata.originalLineOffsets());
    return this;
  }

  public TestInputFileBuilder initMetadata(String content) {
    return setMetadata(
      new FileMetadata().readMetadata(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8, URI.create("file://test"), null));
  }

  public SonarLintInputFile build() {
    ClientInputFile clientInputFile = new InMemoryTestClientInputFile(contents, relativePath, baseDir != null ? baseDir.resolve(relativePath) : null, type == Type.TEST,
      language);
    return new SonarLintInputFile(clientInputFile, f -> new FileMetadata.Metadata(lines, originalLineStartOffsets, lastValidOffset))
      .setType(type)
      .setLanguage(language);
  }
}
