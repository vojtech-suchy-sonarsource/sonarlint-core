/*
ACR-df62e794571e4a229e5cf9774eb31617
ACR-cf08b041719b469fa56e226ac535a195
ACR-f37c3bc50cb1418a822f11cfb61502d8
ACR-b9383b96634b4a8aa28da7a3c76f73f2
ACR-7e83320fc497482d82a30e7c417d9402
ACR-7fc668eba5464903ade5038d081d072b
ACR-b85063a2ebd7477588f66b0bb96c9641
ACR-0ab2d9ec377e4f5ba4c2d79898439296
ACR-db31562e897743f4a98b099e3f560017
ACR-f5acc7ed38b04c319726f7290976bf0d
ACR-392e8f92760444e4a1afffbabc49b870
ACR-97fe9fc9e97f49339bf60bf253fcba3c
ACR-32fcd11124d0414abaf688c01776270a
ACR-ae524769fe624e99936056061feda906
ACR-c82933adc6e14956a290cd14364d9ee1
ACR-abd985f4d9a04b41a6cfd56ecccfc020
ACR-495f5b83994d44e2aa736ce3f049aa0c
 */
package mediumtest.http;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStoreException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import nl.altindag.ssl.util.CertificateUtils;
import nl.altindag.ssl.util.KeyStoreUtils;
import org.eclipse.lsp4j.jsonrpc.ResponseErrorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.ArgumentCaptor;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.org.GetOrganizationParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.http.X509CertificateDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Either;
import org.sonarsource.sonarlint.core.rpc.protocol.common.SonarCloudRegion;
import org.sonarsource.sonarlint.core.rpc.protocol.common.TokenDto;
import org.sonarsource.sonarlint.core.serverapi.proto.sonarcloud.ws.Organizations;
import org.sonarsource.sonarlint.core.serverapi.proto.sonarqube.ws.Common;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTest;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTestHarness;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.sonarsource.sonarlint.core.test.utils.ProtobufUtils.protobufBody;

class SslMediumTests {

  public static final String KEYSTORE_PWD = "pwdServerP12";

  @RegisterExtension
  SonarLintLogTester logTester = new SonarLintLogTester(true);

  @Nested
  //ACR-fd6e640ef12f4cec8795c23e6864016a
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  class ServerCertificate {

    @RegisterExtension
    WireMockExtension sonarcloudMock = WireMockExtension.newInstance()
      .options(wireMockConfig().dynamicHttpsPort().httpDisabled(true)
        .keystoreType("pkcs12")
        .keystorePath(toPath(Objects.requireNonNull(SslMediumTests.class.getResource("/ssl/server.p12"))).toString())
        .keystorePassword(KEYSTORE_PWD)
        .keyManagerPassword(KEYSTORE_PWD))
      .build();

    @BeforeEach
    void prepare() {
      sonarcloudMock.stubFor(get("/api/system/status")
        .willReturn(aResponse().withStatus(200).withBody("{\"id\": \"20160308094653\",\"version\": \"8.0\",\"status\": " +
          "\"UP\"}")));
      sonarcloudMock.stubFor(get("/api/organizations/search.protobuf?organizations=myOrg&ps=500&p=1")
        .willReturn(aResponse().withStatus(200)
          .withResponseBody(protobufBody(Organizations.SearchWsResponse.newBuilder()
            .addOrganizations(Organizations.Organization.newBuilder()
              .setKey("myCustom")
              .setName("orgName")
              .setDescription("orgDesc")
              .build())
            .setPaging(Common.Paging.newBuilder()
              .setTotal(1)
              .setPageSize(1)
              .setPageIndex(1)
              .build())
            .build()))));
    }

    @SonarLintTest
    void it_should_not_trust_server_self_signed_certificate_by_default(SonarLintTestHarness harness) {
      var fakeClient = harness.newFakeClient().build();
      var backend = harness.newBackend()
        .withSonarQubeCloudEuRegionUri(sonarcloudMock.baseUrl())
        .start(fakeClient);

      var future = backend.getConnectionService().getOrganization(new GetOrganizationParams(Either.forLeft(new TokenDto("token")), "myOrg", SonarCloudRegion.EU));
      var thrown = assertThrows(CompletionException.class, future::join);
      assertThat(thrown).hasRootCauseInstanceOf(ResponseErrorException.class).hasRootCauseMessage("org.sonarsource.sonarlint.core.serverapi.exception.NetworkException: Request failed");
      assertThat(future).isCompletedExceptionally();
    }

    @SonarLintTest
    void it_should_ask_user_only_once_if_server_certificate_is_trusted(SonarLintTestHarness harness) throws ExecutionException, InterruptedException, KeyStoreException {
      var fakeClient = harness.newFakeClient().build();

      var backend = harness.newBackend()
        .withSonarQubeCloudEuRegionUri(sonarcloudMock.baseUrl())
        .start(fakeClient);

      when(fakeClient.checkServerTrusted(any(), any()))
        .thenReturn(true);

      //ACR-c62398cb51a24fa782a0b5e4784849c7
      var future = backend.getConnectionService().getOrganization(new GetOrganizationParams(Either.forLeft(new TokenDto("token")), "myOrg", SonarCloudRegion.EU));
      var future2 = backend.getConnectionService().getOrganization(new GetOrganizationParams(Either.forLeft(new TokenDto("token")), "myOrg", SonarCloudRegion.EU));

      future.get();
      future2.get();

      ArgumentCaptor<List<X509CertificateDto>> captor = ArgumentCaptor.forClass(List.class);
      verify(fakeClient, times(1)).checkServerTrusted(captor.capture(), eq("UNKNOWN"));

      var chain = captor.getValue();

      assertThat(chain).hasSize(1);
      var pems = CertificateUtils.parsePemCertificate(chain.get(0).getPem());
      assertThat(pems).hasSize(1);
      assertThat(pems.get(0)).isInstanceOf(X509Certificate.class);

      var keyStore = KeyStoreUtils.loadKeyStore(backend.getUserHome().resolve("ssl/truststore.p12"), "sonarlint".toCharArray(), "PKCS12");
      assertThat(Collections.list(keyStore.aliases())).containsExactly("cn=localhost_o=sonarsource-sa_l=geneva_st=geneva_c=ch");

    }

  }

