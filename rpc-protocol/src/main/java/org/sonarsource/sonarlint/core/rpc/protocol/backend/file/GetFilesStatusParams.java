/*
ACR-40b47b1ce1134db39b137679c897282e
ACR-87f01518de2943848fd98964618cd709
ACR-230877cce44342afbd19bfbf644a9d8d
ACR-a68502444f734502b95ec587268e4713
ACR-7860f2235dac422c8271896398033ac3
ACR-06417b45e86e469ca710b8dd78b881a8
ACR-5445034641e54657a0602a2c728289df
ACR-ee1865272e6e4468bd7b445e6bd45008
ACR-1cecef5ca3c14f259734265db69adb19
ACR-c8cc051ce7894176bbbda0003cd29ac8
ACR-372d34e5f45f41c1b72c646c3fc51578
ACR-bb8d797126e14b7c8b9abff2abae94ef
ACR-ca3b111c9375407ebd5573fe502195cf
ACR-4ae3fa185a624c9a98aa698b193c3318
ACR-8a5bbec6da6343b19e35a32f09a02de4
ACR-fd2f276e4f154e078a13321a017fdb59
ACR-a486aa238ffc467fa9b3bacac68fe322
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.file;

import java.net.URI;
import java.util.List;
import java.util.Map;

public class GetFilesStatusParams {
  
  private final Map<String, List<URI>> fileUrisByConfigScopeId;

  public GetFilesStatusParams(Map<String, List<URI>> fileUrisByConfigScopeId) {
    this.fileUrisByConfigScopeId = fileUrisByConfigScopeId;
  }

  public Map<String, List<URI>> getFileUrisByConfigScopeId() {
    return fileUrisByConfigScopeId;
  }
}
