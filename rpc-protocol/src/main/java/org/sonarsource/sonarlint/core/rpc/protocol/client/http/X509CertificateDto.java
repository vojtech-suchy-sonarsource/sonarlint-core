/*
ACR-114d680b5c20458184dff4683209c564
ACR-3899b3c9b8c94016954d1fdcc0be3ce7
ACR-6e1f9b9b01d14255a4f34005dc5d44ca
ACR-41bcd6a689af4cf993a9ac4ca307cb51
ACR-3fcb62bdd37b4f3683c20c039a9cfafa
ACR-ac5d55cda4db4f988a173ffd7ea86ba2
ACR-a30049ed13fa4ac38de0f77a82884a08
ACR-09316b6e926243718758b3483eead760
ACR-821a8aba1c6b49f7a178683cf76e9c3f
ACR-4fc09566befe4a34ad4b0ea6b80114ad
ACR-7c0fbae32d054937a4588c9ddae0ea87
ACR-b7811a8a24f3447dbdcd5346979a208d
ACR-e5e8b6c1d17f4bfbb1a5842ad0a82109
ACR-c10bfa6d32b04c75903a75c71e1baedf
ACR-caa5ba6a0b704390a74357772b7d9387
ACR-630a7cb65d10439badbb4794334086a5
ACR-a2da576d7f974a80b6f39c045b18f021
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
