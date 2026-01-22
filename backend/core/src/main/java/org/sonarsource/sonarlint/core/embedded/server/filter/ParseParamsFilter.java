/*
ACR-0407e2eea808483183923724a1db7426
ACR-dfae89208357443d9751f8625e0283ef
ACR-3266958a3d454cbd95cef8417fb5751c
ACR-3ba1a424562d48188ba7f8710822d65f
ACR-636a65eca6b64d5c8958f7b52b5600ba
ACR-7bf09fc87fd24e70810938a86c980483
ACR-a54a60a7ec0d4161a0247a33a6d4a63f
ACR-6104fc201bb4400fb91bf952b0149827
ACR-9ed1ac1b377146c1be9bf21d724889b4
ACR-0da67e2f8bbd45ccbd642bfd4c4bd990
ACR-e046c394160648b59f0bc9afa85d9a5e
ACR-fae9d39267464d74a4092cbb4d06e480
ACR-d52ed50efe0c402b9aac9e56326cefc0
ACR-f2c2e3a3ca2e4a6e945d48d710a62103
ACR-d55c20de6e384ed3a37aafa02af627f4
ACR-074de545de26491fb980be5079e4c850
ACR-e410b1f2ea6742638c341a2787c11db7
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
      //ACR-a5cdae3add604de08ba7004bdeb2ad67
    }
    return new HashMap<>();
  }
}
