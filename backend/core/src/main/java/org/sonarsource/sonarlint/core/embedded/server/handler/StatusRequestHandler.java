/*
ACR-046a496c73db45429a328d2b9e8b0e75
ACR-0a92dbf7e81b484ba9d847e6f15a439b
ACR-b5e485202dd04c1d9b7438e5ab60d1aa
ACR-1123a1d873a14a0cbeffe94a53cd1ed5
ACR-5a904aca79ce4bfc9837dc0ff70a1274
ACR-087b1c34834f4baa9bf7616591646a40
ACR-42f625927c0a412984d00598c8bdcbda
ACR-fd5ba0fdb6154f9785d131baecddfc24
ACR-f49485a5da1242da982b1a2127d9c4f0
ACR-522e46deef804977b3dc8d8682797821
ACR-2f350f2b71854b7fa567e5b907362a10
ACR-973bc1416cdb4b3b9edbf0e7e97df631
ACR-75ac9b6d15ed4105a01103e087da98e8
ACR-ed6e364c5bbf40a7a763b7a00449bd8f
ACR-b0eb4a130d68450980d71118dd9eee9f
ACR-2d64070173b441d9baf0892abb1bca7c
ACR-39009d89c5ab4ab092256a7244b6addc
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
    //ACR-7fa6b696a4b6442c8b6210b0faa2e958
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
