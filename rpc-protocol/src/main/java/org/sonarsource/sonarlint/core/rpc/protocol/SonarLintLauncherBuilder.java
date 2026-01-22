/*
ACR-b7ca690ae5f9409985e8299ccb855a94
ACR-8134de1e62de4bfbb036802dad6ff2ce
ACR-5d3a087d34f3492597b5f206048127d0
ACR-6b2162dee60045a3ae594cba414a6f38
ACR-1bec50972341496da470a353d7b2793b
ACR-171faebf8a494ed89e61181a70807768
ACR-7bd147c87b0d4578907f5acd6182ef4e
ACR-e8541eb4d8be42ff8c98ad00f582ac16
ACR-2bb01ec4475a41f18b88bbdfef372941
ACR-194c567d988b47698f7514431b688e32
ACR-79fa47e3af0649c5a4ed5ac51538fdb2
ACR-f10e66b68b9540768ba3ca605cdf531a
ACR-808516e931b4467bb6d01bb4af2a0855
ACR-44f27ebcd3244e619338ab81a92e596e
ACR-5e5111b172fd4d128dc7bb234a0d6f51
ACR-ed1ffe6fb98142ddb0782fcafbb3e172
ACR-f0265cdb7f18484e922eecbf89028c50
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

/*ACR-9a1e4fb679614b519c821f6880311956
ACR-b7d6250adc0540f7ad6a85034d5d59d7
 */
public class SonarLintLauncherBuilder<T> extends Launcher.Builder<T> {

  @Override
  protected MessageJsonHandler createJsonHandler() {
    Map<String, JsonRpcMethod> supportedMethods = getSupportedMethods();
    return new MessageJsonHandler(supportedMethods) {
      @Override
      public GsonBuilder getDefaultGsonBuilder() {
        //ACR-aca0bac3cc694b36b37973b1c56d87bb
        return new GsonBuilder()
          .registerTypeAdapterFactory(new EitherTypeAdapter.Factory())
          //ACR-a4a67b90387141888842e98cadc44d67
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
