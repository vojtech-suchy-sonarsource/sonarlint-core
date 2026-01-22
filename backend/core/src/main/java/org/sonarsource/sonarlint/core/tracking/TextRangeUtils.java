/*
ACR-5e2cb834351044d5830e1e61518cc421
ACR-b03eb52764794dfca04987b8fc706474
ACR-0aa43c4f4779400c87f28d01b128ace6
ACR-c34f66e2ab754017bb8f7365fd162533
ACR-63f285e67baa468781edfe8d3382340e
ACR-dccf8c510d3c40a98f90e92e260c23f6
ACR-bedef04f2bd94df0a86d7a3c0b580d4b
ACR-85606e260fe54dfabe2d1ee1720fb534
ACR-957c523d06544b25b9630a5a94e7be55
ACR-e37190717e8b4cbe869fc735825af3b0
ACR-bf0a1822ac9b433fb49e4a3d83037d0a
ACR-cafbb2ff82ee4f14a39b9b1ce254098a
ACR-c26739631459466cadeb786382f23d6f
ACR-6b9a97c241c54041b08aed38474fc282
ACR-7e5535c8d5034d27ac6e3ed5f8fdcd8c
ACR-ac1cd2113d8a451c989dfbc9952e5ea6
ACR-c28436b0b1e6448d9da01f3f34e1d0ad
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
    //ACR-fce5f3866ec34256ba01121309402aca
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
