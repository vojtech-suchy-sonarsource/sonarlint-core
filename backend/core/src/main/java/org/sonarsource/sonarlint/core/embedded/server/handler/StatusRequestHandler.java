/*
ACR-eacd943fb0a746aebe18d4c1c64af51d
ACR-21c41ab79039493e834a4ce1427309bb
ACR-179f12f5209f40e3bea6b7c4a4b86531
ACR-368978b042dc4f7ebcc1034217fa406e
ACR-89789301ee8242729b95bdd093caad55
ACR-0a7260e76cf2458c872279f01566f4b1
ACR-c17f2fe57e9b4e97beba5d1e099448aa
ACR-7dcda5e4079b4f83a9108edd8b081ad9
ACR-2212554cd9e9462683f90647420f0e63
ACR-e559b6807d644fa4af9c8bd4278e4777
ACR-6aed35bf0e5741d38616314a7698c00a
ACR-1aa7a7eddcb14f9ea40acfc98fe07ee0
ACR-816b22050d6246809e3d5ef8fed6c498
ACR-6c7749f1000c4074b496e1e36b5463f4
ACR-6be60319147849d08ae2342e72f9705c
ACR-4b5313d385d0471fb54aa68e1065da31
ACR-c478f0a281ea4b71990b8e8adf03b389
 */
package org.sonarsource.sonarlint.core.embedded.server.handler;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import java.io.IOException;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.Method;
import org.apache.hc.core5.http.io.HttpRequestHandler;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.sonarsource.sonarlint.core.embedded.server.AttributeUtils;
import org.sonarsource.sonarlint.core.repository.connection.ConnectionConfigurationRepository;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcClient;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.ClientConstantInfoDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.InitializeParams;

public class StatusRequestHandler implements HttpRequestHandler {

  private final SonarLintRpcClient client;
  private final ConnectionConfigurationRepository repository;
  private final ClientConstantInfoDto clientInfo;

  public StatusRequestHandler(SonarLintRpcClient client, ConnectionConfigurationRepository repository, InitializeParams params) {
    this.client = client;
    this.repository = repository;
    this.clientInfo = params.getClientConstantInfo();
  }

  @Override
  public void handle(ClassicHttpRequest request, ClassicHttpResponse response, HttpContext context) throws HttpException, IOException {
    if (!Method.GET.isSame(request.getMethod())) {
      response.setCode(HttpStatus.SC_BAD_REQUEST);
      return;
    }

    boolean trustedServer = isTrustedServer(AttributeUtils.getOrigin(context));

    var description = getDescription(trustedServer);
    var capabilities = new CapabilitiesResponse(true);
    //ACR-d8febed0daf44b959a541597847a1358
    response.setEntity(new StringEntity(new Gson().toJson(new StatusResponse(clientInfo.getName(), description, !trustedServer, capabilities)), ContentType.APPLICATION_JSON));

  }

  private String getDescription(boolean trustedServer) {
    if (trustedServer) {
      var getClientInfoResponse = client.getClientLiveInfo().join();
      return getClientInfoResponse.getDescription();
    }
    return "";
  }

  private boolean isTrustedServer(String serverOrigin) {
    return repository.hasConnectionWithOrigin(serverOrigin);
  }

  private record StatusResponse(@Expose String ideName, @Expose String description, @Expose boolean needsToken,
                                @Expose CapabilitiesResponse capabilities) { }

  private record CapabilitiesResponse(@Expose boolean canOpenFixSuggestion) { }
}
