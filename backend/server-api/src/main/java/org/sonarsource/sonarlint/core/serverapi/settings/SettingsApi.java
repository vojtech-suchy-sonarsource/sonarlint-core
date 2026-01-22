/*
ACR-b1808a899da8468fb0b23de9ee5f95ad
ACR-b4cd274eb7664554becb6629909e9c17
ACR-f42d6c733beb4e02850efb8ae2768d4d
ACR-b0b3e83251274035a4ce2fe58d5ed890
ACR-d1afe5cf3ec546d5bcf2036dbf2ec0e8
ACR-00e361f4992b4bcabe5962a7b72512ed
ACR-bbfa3f1237bd43089701c9a2f9650199
ACR-ccc068c170a647f588e425d5c8e7405f
ACR-16bd440c9f9e43db91c93b3087f6c0ab
ACR-80a0026c80b34ffb9b83f651257d8917
ACR-d2719289ea334a4a8e6e96e4fa82bc08
ACR-07ba21f367cd4e4fb30b814efa526dc2
ACR-3101ad4834e843f88b2ef8f104268c92
ACR-324e2505ea9141faa55682fdbef83026
ACR-878aabc9a1a84744ba43ebc78e4b3881
ACR-a37a6146f6474316813061069288fe13
ACR-db0be28980a7475fab2f03ce21915315
 */
package org.sonarsource.sonarlint.core.serverapi.settings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.serverapi.ServerApiHelper;
import org.sonarsource.sonarlint.core.serverapi.UrlUtils;
import org.sonarsource.sonarlint.core.serverapi.proto.sonarqube.ws.Settings;

public class SettingsApi {
  private static final SonarLintLogger LOG = SonarLintLogger.get();
  private static final String API_SETTINGS_PATH = "/api/settings/values.protobuf";

  private final ServerApiHelper helper;

  public SettingsApi(ServerApiHelper helper) {
    this.helper = helper;
  }

  public Map<String, String> getGlobalSettings(SonarLintCancelMonitor cancelMonitor) {
    return getSettings("", cancelMonitor);
  }

  public Map<String, String> getProjectSettings(String projectKey, SonarLintCancelMonitor cancelMonitor) {
    return getSettings("?component=" + UrlUtils.urlEncode(projectKey), cancelMonitor);
  }

  private Map<String, String> getSettings(String queryParameters, SonarLintCancelMonitor cancelMonitor) {
    var settings = new HashMap<String, String>();
    var url = API_SETTINGS_PATH + queryParameters;
    ServerApiHelper.consumeTimed(
      () -> helper.get(url, cancelMonitor),
      response -> {
        try (var is = response.bodyAsStream()) {
          var values = Settings.ValuesWsResponse.parseFrom(is);
          for (Settings.Setting s : values.getSettingsList()) {
            processSetting(settings::put, s);
          }
        } catch (IOException e) {
          throw new IllegalStateException("Unable to parse properties from: " + response.bodyAsString(), e);
        }
      },
      duration -> LOG.info("Downloaded settings in {}ms", duration));
    return settings;
  }

  private static void processSetting(BiConsumer<String, String> consumer, Settings.Setting s) {
    switch (s.getValueOneOfCase()) {
      case VALUE:
        consumer.accept(s.getKey(), s.getValue());
        break;
      case VALUES:
        consumer.accept(s.getKey(), String.join(",", s.getValues().getValuesList()));
        break;
      case FIELDVALUES:
        processPropertySet(s, consumer);
        break;
      default:
        throw new IllegalStateException("Unknown property value for " + s.getKey());
    }
  }

  private static void processPropertySet(Settings.Setting s, BiConsumer<String, String> consumer) {
    var ids = new ArrayList<String>();
    var id = 1;
    for (Settings.FieldValues.Value v : s.getFieldValues().getFieldValuesList()) {
      for (Map.Entry<String, String> entry : v.getValueMap().entrySet()) {
        consumer.accept(s.getKey() + "." + id + "." + entry.getKey(), entry.getValue());
      }
      ids.add(String.valueOf(id));
      id++;
    }
    consumer.accept(s.getKey(), String.join(",", ids));
  }

}
