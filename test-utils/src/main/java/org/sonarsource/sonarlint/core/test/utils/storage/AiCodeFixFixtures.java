/*
ACR-eb4b82292b8f4f69882973dc4ae78619
ACR-e48fc5fe3ff8467594192a08d64802a5
ACR-6fa28130419c42b3941a539d545020d5
ACR-90b0e44943884a388790fc51ae185fba
ACR-c0eafca15e584f73a94f587e024e4a85
ACR-42f8d41b24154a9b9b3c96bb2c9a45fc
ACR-8e074241ad1044aea122e176c1851943
ACR-b0450dc7aee04e32b55beded6c049ab9
ACR-f1ee1f4fe94c408cba31ecba3438ff6b
ACR-cf0e5497e43941d7b2d2a50aa33771bf
ACR-8a98b98fc6b649bab659951645fa42e3
ACR-c69fbb7260234319a365abad556aeb58
ACR-6a73277cfe344e5f91fbbb6230d7e13e
ACR-dbe79b194db146b897d7d4e70b9eeef1
ACR-4eff78731e844de38d06b26fda1faee3
ACR-f4da805ac7014ced8c04b4026a47e539
ACR-c428808854dd448abbc66eac4f65ffc3
 */
package org.sonarsource.sonarlint.core.test.utils.storage;

import java.util.List;
import java.util.Set;
import org.sonarsource.sonarlint.core.serverconnection.aicodefix.AiCodeFix;
import org.sonarsource.sonarlint.core.serverconnection.aicodefix.AiCodeFixRepository;
import org.sonarsource.sonarlint.core.serverconnection.aicodefix.AiCodeFixFeatureEnablement;

public class AiCodeFixFixtures {
  private AiCodeFixFixtures() {
    //ACR-d879e731024e49608c15aaa8bfd81662
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
