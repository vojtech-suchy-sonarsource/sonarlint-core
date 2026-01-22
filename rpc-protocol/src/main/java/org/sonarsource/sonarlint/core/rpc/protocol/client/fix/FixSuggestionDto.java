/*
ACR-994d601c966c45a3bdd17417ec35d98c
ACR-0a4d61124cf44f0399782d81bde9da9b
ACR-753114e1feed4d18b0634d6c628de6d8
ACR-b732d7d09b05451e8a49f4357176e543
ACR-c28f2342b8a4431b80e1e0267217ad6e
ACR-e7cd122c9ac44dfe86a7fbdfa1585b85
ACR-e5951bd5d09a4a9498a03c4fe83d2592
ACR-53e6db74280849029bb2b746135c07bc
ACR-9d3e8b2e0d7b4834b981455336f4c7fa
ACR-46471457ec5d42dea08b83ac8f1d3424
ACR-44d6e762461547519fda83b5e288b705
ACR-a08dd1167204452bb948174d56140440
ACR-0d67fb010a6143378f36897ddd1ae9df
ACR-bfc2ee22b5e848abb7259ff13054cce6
ACR-ea92e0be4a224f61931eabf8bc6053f3
ACR-354eaadb387f4046b4d80bbb689ff8bb
ACR-00c4de7ae5c04e3cb2122dfb26c56daa
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.fix;

public class FixSuggestionDto {

  private final String suggestionId;
  private final String explanation;
  private final FileEditDto fileEdit;

  public FixSuggestionDto(String suggestionId, String explanation, FileEditDto fileEdit) {
    this.suggestionId = suggestionId;
    this.explanation = explanation;
    this.fileEdit = fileEdit;
  }

  public String suggestionId() {
    return suggestionId;
  }

  public String explanation() {
    return explanation;
  }

  public FileEditDto fileEdit() {
    return fileEdit;
  }

}
