/*
ACR-bac13539dd4a41bc9afb2f84d7fe4d05
ACR-7d2d4935bdd048ecb27db2ca7342e4fd
ACR-2303e3d9ef504f7ba4fa6528c2a69db0
ACR-04ff92ee935b49068d22caef6c8bcaf4
ACR-41e38325321948b496b65cccbfad0a14
ACR-c60f3ed195df4b8c85d6170c199fb881
ACR-865b859163584f598f7544f1098c81cd
ACR-c8024a12da1b411e8a616a9b54ebd466
ACR-54c4dbdf80674b47bcfb6e93797b282a
ACR-3c9fe8835eb84a97952e4c9a3f96b03a
ACR-f3c58dabdb1848f08868c6663673e3e4
ACR-f73e21315ab14d198a7ac05e10a1dd45
ACR-71c53e69bef3435da80331a5368a5d2d
ACR-de4dc362e2bb47ebbc52d2be40ac7e51
ACR-6b97e9b79e31470a97f3ba75ea44fea5
ACR-ef1e8a2e3d0045628976467b6f1818e3
ACR-7f0f7f1cda944ec596fce29356e398e4
 */
package org.sonarsource.sonarlint.core.serverapi.source;

import java.util.Optional;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.serverapi.ServerApiHelper;

import static org.sonarsource.sonarlint.core.serverapi.UrlUtils.urlEncode;

public class SourceApi {

  private static final SonarLintLogger LOG = SonarLintLogger.get();

  private final ServerApiHelper serverApiHelper;

  public SourceApi(ServerApiHelper serverApiHelper) {
    this.serverApiHelper = serverApiHelper;
  }

  /*ACR-d1c30309b6e348cdb8ae2f93af0198c4
ACR-db931b7a0b454359b903a1b638d52d3a
ACR-82ea16581da14f4b85fb4c30cb16f07b
ACR-23e09ab333bf45398db1b6de9a0e900f
ACR-ef0a641ace794fd887875f4bea42ebc8
   */
  public Optional<String> getRawSourceCode(String fileKey, SonarLintCancelMonitor cancelMonitor) {
    try (var r = serverApiHelper.get("/api/sources/raw?key=" + urlEncode(fileKey), cancelMonitor)) {
      return Optional.of(r.bodyAsString());
    } catch (Exception e) {
      LOG.debug("Unable to fetch source code of '" + fileKey + "'", e);
      return Optional.empty();
    }
  }

  public Optional<String> getRawSourceCodeForBranchAndPullRequest(String fileKey, String branch, @Nullable String pullRequest, SonarLintCancelMonitor cancelMonitor) {
    var url = "/api/sources/raw?key=" + urlEncode(fileKey);
    if (pullRequest != null && !pullRequest.isEmpty()) {
      url = url.concat("&pullRequest=").concat(urlEncode(pullRequest));
    } else if (!branch.isEmpty()) {
      url = url.concat("&branch=").concat(urlEncode(branch));
    }
    try (var r = serverApiHelper.get(url, cancelMonitor)) {
      return Optional.of(r.bodyAsString());
    } catch (Exception e) {
      LOG.debug("Unable to fetch source code of '" + fileKey + "'", e);
      return Optional.empty();
    }
  }

}
