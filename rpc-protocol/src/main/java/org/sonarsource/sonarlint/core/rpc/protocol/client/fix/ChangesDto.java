/*
ACR-f1acca897bac42e9bea9d88b26f8c8f0
ACR-ca806f35b9c845119ae293b70ecb255a
ACR-9228aaf2fa5c4a2d9d4905d16c653e9e
ACR-1a23108a043b4de88d10d719757375d4
ACR-4f1c09b77219450fb2014f5395de44f5
ACR-eb53b1c4c214474aa6f849203e178cf8
ACR-92262db567244bc2afcaca67ae2d48e4
ACR-a4eb75589072420b9b7679dda13c56d3
ACR-0e9aab4a4f6e427782eca871edc9859b
ACR-f11524e87f664529b1bd595b3b87a640
ACR-63d6d739678f4809ab5f83e62dd1e62a
ACR-d9e89cdf5b2a4f95a5a90dc71f780874
ACR-404a6c50535642e1ad53670731c27e30
ACR-e6823fa323da48859ab1d2c93b00ada5
ACR-a7336314d166441490d5a5394139cfb4
ACR-91de45bbc52748609bb674a6525d1fb4
ACR-018d441747804c11a0208f2c7974838c
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.fix;

public class ChangesDto {

  private final LineRangeDto beforeLineRange;
  private final String before;
  private final String after;

  public ChangesDto(LineRangeDto beforeLineRange, String before, String after) {
    this.beforeLineRange = beforeLineRange;
    this.before = before;
    this.after = after;
  }

  public LineRangeDto beforeLineRange() {
    return beforeLineRange;
  }

  public String before() {
    return before;
  }

  public String after() {
    return after;
  }

}
