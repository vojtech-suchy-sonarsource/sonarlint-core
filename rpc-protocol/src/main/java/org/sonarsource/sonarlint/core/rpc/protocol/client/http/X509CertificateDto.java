/*
ACR-83c93613b4734ab1afe2ab39c1c20c22
ACR-17469c79059d446182bbfadc00dfa9ba
ACR-cd24ab0f94814c9d8441af8b2a8a2c7c
ACR-7948b0ae292846d8b899427d29b6be2a
ACR-ef37337bf100449ebe88d00acd640921
ACR-25b176c666e245278ec6b1763ed5087c
ACR-57fca46e190f4f758df6dfc4ea24ae02
ACR-bd6b9fb363a447aea877f9cebb5cc808
ACR-48c4bca3d3ec4d079aecd2de9b44f422
ACR-3cc11865a2394f509dcd56401ed584f4
ACR-44144b4312fb4d76a8d778bc8bbd06d9
ACR-878c06bb168a482495287ec161875c17
ACR-fb37306ecd4848f8a759e3aa6cfcd24c
ACR-0d758888c95d4f59a19c8a7aa1454517
ACR-7434458ce24e4d3c8f313d0484ee8b59
ACR-922dea1cce4e4287895c89a330f1aca3
ACR-80ef18f919e5496b830cfbefa7a71033
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.http;

public class X509CertificateDto {

  private final String pem;


  public X509CertificateDto(String pem) {
    this.pem = pem;
  }

  public String getPem() {
    return pem;
  }
}
