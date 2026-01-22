/*
ACR-e71bb94d217345618c279723327fcdbf
ACR-37dbf965e864476c80a7b61df50fbc85
ACR-3ce4591b9bdd48b58d4987cb2cb6b419
ACR-dc8ea88bc5de49ed909b45eeb8ab6509
ACR-b82d2c40bc32464abe5476cce82f0c95
ACR-9a0fd4b8db3f4e2f947f22b20cbf8261
ACR-e13f8485eedb45949a6245a986977488
ACR-128d1b73393d4c068d75a5de85fa6023
ACR-5f257b8314bf4cd183cdf0b34895c49b
ACR-db962d488c1e4a16aabb16e523fb8128
ACR-716010d001a3480bbfdb86b912526b5d
ACR-1e55793046fa4968aa135945bad14dfc
ACR-d300a226d990441aa122af150a929e08
ACR-c91caba30ee7407fb2bdd5a15c3c57fc
ACR-dd00c727e31d46bda97330d163b0d313
ACR-a798c26672954b5ab4a41fb977b50aa1
ACR-e93936e81cf14250b71280b499738bde
 */
package org.sonarsource.sonarlint.core.test.utils.server.websockets;

import jakarta.websocket.Session;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class WebSocketConnection {
  private final WebSocketRequest request;
  private boolean isOpened = true;
  private final List<String> receivedMessages = new CopyOnWriteArrayList<>();
  private Throwable throwable;
  private final Session session;

  public WebSocketConnection(WebSocketRequest request, Session session) {
    this.request = request;
    this.session = session;
  }

  public String getAuthorizationHeader() {
    return request.getAuthorizationHeader();
  }

  public String getUserAgent() {
    return request.getUserAgent();
  }

  public boolean isOpened() {
    return isOpened;
  }

  public List<String> getReceivedMessages() {
    return receivedMessages;
  }

  public void addReceivedMessage(String message) {
    receivedMessages.add(message);
  }

  void setIsClosed() {
    isOpened = false;
  }

  public void setIsError(Throwable throwable) {
    this.throwable = throwable;
  }

  public void sendMessage(String message) {
    if (session == null) {
      throw new IllegalStateException("Cannot send a message, session is null");
    }
    if (!isOpened) {
      throw new IllegalStateException("Cannot send a message, the WebSocket is not opened");
    }
    if (throwable != null) {
      throw new IllegalStateException("Cannot send a message, the WebSocket previously errored", throwable);
    }
    try {
      session.getBasicRemote().sendText(message);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void close() {
    if (session != null) {
      try {
        session.close();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
