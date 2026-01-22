/*
ACR-908fbaea5a39423fb231c2a2ebba14ab
ACR-9d3157c09bcd4731b31d1c9cec35d505
ACR-3cb9eac269ae4a3597c2759da7af27a0
ACR-88e1dbaeb9864df19cb026fdcdd1658c
ACR-9b289d6826774faebed7a7967211aefb
ACR-0e421b0eac584e3a9ce94fe9aaa1cac4
ACR-382501accd7647ce9ee58fb281b94023
ACR-109c6dea75244049b365f43ad68eab3c
ACR-fcf7a8ef661240e68aa049a2647d3e12
ACR-5eeb960660254959abe9f3b7595be961
ACR-49064bfd8e064bcfbe895d3f79e5718c
ACR-cea95905ed784e56b417a6ecb3e6d8d3
ACR-d8a16e1061d049f5b58fccbfffa49158
ACR-dd52752b42fa487ba4b707fc38d3b0db
ACR-728ed8807ee04497becef978e8a614a9
ACR-38415ceef6d2400a9cbe340521f93b9a
ACR-95ff66727b1f4d3693127018eae87228
 */
package org.sonarsource.sonarlint.core.websocket;

public class WebSocketEventSubscribePayload {
  private final String action;
  private final String[] events;
  private final String filterType;
  private final String project;

  public WebSocketEventSubscribePayload(String action, String[] events, String filterType, String project) {
    this.action = action;
    this.events = events;
    this.filterType = filterType;
    this.project = project;
  }
}
