/*
ACR-6916186d2cdd47bd84ca788849b36c8c
ACR-2346085211ad4473aacf73bf410465a1
ACR-16efdb0d13e440a6a3c5d8d07fec327a
ACR-a9b1c1a898db478aa51dd5a5684fff36
ACR-7d241fd48cf74396aa571e6e721b1374
ACR-a6061d608b4b43a8b1e0e77bb11dcee7
ACR-52352b12996a4105ae73fdb10e527ce2
ACR-05417f99d7544f848b5fb000b2c5d1de
ACR-ec50ae2daca24162bbb5eb8e9e93856f
ACR-2663f39a900f4d4cb290c466cc5a1ab6
ACR-39de81eb49224beba5fe3876d58d98de
ACR-a9d694b582434add943f7ab76250da23
ACR-8d3cbe0bc37a40eb81541d9234d7939b
ACR-c04cf1e316ce49349886862d17799381
ACR-c273d82ffa0948d0b19591af46c0367b
ACR-d78564eefae94e10961598d1ded8f72c
ACR-9e06197edf224cc39fb4eb4a736fad5f
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
    //ACR-89d38fe2f65e4b02974f0578379dcef9
  }
}
