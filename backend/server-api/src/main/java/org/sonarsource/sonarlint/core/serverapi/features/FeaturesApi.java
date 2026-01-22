/*
ACR-a67dd5b2365342d3a889108c218f1bf8
ACR-571161e86b42408284784e4805ee770f
ACR-160b0eaed1834f0e9f644b2ced68b1d4
ACR-4fdf00a027f34f7ead43969ca6904bba
ACR-3fac7dfa6c0a43e387b754f405ee283c
ACR-14aadae9fcf045388c8fc78fcacba16b
ACR-8a9193ff4af842f298d78b160875480b
ACR-ceec0fc0006b4122a9c4ee29039bc5ba
ACR-0356672c04c7423e8c84071f1ae772a7
ACR-84a1b90f53ed4105ad56e777025cf794
ACR-c1225250eb3c4e20891dbe4797f48fc3
ACR-39ade90f58fa4d008a52256ddd24ef31
ACR-66651f9470934bd1945098f62cb00374
ACR-7c7aec10465e428ea40f9e28933d48a4
ACR-dce0d585ef844b479ebb1a497edb0c78
ACR-a9d27f1d081442a0beaaec3f40371e70
ACR-67320df942a34adc872ec635311e9c22
 */
package org.sonarsource.sonarlint.core.serverapi.features;

import com.google.gson.Gson;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.serverapi.ServerApiHelper;
import org.sonarsource.sonarlint.core.serverapi.exception.UnexpectedBodyException;

public class FeaturesApi {
  private static final SonarLintLogger LOG = SonarLintLogger.get();

  private final ServerApiHelper helper;

  public FeaturesApi(ServerApiHelper helper) {
    this.helper = helper;
  }

  public Set<Feature> list(SonarLintCancelMonitor cancelMonitor) {
    try (var response = helper.rawGet("api/features/list", cancelMonitor)) {
      var responseStr = response.bodyAsString();
      var featureKeys = new Gson().fromJson(responseStr, String[].class);
      return Arrays.stream(featureKeys).flatMap(key -> Feature.fromKey(key).stream()).collect(Collectors.toSet());
    } catch (Exception e) {
      LOG.error("Error while fetching the list of features", e);
      throw new UnexpectedBodyException(e);
    }
  }
}
