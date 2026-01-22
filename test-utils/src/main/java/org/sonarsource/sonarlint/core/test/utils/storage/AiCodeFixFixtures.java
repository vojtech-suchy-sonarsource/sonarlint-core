/*
ACR-807201a55f294050917e0a2106e06369
ACR-9eb286e06fe747e7841463394a244a41
ACR-d3ac5fb764614bab93c4c9e8d8b72fae
ACR-cb3c2cf4d9f44755a108e90f9c51761d
ACR-0e7b96c3207447629b5facd8e2cdf0db
ACR-d10b08bcd51249708b41c24761c5816d
ACR-8103c1daa2f84d6d98fce46bbf50b8ef
ACR-454392ebc7c24c5a8f5718557f3efe3c
ACR-1b146875daf7469690756be66125e9a1
ACR-dc8ee38b165f4cde874b02fdd0c9db39
ACR-715e15e441a043ee894cc8abe3e66163
ACR-c94d87d86174440f9db9a64b60945917
ACR-c46cb3d248b7445e888abf7f087b111f
ACR-ad0d5c959cb94ef38abe274f167f9a4e
ACR-9749e94698004e8f839efcd75cddaead
ACR-3e7888494c0346e6b8f6199e3e41683f
ACR-1288af4ba0cb400689aa50ee0eb04c6a
 */
package org.sonarsource.sonarlint.core.test.utils.storage;

import java.util.List;
import java.util.Set;
import org.sonarsource.sonarlint.core.serverconnection.aicodefix.AiCodeFix;
import org.sonarsource.sonarlint.core.serverconnection.aicodefix.AiCodeFixRepository;
import org.sonarsource.sonarlint.core.serverconnection.aicodefix.AiCodeFixFeatureEnablement;

public class AiCodeFixFixtures {
  private AiCodeFixFixtures() {
    //ACR-7febaad465cb4d76ae5666f600bd463c
  }

  public static class Builder {
    private final String connectionId;
    private Set<String> supportedRules = Set.of();
    private boolean organizationEligible = true;
    private AiCodeFixFeatureEnablement enablement = AiCodeFixFeatureEnablement.DISABLED;
    private List<String> enabledProjectKeys = List.of();

    public Builder(String connectionId) {
      this.connectionId = connectionId;
    }

    public Builder withSupportedRules(Set<String> supportedRules) {
      this.supportedRules = supportedRules;
      return this;
    }

    public Builder organizationEligible(boolean organizationEligible) {
      this.organizationEligible = organizationEligible;
      return this;
    }

    public Builder disabled() {
      this.enablement = AiCodeFixFeatureEnablement.DISABLED;
      return this;
    }

    public Builder enabledForProjects(String projectKey) {
      this.enablement = AiCodeFixFeatureEnablement.ENABLED_FOR_SOME_PROJECTS;
      this.enabledProjectKeys = List.of(projectKey);
      return this;
    }

    public Builder enabledForAllProjects() {
      this.enablement = AiCodeFixFeatureEnablement.ENABLED_FOR_ALL_PROJECTS;
      return this;
    }

    public void populate(TestDatabase database) {
      var aiCodeFixRepository = new AiCodeFixRepository(database.dsl());
      aiCodeFixRepository
        .upsert(new AiCodeFix(connectionId, supportedRules, organizationEligible, AiCodeFix.Enablement.valueOf(enablement.name()), Set.copyOf(enabledProjectKeys)));
    }
  }
}
