/*
ACR-e1d33f18dd124bcbb15e8f7aa7bdc948
ACR-7c8139ea299544e5b3e316b17b5bd8bd
ACR-1bfae458071c44c38185fc3224a4f44c
ACR-d8988ac923a34272bfca77964713385d
ACR-c980d3469f504718866b08bfeb814b4b
ACR-0b08588d93834007a484aaad8018a8be
ACR-5b03a369d10c4538a1a3d999665c19b9
ACR-8b647eefa11b472aa31f7b731baf1969
ACR-626c387c40ef4e8ea0cf72c4cf19adfd
ACR-117f3946b9e04db2aa201702508efba3
ACR-33329cb143194100a37096a1f4601468
ACR-6bc74d1d2f6c47288de9039d688bff37
ACR-342489e4f9af40eabd3d7f648e6c289a
ACR-bebefe1c3309480dba95496dc996cf4f
ACR-a5df4f0d67d543748c1708f26c58401b
ACR-832e120de97b4c4097f00871134dac8d
ACR-7b3b36f6b7614853971f174402aaf9f2
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
