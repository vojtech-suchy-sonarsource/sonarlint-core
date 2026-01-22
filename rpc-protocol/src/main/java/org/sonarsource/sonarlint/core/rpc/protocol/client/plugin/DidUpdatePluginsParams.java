/*
ACR-e454ffb3c24f4aad964a4d5553f5af68
ACR-a0d78e19130c4556a48271489b4b07fa
ACR-8715e2cd9a994b75b9eb5f26c9b73583
ACR-ccad259ea987458bbdd8df356d957833
ACR-5d921266a9414f17badfacc1514702df
ACR-b214ac678856476e9d0e10c622809159
ACR-94c7e31171404d6ca97cd8c4507c9a59
ACR-afb3f9d735864c94b5d3653830b32f4b
ACR-da39545156f5488ca7a263d356935cd6
ACR-86ecfb17a8714c0abdc0f6ba5d0a293c
ACR-0b6cab029944411ba7a83836274336f6
ACR-0c52906228d7461cba7d75c505d740c1
ACR-7e533c48e51f4c5ca6a96f36a24e2b65
ACR-674bf8f0fbf24f25b3506e953b17712c
ACR-8b0043c394b2417ea0e226e38f2b7012
ACR-5ffb52073b3a45a48d1173fbf0d3113b
ACR-2465f38422ca42fc8c15d97a835a453e
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.plugin;

public class DidUpdatePluginsParams {
  private final String connectionId;

  public DidUpdatePluginsParams(String connectionId) {
    this.connectionId = connectionId;
  }

  public String getConnectionId() {
    return connectionId;
  }
}
