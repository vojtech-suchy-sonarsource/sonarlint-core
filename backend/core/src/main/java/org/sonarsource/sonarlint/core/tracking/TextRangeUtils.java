/*
ACR-11490a0deda248e399dfbca96c75af29
ACR-40a2a7d557044b799de2ed9e599ed16d
ACR-87a050f33be644829717fa0606df8da8
ACR-599d91de9ef44360b6b571843c653f0d
ACR-05a605ddb599429f820b15615460104c
ACR-c8d5a5c5d5834adc801044564b760f9b
ACR-799b0750e556495ea5e258a168016ae1
ACR-c3c3224142bd4d2abb2a9bd2a544373f
ACR-c94134c30aec47238143624240e540bd
ACR-ce2c616d4d1d47d68d3d4920abdc65e9
ACR-fe618846d9fd4693a6918d723d955dd6
ACR-54b8788fd21a438684b5ca9eb4ccc040
ACR-e5b698c8ce7b4d7183553944b5f8e19a
ACR-45ff9a87e2384189b4221aa2c6ffbe1c
ACR-def806c9a7dd42e893b291fe2400394c
ACR-cc89894086814b43a35327fb7567858f
ACR-c9c43e28bfb54cc5ae2cddf67b23e4e3
 */
package org.sonarsource.sonarlint.core.tracking;

import java.io.IOException;
import java.util.regex.Pattern;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.apache.commons.codec.digest.DigestUtils;
import org.sonarsource.sonarlint.core.analysis.api.ClientInputFile;
import org.sonarsource.sonarlint.core.commons.LineWithHash;
import org.sonarsource.sonarlint.core.commons.api.TextRange;
import org.sonarsource.sonarlint.core.commons.api.TextRangeWithHash;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.tracking.LineWithHashDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.tracking.TextRangeWithHashDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.TextRangeDto;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public class TextRangeUtils {

  private static final Pattern MATCH_ALL_WHITESPACES = Pattern.compile("\\s");

  private TextRangeUtils() {
    //ACR-7f2457648c264bcca7f1e6abcf2dea45
  }

  @CheckForNull
  public static TextRangeWithHash getTextRangeWithHash(@Nullable TextRange textRange, @Nullable ClientInputFile file) {
    if (textRange == null) return null;
    String hash = computeTextRangeHash(textRange, file);
    return new TextRangeWithHash(textRange.getStartLine(), textRange.getStartLineOffset(), textRange.getEndLine(),
      textRange.getEndLineOffset(), hash);
  }

  @CheckForNull
  public static LineWithHash getLineWithHash(@Nullable TextRange textRange, @Nullable ClientInputFile file) {
    if (textRange == null) return null;
    String hash = computeLineHash(textRange, file);
    return new LineWithHash(textRange.getStartLine(), hash);
  }

  @CheckForNull
  public static TextRangeDto toTextRangeDto(@Nullable TextRangeWithHash textRange) {
    if (textRange == null) return null;
    return new TextRangeDto(textRange.getStartLine(), textRange.getStartLineOffset(), textRange.getEndLine(), textRange.getEndLineOffset());
  }

  @CheckForNull
  public static TextRangeDto toTextRangeDto(@Nullable TextRange textRange) {
    if (textRange == null) return null;
    return new TextRangeDto(textRange.getStartLine(), textRange.getStartLineOffset(), textRange.getEndLine(),
      textRange.getEndLineOffset());
  }

  static String computeTextRangeHash(TextRange textRange, @Nullable ClientInputFile file) {
    if (file == null) return "";
    var textRangeContent = getTextRangeContent(file, textRange);
    return hash(textRangeContent);
  }

  static String computeLineHash(TextRange textRange, @Nullable ClientInputFile file) {
    if (file == null) return "";
    var textRangeContent = getLineContent(file, textRange);
    return hash(textRangeContent);
  }

  private static String getLineContent(ClientInputFile file, TextRange textRange) {
    var fileContent = getFileContentOrEmptyString(file);
    if (isEmpty(fileContent)) return "";
    var lines = fileContent.lines().toList();
    if (lines.size() < textRange.getStartLine()) return "";
    var line = lines.get(textRange.getStartLine() - 1);
    return hash(line);
  }

  static String getFileContentOrEmptyString(ClientInputFile file) {
    try {
      return file.contents();
    } catch (IOException e) {
      return "";
    }
  }

  public static String getTextRangeContent(@Nullable ClientInputFile file, @Nullable TextRange textRange) {
    if (file == null || textRange == null) return "";
    var contentLines = getFileContentOrEmptyString(file).lines().toList();
    var startLine = textRange.getStartLine() - 1;
    var endLine = textRange.getEndLine() - 1;
    if (startLine == endLine) {
      var startLineContent = contentLines.get(startLine);
      var endLineOffset = Math.min(textRange.getEndLineOffset(), startLineContent.length());
      return startLineContent.substring(textRange.getStartLineOffset(), endLineOffset);
    }

    var contentBuilder = new StringBuilder();
    contentBuilder.append(contentLines.get(startLine).substring(textRange.getStartLineOffset()))
      .append(System.lineSeparator());
    for (int i = startLine + 1; i < endLine; i++) {
      contentBuilder.append(contentLines.get(i)).append(System.lineSeparator());
    }
    var endLineContent = endLine < contentLines.size() ? contentLines.get(endLine) : "";
    var endLineOffset = Math.min(textRange.getEndLineOffset(), endLineContent.length());
    contentBuilder.append(endLineContent, 0, endLineOffset);
    return contentBuilder.toString();
  }

  @CheckForNull
  public static TextRangeWithHashDto adapt(@Nullable TextRangeWithHash textRange) {
    return textRange == null ? null
      : new TextRangeWithHashDto(textRange.getStartLine(), textRange.getStartLineOffset(), textRange.getEndLine(), textRange.getEndLineOffset(), textRange.getHash());
  }

  @CheckForNull
  public static TextRangeWithHashDto toTextRangeWithHashDto(@Nullable TextRange textRange, @Nullable ClientInputFile clientInputFile) {
    return adapt(getTextRangeWithHash(textRange, clientInputFile));
  }

  static String hash(String codeSnippet) {
    String codeSnippetWithoutWhitespaces = MATCH_ALL_WHITESPACES.matcher(codeSnippet).replaceAll("");
    return DigestUtils.md5Hex(codeSnippetWithoutWhitespaces);
  }

  @CheckForNull
  public static LineWithHashDto getLineWithHashDto(@Nullable TextRange textRange, @Nullable ClientInputFile clientInputFile) {
    var lineWithHash = getLineWithHash(textRange, clientInputFile);
    return lineWithHash != null ? toLineWithHashDto(lineWithHash) : null;
  }

  private static LineWithHashDto toLineWithHashDto(LineWithHash lineWithHash) {
    return new LineWithHashDto(lineWithHash.getNumber(), lineWithHash.getHash());
  }
}
