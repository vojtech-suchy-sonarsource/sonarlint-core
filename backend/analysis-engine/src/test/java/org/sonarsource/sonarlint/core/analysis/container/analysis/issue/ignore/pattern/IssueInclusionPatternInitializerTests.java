/*
ACR-eef0228d0f1146ea9202ea6b4994ac7e
ACR-ba3c13249eac42bca9cf986abe42758d
ACR-412fc9b6e1a044e1adaa481602fd637e
ACR-c56648e02f504fe8ab20e9d5245dbc80
ACR-1ff821d52f1747fc8e67ee693e93af89
ACR-9e7f00c6270742e8b92dfd4add4de2e7
ACR-3d934c0280044e2590b5e619ae1f45ca
ACR-7b7232705e50483d9fcc310e977c647a
ACR-4369f244a5c34b1294e2641d58efc67c
ACR-df19c9df9bde47dbb8f8d39d3281668f
ACR-0e60f05c1553468a9be54fd117bc07dd
ACR-b58a9257921748dba017a093768f6401
ACR-8b5b9524f6554f8f9f31cfd000e3ff50
ACR-bf723771747543d5bb1d6691a1ae6ec6
ACR-b18f8fe15e9641db9d3cd9f68615d8c2
ACR-85ddd022c12a4826a6179d15d285356e
ACR-19d409b7620d4c749a44afac252729b0
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.issue.ignore.pattern;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.sonarsource.sonarlint.core.plugin.commons.sonarapi.MapSettings;

import static org.assertj.core.api.Assertions.assertThat;

class IssueInclusionPatternInitializerTests {

  @Test
  void testNoConfiguration() {
    var patternsInitializer = new IssueInclusionPatternInitializer(new MapSettings(Collections.emptyMap()).asConfig());
    patternsInitializer.initPatterns();
    assertThat(patternsInitializer.hasConfiguredPatterns()).isFalse();
  }

  @Test
  void shouldHavePatternsBasedOnMulticriteriaPattern() {
    Map<String, String> settings = new HashMap<>();
    settings.put("sonar.issue.enforce" + ".multicriteria", "1,2");
    settings.put("sonar.issue.enforce" + ".multicriteria" + ".1." + "resourceKey", "org/foo/Bar.java");
    settings.put("sonar.issue.enforce" + ".multicriteria" + ".1." + "ruleKey", "*");
    settings.put("sonar.issue.enforce" + ".multicriteria" + ".2." + "resourceKey", "org/foo/Hello.java");
    settings.put("sonar.issue.enforce" + ".multicriteria" + ".2." + "ruleKey", "checkstyle:MagicNumber");
    var patternsInitializer = new IssueInclusionPatternInitializer(new MapSettings(settings).asConfig());
    patternsInitializer.initPatterns();

    assertThat(patternsInitializer.hasConfiguredPatterns()).isTrue();
    assertThat(patternsInitializer.hasMulticriteriaPatterns()).isTrue();
    assertThat(patternsInitializer.getMulticriteriaPatterns().size()).isEqualTo(2);
  }

}
