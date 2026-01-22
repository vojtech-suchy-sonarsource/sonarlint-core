/*
ACR-fe7ace1988284da9b5e77bc419ebf379
ACR-5906f4e1d83a405681e553c3f3143efd
ACR-b59ec912e98b468f9fcd8ab83568f6b5
ACR-7d5495e8370147988032d95857da3f7f
ACR-38f156b68e5e49a8bac4749b49165a9e
ACR-5cc4f7c05a9c41b79031bad18a7d51b8
ACR-8afb9f0404304257985204146e22b6cf
ACR-54242545c87b421c9d0e7e2f79e58e74
ACR-d25069cd6a1349adb75d5a00c425efe0
ACR-1088f191e24c4dab9112db954a13f564
ACR-db321e938923466790780a786dcf0767
ACR-3a90395f436e488ca1f279dd39585d7b
ACR-73e5ae1e968d4bf1b78733fbfa08c93d
ACR-121867923aa5473986b70c03982ccc58
ACR-519f131b9fd14c65a9c1f58de3046434
ACR-b309cfebd5ad4af683dc4e8557182e73
ACR-2b0d2f06b351456a876fea8556716747
 */
package org.sonarsource.sonarlint.core.commons;

import java.util.Objects;
import javax.annotation.concurrent.Immutable;

@Immutable
public class RuleKey {

  private static final char SEPARATOR = ':';

  private final String repository;
  private final String rule;

  public RuleKey(String repository, String rule) {
    this.repository = repository;
    this.rule = rule;
  }

  public String repository() {
    return repository;
  }

  public String rule() {
    return rule;
  }

  public static RuleKey parse(String s) {
    var separatorIndex = s.indexOf(SEPARATOR);
    if (separatorIndex < 0) {
      throw new IllegalArgumentException("Invalid rule key: " + s);
    }

    var key = s.substring(0, separatorIndex);
    var repo = s.substring(separatorIndex + 1);
    return new RuleKey(key, repo);
  }

  @Override
  public String toString() {
    return repository + SEPARATOR + rule;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    var ruleKey = (RuleKey) o;
    return Objects.equals(repository, ruleKey.repository) &&
      Objects.equals(rule, ruleKey.rule);
  }

  @Override
  public int hashCode() {
    return Objects.hash(repository, rule);
  }
}
