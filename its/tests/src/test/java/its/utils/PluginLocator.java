/*
ACR-c6a7633cd4d34e948de3457f7b880ce5
ACR-a9d5e5f26ba84314b89e083702781a08
ACR-ac086fd2a20d48859e3aceb7a7b0a85c
ACR-8e9f69ce767a4c35891db0ea3933a637
ACR-8272273d711e4285b38aebf1a34c4818
ACR-91f16cb96919493cba25aef0d7c19519
ACR-1da33eaf75c64b17bcdcd4b0749af20f
ACR-b70d4b185cf1437f804cfac5045a161d
ACR-9ca30948c9c14ee99f10150ecb668d31
ACR-1edc218b64bd4492b5d7f800233011c4
ACR-38a23fbd70fd4bdbb854bacd533a6d92
ACR-03da57a429d3470da4e8e40855a0702b
ACR-549dd3c169d34ac591e40118c6bb17a9
ACR-50e37751a9404866b8ef0005fdca35a6
ACR-0693776bf44e43ef8f8da40e522b7a97
ACR-6fd95124428c4c4bb62acb21a0e64947
ACR-1d57ca4ec4e04754adbdfdd80d38cb90
 */
package its.utils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class PluginLocator {

  public static Path getCppPluginPath() {
    return getPluginPath("sonar-cfamily-plugin-6.75.1.93101.jar");
  }

  public static Path getGoPluginPath() {
    return getPluginPath("sonar-go-plugin-1.31.0.4938.jar");
  }

  public static Path getIacPluginPath() {
    return getPluginPath("sonar-iac-plugin-2.2.0.18377.jar");
  }

  public static Path getJavascriptPluginPath() {
    return getPluginPath("sonar-javascript-plugin-11.7.1.36988.jar");
  }

  public static Map<String, Path> getEmbeddedPluginsByKeyForTests() {
    return Map.of(
      "javascript", getJavascriptPluginPath(),
      "go", PluginLocator.getGoPluginPath(),
      "iac", PluginLocator.getIacPluginPath());
  }

  private static Path getPluginPath(String file) {
    return Paths.get("target/plugins/").resolve(file);
  }

}
