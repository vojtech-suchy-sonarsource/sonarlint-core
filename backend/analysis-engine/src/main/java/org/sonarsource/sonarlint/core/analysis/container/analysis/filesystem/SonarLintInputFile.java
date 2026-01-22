/*
ACR-e0250251ff08440b93a54eb2624d9f3c
ACR-a4abcb3e53e8478b840d7208a6fde82f
ACR-beb79a19232f4e4f855caf77177db50a
ACR-fb27b6cb1fd94f1abde959526d31914b
ACR-3a62e5f6d0c64e4b9edd4395885b6c98
ACR-85b3a548533e4a53b46958ff6b6e60f7
ACR-6a175079037c4987bdfe54b03c965f16
ACR-c69703da113e43c481190815f95e59a6
ACR-0fb021f9049649acaa2365a256f6c5da
ACR-829778c1cb0344918ee773c8621c2276
ACR-4c7bdabf4f5c44b68ccb40f362e7b03e
ACR-571b23d84736449a9f2246c64dd0cac0
ACR-15acf31b20174b86bb8b7777f009a12e
ACR-2ba68cacec82415fba473ba785ee28fb
ACR-c38affc1f2754f4e819095098d63cb22
ACR-54a473bd213e4953b8d6bf3acbaf99df
ACR-2c51ffb2f991492fb24051bf8eb47880
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.apache.commons.codec.digest.DigestUtils;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.TextPointer;
import org.sonar.api.batch.fs.TextRange;
import org.sonar.api.utils.PathUtils;
import org.sonarsource.sonarlint.core.analysis.api.ClientInputFile;
import org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem.FileMetadata.Metadata;
import org.sonarsource.sonarlint.core.commons.api.SonarLanguage;

public class SonarLintInputFile implements InputFile {

  private final ClientInputFile clientInputFile;
  private final String relativePath;
  private SonarLanguage language;
  private Type type;
  private Metadata metadata;
  private final Function<SonarLintInputFile, Metadata> metadataGenerator;
  private boolean ignoreAllIssues;
  private final Set<Integer> noSonarLines = new HashSet<>();
  private Collection<int[]> ignoreIssuesOnlineRanges;

  public SonarLintInputFile(ClientInputFile clientInputFile, Function<SonarLintInputFile, Metadata> metadataGenerator) {
    this.clientInputFile = clientInputFile;
    this.metadataGenerator = metadataGenerator;
    this.relativePath = PathUtils.sanitize(clientInputFile.relativePath());
  }

  public void checkMetadata() {
    if (metadata == null) {
      this.metadata = metadataGenerator.apply(this);
    }
  }

  public ClientInputFile getClientInputFile() {
    return clientInputFile;
  }

  @Override
  public String relativePath() {
    return relativePath;
  }

  public SonarLintInputFile setLanguage(@Nullable SonarLanguage language) {
    this.language = language;
    return this;
  }

  public SonarLintInputFile setType(Type type) {
    this.type = type;
    return this;
  }

  @CheckForNull
  @Override
  public String language() {
    return language != null ? language.getSonarLanguageKey() : null;
  }

  @CheckForNull
  public SonarLanguage getLanguage() {
    return language;
  }

  @Override
  public Type type() {
    return type;
  }

  /*ACR-83c9b49937ad4b5caca18e829a475c7d
ACR-cc5747e96a384448ba8ceae393f51c57
   */
  @Deprecated
  @Override
  public String absolutePath() {
    return PathUtils.sanitize(clientInputFile.getPath());
  }

  /*ACR-01822c6f728b42849fb138e17127379b
ACR-ce44bdb28b104a5ca450efd0ec3bf254
   */
  @Deprecated
  @Override
  public File file() {
    return path().toFile();
  }

  /*ACR-35b771c1590b420ebb8ab8218016d3e4
ACR-f1c59d70a7b642a0bba97778594b19d4
   */
  @Deprecated
  @Override
  public Path path() {
    return Paths.get(clientInputFile.getPath());
  }

  @Override
  public InputStream inputStream() throws IOException {
    return clientInputFile.inputStream();
  }

  @Override
  public String contents() throws IOException {
    return clientInputFile.contents();
  }

  @Override
  public Status status() {
    return Status.ADDED;
  }

  /*ACR-0a0a58cd6e3e4a5ab2e6fb3861e75df9
ACR-09f4c185a8f04d0788f5618eb741ecc1
   */
  @Override
  public String key() {
    return uri().toString();
  }

  @Override
  public URI uri() {
    return clientInputFile.uri();
  }

  @Override
  public Charset charset() {
    var charset = clientInputFile.getCharset();
    return charset != null ? charset : Charset.defaultCharset();
  }

  @Override
  public String md5Hash() {
    try {
      return DigestUtils.md5Hex(contents());
    } catch (IOException e) {
      throw new IllegalStateException("Unable to compute md5Hash for " + uri(), e);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (!(o instanceof SonarLintInputFile file)) {
      return false;
    }

    return uri().equals(file.uri());
  }

  @Override
  public int hashCode() {
    return uri().hashCode();
  }

  @Override
  public String toString() {
    return "[uri=" + uri() + "]";
  }

  @Override
  public boolean isFile() {
    return true;
  }

  @Override
  public String filename() {
    return Paths.get(relativePath).getFileName().toString();
  }

  @Override
  public int lines() {
    checkMetadata();
    return metadata.lines();
  }

  @Override
  public boolean isEmpty() {
    checkMetadata();
    return metadata.lastValidOffset() == 0;
  }

  @Override
  public TextPointer newPointer(int line, int lineOffset) {
    checkMetadata();
    return new DefaultTextPointer(line, lineOffset);
  }

  @Override
  public TextRange newRange(TextPointer start, TextPointer end) {
    checkMetadata();
    return newRangeValidPointers(start, end);
  }

  @Override
  public TextRange newRange(int startLine, int startLineOffset, int endLine, int endLineOffset) {
    checkMetadata();
    var start = newPointer(startLine, startLineOffset);
    var end = newPointer(endLine, endLineOffset);
    return newRangeValidPointers(start, end);
  }

  @Override
  public TextRange selectLine(int line) {
    checkMetadata();
    var startPointer = newPointer(line, 0);
    var endPointer = newPointer(line, lineLength(line));
    return newRangeValidPointers(startPointer, endPointer);
  }

  private static TextRange newRangeValidPointers(TextPointer start, TextPointer end) {
    return new DefaultTextRange(start, end);
  }

  private int lineLength(int line) {
    return lastValidGlobalOffsetForLine(line) - metadata.originalLineOffsets()[line - 1];
  }

  private int lastValidGlobalOffsetForLine(int line) {
    return line < this.metadata.lines() ? (metadata.originalLineOffsets()[line] - 1) : metadata.lastValidOffset();
  }

  public void noSonarAt(Set<Integer> noSonarLines) {
    this.noSonarLines.addAll(noSonarLines);
  }

  public boolean hasNoSonarAt(int line) {
    return this.noSonarLines.contains(line);
  }

  public boolean isIgnoreAllIssues() {
    checkMetadata();
    return ignoreAllIssues;
  }

  public void setIgnoreAllIssues(boolean ignoreAllIssues) {
    this.ignoreAllIssues = ignoreAllIssues;
  }

  public void addIgnoreIssuesOnLineRanges(Collection<int[]> lineRanges) {
    if (this.ignoreIssuesOnlineRanges == null) {
      this.ignoreIssuesOnlineRanges = new ArrayList<>();
    }
    this.ignoreIssuesOnlineRanges.addAll(lineRanges);
  }

  public boolean isIgnoreAllIssuesOnLine(@Nullable Integer line) {
    checkMetadata();
    if (line == null || ignoreIssuesOnlineRanges == null) {
      return false;
    }
    return ignoreIssuesOnlineRanges.stream().anyMatch(r -> r[0] <= line && line <= r[1]);
  }

}
