/*
ACR-af3c670f3de840dd99229baad7b432a9
ACR-207ab85f4cae460b9415d657f5f41212
ACR-fb3ed5621fb94f3c8383c1cd3e889812
ACR-0378b66afbe343f7ac92656377d1d35d
ACR-0c09a42474b1467388830248a2c17951
ACR-814a1a08e4134757bbb5807f38ac5235
ACR-ec6ae008265f43309bc0794cff39fff2
ACR-41b1592c6d334abe9ee2f44e90d5cb8f
ACR-351cb7c58a7e426aba91aed8229c1d56
ACR-64b1c3e2e3954ea9906060fe41e40c26
ACR-41f5c0b9c2944ebe8b0a5207dd77a2d6
ACR-3e893e31648d4c2fba16e58ceaf1d1d7
ACR-a5e177e5df3d44b195154a8a7289e071
ACR-821c3fd165514488bb3c270209d2b2d3
ACR-c9af96a1ff174262b82eadd0e44fde0f
ACR-26c679b9dda546bfa782123eecb8a71e
ACR-5c56a475d5254c14b575bf6c75497b7e
 */
package org.sonarsource.sonarlint.core.embedded.server.filter;

import java.io.IOException;
import org.apache.commons.lang3.Strings;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.HttpFilterChain;
import org.apache.hc.core5.http.io.HttpFilterHandler;
import org.apache.hc.core5.http.message.BasicClassicHttpResponse;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.sonarsource.sonarlint.core.SonarCloudActiveEnvironment;
import org.sonarsource.sonarlint.core.SonarCloudRegion;
import org.sonarsource.sonarlint.core.embedded.server.AttributeUtils;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcClient;
import org.sonarsource.sonarlint.core.rpc.protocol.client.message.MessageType;
import org.sonarsource.sonarlint.core.rpc.protocol.client.message.ShowMessageParams;

import static org.apache.hc.core5.http.io.HttpFilterChain.ResponseTrigger;

public class ValidationFilter implements HttpFilterHandler {

  private final SonarLintRpcClient client;
  private final SonarCloudActiveEnvironment sonarCloudActiveEnvironment;

  public ValidationFilter(SonarLintRpcClient client, SonarCloudActiveEnvironment sonarCloudActiveEnvironment) {
    this.client = client;
    this.sonarCloudActiveEnvironment = sonarCloudActiveEnvironment;
  }

  @Override
  public void handle(ClassicHttpRequest request, ResponseTrigger responseTrigger, HttpContext context, HttpFilterChain chain) throws HttpException, IOException {
    var origin = AttributeUtils.getOrigin(context);
    boolean isSonarCloud = sonarCloudActiveEnvironment.isSonarQubeCloud(origin);
    var params = AttributeUtils.getParams(context);
    if (!isSonarCloud && params.containsKey("server")) {
      var serverUrl = params.get("server");
      if (Strings.CI.startsWithAny(serverUrl, SonarCloudRegion.CLOUD_URLS)) {
        var response = new BasicClassicHttpResponse(HttpStatus.SC_BAD_REQUEST);
        client.showMessage(new ShowMessageParams(MessageType.ERROR,
          "Invalid request to SonarQube backend. " +
            "The 'server' parameter should not be SonarQube Cloud URL, use it only to specify URL of a SonarQube Server."));
        responseTrigger.submitResponse(response);
      }
    }
    chain.proceed(request, responseTrigger, context);
  }

}
