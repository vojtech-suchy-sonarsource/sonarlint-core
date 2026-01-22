/*
ACR-9542225fc5554a8eba6aed9c33841e1a
ACR-a736bfc73b3e457cba8555f646a9a7e2
ACR-ac72ac20879c405baca8d44b74c4045f
ACR-e156e02cd26a4207a0c65daa90ed856d
ACR-2da001c66d444e63b0ed405c4ab45405
ACR-6e16402ab8284944ab6eb68ac9994885
ACR-d63e438961b74c5b94179b20a52210b3
ACR-782c2534c761481f83b636d3eb919152
ACR-f0d4cc102c3a48d589bcdb468e5ae42e
ACR-d08decabb4c44b6b8fdc651943f2a4f3
ACR-f90abd198d7e4e9e821160107d9c6281
ACR-b5bd56f76870422aa5015149ba5e2d7a
ACR-9eab1e5ecf854237b30034973abe6ff0
ACR-51749c0376594ac28007d013e8a1afb5
ACR-df1d41e5285c40e79ae5973503746993
ACR-d4286a5adbeb41bdb17e57dbeaaaa439
ACR-a19750821f3742e48520a7cd0b317dbd
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

/*ACR-272dcf4c34ed497d89b252de6232c209
ACR-f4bf7c5418a2489296c7fdf206be9d26
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
    //ACR-9d14b72f5ad040a4bc5e287f44dacdcd
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
