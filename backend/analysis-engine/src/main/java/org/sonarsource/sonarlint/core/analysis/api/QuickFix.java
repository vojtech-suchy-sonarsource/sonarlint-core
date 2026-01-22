/*
ACR-083cb9c05ce84742b1c41a9062eb13af
ACR-8ae1707b662145dcb93a6bf1609ca70a
ACR-9ccf84b8859c4b13ae2ce4d5426f650f
ACR-b4c3d3f4eac14c2f9cbace4ca665c4b1
ACR-57855d715f1148cca4b2214f31c54c6c
ACR-6273eecf7cda4c089e1334257612a6b1
ACR-ed466769a7b14199b6d7b034945e8b8b
ACR-c282cdd7a37d4cb88d18df77f2640c6b
ACR-1432b3dc24064b778c176c0a14a8417a
ACR-0fa4163f57b341e2ab53e0bc6ffe725d
ACR-f0b96b6e83e7410ba6b5dbcd731f9060
ACR-8b7d4856192e425394be515458c97917
ACR-3e59b9333bd24b6ebe3d72d50ce830f9
ACR-f0f55b95f92440678f2c2e66ab30e08d
ACR-592ad7707c5b4cefb4034a13204f6faa
ACR-5ab91e24e6054132942d4721223bcb07
ACR-72540dfa3b4245c7b3f626577a2058e4
 */
package org.sonarsource.sonarlint.core.analysis.api;

import java.util.List;

public class QuickFix {

  private final List<ClientInputFileEdit> inputFileEdits;
  private final String message;

  public QuickFix(List<ClientInputFileEdit> inputFileEdits, String message) {
    this.inputFileEdits = inputFileEdits;
    this.message = message;
  }

  public List<ClientInputFileEdit> inputFileEdits() {
    return inputFileEdits;
  }

  public String message() {
    return message;
  }
}
