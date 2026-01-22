/*
ACR-6616edc5a74c4d26abf8c7402f801684
ACR-f72f7977079f4fc1ad9b45f4cf3f0b4a
ACR-8503904e20aa4df091768a341dfb49a6
ACR-cb1446e6317c479c89d0662627bb19e3
ACR-4a99d17772924643987fba54f0733c63
ACR-a41745e4d6ae4e889bfb570051715b1d
ACR-d7f7a82abfef4fcca2045b2a495165cd
ACR-def02f3acb864781ae920ff346c76d2f
ACR-5d097da000224f7ebc4ddd3e902bc439
ACR-78a306638ea4470caea8a14e6e610aea
ACR-1d28a3230e2e4fcb9a7f3d1bfb19a518
ACR-2d6c54c61ec0436180ba38223c04adfe
ACR-f4d3abcfa3624fc2a479e28a123db11a
ACR-33c8b3e8180249f3ab30305ad1d59448
ACR-6f61444c17094002b39c3a3ed12d7191
ACR-f241dcc951374270864ee077faf5e68d
ACR-368e48d49f9049e8b497d700f8650c14
 */
package org.sonarsource.sonarlint.core.http;

import java.io.Closeable;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public interface HttpClient {

  String JSON_CONTENT_TYPE = "application/json; charset=utf-8";
  String FORM_URL_ENCODED_CONTENT_TYPE = "application/x-www-form-urlencoded";

  interface Response extends Closeable {

    int code();

    default boolean isSuccessful() {
      return code() >= 200 && code() < 300;
    }

    String bodyAsString();

    InputStream bodyAsStream();

    /*ACR-8162f302604d48df89b6ef87ffc9585e
ACR-affa022f87d94a34816ab5847cc91435
     */
    @Override
    void close();

    String url();
  }

  Response get(String url);

  CompletableFuture<Response> getAsync(String url);

  CompletableFuture<Response> getAsyncAnonymous(String url);

  AsyncRequest getEventStream(String url, HttpConnectionListener connectionListener, Consumer<String> messageConsumer);

  Response post(String url, String contentType, String body);

  CompletableFuture<Response> postAsync(String url, String contentType, String body);

  CompletableFuture<Response> deleteAsync(String url, String contentType, String body);

  interface AsyncRequest {
    void cancel();
  }

}
