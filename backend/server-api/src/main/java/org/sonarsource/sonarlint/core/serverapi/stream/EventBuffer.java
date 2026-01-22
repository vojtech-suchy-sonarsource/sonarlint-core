/*
ACR-6f8c4b5340614d70a5a0744645bd5a4d
ACR-bd619a911ff9466bb661bc0ecb6f3024
ACR-2f548a17ba89421199757f0776a361da
ACR-a9fa73f18c614c1c8b4d9b90bc983009
ACR-4ebd101a68ff48539d11dbb35c78576e
ACR-96eceb6bbedb4fc5aa03f81ca4f6292a
ACR-154cdbe416764a0abdc59574c3a9e7ea
ACR-7591fd2819914f3c8e99a8eb8bc96917
ACR-ce98c26b888448ca861cca141b69c198
ACR-95b1b3be50e541e2836a882a1f2d2d00
ACR-ebcbb9b873304edf8b65fd64335da17e
ACR-8b5255840f6747408489bcc82f7e575f
ACR-de41601c7d0a48a3b674442196b119ed
ACR-8bc90a2272434a50812ddb7e5ce199fb
ACR-889b336524694e79bfe7a288bc9d531d
ACR-1d2b718b18e54425ab74accee58c5324
ACR-ff3c9ddf8242493a9028e73a1bffff34
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
