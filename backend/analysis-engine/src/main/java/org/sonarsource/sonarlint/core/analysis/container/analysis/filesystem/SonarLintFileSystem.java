/*
ACR-3e54a2962cb14e94ab79b3c36203d7ba
ACR-17548f31839b4c98a2a3103ef3bd5c85
ACR-a5a866a89c3a4dc7b2a41a3d576133d4
ACR-38c957125b6f4bb2ada6c19a59ee383c
ACR-571de9ca399b4c788314755d9677c2ba
ACR-e32491a96ea14afc99e90cbc45c8aabb
ACR-f5de398fb9504c45a34b8b05dffdb1ed
ACR-8765ef7ebd5141db80837b9662d9a6cf
ACR-ac1f3c057a494e7f960ea742c6233bc3
ACR-145b84bc7095436d8cb7407875a724aa
ACR-2846f54b5b2f4070864c7f4e2d58b772
ACR-b252ec3c4e2640259c40475530d5583a
ACR-77c32622dc254a0da54cb2fe95c4e037
ACR-239118286f5b4dcd99e311c26ec70cd8
ACR-2b2e285aa51740bf96bfb9cb893ea7ce
ACR-a806d803537f4e01a8bb7f7e222b02cd
ACR-e2196d5385b743a6ac15dbea0289dd65
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.SortedSet;
import java.util.stream.StreamSupport;
import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FilePredicates;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputDir;
import org.sonar.api.batch.fs.InputFile;
import org.sonarsource.sonarlint.core.analysis.api.AnalysisConfiguration;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;

public class SonarLintFileSystem implements FileSystem {

  private static final SonarLintLogger LOG = SonarLintLogger.get();

  private final DefaultFilePredicates filePredicates;
  private final Path baseDir;
  private Charset encoding;

  private final InputFileIndex inputFileCache;

  public SonarLintFileSystem(AnalysisConfiguration analysisConfiguration, InputFileIndex inputFileCache) {
    this.inputFileCache = inputFileCache;
    this.baseDir = analysisConfiguration.baseDir();
    this.filePredicates = new DefaultFilePredicates();
  }

  @Override
  public File workDir() {
    LOG.warn("No workDir in SonarLint");
    return baseDir();
  }

  @Override
  public InputDir inputDir(File dir) {
    return new SonarLintInputDir(dir.toPath());
  }

  @Override
  public FilePredicates predicates() {
    return filePredicates;
  }

  @Override
  public File baseDir() {
    return baseDir.toFile();
  }

  private SonarLintFileSystem setEncoding(Charset c) {
    LOG.debug("Setting filesystem encoding: " + c);
    this.encoding = c;
    return this;
  }

  @Override
  public Charset encoding() {
    if (encoding == null) {
      setEncoding(StreamSupport.stream(inputFiles().spliterator(), false)
        .map(InputFile::charset)
        .findFirst()
        .orElse(Charset.defaultCharset()));
    }
    return encoding;
  }

  @Override
  public InputFile inputFile(FilePredicate predicate) {
    var files = inputFiles(predicate);
    var iterator = files.iterator();
    if (!iterator.hasNext()) {
      return null;
    }
    var first = iterator.next();
    if (!iterator.hasNext()) {
      return first;
    }

    var sb = new StringBuilder();
    sb.append("expected one element but was: <" + first);
    for (var i = 0; i < 4 && iterator.hasNext(); i++) {
      sb.append(", " + iterator.next());
    }
    if (iterator.hasNext()) {
      sb.append(", ...");
    }
    sb.append('>');

    throw new IllegalArgumentException(sb.toString());

  }

  public Iterable<InputFile> inputFiles() {
    return inputFiles(filePredicates.all());
  }

  @Override
  public Iterable<InputFile> inputFiles(FilePredicate predicate) {
    return OptimizedFilePredicateAdapter.create(predicate).get(inputFileCache);
  }

  @Override
  public boolean hasFiles(FilePredicate predicate) {
    return inputFiles(predicate).iterator().hasNext();
  }

  @Override
  public Iterable<File> files(FilePredicate predicate) {
    return () -> StreamSupport.stream(inputFiles(predicate).spliterator(), false)
      .map(InputFile::file)
      .iterator();
  }

  @Override
  public SortedSet<String> languages() {
    return inputFileCache.languages();
  }

  @Override
  public File resolvePath(String path) {
    throw new UnsupportedOperationException("resolvePath");
  }

}
