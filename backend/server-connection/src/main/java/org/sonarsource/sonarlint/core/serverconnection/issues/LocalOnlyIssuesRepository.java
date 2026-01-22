/*
ACR-c687e88160d845d697bcc4a81212a019
ACR-3f358dc953c746c8afc147e3f050c465
ACR-0d4180f0af7a40b79166613083ebfe11
ACR-603a66a32695498c98df8ef38184e65a
ACR-0506e00159c34502927eef3e323e952f
ACR-44c0bb66f35c4fff90875ac4e0b95b6f
ACR-f981a43075d944f3a01ce38a143908e8
ACR-5370140a5540487f9cff08229905ce17
ACR-b3dc44bb25d44db7ad0eac666fe98997
ACR-66e4e183f5664d57bf595591f9ce08fc
ACR-eb63a56c008d4019815754e04d5269be
ACR-9d361a30716b4cd68f3ae939481e1122
ACR-fcfd13c4990342b8bb9f4623d308dd2b
ACR-989a293310ae4d86af8e6b4f03c31407
ACR-18989c8c38f04d9daf89056c01363c48
ACR-4110a295a39a4b4e8c2f8a62801b5f02
ACR-1fd2066be4574edd80345ae2145f2cd2
 */
package org.sonarsource.sonarlint.core.serverconnection.issues;

import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.sonarsource.sonarlint.core.commons.IssueStatus;
import org.sonarsource.sonarlint.core.commons.LineWithHash;
import org.sonarsource.sonarlint.core.commons.LocalOnlyIssue;
import org.sonarsource.sonarlint.core.commons.LocalOnlyIssueResolution;
import org.sonarsource.sonarlint.core.commons.api.TextRangeWithHash;
import org.sonarsource.sonarlint.core.commons.storage.model.tables.records.LocalOnlyIssuesRecord;

import static org.sonarsource.sonarlint.core.commons.storage.model.Tables.LOCAL_ONLY_ISSUES;

public class LocalOnlyIssuesRepository {

  private final DSLContext database;

  public LocalOnlyIssuesRepository(DSLContext database) {
    this.database = database;
  }

  public List<LocalOnlyIssue> loadForFile(String configurationScopeId, Path filePath) {
    var issuesInFile = database
      .selectFrom(LOCAL_ONLY_ISSUES)
      .where(LOCAL_ONLY_ISSUES.CONFIGURATION_SCOPE_ID.eq(configurationScopeId)
        .and(LOCAL_ONLY_ISSUES.SERVER_RELATIVE_PATH.eq(filePath.toString())))
      .fetch();
    return issuesInFile.stream()
      .map(LocalOnlyIssuesRepository::recordToLocalOnlyIssue)
      .toList();
  }

  public List<LocalOnlyIssue> loadAll(String configurationScopeId) {
    var allIssues = database
      .selectFrom(LOCAL_ONLY_ISSUES)
      .where(LOCAL_ONLY_ISSUES.CONFIGURATION_SCOPE_ID.eq(configurationScopeId))
      .fetch();
    return allIssues.stream()
      .map(LocalOnlyIssuesRepository::recordToLocalOnlyIssue)
      .toList();
  }

  public void storeIssues(Map<String, List<LocalOnlyIssue>> issuesPerConfigScopeId) {
    database.deleteFrom(LOCAL_ONLY_ISSUES).execute();
    database.batchInsert(issuesPerConfigScopeId.entrySet().stream()
      .flatMap(entry -> {
        var configScopeId = entry.getKey();
        return entry.getValue().stream().map(
          issue -> {
            var resolution = issue.getResolution();
            var textRangeWithHash = issue.getTextRangeWithHash();
            var lineWithHash = issue.getLineWithHash();
            return new LocalOnlyIssuesRecord(
              issue.getId(),
              configScopeId,
              issue.getServerRelativePath().toString(),
              issue.getRuleKey(),
              issue.getMessage(),
              resolution == null ? null : resolution.getStatus().name(),
              resolution == null ? null : LocalDateTime.ofInstant(resolution.getResolutionDate(), ZoneOffset.UTC),
              resolution == null ? null : resolution.getComment(),
              textRangeWithHash == null ? null : textRangeWithHash.getStartLine(),
              textRangeWithHash == null ? null : textRangeWithHash.getStartLineOffset(),
              textRangeWithHash == null ? null : textRangeWithHash.getEndLine(),
              textRangeWithHash == null ? null : textRangeWithHash.getEndLineOffset(),
              textRangeWithHash == null ? null : textRangeWithHash.getHash(),
              lineWithHash == null ? null : lineWithHash.getNumber(),
              lineWithHash == null ? null : lineWithHash.getHash());

          });
      })
      .toList())
      .execute();
  }

