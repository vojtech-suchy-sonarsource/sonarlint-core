/*
ACR-fdd21b89ced0403f88422b89898c2cee
ACR-22750e559a5f4d2690597b0c06528472
ACR-2874cc003deb474ab04cbcc2c0619a9d
ACR-d84447c2bcb84929b031167da0aab4f5
ACR-11b43d16ba7d4125a21f2adcadfda184
ACR-1a80f5c8ada046db86b900de603be9e4
ACR-5df05eeca0bc48cfad4088b2dc7f0cfe
ACR-083d6177bd1a4a59969673a4bb13f102
ACR-336c7232146a4c21bfb1394112c34c55
ACR-bda8f9857de349dabc82514b65b056bf
ACR-838c06ae0cdf4276a745d6c7603d6724
ACR-b568bcc8f9504c36b3a4d6d1a82f527b
ACR-c0d62ddf0cd24d0fb23fdc4d5a7bccc3
ACR-71019548ace24d12ac2a428a9b7817fc
ACR-6c34b14146c34df4b41869ef05fdad23
ACR-96bc04e80d9e467892de6eb53faa1dd4
ACR-46d8190102bc4495afb837b400a444e2
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
