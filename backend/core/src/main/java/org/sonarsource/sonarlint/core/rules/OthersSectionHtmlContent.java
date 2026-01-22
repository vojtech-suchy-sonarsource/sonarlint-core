/*
ACR-6472a7e1d9c749d48ef5af67b66eafe8
ACR-d859e2941883410395a82c66fb57f8d2
ACR-db2a5f132616497486901024bd36867b
ACR-cf35bf2493a747baa351b7e7a7372d08
ACR-820b13435f5c4e3f9151470fbb3a469f
ACR-b487392a33514b9189fd847093fc3d54
ACR-e8e6653fa1904a88a3a7ecf36083a70f
ACR-c1ad12772f984289a5840d11d7e7187f
ACR-3a0cece71bd1479b98399aa425ed5917
ACR-b50565279a0f4c499e3cd54f1a36e323
ACR-b6232efc52d549d894c6c1a8ebabaa86
ACR-bf6937a4fc0243c5af34e408143b27c2
ACR-07c3e7b044bd4e8686c26c2d612a3166
ACR-0d6f0e27e74540a6875b79db985f5fff
ACR-7735f46569ac409692e19ac87d0c21c0
ACR-78f29b07a79844ba8abad77aca647cca
ACR-03989cdd678047d4b386539a57cfeb7a
 */
package org.sonarsource.sonarlint.core.rules;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;

public class OthersSectionHtmlContent {

  private static final String FOLDER_NAME = "/context-rule-description/";
  private static final String FILE_EXTENSION = ".html";
  private static final String UNSUPPORTED_RULE_DESCRIPTION_FOR_CONTEXT_KEY = "Unsupported rule description for context key: ";
  private static final String ERROR_READING_FILE_CONTENT = "Could not read the content for rule description for context key: ";

  private static final String OTHERS_SECTION_HTML_CONTENT_KEY = "others_section_html_content";
  private OthersSectionHtmlContent() {}

  public static String getHtmlContent() {
    try (var htmlContentFile = OthersSectionHtmlContent.class.getResourceAsStream(FOLDER_NAME +
      OTHERS_SECTION_HTML_CONTENT_KEY + FILE_EXTENSION)) {
      if (htmlContentFile == null) {
        SonarLintLogger.get().info(UNSUPPORTED_RULE_DESCRIPTION_FOR_CONTEXT_KEY + OTHERS_SECTION_HTML_CONTENT_KEY);
        return "";
      }

      return IOUtils.toString(htmlContentFile, StandardCharsets.UTF_8).trim().replaceAll("\\r\\n?", "\n");
    } catch (IOException ioException) {
      SonarLintLogger.get().error(ERROR_READING_FILE_CONTENT + OTHERS_SECTION_HTML_CONTENT_KEY, ioException);
      return "";
    }
  }

}