  public void storeLocalOnlyIssue(String configurationScopeId, LocalOnlyIssue issue) {
    database.transaction((Configuration trx) -> {
      var textRangeWithHash = issue.getTextRangeWithHash();
      var startLine = textRangeWithHash == null ? null : textRangeWithHash.getStartLine();
      var startLineOffset = textRangeWithHash == null ? null : textRangeWithHash.getStartLineOffset();
      var endLine = textRangeWithHash == null ? null : textRangeWithHash.getEndLine();
      var endLineOffset = textRangeWithHash == null ? null : textRangeWithHash.getEndLineOffset();
      var textRangeHash = textRangeWithHash == null ? null : textRangeWithHash.getHash();

      var lineWithHash = issue.getLineWithHash();
      var line = lineWithHash == null ? null : lineWithHash.getNumber();
      var lineHash = lineWithHash == null ? null : lineWithHash.getHash();

      var resolution = issue.getResolution();
      var resolutionStatus = resolution == null ? null : resolution.getStatus().name();
      var resolutionDate = resolution == null ? null : LocalDateTime.ofInstant(resolution.getResolutionDate(), ZoneOffset.UTC);
      var comment = resolution == null ? null : resolution.getComment();

      trx.dsl().mergeInto(LOCAL_ONLY_ISSUES)
        .using(trx.dsl().selectOne())
        .on(LOCAL_ONLY_ISSUES.ID.eq(issue.getId()))
        .whenMatchedThenUpdate()
        .set(LOCAL_ONLY_ISSUES.CONFIGURATION_SCOPE_ID, configurationScopeId)
        .set(LOCAL_ONLY_ISSUES.SERVER_RELATIVE_PATH, issue.getServerRelativePath().toString())
        .set(LOCAL_ONLY_ISSUES.RULE_KEY, issue.getRuleKey())
        .set(LOCAL_ONLY_ISSUES.MESSAGE, issue.getMessage())
        .set(LOCAL_ONLY_ISSUES.RESOLUTION_STATUS, resolutionStatus)
        .set(LOCAL_ONLY_ISSUES.RESOLUTION_DATE, resolutionDate)
        .set(LOCAL_ONLY_ISSUES.COMMENT, comment)
        .set(LOCAL_ONLY_ISSUES.START_LINE, startLine)
        .set(LOCAL_ONLY_ISSUES.START_LINE_OFFSET, startLineOffset)
        .set(LOCAL_ONLY_ISSUES.END_LINE, endLine)
        .set(LOCAL_ONLY_ISSUES.END_LINE_OFFSET, endLineOffset)
        .set(LOCAL_ONLY_ISSUES.TEXT_RANGE_HASH, textRangeHash)
        .set(LOCAL_ONLY_ISSUES.LINE, line)
        .set(LOCAL_ONLY_ISSUES.LINE_HASH, lineHash)
        .whenNotMatchedThenInsert(
          LOCAL_ONLY_ISSUES.ID,
          LOCAL_ONLY_ISSUES.CONFIGURATION_SCOPE_ID,
          LOCAL_ONLY_ISSUES.SERVER_RELATIVE_PATH,
          LOCAL_ONLY_ISSUES.RULE_KEY,
          LOCAL_ONLY_ISSUES.MESSAGE,
          LOCAL_ONLY_ISSUES.RESOLUTION_STATUS,
          LOCAL_ONLY_ISSUES.RESOLUTION_DATE,
          LOCAL_ONLY_ISSUES.COMMENT,
          LOCAL_ONLY_ISSUES.START_LINE,
          LOCAL_ONLY_ISSUES.START_LINE_OFFSET,
          LOCAL_ONLY_ISSUES.END_LINE,
          LOCAL_ONLY_ISSUES.END_LINE_OFFSET,
          LOCAL_ONLY_ISSUES.TEXT_RANGE_HASH,
          LOCAL_ONLY_ISSUES.LINE,
          LOCAL_ONLY_ISSUES.LINE_HASH)
        .values(
          issue.getId(),
          configurationScopeId,
          issue.getServerRelativePath().toString(),
          issue.getRuleKey(),
          issue.getMessage(),
          resolutionStatus,
          resolutionDate,
          comment,
          startLine,
          startLineOffset,
          endLine,
          endLineOffset,
          textRangeHash,
          line,
          lineHash)
        .execute();
    });
  }

