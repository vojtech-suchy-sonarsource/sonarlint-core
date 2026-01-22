/*
ACR-10b6bcf030dd4d2aba3b0e66fce9c506
ACR-6c7d406c6198499683bc02559b7ca05d
ACR-dd5bd32012c340c6bb644299d862fbec
ACR-2f4fc17dffbf4e2f89b55c1daf1bb686
ACR-be854b0984ec41fea6dfae9914425e01
ACR-ec4d3fe8c8ab456cbb232edcbce6d093
ACR-d4ed1441a8734e809c56fbcb92061782
ACR-56a49b2eac95477dbda0d48cb1a13bf5
ACR-4ae493c8abe64d97aa94d0bdc18654b9
ACR-116488ebea964fd99c1725e03dca1158
ACR-e8151fc8cc5a4398a99562e9d294fe25
ACR-0eacdc7a58a34724973d7a98c9f2d658
ACR-c004788572a941e5bf3f898674ef0c65
ACR-d82e5495aa8f4cb687bc6cc7d7545032
ACR-a34a40174dd848b494105f100cd983b2
ACR-8dcf47b814444f32ae4529c19d319cac
ACR-ee0f57ea54e44833983c07ce5d79b2d2
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis;

import java.util.List;

public class GetSupportedFilePatternsResponse {
  private final List<String> patterns;

  public GetSupportedFilePatternsResponse(List<String> patterns) {
    this.patterns = patterns;
  }

  public List<String> getPatterns() {
    return patterns;
  }
}
