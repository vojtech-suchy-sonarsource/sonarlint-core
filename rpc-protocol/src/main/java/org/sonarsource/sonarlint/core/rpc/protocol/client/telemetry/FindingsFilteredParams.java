/*
ACR-2be4b34637e740b484efe150a5a1ee1c
ACR-25ddc2f2715b454e9a463cc551b92070
ACR-339443b1e308497da0cdfd35d0d1c42c
ACR-5f43a4b38346466ea64e9f89805f0da3
ACR-14e2a280917a4b4eb82576f5d4dc3096
ACR-fd9b42ecb6f448eeb796388630307568
ACR-53686c63776d419e88dc738cf7326458
ACR-0e2883091dc5408ca1e1d4c0751f9333
ACR-59309e021196418680c5cf79273c9f6c
ACR-55b9c043cffa432ea37786dfa363a0aa
ACR-8dd9d7a12ef345d59b360fe496aca755
ACR-ffe495f864894e4895c120b41e470a8d
ACR-a80f93becd2f4ee7a9e70417f6d89a1e
ACR-134635857b33464ca6b4d83d76a25855
ACR-f45fd9d41609426885921d51d7cf0e1a
ACR-b5cb02b437d74d97bb40d43cbf33b522
ACR-cc32e222968e4103943931dd719dfcf5
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry;

public class FindingsFilteredParams {
  private final String filterType;

  public FindingsFilteredParams(String filterType) {
    this.filterType = filterType;
  }

  public String getFilterType() {
    return filterType;
  }
}
