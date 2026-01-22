/*
ACR-c82557024d6b407caf8b59a89373d1cc
ACR-d1501632f7c74898a85d2f48412f6caf
ACR-72b3aa8756994249ab296b224b1b8dff
ACR-b56edd069dc84e3b9bbd7e2ba9fceb04
ACR-0c34ddff56044257b0a6a08c26dc7b9a
ACR-49947095abb84ab6b3374ae2d7b7f0a1
ACR-6504cd76c5074256acb3bde68e43d06f
ACR-d707b7e039e645e184ea5f9a6d4ba3b2
ACR-2e7a4eeed2cb4255bd42fb9e67550a6d
ACR-0fb3518a234f4be7b00ed86f4e71aeb5
ACR-4ee6279ce28c4f4b81484d32824eaaca
ACR-1a65f4c1de4a47d7a97783d241222687
ACR-fd57499545094cbb90f3be0f3afdd23f
ACR-ea1114cf6dd943d989339c60e0263c04
ACR-cabcb794c240412f91776ea43c490dd3
ACR-edfa06fe1b654d7294cc0730629f60cf
ACR-e0596efabea2465ba9d447022831f9de
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.config.binding;


public class BindingSuggestionDto {
  private final String connectionId;
  private final String sonarProjectKey;
  private final String sonarProjectName;
  @Deprecated(forRemoval = true)
  private final boolean isFromSharedConfiguration;
  private final BindingSuggestionOrigin origin;

  public BindingSuggestionDto(String connectionId, String sonarProjectKey, String sonarProjectName, BindingSuggestionOrigin origin) {
    this.connectionId = connectionId;
    this.sonarProjectKey = sonarProjectKey;
    this.sonarProjectName = sonarProjectName;
    this.isFromSharedConfiguration = origin == BindingSuggestionOrigin.SHARED_CONFIGURATION;
    this.origin = origin;
  }


  public String getConnectionId() {
    return connectionId;
  }

  public String getSonarProjectKey() {
    return sonarProjectKey;
  }

  public String getSonarProjectName() {
    return sonarProjectName;
  }

  /*ACR-0d8fd07576274b1283e1eafc8dd18961
ACR-fd7fb2009c484647a14d3576519e7cd2
ACR-0c05946157a64470ad6329f50d5a63a0
   */
  @Deprecated(forRemoval = true)
  public boolean isFromSharedConfiguration() {
    return isFromSharedConfiguration;
  }

  public BindingSuggestionOrigin getOrigin() {
    return origin;
  }
}
