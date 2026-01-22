/*
ACR-251e07fc354f41bc8fdea652b38d19a2
ACR-89256aaa090145bfab7d34a5a984cd93
ACR-bb4bb7faee8448b0846b6ea3aabee482
ACR-cbb7316ba7194855bbf8f581d3ebd148
ACR-9093e457048548babd4a71aad8314535
ACR-4013292772014a88925e942ec7787f95
ACR-da94d659b7b0409eb00426831700f08c
ACR-8fa84522cc294505a3624a91e60d5819
ACR-17e24b798cba47048f20f0b34abdfde8
ACR-8912196640cd43d9a90a1fc730a8ab84
ACR-1893b759f37b416887221f7da68bac5a
ACR-a176b4785bcd4f77a5f7e634db926941
ACR-311d5036ef6e418aa33491376d8822b8
ACR-0f759e64616a432ebe241bfaa6f80225
ACR-7a3d2817a5144f9491a0ede20477abf6
ACR-d5768e89f8144892bd0cb08dcb09c719
ACR-bcc3db25fd594c239a6225e59003658d
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
