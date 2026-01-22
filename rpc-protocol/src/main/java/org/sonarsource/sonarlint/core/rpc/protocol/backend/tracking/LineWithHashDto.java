/*
ACR-ad6a705f3319436a922e62d3da328339
ACR-a04a0313303e4459bd04222a5e83e763
ACR-ad774e94de994d8a802c78c8b6c7a75c
ACR-df43553cba87485fa2328cb2b0168612
ACR-52c0bb61549c4dbf815dd68a300a61c6
ACR-d5ebc49f3611400cb24d4d4be96be8cb
ACR-2736a7c22f66411f8e3c1c8e8e11953c
ACR-ec45f3392a4b4cd891160c1bd84f7e7e
ACR-0de75cabb9f24ec4abad9fe4478e4d23
ACR-8bd5a01822ee4e68a2344f3469799101
ACR-061eeb441c4a4b5f91faeca4e265a71c
ACR-8561d01175814d179fb897e8c02dc8ee
ACR-67bea3f51c8b4f5b9370aec2a2e9ea54
ACR-dd8bcd03c8c141ac96410ac236b94924
ACR-922cebe66ee14c8f801e4fa0252030ab
ACR-9929b219635e400cb3651a23df9f9d44
ACR-d83e725dc13b4b3ca77f445e9ceac1e1
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.tracking;

public class LineWithHashDto {
  private final int number;
  private final String hash;

  public LineWithHashDto(int number, String hash) {
    this.number = number;
    this.hash = hash;
  }

  public int getNumber() {
    return number;
  }


  public String getHash() {
    return hash;
  }
}
