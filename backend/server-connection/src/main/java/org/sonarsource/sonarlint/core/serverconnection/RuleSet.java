/*
ACR-79a3cd62491a4669b7adcb8ace3c6a61
ACR-c8608eb9097b4e768efec178dff7b36b
ACR-cab2fea0906e41a4865c9e4c2630e685
ACR-737a627c908540ba93a5282400c41a9a
ACR-a20c2201fdda42b1af3059a5b1311287
ACR-69c2b63c2ea8418cba0b7bdb3a7122c5
ACR-1f1fd7c9322e459d90f31b1129d67b11
ACR-be164a558e134478abe6ce3d86dc0a2c
ACR-f071fbd44da1457e8aacad88ef5d173f
ACR-3c2f19aac9224e41ada73f90aa3ec065
ACR-8f88859aaa834f7982effef97045bf37
ACR-e17c024f0efe446889b5fc8d17828cfc
ACR-5d7f10ca2a274dd6a9dc68c1f4be98cc
ACR-ddee3b3d894c4053ba98e514541aa189
ACR-7e520e9a702a465b951e77e270f316d9
ACR-3cbc75e06cbf4aee887a6f5d6dee34b8
ACR-078136af043c4b4fb764360f287eb699
 */
package org.sonarsource.sonarlint.core.serverconnection;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.sonarsource.sonarlint.core.serverapi.rules.ServerActiveRule;

public class RuleSet {
  private final Collection<ServerActiveRule> rules;
  private final Map<String, ServerActiveRule> rulesByKey;
  private final String lastModified;

  public RuleSet(Collection<ServerActiveRule> rules, String lastModified) {
    this.rules = rules;
    this.rulesByKey = rules.stream().collect(Collectors.toMap(ServerActiveRule::getRuleKey, Function.identity()));
    this.lastModified = lastModified;
  }

  public Collection<ServerActiveRule> getRules() {
    return rules;
  }

  public Map<String, ServerActiveRule> getRulesByKey() {
    return rulesByKey;
  }

  public String getLastModified() {
    return lastModified;
  }
}
