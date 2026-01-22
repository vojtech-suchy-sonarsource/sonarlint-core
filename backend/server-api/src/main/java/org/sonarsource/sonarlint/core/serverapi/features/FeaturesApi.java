/*
ACR-4c4b9e1e041d4bcca9c51686890f8420
ACR-d8e1fe4d5713423a9516cc8ff4e32844
ACR-611168ca0ad94e979ea9a9a4423d479c
ACR-f1eb714b70714be79db7670639d31abb
ACR-198ca00312074e54992b3872886f8f45
ACR-cb6aac9e792b490991505ba99e00a7a2
ACR-332b0d904d3a474fa367532fecf649bb
ACR-2a61827209f44017943f129af85ce175
ACR-8455362eb47a43919dc406b0077b848a
ACR-48abdd4dacab4df48efa379c115b37f6
ACR-3d7b8c1d595147c4a18611c917ddca6f
ACR-8cdb5b32083f4563bced680901fd491e
ACR-3386a33e1da744e7889591dbb669bed9
ACR-d590c255cf4549548f86efdbc066fbb5
ACR-79ecdd6c66ef42a492d91057c8cd8144
ACR-522fb859208a460c934b5ace275b2e7b
ACR-863c922651ec4ecc8635c7b068a6a857
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
