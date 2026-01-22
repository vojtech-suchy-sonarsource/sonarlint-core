/*
ACR-25d58eccf07b4f2b822cf38fd7a7094d
ACR-37bebba7a5444da69cb73c35ee0982e8
ACR-6889482cf81d4cbeb17f33e5de524cba
ACR-c2aa10ec448f42c48884f4f4172445dd
ACR-b89a0ef25ea245b19c859bb77f8b6ef1
ACR-e4d8a1eb17084efc8b54cabc8eb6a481
ACR-cd46473e6e9040e7a72e7ff84aedacb4
ACR-01e9919dd5fa4af0bcc165e4480965b1
ACR-9b091714f7704cd587d308840a13885b
ACR-509f3f0a82b24c889f794f170a442f83
ACR-035b078cb2a746caa5eaf3fc519f4b60
ACR-419b1012918f46a1abf7cf445773e24f
ACR-9d19ea210f39429d87d9642a67a83423
ACR-7b6fb41c920741999991e8b11c59fad3
ACR-dcbe1007c6ca4d7a8c6c1739634708e0
ACR-3ad6cfaeb2724d26b16878a36003405c
ACR-42e240e27ced442fbfd998943ef08680
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.connection;

import com.google.gson.annotations.JsonAdapter;
import org.sonarsource.sonarlint.core.rpc.protocol.adapter.EitherSonarQubeSonarCloudConnectionAdapterFactory;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.binding.BindingSuggestionOrigin;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Either;

public class ConnectionSuggestionDto {

  @JsonAdapter(EitherSonarQubeSonarCloudConnectionAdapterFactory.class)
  private final Either<SonarQubeConnectionSuggestionDto, SonarCloudConnectionSuggestionDto> connectionSuggestion;
  @Deprecated(forRemoval = true)
  private final boolean isFromSharedConfiguration;

  private final BindingSuggestionOrigin origin;

  public ConnectionSuggestionDto(Either<SonarQubeConnectionSuggestionDto, SonarCloudConnectionSuggestionDto> connectionSuggestion,
    BindingSuggestionOrigin origin) {
    this.connectionSuggestion = connectionSuggestion;
    this.isFromSharedConfiguration = origin == BindingSuggestionOrigin.SHARED_CONFIGURATION;
    this.origin = origin;
  }

  public ConnectionSuggestionDto(SonarQubeConnectionSuggestionDto connection, BindingSuggestionOrigin origin) {
    this(Either.forLeft(connection), origin);
  }

  public ConnectionSuggestionDto(SonarCloudConnectionSuggestionDto connection, BindingSuggestionOrigin origin) {
    this(Either.forRight(connection), origin);
  }

  public Either<SonarQubeConnectionSuggestionDto, SonarCloudConnectionSuggestionDto> getConnectionSuggestion() {
    return connectionSuggestion;
  }

  /*ACR-cde21cdc0f4249e59fa85c5599057324
ACR-3a8422d02c3741d5b4962946cabe3f6c
ACR-d692f96300104576a31763c0604c1f50
   */
  @Deprecated(forRemoval = true)
  public boolean isFromSharedConfiguration() {
    return isFromSharedConfiguration;
  }

  public BindingSuggestionOrigin getOrigin() {
    return origin;
  }
}
