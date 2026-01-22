/*
ACR-8f69528fb49e4698ab01491d65db1f3f
ACR-2c02e812aad74a438118b06bf820397f
ACR-d49522511d6b4660821c85d32a551594
ACR-94c51edf2b3a493a9390ff6af3a54d57
ACR-f2b1b1105aed4afcb51ff5ce558c1939
ACR-44e4a5c23365412ea49ee60dbfbb7a63
ACR-aea9f270f1464dc196c6e2cfaba9bf0a
ACR-ee8052aa57964d29a12be434374bcc38
ACR-060b067bfd494e6dab655633f1ca4119
ACR-5ef2a5620d0546388d2b9cebc03f34fb
ACR-a3f13e93d74f40e8a3bc9f33747fa760
ACR-75fc6eb079984167bea2ad20a274539d
ACR-a2ac4c5769154dcead024a3b95d2e1cb
ACR-2e4e18be9a2143628cae75255f3d18ef
ACR-c1450b790b32440eb155b897437069b6
ACR-c1603e4b059e4bd6a2acad130c254c28
ACR-6c43dc128ee64f78ba4509434de448c9
 */
package org.sonarsource.sonarlint.core.commons.log;

import ch.qos.logback.core.AppenderBase;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ConcurrentListAppender<E> extends AppenderBase<E> {
  public final Queue<E> list = new ConcurrentLinkedQueue<E>();

  protected void append(E e) {
    list.add(e);
  }
}