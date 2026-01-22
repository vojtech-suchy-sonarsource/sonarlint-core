/*
ACR-81cd2d286c3a4faaa04dad0e18c2b002
ACR-66a489b4d35c421cb54844bff88e4990
ACR-7e46bd488c364aaea45c574bf23785ac
ACR-701a192530614828b5f42f6ad360dd59
ACR-4e4cdba421ea4502864e6e490c044b6d
ACR-7f78c27de0014656b6ffd549bd1289fb
ACR-d0e7c0c7b6cc4bb4b17a8da0f861e7e1
ACR-7da97b8cc36445468f724936462ef409
ACR-c5d1760323544071bd445703945d6f6e
ACR-ac4f4215ea3b4a64aaf5c55c6f0f335e
ACR-0288f944451e49759784f282bb4d2ec3
ACR-d6fa7782aa674dd69754b03984ae715e
ACR-e81d99d6056c4e1f94fc73a93f411758
ACR-33cb1e7cdb05457cb281e0c531e4dc34
ACR-0382cfea57e74f1ebfb9b16a6f8de34d
ACR-65f211461b9b4d35ae4cf2becaf4bffa
ACR-a5e5e22a5cdf4d7599a6444d8b933b7b
 */
package org.sonarsource.sonarlint.core.serverapi.stream;

import java.util.List;

public class EventParser {
  private static final String EVENT_TYPE_PREFIX = "event: ";
  private static final String DATA_PREFIX = "data: ";

  static Event parse(String eventPayload) {
    var fields = List.of(eventPayload.split("\\n"));
    var type = "";
    var data = new StringBuilder();
    for (String field : fields) {
      if (field.startsWith(EVENT_TYPE_PREFIX)) {
        type = field.substring(EVENT_TYPE_PREFIX.length());
      } else if (field.startsWith(DATA_PREFIX)) {
        data.append(field.substring(DATA_PREFIX.length()));
      }
    }
    return new Event(type, data.toString());
  }

  private EventParser() {
    //ACR-8f86b8517baa40e398c9e81383b189d0
  }
}
