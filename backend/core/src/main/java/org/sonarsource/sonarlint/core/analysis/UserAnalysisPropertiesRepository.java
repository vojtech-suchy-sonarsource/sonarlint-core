/*
ACR-ebd44487bec7465c92eb54af915292d7
ACR-b30ee2f19adb41bb99e725e324a93cee
ACR-f7b9509e73fe4c208b14ab845b0a6e17
ACR-f7821015244549e09a0d4d6d5fb76d37
ACR-e796b74101e347a58dd37e412b438d88
ACR-c6bb3e3e744a43c597933979771fbb32
ACR-c9ef477e94c7475cb4235bbabc10c531
ACR-af156cdacaaf41909dd4ff2dd64358dd
ACR-8cdbfb758c0b4838bcfdccf4acc612ac
ACR-94050d9f58f74b6aaf0088f6524c97bd
ACR-41f9ff206699421c93b72007645d349c
ACR-8950f1c2c9fe4b7ba80c685e13d1a6f3
ACR-f73aa1adbbec4354a7752d0cd226a19a
ACR-042e8d938e484f42abfe84da52ee5864
ACR-b16a2ccf8ca14a6892c778f8cec74274
ACR-f3a7f038110b4a688c7e1ff38e12e7f2
ACR-e096e927b3304bf08aba6995e3613de3
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
