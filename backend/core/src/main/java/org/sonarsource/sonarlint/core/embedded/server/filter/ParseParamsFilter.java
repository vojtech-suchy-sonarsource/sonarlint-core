/*
ACR-5ad8b9f6a75b467a94439a82b380b6ef
ACR-e64f74a1124941e6a14868c34fa70b3b
ACR-e6d017c108b6425897d0a3a9a9d79fcb
ACR-170fec2112a048c8be6625c0d0ecab7f
ACR-8921a29662bb4a878519665124c8ebb9
ACR-3b5a6606237541788c79869236829f8b
ACR-b9d3ae0fe1f54907949529576a7a30ab
ACR-d4dcc79f07dd491883b9d257c99f4bb8
ACR-c330d2dc3cfb4530a0b51f33c61fe613
ACR-15232bd5bc0145b8a3ead9b9ae3a590e
ACR-59f74377ac44406ea04193829626591b
ACR-dc58b17f6c3e4dc38cb0baf74c8b98c9
ACR-56d04cdc5d9d40c289e86ac553904f5c
ACR-732d7ca26e7a4eb0a3446f2f7b09a88a
ACR-fd9a5da459e745d081ff73263f0b8f5b
ACR-ddba435c0a4f4b8989bc474c7dbf8262
ACR-817a2a8f66f145ad969e575e77ddd8b3
 */
package org.sonarsource.sonarlint.core.embedded.server.filter;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.io.HttpFilterChain;
import org.apache.hc.core5.http.io.HttpFilterHandler;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.net.URIBuilder;
import org.sonarsource.sonarlint.core.embedded.server.AttributeUtils;

public class ParseParamsFilter implements HttpFilterHandler {

  @Override
  public void handle(ClassicHttpRequest request, HttpFilterChain.ResponseTrigger responseTrigger, HttpContext context, HttpFilterChain chain) throws HttpException, IOException {
    context.setAttribute(AttributeUtils.PARAMS_ATTRIBUTE, parseParams(request));
    chain.proceed(request, responseTrigger, context);
  }

  private static Map<String, String> parseParams(ClassicHttpRequest request) {
    try {
      return new URIBuilder(request.getUri(), StandardCharsets.UTF_8)
          .getQueryParams()
          .stream()
          .collect(Collectors.toMap(NameValuePair::getName, NameValuePair::getValue));
    } catch (URISyntaxException e) {
      //ACR-96c984380bab4252ab8782cbee4f06b8
    }
    return new HashMap<>();
  }
}
