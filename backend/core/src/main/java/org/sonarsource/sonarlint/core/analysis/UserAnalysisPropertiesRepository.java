/*
ACR-93b2761ceed24548a79d71914a8eb9bb
ACR-853375605c384614ad7dbce8db1c2747
ACR-9d79f265fda1444b979eee180b99f937
ACR-3e3362b568f548d6b70f41f75f501bc4
ACR-42bcac7537e5419b85ebf50df3ca30ff
ACR-af207f4163164b0eaa8dba83679a1998
ACR-e6bb10c01ed0491bb58b79a08aa60cde
ACR-20a71a3234fd47e88234343ad83b1bad
ACR-e89e106bd62740b0ad434d83d00c9dd6
ACR-7a4d8530722b490c9058bff53b7137cd
ACR-e6969b398feb4247b31f80df76cd73c4
ACR-81e8cdb740f942aab89401742d3bf688
ACR-3aa660c932e34ca78b1add9bbe6547a3
ACR-7753c61b72ba4ba4b8b8fa927b0e453e
ACR-e505d008aece4c2c9e4e1fd92f849081
ACR-74c923527a864a51ab5edb76fdcf01c4
ACR-e66dbb111159490a929cf2d9288fd542
 */
package org.sonarsource.sonarlint.core.analysis;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;

public class UserAnalysisPropertiesRepository {
  private static final String PATH_TO_COMPILE_COMMANDS_ANALYZER_PROPERTY = "sonar.cfamily.compile-commands";
  private final Map<String, String> pathToCompileCommandsByConfigScope = new ConcurrentHashMap<>();
  private final Map<String, Map<String, String>> propertiesByConfigScope = new ConcurrentHashMap<>();

  public Map<String, String> getUserProperties(String configurationScopeId) {
    var properties = propertiesByConfigScope.getOrDefault(configurationScopeId, new HashMap<>());
    var pathToCompileCommands = pathToCompileCommandsByConfigScope.get(configurationScopeId);
    if (pathToCompileCommands == null) {
      return properties;
    }
    properties.put(PATH_TO_COMPILE_COMMANDS_ANALYZER_PROPERTY, pathToCompileCommands);
    return properties;
  }

  public boolean setUserProperties(String configurationScopeId, Map<String, String> userProperties) {
    var oldProperties = propertiesByConfigScope.get(configurationScopeId);
    var newProperties = new HashMap<>(userProperties);
    var changed = !newProperties.equals(oldProperties);
    if (changed) {
      propertiesByConfigScope.put(configurationScopeId, newProperties);
    }
    return changed;
  }

  public boolean setOrUpdatePathToCompileCommands(String configurationScopeId, @Nullable String value) {
    var newValue = value == null ? "" : value;
    var oldValue = pathToCompileCommandsByConfigScope.get(configurationScopeId);
    var changed = !newValue.equals(oldValue);
    if (changed) {
      pathToCompileCommandsByConfigScope.put(configurationScopeId, newValue);
    }
    return changed;
  }
}
