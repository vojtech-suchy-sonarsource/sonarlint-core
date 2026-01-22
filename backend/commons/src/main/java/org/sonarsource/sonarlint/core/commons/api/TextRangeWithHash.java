/*
ACR-c86139b94c21497aa137b59c5ea2fe95
ACR-0061c6bbe7834124b6f24b17f49be420
ACR-ad5f557359af476ea7b2e295a9b2801b
ACR-a5fe50959cfe4325a47d1357cd0a7823
ACR-be92cc4302c24634b02754de616fd6b3
ACR-3930581f553a4dc893e73e1e057c056b
ACR-1fe6681229a34658bd0c1d981795ef29
ACR-ca75e58b93b8436dbe5eed05ecefe87d
ACR-0fc20d7330f94ee48d9f2ffcf79b6890
ACR-c12e1ca87b014af781b97ddd0b00ab75
ACR-eee51477fff84492b7e8d3d525ec08d9
ACR-884a01708ca64b24be469bd33e340508
ACR-d631e94941e3472a8ea68e4d6964d203
ACR-420b125edadc4aa292182a3a5dd5de4b
ACR-4bcebdb3c3aa44788e323f7420ae2aca
ACR-eb9c9b06671548ccbee18d54e130e119
ACR-c6e76fc7559b49c799856826e74f3aa4
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
