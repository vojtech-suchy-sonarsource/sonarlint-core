/*
ACR-5f42a2cb01e24b64b625a46af9fcb7ca
ACR-62813450337f4feb806ae63e53f38519
ACR-ef1f19ddfbf64dc5b6f958398e71097e
ACR-524fe82f68e84044a248703edd228eb8
ACR-51674d2cb7ff4a8eb9efbcbd4c4b1af8
ACR-9be4bca2231c45e7b7830370dcf6ad79
ACR-7585238c19054331aa0e8aa3ed6d851e
ACR-71e925c872804326be5138ac7783451a
ACR-bc03def3d34e4ddb922b2b919ed4738f
ACR-89136c9e252e490eb5dc128c34190d15
ACR-78f6e8f45833408ba2f9f782d2160466
ACR-abcdfb17c4fc460b863fb99f3fdf65fd
ACR-53c7ab1df04b4344b21b525d7f916ec4
ACR-9a9d247907444a909b6f6121a741c5b0
ACR-889ebf2be2e54cfb864e8b3fca71cced
ACR-7fad37410f6543cb872c85a25b19275f
ACR-1826fa6a29464a039a6b1a71bc5c2a4b
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

  /*ACR-e29736860af24de2bf47faabb3f5315c
ACR-af2f4663823c420f91c9be62cb62ef92
ACR-62f62f7f482c4476a7a03f6638871ff7
   */
  @Deprecated(forRemoval = true)
  public boolean isFromSharedConfiguration() {
    return isFromSharedConfiguration;
  }

  public BindingSuggestionOrigin getOrigin() {
    return origin;
  }
}
