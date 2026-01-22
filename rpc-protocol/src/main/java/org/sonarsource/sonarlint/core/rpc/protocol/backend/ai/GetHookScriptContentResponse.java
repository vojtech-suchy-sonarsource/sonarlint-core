/*
ACR-18ee402f71954e2e808b78b9a1c397a5
ACR-3316b721692d4e49a432130c312fb5f9
ACR-0ad977f0638e4572b95ad60fc36b605e
ACR-6d734808e09b479dadd777c41c49dada
ACR-a83d87f6fc7f4ffc93980433dc9993fc
ACR-99e74cb184a840b7b210f236d243f997
ACR-c19bf67cd15b45bdb072c38aadd683c7
ACR-f999e34ab16149228d43e52c510906dc
ACR-2ae9b54455fd42ada090c987ae20c924
ACR-9ef5f084dad946579b7585ae42dec41d
ACR-156ded08ab6c43b8bbe7407e0fe802e0
ACR-100f3b3df7b04a31bb63e94bb6dfc870
ACR-63623b3a5d264101a3f55be6d7e0aa98
ACR-5677d9f52afe4cae89af65d2fc8d080a
ACR-8617433d3f2a4ff09628cafa2ff36dde
ACR-625ac7c8ccae42098cc8c329c96ede52
ACR-862d79e915384262b0de4c9bbe2faac1
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.ai;

public class GetHookScriptContentResponse {
  private final String scriptContent;
  private final String scriptFileName;
  private final String configContent;
  private final String configFileName;

  public GetHookScriptContentResponse(String scriptContent, String scriptFileName, String configContent, String configFileName) {
    this.scriptContent = scriptContent;
    this.scriptFileName = scriptFileName;
    this.configContent = configContent;
    this.configFileName = configFileName;
  }

  public String getScriptContent() {
    return scriptContent;
  }

  public String getScriptFileName() {
    return scriptFileName;
  }

  public String getConfigContent() {
    return configContent;
  }

  public String getConfigFileName() {
    return configFileName;
  }
}

