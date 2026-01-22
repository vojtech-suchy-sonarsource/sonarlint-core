/*
ACR-a4ac98ae6e7c4afa912862c830838093
ACR-6b6ac7e9aec043a69464056aa8adaa33
ACR-dc4f49dd789d4988be835f99d7e346ae
ACR-0f708d20f27d4feca39a980248f9c186
ACR-10fc81308f814eaabd142d86e86c8754
ACR-9a1c2681578d42bf80392456bdf77063
ACR-6893d241efd24eb7b766d066b22ea1fe
ACR-200b63a60bed47e8bc9da817b9a94614
ACR-75c398867b174b10a33c1873a9bb8859
ACR-65b6391b432a4370a378f9b79b5f9e46
ACR-36b9070f057748679bb44bc5ca06ca13
ACR-33383db83cb644099962d09977dba9b1
ACR-23fadcf19c044da498e085ac69bd93b4
ACR-fae340a6fd9d4d4e85d87bbdba8262b8
ACR-5052663142a1415b82fb7fea2b1612e3
ACR-4625a973b2e74da1ae849b3bdc2b95b8
ACR-4b5b1238503c441cb6ac63895607a326
 */
package org.sonarsource.sonarlint.core.serverapi.stream;

import java.util.ArrayList;
import java.util.List;

public class EventBuffer {
  private final StringBuilder buffer = new StringBuilder();

  EventBuffer append(String data) {
    buffer.append(data);
    return this;
  }

  List<String> drainCompleteEvents() {
    List<String> completeEvents = new ArrayList<>();
    int firstEventEndIndex;
    do {
      firstEventEndIndex = buffer.indexOf("\n\n");
      if (firstEventEndIndex == -1) {
        break;
      }
      var completeEvent = buffer.substring(0, firstEventEndIndex).trim();
      buffer.delete(0, firstEventEndIndex + 2);
      if (!completeEvent.isEmpty()) {
        completeEvents.add(completeEvent);
      }
    } while (true);
    return completeEvents;
  }
}
