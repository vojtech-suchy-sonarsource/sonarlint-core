/*
ACR-493971745ac447c7a9a6e8ab0d4c8c88
ACR-c15153afc02b4991a025e1573449b833
ACR-b627accf24344d689eb60702ba1fae12
ACR-ccad27b341da44f5916150875e43b082
ACR-10d338d9d96844a7986928822298b3a4
ACR-3a4bd97ac0314cfa8c5eb27eb4426dc7
ACR-b95c38087bf4426488bf5e53c51661ec
ACR-cd489115fb0847f3a53f6784cf44a2cc
ACR-b32ed622b868450b86f9b3f2e8c5487d
ACR-655e5107679b46fd8b4fb126bfae7ef6
ACR-d4fcd51db30647dea5c220f05c01baaa
ACR-4b702900e1654518bbe98d6ddc28bb43
ACR-244ded9727204f7090642355fe1e4d1a
ACR-a53cc917fb6c4b0c906342879cf1e642
ACR-c8d9ed35e5034e3f8b6a7a17c2ba6874
ACR-f53da78219c644bd98711d20a4df4bf1
ACR-e43c8f8f93924af188e12604c52d85cc
 */
package org.sonarsource.sonarlint.core.serverapi.fixsuggestions;

import javax.annotation.Nullable;

public record AiSuggestionRequestBodyDto(@Nullable String organizationKey, String projectKey, Issue issue) {
  public record Issue(String message, Integer startLine, Integer endLine, String ruleKey, String sourceCode) {
  }
}
