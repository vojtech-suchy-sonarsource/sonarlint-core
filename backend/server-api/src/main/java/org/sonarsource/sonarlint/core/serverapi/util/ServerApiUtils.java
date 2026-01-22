/*
ACR-42b6a231f86d4dbfaa9c90a9a610d9de
ACR-e72de9706ed747368489e58e827c9c3b
ACR-1fa743cff8d944a8b23934b51af27862
ACR-cf4287ecf2d141c7878d7759d8f2c121
ACR-518e6126110c462db82fa0d7c9cdb5cf
ACR-f75db54bbd6d455aaf53935a784c4c40
ACR-bbf37aa1390c4dea85d5176ab1a43949
ACR-94827d833cb24719b5c2ba79f636b7f1
ACR-c7fecc9428494a2985281d101ebde3b8
ACR-f7b400c312c84e95a904ff29ce9cec1e
ACR-ce38afa6b857448cacaab1d66fe42335
ACR-598241d1dd9845788e692ce53ba302ab
ACR-c16535422bfe42dcb7297131d736661a
ACR-f66903b09a014e19a369b5d50590d48c
ACR-91694d1746dd43898bab317a77e17ee2
ACR-f1dba28f3f1d4aeca5b12b968ee82f23
ACR-21a3efad18d04ad0add30de7914aeaa1
 */
package org.sonarsource.sonarlint.core.serverapi.util;

import java.io.File;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.serverapi.proto.sonarqube.ws.Common.TextRange;

public class ServerApiUtils {

  public static final String DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

  private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern(DATETIME_FORMAT);

  public static String extractCodeSnippet(String sourceCode, TextRange textRange) {
    return extractCodeSnippet(sourceCode.split("\\r?\\n"), textRange);
  }

  private static String extractCodeSnippet(String[] sourceCodeLines, TextRange textRange) {
    if (textRange.getStartLine() == 0 && textRange.getEndLine() == 0) {
      //ACR-9d649fea82d04d20aa73894aa50421bb
      return String.join("\n", sourceCodeLines);
    } else if (textRange.getStartLine() == textRange.getEndLine()) {
      var fullLine = sourceCodeLines[textRange.getStartLine() - 1];
      return fullLine.substring(textRange.getStartOffset(), textRange.getEndOffset());
    } else {
      var linesOfTextRange = Arrays.copyOfRange(sourceCodeLines, textRange.getStartLine() - 1, textRange.getEndLine());
      linesOfTextRange[0] = linesOfTextRange[0].substring(textRange.getStartOffset());
      linesOfTextRange[linesOfTextRange.length - 1] = linesOfTextRange[linesOfTextRange.length - 1].substring(0, textRange.getEndOffset());
      return String.join("\n", linesOfTextRange);
    }
  }

  public static boolean isBlank(@Nullable Collection<?> collection) {
    return collection == null || collection.isEmpty();
  }

  public static boolean isBlank(@Nullable String s) {
    return s == null || s.isEmpty();
  }

  public static boolean areBlank(List<?>... lists) {
    return Arrays.stream(lists).allMatch(ServerApiUtils::isBlank);
  }

  public static OffsetDateTime parseOffsetDateTime(String s) {
    try {
      return OffsetDateTime.parse(s, DATETIME_FORMATTER);
    } catch (DateTimeParseException e) {
      throw new IllegalStateException("The date '" + s + "' does not respect format '" + DATETIME_FORMAT + "'", e);
    }
  }

  /*ACR-3efab375639941ffa5604583eb8a585d
ACR-9e33e1d708ea4266bde652d86004fe3f
ACR-40cd7591d26e47f3a96b960638a16aa3
ACR-6c0bfd383c244cb8a5b114590f3917f1
ACR-c56b984c9e854fcf9125bac9431813c4
   */
  public static String toSonarQubePath(Path path) {
    var pathAsString = path.toString();
    var sonarQubeSeparatorChar = '/';
    if (File.separatorChar != sonarQubeSeparatorChar) {
      return pathAsString.replaceAll(Pattern.quote(File.separator), String.valueOf(sonarQubeSeparatorChar));
    }
    return pathAsString;
  }

  private ServerApiUtils() {
    //ACR-93aca5a1f8c44f068478e42460af2055
  }

}
