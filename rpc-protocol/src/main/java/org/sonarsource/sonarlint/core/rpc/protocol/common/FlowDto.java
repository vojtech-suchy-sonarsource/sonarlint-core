/*
ACR-0f88ee7ba4064a7cb428ad50b79e6d8b
ACR-ec64f3c15d81418bb698af4ddecc9e05
ACR-6ea148ae79e640e3923463c5df4388dd
ACR-dbac26c6a4064cc2a9824ae852094394
ACR-002c5af1c4ed4501bac9e1d92983df8d
ACR-69b7235afdbb4ae1a09f17226ed845dd
ACR-9f7a8aa96f544c66af3839ddd9db5fb4
ACR-832160fb1f9146b5bc862fb01999ada8
ACR-a280303e56454484999e452bf9ceb3c8
ACR-f2280b171ff24a47a49e48dac4f7f97d
ACR-26953103ae1140398cda406e1d6ca14c
ACR-d0ceadacae6940f8a8a4e905757a10ed
ACR-2bc57d913e31455e83690089df93c6a9
ACR-e3659c02b0db4e9da10bff433c5d118c
ACR-d29aa0d632284a4d8dfb7f62705177e0
ACR-092592ef7f5c49ae9d5647beef1ae78e
ACR-095255c4496243598a072c73da19402e
 */
package org.sonarsource.sonarlint.core.rpc.protocol.common;

import java.util.List;

public class FlowDto {
  private final List<LocationDto> locations;

  public FlowDto(List<LocationDto> locations) {
    this.locations = locations;
  }

  public List<LocationDto> getLocations() {
    return locations;
  }
}
