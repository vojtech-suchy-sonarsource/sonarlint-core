/*
ACR-e40297a805764d0db240b51332c2caf0
ACR-b32946ec7c824e27af4f2bc7ef36b3ec
ACR-22f69da9c73943bb9ff3f786f6d3da83
ACR-3ad3346db2e64379a78e738436e87b8b
ACR-e8068c75440843efb08d9eed1fffb2ed
ACR-d4275288b0784b53b30eb14ff88d17e6
ACR-8115634ef669466889505f78645bcd17
ACR-6519972f5b1344b3bdcf9a0607c05632
ACR-c6d73f7f6408473686c340595b772bdd
ACR-8cbc4d4ba0d9463fa146cbd636c24578
ACR-14d827f7958a4e8b833f97b6123ba5d8
ACR-743706621ea04c6288f70122a510575a
ACR-3ba78beee61848f78d50913e8b5073b0
ACR-537ae85ca7b348cfb26c1dd221ff1a80
ACR-24424da5518a44bdb3583d2b33412b29
ACR-1f8df16491674e5eb1328671bca85528
ACR-deb2455777924ccfacdcc25dd68999fa
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.InputFileFilter;
import org.sonar.api.utils.MessageException;
import org.sonarsource.api.sonarlint.SonarLintSide;
import org.sonarsource.sonarlint.core.analysis.api.AnalysisConfiguration;
import org.sonarsource.sonarlint.core.analysis.api.AnalysisResults;
import org.sonarsource.sonarlint.core.analysis.api.ClientInputFile;
import org.sonarsource.sonarlint.core.analysis.container.analysis.issue.ignore.scanner.IssueExclusionsLoader;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;

/*ACR-daf10486e6144dfc8e540c31f0a0c0c9
ACR-f0778efeb072458c9bb760b91c9e6c88
 */
@SonarLintSide
public class FileIndexer {

  private static final SonarLintLogger LOG = SonarLintLogger.get();

  private final InputFileBuilder inputFileBuilder;
  private final AnalysisConfiguration analysisConfiguration;
  private final AnalysisResults analysisResult;
  private final List<InputFileFilter> filters;
  private final IssueExclusionsLoader issueExclusionsLoader;
  private final InputFileIndex inputFileCache;

  private ProgressReport progressReport;

  public FileIndexer(InputFileIndex inputFileCache, InputFileBuilder inputFileBuilder, AnalysisConfiguration analysisConfiguration,
    AnalysisResults analysisResult, IssueExclusionsLoader issueExclusionsLoader,
    Optional<List<InputFileFilter>> filters) {
    this.inputFileCache = inputFileCache;
    this.inputFileBuilder = inputFileBuilder;
    this.analysisConfiguration = analysisConfiguration;
    this.analysisResult = analysisResult;
    this.issueExclusionsLoader = issueExclusionsLoader;
    this.filters = filters.orElse(List.of());
  }

  public void index() {
    progressReport = new ProgressReport("Report about progress of file indexation", TimeUnit.SECONDS.toMillis(10));
    progressReport.start("Index files");

    var progress = new Progress();

    try {
      indexFiles(inputFileCache, progress, analysisConfiguration.inputFiles());
    } catch (Exception e) {
      progressReport.stop(null);
      throw e;
    }
    var totalIndexed = progress.count();
    progressReport.stop(totalIndexed + " " + pluralizeFiles(totalIndexed) + " indexed");
  }

  private static String pluralizeFiles(int count) {
    return count == 1 ? "file" : "files";
  }

  private void indexFiles(InputFileIndex inputFileCache, Progress progress, Iterable<ClientInputFile> inputFiles) {
    for (ClientInputFile file : inputFiles) {
      indexFile(inputFileCache, progress, file);
    }
  }

  private void indexFile(InputFileIndex inputFileCache, Progress progress, ClientInputFile file) {
    var inputFile = inputFileBuilder.create(file);
    if (accept(inputFile)) {
      analysisResult.setLanguageForFile(file, inputFile.getLanguage());
      indexFile(inputFileCache, progress, inputFile);
      issueExclusionsLoader.addMulticriteriaPatterns(inputFile);
    }
  }

  private void indexFile(final InputFileIndex inputFileCache, final Progress status, final SonarLintInputFile inputFile) {
    inputFileCache.doAdd(inputFile);
    status.markAsIndexed(inputFile);
  }

  private boolean accept(InputFile indexedFile) {
    //ACR-6ef1b0b5fd4e403d8aef57535a28a305
    for (InputFileFilter filter : filters) {
      if (!filter.accept(indexedFile)) {
        LOG.debug("'{}' excluded by {}", indexedFile, filter.getClass().getName());
        return false;
      }
    }
    return true;
  }

  private class Progress {
    private final Set<URI> indexed = new HashSet<>();

    void markAsIndexed(SonarLintInputFile inputFile) {
      if (indexed.contains(inputFile.uri())) {
        throw MessageException.of("File " + inputFile + " can't be indexed twice.");
      }
      indexed.add(inputFile.uri());
      var size = indexed.size();
      progressReport.message(() -> size + " files indexed...  (last one was " + inputFile.uri() + ")");
    }

    int count() {
      return indexed.size();
    }
  }

}
