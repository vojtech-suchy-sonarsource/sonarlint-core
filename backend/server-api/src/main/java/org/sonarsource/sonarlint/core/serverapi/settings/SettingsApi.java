/*
ACR-ae6016033cbc49c18b77e0e1e03769eb
ACR-62d44ced5d3e49f7bcbcdc9ba84dc16d
ACR-f666f7afe14b4b0d832a63063b299517
ACR-3e44ce6319134c83adc2fdd3825cd18b
ACR-711732febeb048f587f5ac548b3f238e
ACR-b5418b227ac74f10b399fd58bcf595ca
ACR-1907242c4bb341a1846d810afd3c0c7e
ACR-b2e745e8c3964717b8582718763efb84
ACR-eb365707381f49f3acbd9b64888e394f
ACR-24cffaca52ed47bca5901a4e62908ff1
ACR-6f303d7d732b4d21b7cbacf73f2f79d3
ACR-79566f0ac9c24eda8ab2b5e9436cc1c4
ACR-d2f3af66a8814960ab98107d4b997dbd
ACR-1137f4050bd94bbdb3b10071deac2423
ACR-cb0968d0eeb7415ca4f9087142927adc
ACR-09e3371fcaed4633983c51d8d9d79fff
ACR-8aaa1ef728024a97a144416611c81be1
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
