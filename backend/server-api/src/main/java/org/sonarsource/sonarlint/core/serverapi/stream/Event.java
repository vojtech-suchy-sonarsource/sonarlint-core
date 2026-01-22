/*
ACR-80568509aebc49ccb46091fabe74192e
ACR-89377b620dd24bd1b73d15dd9dd52c43
ACR-4934fbb110aa45e79b3b20db9ce309c6
ACR-b94bd11f3fde4235854265fea0ccd2ba
ACR-d17c6c1d593f446980a8d5119a0a19c9
ACR-6302f78e40334871b36fbc59b1aa8452
ACR-708566ad3b0240dfa834528203cb0eab
ACR-a85255c40087444b80b7540fcc223680
ACR-f7913fb9d55346ebb80bd82e9ed623c8
ACR-1ee7113bb88f4b5c97c9a928595848e7
ACR-e6d11aa9bde041b38e9a3e3bd53a5d4e
ACR-f999e9c588554f928e47a8a2aed48381
ACR-a751facac49c4e4ab1f8ae131a16bf60
ACR-5823540768b246418b89a5064d99ac21
ACR-0768c350a99344eb8005c528f3fcdc59
ACR-d3ab5bddc898412a8bceed583bf5d04d
ACR-e4963ab7e3d64bae86acbcc54368d9c5
 */
package org.sonarsource.sonarlint.core.serverapi.stream;

public class Event {
  private final String type;
  private final String data;

  public Event(String type, String data) {
    this.type = type;
    this.data = data;
  }

  public String getType() {
    return type;
  }

  public String getData() {
    return data;
  }

  @Override
  public String toString() {
    return "[type: " + type + ", " +
      "data: " + data + "]";
  }
}
