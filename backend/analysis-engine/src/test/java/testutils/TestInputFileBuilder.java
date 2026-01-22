/*
ACR-1c3d4b3f2e7f419d94421a7fed27e594
ACR-544e8f6857db4bc7994973772beac811
ACR-3882c59d961d4c15a493a5ed95d686ec
ACR-b19bbcababef43029788e34bf7b109e8
ACR-438bebf94b294cbd90a45b4576fe9860
ACR-694d8f10f92d406dba02a1c27a2d0ee0
ACR-9c2fa842cbfa447db5cb9ef864b442a9
ACR-534b899f42324b18877e4a1ffd5192ee
ACR-b52b617042d64a32836f439aebe4c5ea
ACR-2221ace8cf4c4e24ba0a142ec696706f
ACR-71597af59fec42dcab580d7d15f496d7
ACR-8ee03c359be6460f94e2e58ae4114eac
ACR-8679c836070e435fbb18b5f08289e247
ACR-ccfe72969023460598ca3bed0e40238d
ACR-2f0be1dd71d343c7951495e15f65f230
ACR-25781938312244bd8ec9d62299efe2fc
ACR-9f655538f0004c77aa4c682919492523
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

/*ACR-35e999e109ea40da894a0425c4c8a4e5
ACR-aceeee78eeed4fd6bb16e7cef7da91dc
ACR-97f59f1257994da1a8602e2429653c9f
ACR-bd08a85c413e4e46ab87d2eae66aaaf5
ACR-09017ce1eb594b45a724ee28ade85a67
ACR-42ca1f20a2124ec1896ada238d9e4d85
ACR-ee7eb1fe89e04f5ea9350519ae9a430c
ACR-4eb9d90f98c34eaabc8fe18ebeb593d8
ACR-165bc0bdb7ae449e837d7556e5809a63
ACR-db48780e2d604ea1b76c19a1a4b6c5e7
ACR-4849a5722a524eb29b8317071457f6fa
ACR-de9f624881334f719d61a574f574998d
ACR-a190e83261654d9b93e3c8aaf544ad56
ACR-125b1929f97e4cb8b5dedcf0fc347887
ACR-7b52258dc2e84883afd802b1b26a910e
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

  /*ACR-5331bcfe2f8c4a5884757ab782108500
ACR-3b9732ca69d248ecae1d2e1e3c271beb
ACR-910138d977b845cb856d017cb3a896a6
ACR-39311176d05547c58341c018d389d26e
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

  /*ACR-a55881c7646047198dcecac40be953f6
ACR-f44265f8ef164bdd83d9d83693662d51
ACR-672891e8050044bcbeff245d3c9d5497
ACR-377a03007a4d4180b1960484ec0ee023
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
