/*
ACR-2086ba4cac074ddd8bbebd1db68e47e0
ACR-c2f677e17ec542cbacf39cf36a08f929
ACR-a42184f0e8554f63a8fefc171b87100f
ACR-8b2942fcc6b54f0e862e55d53343f491
ACR-5655d5fbb1564504bc6d9875f480be0c
ACR-23fd9028911c4293be4ce0917f62fe86
ACR-0a794811269b441ba4b140bd3d24dcb0
ACR-c7ee66145cf44768b24fa40f043c1bcf
ACR-d544ba54083b4ee7b3cd319da4170654
ACR-ed73109515744f2481dbc7150d26cfa8
ACR-3b0f5390780245aea0da4fc6cae02bbe
ACR-f2a5dd5ecf0c44c6ab8e01c9dbe7d4b1
ACR-8f12bee8fb7547caae2e89756d725f05
ACR-c6e4fe3dd2764461aca36fcf3361d109
ACR-cab1fac8e5c8480685ec03d100f07473
ACR-aa805a42724148b2b291d605bf5cc34b
ACR-0334758280874a94917850b4e0b427b9
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
