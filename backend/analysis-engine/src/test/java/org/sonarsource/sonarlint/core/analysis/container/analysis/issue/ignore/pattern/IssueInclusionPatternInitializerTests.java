/*
ACR-7960f230dd51454eb6d98b9fd8e9e53f
ACR-bb5a8b563a514e5b9146ad50c3d9237b
ACR-429f6449bdd9431d801f73edccc063ee
ACR-7093679459044b16a2bdc8bcd1aa6326
ACR-12444d38b5004e3c9660b4422fafdce7
ACR-7fbbe055753e421ea0cd788e2866180d
ACR-d81eaf59b4354e0fa05acd7d8e2fc46a
ACR-56f501ef890e40ff83e31a88e917876a
ACR-68355fad4a914a0eb228b29bcdd85af6
ACR-8ea1b0d530174e0d8b8dd653c46718db
ACR-14ddb4ccbbf941139e6a2f9e55805926
ACR-a61f0cd58056451396dead17a225d0a1
ACR-689d625f09d847e8aa62c7dc6b8bec01
ACR-a138dea596994c008c0b6e1a7f383243
ACR-e9e18191b6ee431abe810f3231b2e645
ACR-8c6e58672c71461b875069bc370f3dd7
ACR-7106b51e1ff24e64bce7677acd56872c
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
