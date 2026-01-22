/*
ACR-fc0ca3ecf02d4ad7865120a80ac96b86
ACR-8d977ecf9c8a431d86b6087412705259
ACR-30b77484b5f54b57a201d62fb3e488d4
ACR-ae0d8c650bc3474e985c4171056cdb35
ACR-071bf485d0c94282aefe49a31de510ba
ACR-8219863aba5f4d9dad5f124e5a5aebfe
ACR-d49d2b84ed5840f9a9910cfa9d1c0233
ACR-5d22bec03cb44bfcb063d2e9ee2af3ad
ACR-68e0d69029574f7da7bc8d787f98a28b
ACR-118386c318004d3caa0a3f78914057dd
ACR-7fd68b2c2fe7459f88155fd8f81d7880
ACR-955424d7960f4653b5370b4b2712c98a
ACR-6b184b1cb18d46729f9710c23f2a5d68
ACR-b875879199714102b3cde7013e47ad78
ACR-c889b9014cd04f1eb08374628de5ab3c
ACR-97ade4d55800447a971c633b3a6afb99
ACR-60e0effe6a33478089bf9f461591934a
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.issue;

import java.util.List;

public class QuickFixDto {

  private final List<FileEditDto> inputFileEdits;
  private final String message;

  public QuickFixDto(List<FileEditDto> inputFileEdits, String message) {
    this.inputFileEdits = inputFileEdits;
    this.message = message;
  }

  public List<FileEditDto> fileEdits() {
    return inputFileEdits;
  }

  public String message() {
    return message;
  }
}
