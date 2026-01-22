/*
ACR-18729ec727114ba090c68683773596ae
ACR-732de0822e1e4110a4265d1a7f5c098d
ACR-e65a7028902e49f99deee08d15b2686a
ACR-c713971970d74391be16cd9ea422f01c
ACR-af9e8c9a2eab45d999e1c43c20aea0b6
ACR-1fb19c93da014e4aa9822dec32976fc7
ACR-3c48cdb9d2f34de8a25e29de2a0dcc94
ACR-2b65b2d860444f3583b790f080ce3ced
ACR-8e3560138a124d63a12815b8c7f27c69
ACR-d517e6b88ef04a0895439591ffa0e922
ACR-41fd2ebf24814505938d71d8b6ba6a60
ACR-d2975d217f1b4863b89468d7845d113f
ACR-58909e07eecb471ab954bfc42410b83d
ACR-6d385163f77a40619abe60506900ad41
ACR-bb3600135efe42f78edcb599faaaa91d
ACR-0a729bd4757f4eb194fc6c184c050a61
ACR-a1e4e429ee5049e9bd1544e65761f7dc
 */
package org.sonarsource.sonarlint.core.event;

public class ConnectionCredentialsChangedEvent {

  private final String connectionId;

  public ConnectionCredentialsChangedEvent(String connectionId) {
    this.connectionId = connectionId;
  }

  public String getConnectionId() {
    return connectionId;
  }
}
