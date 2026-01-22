/*
ACR-30eda1be6cde42ff8c5e01447684f89b
ACR-ad8713a126f848cf84b3792109b19e54
ACR-aca851b231804247a271148dd7d1f1c4
ACR-fb0a391025cd4393b515f495ff05bb5e
ACR-b589ebf97a44495cb5de3bc4b7ee2012
ACR-a807ce1a029c4c1c8b4669e8f60191a6
ACR-4c328b14ea694a479fea255d254cc3b8
ACR-1c0a0d5dad924ceea5bca7080886a103
ACR-719bb9a4cd794487af79bae6bce1208f
ACR-31aad9cc1e6346f8b95ee08dc356c983
ACR-e01bb3ab80e848fda4d7f167cd98134a
ACR-c7779547f08241a3b5a90b0962f0d33b
ACR-c05e5cb2e455494a934ae652c50f71b3
ACR-52897f5fd62b4ffdb46f618e922be4e9
ACR-ff55ea63275c496ba20a3f5238bc957b
ACR-7021ba2109df4454a98e7dea1b249be1
ACR-fdb4817e406a458fbff1e495533ef61e
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

  /*ACR-cf50bd2c90024183ba5ba3061ccfed8c
ACR-d4a1803a190c49a3a7c7deef9cf64ae4
ACR-482e691681174ba998b1ba3e15a1cf00
ACR-eff29d7e650c491d95a763102070c0e3
ACR-41e48ec7a7614e9e9726f9e441be29e1
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