  public boolean removeIssue(UUID issueId) {
    var deleted = database
      .deleteFrom(LOCAL_ONLY_ISSUES)
      .where(LOCAL_ONLY_ISSUES.ID.eq(issueId))
      .execute();
    return deleted > 0;
  }

  public boolean removeAllIssuesForFile(String configurationScopeId, Path filePath) {
    var deleted = database
      .deleteFrom(LOCAL_ONLY_ISSUES)
      .where(LOCAL_ONLY_ISSUES.CONFIGURATION_SCOPE_ID.eq(configurationScopeId)
        .and(LOCAL_ONLY_ISSUES.SERVER_RELATIVE_PATH.eq(filePath.toString())))
      .execute();
    return deleted > 0;
  }

  public Optional<LocalOnlyIssue> find(UUID issueId) {
    var issue = database
      .selectFrom(LOCAL_ONLY_ISSUES)
      .where(LOCAL_ONLY_ISSUES.ID.eq(issueId))
      .fetchOne();
    return issue == null ? Optional.empty() : Optional.of(recordToLocalOnlyIssue(issue));
  }

  public void purgeIssuesOlderThan(Instant limit) {
    var limitDateTime = LocalDateTime.ofInstant(limit, ZoneOffset.UTC);
    database
      .deleteFrom(LOCAL_ONLY_ISSUES)
      .where(LOCAL_ONLY_ISSUES.RESOLUTION_DATE.isNotNull()
        .and(LOCAL_ONLY_ISSUES.RESOLUTION_DATE.le(limitDateTime)))
      .execute();
  }

  private static LocalOnlyIssue recordToLocalOnlyIssue(Record rec) {
    var id = rec.get(LOCAL_ONLY_ISSUES.ID);
    var serverRelativePath = Path.of(rec.get(LOCAL_ONLY_ISSUES.SERVER_RELATIVE_PATH));
    var ruleKey = rec.get(LOCAL_ONLY_ISSUES.RULE_KEY);
    var message = rec.get(LOCAL_ONLY_ISSUES.MESSAGE);

    var textRangeWithHash = getTextRangeWithHash(rec);
    var lineWithHash = getLineWithHash(rec);

    LocalOnlyIssueResolution resolution = null;
    var resolutionStatus = rec.get(LOCAL_ONLY_ISSUES.RESOLUTION_STATUS);
    var resolutionDate = rec.get(LOCAL_ONLY_ISSUES.RESOLUTION_DATE);
    if (resolutionStatus != null && resolutionDate != null) {
      var status = IssueStatus.valueOf(resolutionStatus);
      var instant = resolutionDate.toInstant(ZoneOffset.UTC);
      var comment = rec.get(LOCAL_ONLY_ISSUES.COMMENT);
      resolution = new LocalOnlyIssueResolution(status, instant, comment);
    }

    return new LocalOnlyIssue(id, serverRelativePath, textRangeWithHash, lineWithHash, ruleKey, message, resolution);
  }

  private static LineWithHash getLineWithHash(Record rec) {
    var line = rec.get(LOCAL_ONLY_ISSUES.LINE);
    if (line == null) {
      return null;
    }
    var hash = rec.get(LOCAL_ONLY_ISSUES.LINE_HASH);
    return new LineWithHash(line, hash);
  }

  private static TextRangeWithHash getTextRangeWithHash(Record rec) {
    var startLine = rec.get(LOCAL_ONLY_ISSUES.START_LINE);
    if (startLine == null) {
      return null;
    }
    var endLine = rec.get(LOCAL_ONLY_ISSUES.END_LINE);
    var startLineOffset = rec.get(LOCAL_ONLY_ISSUES.START_LINE_OFFSET);
    var endLineOffset = rec.get(LOCAL_ONLY_ISSUES.END_LINE_OFFSET);
    var hash = rec.get(LOCAL_ONLY_ISSUES.TEXT_RANGE_HASH);
    if (hash == null) {
      return null;
    }
    return new TextRangeWithHash(startLine, startLineOffset, endLine, endLineOffset, hash);
  }
}
