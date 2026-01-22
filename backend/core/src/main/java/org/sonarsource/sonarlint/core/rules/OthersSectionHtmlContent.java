/*
ACR-dfad0da855ce4f5c97436649e5366b05
ACR-7f3a58fcbfc845d78c1350f092a20104
ACR-03343b91640641ecb7ab22707bf6bd22
ACR-09530ec9cae344e1a13648e9d51a42a2
ACR-c01fced1c6884cd18108bb90c4b6978c
ACR-26f95688092d452497218d2779b3c36f
ACR-0f32173b858b43928fa25795b232fe1e
ACR-99a335e40a6e4352badaf059c4fd55a9
ACR-2e081f06dc264ca0b16e970894d0fb4e
ACR-9093cac04bca4d9d8a2a5b8c5b1fbf25
ACR-83e78da912d54f16b51ca65870f79fea
ACR-09442e16df084d498a68f7f475b9fca4
ACR-cd3e5c6f20f64cd8bb90f9fd7dad10ef
ACR-d25a148eb2c342dbb419f521761cc2b1
ACR-0e64461c3ef847d8aacd484b62b3d4d8
ACR-17f6b6c974954056b0a76bb915c982d9
ACR-5892efe1aea045e69eb2d76853da04e2
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
