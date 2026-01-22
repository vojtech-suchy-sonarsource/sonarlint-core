/*
ACR-ed0d562bc3f44e1eaa53f3d668317530
ACR-25482a8879d54adabd4245d316c2c07d
ACR-1da37d7fcad446d6b8a2e60633d5ef1d
ACR-bb2498c8b865452bb6071c14e4926c76
ACR-1e0dab7815a449baa1dc74bf5ae2e92b
ACR-3d5b44d164f04aa9aab59402c311d450
ACR-9a95d2f5d1cf4339a4240717d4268c1f
ACR-4405c8530a5d48a38f660f0ca72c60ae
ACR-6fc00646b2ec48b7b0fa20e9ec29cc98
ACR-1350cdb8a1fe488bae716b056bae4747
ACR-d67bc502b78847bb9f9f78f20e209327
ACR-6e48deae904b427b8a19a960ca07bcfb
ACR-a63d295f6304489bba796e446294f3ff
ACR-1ffe28eb48e74e61adac0e4fb4f15965
ACR-8f6ebcafe3404b028a232bc3699829bd
ACR-6d92b3dd6c614d999c198c3c4247af58
ACR-cfcf8b5f882e49cfb2558791cc731ed8
 */
package org.sonarsource.sonarlint.core.websocket;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class History {
  private final Map<String, Instant> receivedMessages = new ConcurrentHashMap<>();
  public void recordMessage(String message) {
    receivedMessages.put(message, Instant.now());
  }

  public boolean exists(String message) {
    return receivedMessages.containsKey(message);
  }

  public void forgetOlderThan(Duration expiryDuration) {
    var now = Instant.now();
    receivedMessages.values().removeIf(messageDate -> messageDate.isBefore(now.minus(expiryDuration)));
  }
}
