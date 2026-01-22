/*
ACR-e507d5506ae44593b5f15d13c22a0f45
ACR-5c7a90ded0bd4d828913c7e0484f4f57
ACR-2bc7afebf7b94e1a8bd29d2ccd15491d
ACR-85f6f9876b4640058238a075df010e5d
ACR-98545d044cbc4ef4bbd3fb5f5cf8e2a0
ACR-27308eec1afe4841a4f9f5203943d720
ACR-3cf0625f7a8e457d9bdc852cc10294b4
ACR-c07dabc8f33f42fe9f2f7f102e33b4b4
ACR-69670bb05ccd45aea8a3a2ff93ce6377
ACR-79a036ea0c6d41c5965c9605b05746f0
ACR-0e032117331b404fbcbe8ed26dc8ddbf
ACR-03edd949f18d452dba4eabfa800ef987
ACR-fe5218d1208a41e98012209c57dd47b7
ACR-1a3c3fb4f32b46949d7870f5527478ff
ACR-fd3a0c83977a4ea2b7112af27a3afec4
ACR-330c5cf904d74ab48c79d827a4b468fe
ACR-c232f92a4f284999aaeb0e3d5b386d60
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.issue.ignore.pattern;

import org.sonar.api.config.Configuration;

public class IssueInclusionPatternInitializer extends AbstractPatternInitializer {

  public static final String INCLUSION_KEY_PREFIX = "sonar.issue.enforce";

  public IssueInclusionPatternInitializer(Configuration config) {
    super(config);
  }

  @Override
  protected String getMulticriteriaConfigurationKey() {
    return INCLUSION_KEY_PREFIX + ".multicriteria";
  }

}
