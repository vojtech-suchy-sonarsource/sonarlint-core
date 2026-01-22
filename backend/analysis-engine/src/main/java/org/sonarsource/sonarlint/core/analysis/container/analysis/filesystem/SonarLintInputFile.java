/*
ACR-bf65b8fb45c04b5199387a766669abaa
ACR-a1d7665043f4484998bd58d96fcb1090
ACR-1e67b47bf373461dbcf6332128251533
ACR-0a395602d09a4efea0fd18f678586c15
ACR-fb1607271da9408dbc5b29a5dd70f891
ACR-b4e407a93ccc4def947179c535a5dbb9
ACR-fc0f81f6995e4b97a2e0f3e850d97269
ACR-1faa9e242ea4447a9793c2f93f6c8729
ACR-3e934b24944c4b9988cff539a9b3f044
ACR-e8b9de95a73c4e8f98a861a2d94122dc
ACR-1ddb119fc3634d37879ed7a95a44c0b9
ACR-8edce41e6b224f84a90f2dbd7c4d5101
ACR-c8553eb379154500aaa43fd0237e0d43
ACR-3a0ff9e08025440f8067d5166c4289f0
ACR-6b817e732a1d469f9807c725b22e80e5
ACR-d29700ddf410475d981a342a061556b4
ACR-aeadccc11a8a45858b374a881a997c31
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

  /*ACR-146141234dcc4c0b948bc18cf25faa6f
ACR-35edcd4e826c451e930509e0a9e15f2b
   */
  @Deprecated
  @Override
  public String absolutePath() {
    return PathUtils.sanitize(clientInputFile.getPath());
  }

  /*ACR-ea2ab4136f9345668cf82061b8b90d0a
ACR-970826fdbce84bb8a75704f3f71cc05c
   */
  @Deprecated
  @Override
  public File file() {
    return path().toFile();
  }

  /*ACR-06709322e5e34575aae4e481b624ea8f
ACR-43f953e76898467e992f47329d2c2a78
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

  /*ACR-400f8ac4ae2f46ff8c406e493d38d36b
ACR-3b04c6d3a6f6438db074410da7b1104e
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
