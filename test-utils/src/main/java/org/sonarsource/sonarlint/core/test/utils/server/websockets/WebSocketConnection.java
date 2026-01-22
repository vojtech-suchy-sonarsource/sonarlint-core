/*
ACR-9663f3b23ba74d5f91213dc5eb139ebe
ACR-b0f56131315b4978abd6c97c8b79b29d
ACR-5591a88466154261a81ade438f9c9c3c
ACR-03149ce9bf8848bc94903917907137cc
ACR-bfa65059152343f2b2b4d2423b519231
ACR-86149b82a589444199831461c5da8c60
ACR-c6217b2ca1ef47f0b5f3b7f99777fce7
ACR-e44e976705b3413b8d1a813f30e866b2
ACR-889a5f838a524122a192ca5ee9898a99
ACR-75081da8140a43118b0143654884e5b0
ACR-9650a5fa5043426a9fe3207908fb8904
ACR-e7e48446accc4f7ca9913f4d943c632c
ACR-4c0f2732f9e642749f95c409c1a48401
ACR-406c0ccda4d6400cb6ebd84f62dcc2b4
ACR-bf7619800b564ab39356b3f8f6bc577b
ACR-d5bafdd950364626912c0124f3b0ebe9
ACR-a12760b31fd94a9981e30b99f82efd47
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
