/*
ACR-764508e20c904c22a8019fa3656208b4
ACR-7f07c63e8f294e018883cc4774a7c878
ACR-c22b722c13d24eb5a8a936263b7b6887
ACR-6430a4d79619441b8badaf2ed033c92b
ACR-84b702e26eae481f860aff7894d504c7
ACR-51f16befe9bc424f80887b113a061766
ACR-9e351d4c7f5845cbbf7c46aa1b9a4d76
ACR-3c701798647c4ccba2f8de659045fb23
ACR-06725c24858f4ff7840619907ea4a36e
ACR-5cf3bba4f280474dbed78e8b3b6d5b74
ACR-d02d4a1432754d04bb5d63ef3a136bf0
ACR-d927cd43432645f5ba75e3ca010e35cc
ACR-382f27a6c3a44374bdbc628dd067a25f
ACR-755d33c88abd4401b640a51b9d9e1a8c
ACR-1c4b23513c514e1f9e8ac075ccbbf7e9
ACR-6d58f6b66bda4cdfb3a78ba5de0e76a8
ACR-8e2dd0a7a51d4b9fb7ca962e4fe8bb52
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

    /*ACR-99a40dbdfd95407e8fb5eca81a214fc3
ACR-14519f469326419293cdb32e3a4c099d
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
