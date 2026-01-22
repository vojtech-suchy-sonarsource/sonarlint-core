/*
ACR-c0c870c3f2074748a3b1e87acddb1933
ACR-786c6742980f46ebb43348c310df4651
ACR-ac6999bda6664bed855d7832bf43de7f
ACR-908b9e463b0a4b5ca7144935edc1c6cd
ACR-e6435d8633e042ad994543739e58834b
ACR-64d50d49c0b943cba4594cd024a4a9ce
ACR-b316ef76263349809e739d34065d8328
ACR-3b1a98451deb4e1f941a2a2a3a7bd3b6
ACR-40a65bda7f164a9b8e4cf1705fae59b1
ACR-2821dcfc96664283ab92e4a59a0ab9b9
ACR-0a28ad8535c54d479ab1df4ba29bdec8
ACR-345d968193b94cc69b71a7fc6c5a88ba
ACR-38b80f71909241b2b555e6cdb9b51b9c
ACR-04dfe50e026a41f8bf24ec2ae11e267c
ACR-4f219d985c2344f79b4b1c358d2ac67c
ACR-3b0e662eb49447c4a217e506506881b7
ACR-9516ad351c02431eb0f881f03707b0fc
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.log;

public class SetLogLevelParams {
  private final LogLevel newLevel;

  public SetLogLevelParams(LogLevel newLevel) {
    this.newLevel = newLevel;
  }

  public LogLevel getNewLevel() {
    return newLevel;
  }
}
