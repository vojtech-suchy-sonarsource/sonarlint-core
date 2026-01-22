/*
ACR-11bef9cd2a5c46f487a6d5a1a24c5365
ACR-3c7e2e3f442740d887ab43f7f78fe335
ACR-927d2d02410741e1850bd3925e2c639c
ACR-457b4e858c2c4d2ca5d8d942bd8d2096
ACR-3be4b0b7ddc24659af6f289e4b4a6449
ACR-53561a1bd491475e945bab09bef131a1
ACR-6a394a0fc05d4058997c142425fdc32f
ACR-5c653f6756bd46dcb4d52de1d6f476a5
ACR-56e3b5b0b16746ecb644b94deeb83657
ACR-b565457d298c4549be408b4ac7dfb297
ACR-dd763a8b8f164bd4835d41883cf7fc76
ACR-4703be5019ef4ce5bb34befc68923b2c
ACR-475c02b4c1064e51a1da4b0c009d99d5
ACR-4d92a084171845d4aa4168a9a190e570
ACR-147cd8cc292b4ce88e49efba46079f2e
ACR-948abcf603194bac84ba47137cad6cc8
ACR-9c343bef07c642f69ca72726efda399e
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
