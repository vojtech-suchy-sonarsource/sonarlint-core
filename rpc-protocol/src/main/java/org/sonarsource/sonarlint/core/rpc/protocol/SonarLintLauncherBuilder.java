/*
ACR-a03de471d15f45f9b55a48826d645f8e
ACR-716401518bfa43da894a2ceac05ec1c6
ACR-f573817f18d44ecba7280b908bb19603
ACR-1f855dc4928f43c3bd80e1e718977c8b
ACR-a7fc43dba5fd4fd08915b8a828627340
ACR-c680eb69995e4af7b08eb97237b029d1
ACR-f3804d0b654242469e52734331d408d1
ACR-555a63aef42b4f54975fb67fe05b125e
ACR-b4840da448b94e71b3c54b5822598071
ACR-919b0e9c9ab044b398b1d1994fd4b2a5
ACR-c092564653e44304b2f87e91f2a73ec3
ACR-941f3e7aa4314e14809809783dc3c590
ACR-a8723422785545b096ff60824010991b
ACR-ee1a7a844a1f4a449715b970d24eda53
ACR-641a715d124f4de59277e69d4c3a3fec
ACR-11f63e07066c456b96dfd6e70c6e36c1
ACR-ca43f1b92071491a8cd48a5d5923f8d6
 */
package org.sonarsource.sonarlint.core.rpc.protocol;

import com.google.gson.GsonBuilder;
import java.net.URI;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.jsonrpc.json.JsonRpcMethod;
import org.eclipse.lsp4j.jsonrpc.json.MessageJsonHandler;
import org.eclipse.lsp4j.jsonrpc.json.adapters.MessageTypeAdapter;
import org.sonarsource.sonarlint.core.rpc.protocol.adapter.DurationTypeAdapter;
import org.sonarsource.sonarlint.core.rpc.protocol.adapter.EitherStandardOrMQRModeAdapterFactory;
import org.sonarsource.sonarlint.core.rpc.protocol.adapter.EitherTypeAdapter;
import org.sonarsource.sonarlint.core.rpc.protocol.adapter.InstantTypeAdapter;
import org.sonarsource.sonarlint.core.rpc.protocol.adapter.OffsetDateTimeAdapter;
import org.sonarsource.sonarlint.core.rpc.protocol.adapter.PathTypeAdapter;
import org.sonarsource.sonarlint.core.rpc.protocol.adapter.UriTypeAdapter;
import org.sonarsource.sonarlint.core.rpc.protocol.adapter.UuidTypeAdapter;

/*ACR-d5a83e9001734951aa85490d069a5c54
ACR-cc6cf6cfd0624c2c9656bbfc4b10fe55
 */
public class SonarLintLauncherBuilder<T> extends Launcher.Builder<T> {

  @Override
  protected MessageJsonHandler createJsonHandler() {
    Map<String, JsonRpcMethod> supportedMethods = getSupportedMethods();
    return new MessageJsonHandler(supportedMethods) {
      @Override
      public GsonBuilder getDefaultGsonBuilder() {
        //ACR-0c2e0794765a4ef8a2763d629c7550c8
        return new GsonBuilder()
          .registerTypeAdapterFactory(new EitherTypeAdapter.Factory())
          //ACR-dca9837bfc6a4b38b7c95e1c40e9454b
          .registerTypeAdapterFactory(new EitherStandardOrMQRModeAdapterFactory())

          .registerTypeAdapterFactory(new MessageTypeAdapter.Factory(this))
          .registerTypeHierarchyAdapter(Path.class, new PathTypeAdapter())
          .registerTypeHierarchyAdapter(OffsetDateTime.class, new OffsetDateTimeAdapter())
          .registerTypeHierarchyAdapter(Instant.class, new InstantTypeAdapter())
          .registerTypeHierarchyAdapter(UUID.class, new UuidTypeAdapter())
          .registerTypeHierarchyAdapter(URI.class, new UriTypeAdapter())
          .registerTypeHierarchyAdapter(Duration.class, new DurationTypeAdapter())
          .serializeNulls();
      }
    };
  }
}
