/*
ACR-53f90fd28b43445d9ed4e5ee316fb9c5
ACR-7a0e6de9abb8412ea5639f29b635cc00
ACR-fe0e46a630f04459bb20033f67b0b0f0
ACR-0fd6757c8a994264a29ee672854e2884
ACR-bd1e7c9622c443bf87e549898e7fdee4
ACR-5a711a4387ff43859004b3d8d255cfcf
ACR-0082fc515b724bdba151c0f759253786
ACR-74ccd79de6294e3fb4bafae2fda478b6
ACR-77cff7404f55490aa12606308bf22942
ACR-2bc7adc19a654bfab26c5525a0a83cab
ACR-7b3448e9258b47749d4203932ec24d9a
ACR-cd56b706f67549f48ade4048ed2b7315
ACR-1c4db01dd16c44ff99fa0e04809d804f
ACR-31b5fa90e05c4c23be9072ce65bf25d3
ACR-0b0cd378879949829bfa1903db0b4007
ACR-ecf18273154b48feb08fdcf2002e634e
ACR-88076a7d1fd84972b715ce85b68eda38
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
