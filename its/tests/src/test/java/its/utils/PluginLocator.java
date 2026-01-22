/*
ACR-2b059c003b13425bb5f2282712c265c4
ACR-a493a06fb3254ec3a778b6e3223f8a2e
ACR-0d4cd96aa1034989ac418ddab8250850
ACR-c60564712eff43fea35238bc18baedcd
ACR-5215dd87f6dd4630a9ea4bd7c1a0c15b
ACR-ff6b77b383dc4c6bb27130bbc235a63b
ACR-15520eee8d0741dca5340be972c643a8
ACR-268000acb8d64991b24ec7ff17e27556
ACR-75a5a980cd5d4808a4a5de2796be3949
ACR-80d891ffc248471d962852342d1e8822
ACR-d505fb605ccd4134b773e3121814d1f9
ACR-1a9d16e1ba1340a793bb88fa9023596e
ACR-fdf1b2bedddb4dd999bc47fb8c6c09a0
ACR-b6310b2dc07546e4ac049e7d0f7482b1
ACR-67c12f1bd9634fcfbd5cc00f439df16a
ACR-7a710cb4de214c78b7ec5baa8eb023e0
ACR-8fba0dcdc84b4c6aa0a4e09b4313c64b
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
