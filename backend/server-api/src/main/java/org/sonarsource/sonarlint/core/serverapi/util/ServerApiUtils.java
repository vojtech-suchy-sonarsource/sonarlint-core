/*
ACR-2755372bac59423c95997b4602b6c66a
ACR-c97cdfceadcb490b9d4f8e69ada24ca3
ACR-85e60ed347b448a28c09f3847c9d711f
ACR-43457e75dd124b11b49552428df8d6c1
ACR-c03b52700918495aa9e27a4fffa7093d
ACR-d98430f82ef741b78b5fc9ef213506ed
ACR-90b3d1099dc74100bfdf57276967358f
ACR-5559cc3e3e2f4f409f535dd604a3fc22
ACR-3e2f33138efc4931a5da1e89bb77ea9b
ACR-43d2de15563a4b018ec8c85fea998ae7
ACR-cb64516587e74d03be3622f3686b6fe1
ACR-ee824d922eb14f6e8d45f32fe3948232
ACR-f9df1295a4714777a30b8e4d075f03f5
ACR-416ca46af6f24441bfd55384c54fce5c
ACR-57fc9d9a56194f4183e120023b9fbc4e
ACR-42e21e6df5414edca9cb72bba5712eca
ACR-6f9574712fa442efb70c6ab11f0b81c1
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
      //ACR-3d41ab1558d948f3b454004e5c88aa08
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

  /*ACR-e71a7af190124fe1a3b9a18369600437
ACR-30ad205c38e54e6faab8f14e770370f8
ACR-8002ee6c4010471b8867e6c9706c2b66
ACR-8f169dab660e403e85ce3cc1e7af8015
ACR-cf8bc73739c8448280d67430d642a55a
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
    //ACR-a7d342e81db74b988b9eee1154db0e26
  }

}