  @Nested
  //ACR-18bdbac7ebf94296ba5a9cb92af36894
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  class ClientCertificate {
    @RegisterExtension
    WireMockExtension sonarcloudMock = WireMockExtension.newInstance()
      .options(wireMockConfig().dynamicHttpsPort().httpDisabled(true)
        .keystoreType("pkcs12")
        .keystorePath(toPath(Objects.requireNonNull(SslMediumTests.class.getResource("/ssl/server.p12"))).toString())
        .keystorePassword(KEYSTORE_PWD)
        .keyManagerPassword(KEYSTORE_PWD)
        .needClientAuth(true)
        .trustStoreType("pkcs12")
        .trustStorePath(toPath(Objects.requireNonNull(SslMediumTests.class.getResource("/ssl/server-with-client-ca.p12"))).toString())
        .trustStorePassword("pwdServerWithClientCA"))
      .build();

    @BeforeEach
    void prepare() {
      sonarcloudMock.stubFor(get("/api/system/status")
        .willReturn(aResponse().withStatus(200).withBody("{\"id\": \"20160308094653\",\"version\": \"8.0\",\"status\": " +
          "\"UP\"}")));
      sonarcloudMock.stubFor(get("/api/organizations/search.protobuf?organizations=myOrg&ps=500&p=1")
        .willReturn(aResponse().withStatus(200)
          .withResponseBody(protobufBody(Organizations.SearchWsResponse.newBuilder()
            .addOrganizations(Organizations.Organization.newBuilder()
              .setKey("myCustom")
              .setName("orgName")
              .setDescription("orgDesc")
              .build())
            .setPaging(Common.Paging.newBuilder()
              .setTotal(1)
              .setPageSize(1)
              .setPageIndex(1)
              .build())
            .build()))));
    }

    @SonarLintTest
    void it_should_fail_if_client_certificate_not_provided(SonarLintTestHarness harness) {
      var fakeClient = harness.newFakeClient().build();
      var backend = harness.newBackend()
        .withSonarQubeCloudEuRegionUri(sonarcloudMock.baseUrl())
        .start(fakeClient);

      when(fakeClient.checkServerTrusted(any(), any()))
        .thenReturn(true);

      var future = backend.getConnectionService().getOrganization(new GetOrganizationParams(Either.forLeft(new TokenDto("token")), "myOrg", SonarCloudRegion.EU));

      var thrown = assertThrows(CompletionException.class, future::join);
      assertThat(thrown).hasRootCauseInstanceOf(ResponseErrorException.class).hasRootCauseMessage("org.sonarsource.sonarlint.core.serverapi.exception.NetworkException: Request failed");
      assertThat(future).isCompletedExceptionally();

    }

    @SonarLintTest
    void it_should_succeed_if_client_certificate_provided(SonarLintTestHarness harness) {
      var fakeClient = harness.newFakeClient().build();
      var backend = harness.newBackend()
        .withKeyStore(toPath(Objects.requireNonNull(SslMediumTests.class.getResource("/ssl/client.p12"))), "pwdClientCertP12", null)
        .withSonarQubeCloudEuRegionUri(sonarcloudMock.baseUrl())
        .start(fakeClient);

      when(fakeClient.checkServerTrusted(any(), any()))
        .thenReturn(true);

      var future = backend.getConnectionService().getOrganization(new GetOrganizationParams(Either.forLeft(new TokenDto("token")), "myOrg", SonarCloudRegion.EU));

      assertThat(future).succeedsWithin(1, TimeUnit.MINUTES);
    }
  }

  private static Path toPath(URL url) {
    try {
      return Paths.get(url.toURI());
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

}
