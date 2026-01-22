/*
ACR-88f7b3d7599d4cf18177831db0289988
ACR-1321736151504981b236181f23c14787
ACR-8a59858b60144080977d7bd7941e05fd
ACR-c79b99a3280f47418f44617184f34fee
ACR-8bb2860991da4a73a93072c693931924
ACR-158bc84b3bac4eceb97cd7c6eb43f51a
ACR-f45e15f5c50845738332a63933f0ca7b
ACR-6c923902476b493abbfdb507ba4a3d9a
ACR-c9839668c2aa440d8083b850f32ff163
ACR-6f4768577a7d4e4aa631ec29752f02f1
ACR-aacd8ff6171e40fd8dce0987b20f1841
ACR-96532dcbcf4c43b1ad07baf9955ded71
ACR-f0e87a3b52da403c86cb8caa6aed9595
ACR-79907eace43d4af2acf60b1981354ec9
ACR-1ea4b32c4560473e838eace7786fe9b2
ACR-792a32bd399c4f1eb124078cb2c1178f
ACR-64d887964bf34234ab1d72beffc0f4bc
 */
package org.sonarsource.sonarlint.core.commons.api;

import java.util.Objects;

public class TextRangeWithHash extends TextRange {

  private final String hash;

  public TextRangeWithHash(int startLine, int startLineOffset, int endLine, int endLineOffset, String hash) {
    super(startLine, startLineOffset, endLine, endLineOffset);
    this.hash = hash;
  }

  public String getHash() {
    return hash;
  }

  @Override
  public int hashCode() {
    final var prime = 31;
    int result = super.hashCode();
    result = prime * result + Objects.hash(hash);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (!(obj instanceof TextRangeWithHash other)) {
      return false;
    }
    return Objects.equals(hash, other.hash);
  }

}
