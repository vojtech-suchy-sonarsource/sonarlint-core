/*
ACR-bc2cccc2064840d5a08ba9e27bf83050
ACR-20a33f202e2d423886f4bbc7adb6fdea
ACR-007434836c3f49fcb91e899d24b15fb3
ACR-4b30b92c7188422783f84cbb0cb2a29e
ACR-ebb9f62ac6fb42af9e312b6f6a185e2e
ACR-eacf41a1c99642df9c3bf859fc0ecfd9
ACR-a57ae6cf08d943b3b8e91ed83bd6a7ad
ACR-cc5d1980b8424186908682f2e67d8b45
ACR-2ca6fe66720c408f98175d703f9bbc62
ACR-929d9064330f49d2babd88da32ba2795
ACR-9e47aa25fc864003987cee5681e6724e
ACR-ada606234b6a44d3a638a39aead9b9a0
ACR-6dbc2a4238404986a90ad6d955d487d8
ACR-ed2cc16f9e6f49e2b473fa8e12c5d52d
ACR-f0179e60491249268cd5ccaa12198dc0
ACR-16974accf4294b269e29790cee551e36
ACR-ee34e77cd81f467da9144c32604913c4
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client;

public class OpenUrlInBrowserParams {

  private final String url;

  public OpenUrlInBrowserParams(String url) {
    this.url = url;
  }

  public String getUrl() {
    return url;
  }
}
