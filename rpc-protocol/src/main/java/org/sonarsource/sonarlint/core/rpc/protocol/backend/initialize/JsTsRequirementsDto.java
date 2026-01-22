/*
ACR-9b7835fe7ee64f33947674f43e3f51b6
ACR-66fd036e1bfb42c8a298bb79081a4158
ACR-03e2146d3e874c45b5c8f17f5441af45
ACR-05d603dff55b42c08d651286a965fabf
ACR-c7863b4a3cbc4add985fc11914e874d7
ACR-b23231816daf4b87937b1ca1cd0977e1
ACR-57db077686cf4f488ec4d9658a11fbce
ACR-65c4d05348c54c8783353f8b67703d47
ACR-ea6b7005810d43408e52496f3effff1b
ACR-4c963b36dd854279a53e7d0e172d88f5
ACR-38d3e92cdcbd472096f98c7b80fd67d3
ACR-0d3dc1588bff436ab637cca9400c1106
ACR-dc6261d9ccc14602a4f6ddbfe1f42603
ACR-a1a64a26dc784ec199b00c9da1fd9d71
ACR-f4fd7ee9958043698ff531a27fcdce1a
ACR-5833935b2ab5469d8cb2b1695586a152
ACR-9a618a8f65fe42ef92b8313ad5b080a4
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize;

import java.nio.file.Path;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

public class JsTsRequirementsDto {
  @Nullable
  private final Path clientNodeJsPath;
  @Nullable
  private final Path bundlePath;

  public JsTsRequirementsDto(@Nullable Path clientNodeJsPath, @Nullable Path bundlePath) {
    this.clientNodeJsPath = clientNodeJsPath;
    this.bundlePath = bundlePath;
  }

  @CheckForNull
  public Path getClientNodeJsPath() {
    return clientNodeJsPath;
  }

  @CheckForNull
  public Path getBundlePath() {
    return bundlePath;
  }
}
