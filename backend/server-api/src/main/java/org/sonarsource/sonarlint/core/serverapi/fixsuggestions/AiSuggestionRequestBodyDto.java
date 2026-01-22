/*
ACR-6c9548cc58964cb0a923a2e3d9f9dfbc
ACR-d493b0bf5bb643d4a3862501ba082682
ACR-67ebe56d3ad5400d931032b936c35fba
ACR-3fde5199b7c4485f949d332e51960ff1
ACR-658bc026ca9c47aba31e921b5b6af279
ACR-b11b136f841e44b0aa36e375c5f158b0
ACR-9365c3bad6da472f96217c81282384a8
ACR-d533fe20b2084e12908888ff3b92475a
ACR-48da187638fe4318b91bcc297c460d39
ACR-a299375101ed4b2496f36f4ef88c4f76
ACR-be49d666563d40fa9a2ce304599480f0
ACR-a203f083e0f84948b61227a6d0f58638
ACR-65e127561c5841ab8fed4b2bf735c29e
ACR-167ee36d8ea7412ab39ee30068b76347
ACR-7f9b4fd15455463191e576dd0e0d0c9c
ACR-7002f804ce5d46ed97a77c6a07f82b96
ACR-bcb7d2bed19c47eebd7e21581d500522
 */
package org.sonarsource.sonarlint.core.serverapi.fixsuggestions;

import javax.annotation.Nullable;

public record AiSuggestionRequestBodyDto(@Nullable String organizationKey, String projectKey, Issue issue) {
  public record Issue(String message, Integer startLine, Integer endLine, String ruleKey, String sourceCode) {
  }
}
