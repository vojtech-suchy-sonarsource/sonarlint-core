/*
ACR-1ea704765d4144db8a325008932b31b7
ACR-f242cf1f9977425b8c2614616b9a93d1
ACR-4c010f5648614455bbaaa5a4f4a8ab7c
ACR-d2b0cf1e375d466fa9b2133f9de7c071
ACR-f7280f79a2954be9ab6b2307474dde2b
ACR-0e968b186ec740dab84179fb9def4b7f
ACR-aec4dc58e7dc4886958ba0e9ec4ab1e0
ACR-6c15bbe99c244065a2ff605a215116d8
ACR-36dad1450679467cac1ea0a6a26b3a5b
ACR-da72b52b3cd645728c4441c113459444
ACR-b5a9a07b06e54f259297e22717d640eb
ACR-b39e13e115374578b0caebb6c3f0f24e
ACR-1c020c0ce22042abbf15c9900179ae45
ACR-7b071983798f4010b6490a73ab89c2c4
ACR-90a522d4ae41418cb22618f70c8a7a84
ACR-b0e701b2679949f88f89edd38826357f
ACR-49b33ec954ba4d17b1812286cb7333a0
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